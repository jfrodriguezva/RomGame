import Phaser from "phaser";

export interface RaceLevelConfig {
  scrollSpeed: number;
  obstacleEveryMs: number;
}

const RIVAL_COLORS = ["#ef4444", "#f97316", "#a855f7", "#ec4899"];

export default class RaceScene extends Phaser.Scene {
  private car!: Phaser.Physics.Arcade.Sprite;
  private items!: Phaser.Physics.Arcade.Group;
  private laneLines: Phaser.GameObjects.TileSprite[] = [];
  private moveDir = 0;
  private config: RaceLevelConfig;
  private score = 0;
  private spawnTimer?: Phaser.Time.TimerEvent;

  constructor(config: RaceLevelConfig) {
    super("race");
    this.config = config;
  }

  preload() {
    this.makeCarTexture("car", "#3b82f6");
    RIVAL_COLORS.forEach((color, i) => this.makeCarTexture(`rival-${i}`, color));
    this.makeCoinTexture("coin");
    this.makeDashTexture("dash");
  }

  /** Un coche legible (carrocería, parabrisas, luces, llantas), no un rectángulo plano. */
  private makeCarTexture(key: string, bodyColor: string, w = 46, h = 70) {
    const g = this.add.graphics();
    const body = Phaser.Display.Color.HexStringToColor(bodyColor).color;

    g.fillStyle(0x0f172a, 1);
    g.fillRoundedRect(-3, h * 0.16, 9, h * 0.24, 3);
    g.fillRoundedRect(w - 6, h * 0.16, 9, h * 0.24, 3);
    g.fillRoundedRect(-3, h * 0.6, 9, h * 0.24, 3);
    g.fillRoundedRect(w - 6, h * 0.6, 9, h * 0.24, 3);

    g.fillStyle(body, 1);
    g.fillRoundedRect(0, 4, w, h - 8, 14);

    g.fillStyle(0xdbeafe, 0.95);
    g.fillRoundedRect(w * 0.16, h * 0.12, w * 0.68, h * 0.24, 6);
    g.fillRoundedRect(w * 0.16, h * 0.62, w * 0.68, h * 0.16, 6);

    g.fillStyle(0xfde68a, 1);
    g.fillCircle(w * 0.2, h * 0.08, 3.5);
    g.fillCircle(w * 0.8, h * 0.08, 3.5);

    g.generateTexture(key, w, h);
    g.destroy();
  }

  private makeCoinTexture(key: string, size = 30) {
    const g = this.add.graphics();
    g.fillStyle(Phaser.Display.Color.HexStringToColor("#facc15").color, 1);
    g.fillCircle(size / 2, size / 2, size / 2);
    g.lineStyle(2, 0xb45309, 1);
    g.strokeCircle(size / 2, size / 2, size / 2 - 2);
    g.generateTexture(key, size, size);
    g.destroy();
  }

  /** Línea punteada que se desplaza en `update()` para que el camino se sienta en movimiento. */
  private makeDashTexture(key: string) {
    const g = this.add.graphics();
    g.fillStyle(0xcbd5e1, 1);
    g.fillRect(0, 0, 6, 26);
    g.generateTexture(key, 6, 46);
    g.destroy();
  }

  create() {
    const width = this.scale.width;
    const height = this.scale.height;

    this.add.rectangle(width / 2, height / 2, width, height, 0x334155);
    this.laneLines = [1, 2].map((i) =>
      this.add.tileSprite((width / 3) * i, height / 2, 6, height, "dash")
    );

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
    const texture = isCoin ? "coin" : `rival-${Phaser.Math.Between(0, RIVAL_COLORS.length - 1)}`;
    const item = this.items.create(x, -40, texture) as Phaser.Physics.Arcade.Sprite;
    item.setData("type", isCoin ? "coin" : "obstacle");
    item.setVelocityY(this.config.scrollSpeed);
  }

  update(_time: number, delta: number) {
    const speed = 260;
    this.car.setVelocityX(this.moveDir * speed);

    const dy = (this.config.scrollSpeed * delta) / 1000;
    this.laneLines.forEach((line) => {
      line.tilePositionY -= dy;
    });

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
