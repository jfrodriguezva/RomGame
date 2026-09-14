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
  private wasGrounded = true;
  private idleBob?: Phaser.Tweens.Tween;

  constructor(character: ToyStoryCharacter, config: ToyStoryLevelConfig) {
    super("toystory");
    this.character = character;
    this.config = config;
  }

  preload() {
    this.makeGroundTexture("ground", 2000, 40);
    this.makePlatformTexture("platform", 140, 24);
    this.makeEnemyTexture("enemy", 40);
    this.makeFlagTexture("flag");
    this.makeTexture("player", this.character.color, 42, 42, true);
    this.makeCloudTexture("cloud");
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

  /** Tierra con una franja de pasto arriba, no un bloque plano de un solo color. */
  private makeGroundTexture(key: string, w: number, h: number) {
    const g = this.add.graphics();
    g.fillStyle(0x92400e, 1);
    g.fillRect(0, 10, w, h - 10);
    g.fillStyle(0x22c55e, 1);
    g.fillRect(0, 0, w, 14);
    g.fillStyle(0x16a34a, 1);
    for (let x = 0; x < w; x += 14) g.fillTriangle(x, 14, x + 7, 4, x + 14, 14);
    g.generateTexture(key, w, h);
    g.destroy();
  }

  private makePlatformTexture(key: string, w: number, h: number) {
    const g = this.add.graphics();
    g.fillStyle(0x4d7c0f, 1);
    g.fillRoundedRect(0, 0, w, h, 8);
    g.fillStyle(0x84cc16, 1);
    g.fillRoundedRect(0, 0, w, 6, { tl: 8, tr: 8, bl: 0, br: 0 });
    g.generateTexture(key, w, h);
    g.destroy();
  }

  /** Una carita simple en vez de un círculo rojo liso: lee como personaje. */
  private makeEnemyTexture(key: string, size: number) {
    const g = this.add.graphics();
    g.fillStyle(0xdc2626, 1);
    g.fillCircle(size / 2, size / 2, size / 2);
    g.fillStyle(0xffffff, 1);
    g.fillCircle(size * 0.32, size * 0.42, size * 0.13);
    g.fillCircle(size * 0.68, size * 0.42, size * 0.13);
    g.fillStyle(0x1f2937, 1);
    g.fillCircle(size * 0.32, size * 0.42, size * 0.06);
    g.fillCircle(size * 0.68, size * 0.42, size * 0.06);
    g.generateTexture(key, size, size);
    g.destroy();
  }

  private makeFlagTexture(key: string) {
    const w = 40;
    const h = 60;
    const g = this.add.graphics();
    g.fillStyle(0x78350f, 1);
    g.fillRect(w / 2 - 2, 0, 4, h);
    g.fillStyle(0xf59e0b, 1);
    g.fillTriangle(w / 2, 4, w / 2 + 28, 12, w / 2, 24);
    g.generateTexture(key, w, h);
    g.destroy();
  }

  private makeCloudTexture(key: string) {
    const w = 90;
    const h = 40;
    const g = this.add.graphics();
    g.fillStyle(0xffffff, 0.9);
    g.fillCircle(28, 24, 18);
    g.fillCircle(50, 16, 16);
    g.fillCircle(66, 24, 14);
    g.fillRoundedRect(14, 22, 62, 14, 10);
    g.generateTexture(key, w, h);
    g.destroy();
  }

  create() {
    this.won = false;
    this.hp = MAX_HP;
    this.lastHitAt = 0;
    this.lastInputAt = this.time.now;
    this.wasGrounded = true;

    const worldWidth = 1400;
    const groundY = 460;
    this.physics.world.setBounds(0, 0, worldWidth, 500);

    // Cielo con degradado, sol fijo en la cámara y nubes que se desplazan
    // más lento que el mundo: sin esto la escena se sentía inmóvil salvo
    // por el jugador.
    const sky = this.add.graphics().setScrollFactor(0);
    sky.fillGradientStyle(0x7dd3fc, 0x7dd3fc, 0xe0f2fe, 0xe0f2fe, 1);
    sky.fillRect(0, 0, this.scale.width, this.scale.height);
    this.add.circle(this.scale.width - 44, 44, 22, 0xfde68a).setScrollFactor(0);
    for (let i = 0; i < 6; i++) {
      this.add
        .image(120 + i * 260, 60 + (i % 3) * 40, "cloud")
        .setScrollFactor(0.35)
        .setAlpha(0.85);
    }

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
    this.startIdleBob();

    this.enemies = this.physics.add.group();
    for (let i = 0; i < this.config.enemyCount; i++) {
      const x = 400 + i * 300;
      const enemy = this.enemies.create(x, groundY - 40, "enemy") as Phaser.Physics.Arcade.Sprite;
      enemy.setVelocityX(this.config.enemySpeed);
      enemy.setBounce(1, 0);
      enemy.setCollideWorldBounds(true);
      enemy.setData("minX", x - 90);
      enemy.setData("maxX", x + 90);
      this.tweens.add({
        targets: enemy,
        scaleY: 0.85,
        scaleX: 1.1,
        duration: 260,
        yoyo: true,
        repeat: -1,
        ease: "Sine.easeInOut",
        delay: i * 120,
      });
    }
    this.physics.add.collider(this.enemies, platforms);
    this.physics.add.overlap(this.player, this.enemies, () => this.handleHit());

    this.flag = this.physics.add.sprite(worldWidth - 60, groundY - 60, "flag");
    this.flag.setImmovable(true);
    this.physics.add.overlap(this.player, this.flag, () => this.handleWin());
    this.tweens.add({
      targets: this.flag,
      angle: 8,
      duration: 700,
      yoyo: true,
      repeat: -1,
      ease: "Sine.easeInOut",
    });

    this.cameras.main.setBounds(0, 0, worldWidth, 500);
    this.cameras.main.startFollow(this.player, true, 0.1, 0.1);

    this.game.events.emit("hp", this.hp);
  }

  private startIdleBob() {
    this.idleBob?.stop();
    this.player.setScale(1, 1);
    this.idleBob = this.tweens.add({
      targets: this.player,
      scaleY: 1.06,
      scaleX: 0.96,
      duration: 480,
      yoyo: true,
      repeat: -1,
      ease: "Sine.easeInOut",
    });
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

    const grounded = !!this.player.body?.blocked.down;

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

    if (this.cursors.jump && grounded) {
      this.player.setVelocityY(-360);
      moving = true;
    }

    // Estirón al saltar, aplastón al aterrizar: sin esto los saltos se
    // sentían como un simple teletransporte hacia arriba y abajo.
    if (!grounded) {
      this.idleBob?.pause();
      this.player.setScale(0.85, 1.2);
    } else if (!this.wasGrounded) {
      this.player.setScale(1.2, 0.82);
      this.tweens.add({
        targets: this.player,
        scaleX: 1,
        scaleY: 1,
        duration: 160,
        ease: "Back.easeOut",
        onComplete: () => this.startIdleBob(),
      });
    }
    this.wasGrounded = grounded;

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
