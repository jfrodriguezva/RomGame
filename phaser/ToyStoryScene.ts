import Phaser from "phaser";

export interface ToyStoryLevelConfig {
  enemySpeed: number;
  enemyCount: number;
}

export interface ToyStoryCharacter {
  id: string;
  emoji: string;
  color: string;
}

const MAX_HP = 100;
const DAMAGE = 20;
const INVULN_MS = 900;
const HEAL_PER_SEC = 12;
const IDLE_TO_HEAL_MS = 1200;

export default class ToyStoryScene extends Phaser.Scene {
  private player!: Phaser.Physics.Arcade.Sprite;
  private cursors: { left: boolean; right: boolean; jump: boolean } = {
    left: false,
    right: false,
    jump: false,
  };
  private enemies!: Phaser.Physics.Arcade.Group;
  private hp = MAX_HP;
  private lastHitAt = 0;
  private lastInputAt = 0;
  private character: ToyStoryCharacter;
  private config: ToyStoryLevelConfig;
  private flag!: Phaser.Physics.Arcade.Sprite;
  private playerLabel!: Phaser.GameObjects.Text;
  private won = false;

  constructor(character: ToyStoryCharacter, config: ToyStoryLevelConfig) {
    super("toystory");
    this.character = character;
    this.config = config;
  }

  preload() {
    this.makeTexture("ground", "#166534", 2000, 40);
    this.makeTexture("platform", "#65a30d", 140, 24);
    this.makeTexture("enemy", "#dc2626", 40, 40, true);
    this.makeTexture("flag", "#f59e0b", 12, 60);
    this.makeTexture("player", this.character.color, 42, 42, true);
  }

  private makeTexture(key: string, color: string, w: number, h: number, circle = false) {
    const g = this.add.graphics();
    g.fillStyle(Phaser.Display.Color.HexStringToColor(color).color, 1);
    if (circle) {
      g.fillCircle(w / 2, h / 2, w / 2);
    } else {
      g.fillRect(0, 0, w, h);
    }
    g.generateTexture(key, w, h);
    g.destroy();
  }

  create() {
    this.won = false;
    this.hp = MAX_HP;
    this.lastHitAt = 0;
    this.lastInputAt = this.time.now;

    const worldWidth = 1400;
    const groundY = 460;
    this.physics.world.setBounds(0, 0, worldWidth, 500);

    const platforms = this.physics.add.staticGroup();
    platforms.create(worldWidth / 2, groundY, "ground").setScale(worldWidth / 2000, 1).refreshBody();
    platforms.create(360, 340, "platform");
    platforms.create(620, 260, "platform");
    platforms.create(900, 340, "platform");
    platforms.create(1150, 260, "platform");

    this.player = this.physics.add.sprite(60, 380, "player");
    this.player.setCollideWorldBounds(true);
    this.player.setBounce(0.1);
    this.playerLabel = this.add
      .text(this.player.x, this.player.y - 28, this.character.emoji, { fontSize: "24px" })
      .setOrigin(0.5)
      .setDepth(5);
    this.physics.add.collider(this.player, platforms);

    this.enemies = this.physics.add.group();
    for (let i = 0; i < this.config.enemyCount; i++) {
      const x = 400 + i * 300;
      const enemy = this.enemies.create(x, groundY - 40, "enemy") as Phaser.Physics.Arcade.Sprite;
      enemy.setVelocityX(this.config.enemySpeed);
      enemy.setBounce(1, 0);
      enemy.setCollideWorldBounds(true);
      enemy.setData("minX", x - 90);
      enemy.setData("maxX", x + 90);
    }
    this.physics.add.collider(this.enemies, platforms);
    this.physics.add.overlap(this.player, this.enemies, () => this.handleHit());

    this.flag = this.physics.add.sprite(worldWidth - 60, groundY - 60, "flag");
    this.flag.setImmovable(true);
    this.physics.add.overlap(this.player, this.flag, () => this.handleWin());

    this.cameras.main.setBounds(0, 0, worldWidth, 500);
    this.cameras.main.startFollow(this.player, true, 0.1, 0.1);

    this.game.events.emit("hp", this.hp);
  }

  private handleHit() {
    if (this.won) return;
    const now = this.time.now;
    if (now - this.lastHitAt < INVULN_MS) return;
    this.lastHitAt = now;
    this.hp = Math.max(0, this.hp - DAMAGE);
    this.game.events.emit("hp", this.hp);
    this.game.events.emit("hit");
    this.player.setTint(0xff8888);
    this.time.delayedCall(200, () => this.player.clearTint());
  }

  private handleWin() {
    if (this.won) return;
    this.won = true;
    this.game.events.emit("win");
  }

  update(time: number) {
    this.playerLabel.setPosition(this.player.x, this.player.y - 28);

    if (this.won) {
      this.player.setVelocityX(0);
      return;
    }

    const speed = 190;
    let moving = false;
    if (this.cursors.left) {
      this.player.setVelocityX(-speed);
      this.player.setFlipX(true);
      moving = true;
    } else if (this.cursors.right) {
      this.player.setVelocityX(speed);
      this.player.setFlipX(false);
      moving = true;
    } else {
      this.player.setVelocityX(0);
    }

    if (this.cursors.jump && this.player.body?.blocked.down) {
      this.player.setVelocityY(-360);
      moving = true;
    }

    if (moving) this.lastInputAt = time;

    // curación al quedarse quieto
    if (time - this.lastInputAt > IDLE_TO_HEAL_MS && this.hp < MAX_HP) {
      this.hp = Math.min(MAX_HP, this.hp + (HEAL_PER_SEC * this.game.loop.delta) / 1000);
      this.game.events.emit("hp", Math.round(this.hp));
    }

    this.enemies.children.forEach((child) => {
      const enemy = child as Phaser.Physics.Arcade.Sprite;
      const minX = enemy.getData("minX");
      const maxX = enemy.getData("maxX");
      if (enemy.x <= minX) enemy.setVelocityX(Math.abs(this.config.enemySpeed));
      if (enemy.x >= maxX) enemy.setVelocityX(-Math.abs(this.config.enemySpeed));
    });
  }

  setInput(key: "left" | "right" | "jump", value: boolean) {
    this.cursors[key] = value;
  }
}
