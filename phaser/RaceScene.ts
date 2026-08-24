import Phaser from "phaser";

export interface RaceLevelConfig {
  scrollSpeed: number;
  obstacleEveryMs: number;
}

export default class RaceScene extends Phaser.Scene {
  private car!: Phaser.Physics.Arcade.Sprite;
  private items!: Phaser.Physics.Arcade.Group;
  private moveDir = 0;
  private config: RaceLevelConfig;
  private score = 0;
  private spawnTimer?: Phaser.Time.TimerEvent;

  constructor(config: RaceLevelConfig) {
    super("race");
    this.config = config;
  }

  preload() {
    this.makeTexture("car", "#3b82f6", 46, 70);
    this.makeTexture("rival", "#ef4444", 46, 70);
    this.makeTexture("coin", "#facc15", 30, 30, true);
  }

  private makeTexture(key: string, color: string, w: number, h: number, circle = false) {
    const g = this.add.graphics();
    g.fillStyle(Phaser.Display.Color.HexStringToColor(color).color, 1);
    if (circle) {
      g.fillCircle(w / 2, h / 2, w / 2);
    } else {
      g.fillRoundedRect(0, 0, w, h, 10);
    }
    g.generateTexture(key, w, h);
    g.destroy();
  }

  create() {
    const width = this.scale.width;
    const height = this.scale.height;

    // carriles de fondo
    this.add.rectangle(width / 2, height / 2, width, height, 0x334155);
    for (let i = 1; i < 3; i++) {
      this.add.rectangle((width / 3) * i, height / 2, 4, height, 0x64748b);
    }

    this.car = this.physics.add.sprite(width / 2, height - 90, "car");
    this.car.setCollideWorldBounds(true);
    this.car.setImmovable(true);

    this.items = this.physics.add.group();

    this.spawnTimer = this.time.addEvent({
      delay: this.config.obstacleEveryMs,
      loop: true,
      callback: () => this.spawnItem(),
    });

    this.physics.add.overlap(this.car, this.items, (_car, item) => {
      const sprite = item as Phaser.Physics.Arcade.Sprite;
      const isCoin = sprite.getData("type") === "coin";
      sprite.destroy();
      if (isCoin) {
        this.score += 1;
        this.game.events.emit("score", this.score);
      } else {
        this.game.events.emit("bump");
      }
    });
  }

  private spawnItem() {
    const width = this.scale.width;
    const lane = Phaser.Math.Between(0, 2);
    const x = width / 6 + lane * (width / 3);
    const isCoin = Math.random() < 0.55;
    const item = this.items.create(x, -40, isCoin ? "coin" : "rival") as Phaser.Physics.Arcade.Sprite;
    item.setData("type", isCoin ? "coin" : "obstacle");
    item.setVelocityY(this.config.scrollSpeed);
  }

  update() {
    const speed = 260;
    this.car.setVelocityX(this.moveDir * speed);

    this.items.children.forEach((child) => {
      const sprite = child as Phaser.Physics.Arcade.Sprite;
      if (sprite.y > this.scale.height + 60) sprite.destroy();
    });
  }

  setDirection(dir: -1 | 0 | 1) {
    this.moveDir = dir;
  }

  setLevelConfig(config: RaceLevelConfig) {
    this.config = config;
    this.spawnTimer?.remove();
    this.spawnTimer = this.time.addEvent({
      delay: config.obstacleEveryMs,
      loop: true,
      callback: () => this.spawnItem(),
    });
  }
}
