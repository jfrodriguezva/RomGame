extends Node2D

const GAMES := {
    "tetris": preload("res://scripts/blocks.gd"),
    "snake": preload("res://scripts/snake.gd"),
    "arkanoid": preload("res://scripts/breakout.gd"),
    "mosaico": preload("res://scripts/territory.gd"),
    "comepuntos": preload("res://scripts/maze_chase.gd"),
    "nieve": preload("res://scripts/snow_rescue.gd")
}

func _ready() -> void:
    var game_id := "tetris"
    for argument in OS.get_cmdline_user_args():
        if argument.begins_with("--game-id="):
            game_id = argument.trim_prefix("--game-id=")
    print("ARCADE_READY:", game_id)
    var script: Script = GAMES.get(game_id, GAMES["tetris"])
    var game := Node2D.new()
    game.set_script(script)
    add_child(game)
