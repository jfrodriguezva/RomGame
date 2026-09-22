extends Node2D

const GAMES := {
    "tetris": preload("res://scripts/blocks.gd"),
    "snake": preload("res://scripts/snake.gd"),
    "arkanoid": preload("res://scripts/breakout.gd"),
    "mosaico": preload("res://scripts/territory.gd"),
    "comepuntos": preload("res://scripts/maze_chase.gd"),
    "nieve": preload("res://scripts/snow_rescue.gd"),
    "vaqueros": preload("res://scripts/cowboy_run.gd"),
    "escuadron-estelar": preload("res://scripts/star_squadron.gd"),
    "gran-premio": preload("res://scripts/racing.gd"),
    "carreras": preload("res://scripts/racing.gd")
}

var active_game: Node2D
var active_game_id := "tetris"
var last_level := 1

func _ready() -> void:
    var game_id := "tetris"
    for argument in OS.get_cmdline_user_args():
        if argument.begins_with("--game-id="):
            game_id = argument.trim_prefix("--game-id=")
    print("ARCADE_READY:", game_id)
    active_game_id = game_id
    var script: Script = GAMES.get(game_id, GAMES["tetris"])
    var game := Node2D.new()
    game.set_script(script)
    game.set("game_id", game_id)
    add_child(game)
    active_game = game

func _process(_delta: float) -> void:
    if not active_game:
        return
    var current_level := int(active_game.get("level"))
    if current_level > last_level:
        var score := int(active_game.get("score"))
        report_completion(last_level, score)
        last_level = current_level

func report_completion(level: int, score: int) -> void:
    if Engine.has_singleton("RomGameProgress"):
        Engine.get_singleton("RomGameProgress").completeLevel(active_game_id, level, score)
