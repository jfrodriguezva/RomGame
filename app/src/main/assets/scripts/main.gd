extends Node2D

var game_id := "tetris"
var title := "ROMINA ARCADE"

func _ready() -> void:
    for argument in OS.get_cmdline_user_args():
        if argument.begins_with("--game-id="):
            game_id = argument.trim_prefix("--game-id=")
    title = game_id.replace("-", " ").to_upper()
    print("ARCADE_READY:", game_id)
    var background := ColorRect.new()
    background.position = Vector2.ZERO
    background.size = Vector2(960, 540)
    background.color = Color("091327")
    add_child(background)
    var header := ColorRect.new()
    header.position = Vector2(30, 30)
    header.size = Vector2(900, 70)
    header.color = Color("253f68")
    add_child(header)
    add_label(title, Vector2(60, 46), 30, Color.WHITE)
    add_label("Motor Godot 4.7.2 integrado", Vector2(60, 145), 24, Color("e0c23c"))
    add_label("Vertical slice de integración", Vector2(60, 190), 22, Color.WHITE)

func add_label(text_value: String, at: Vector2, font_size: int, color: Color) -> void:
    var label := Label.new()
    label.text = text_value
    label.position = at
    label.size = Vector2(820, 60)
    label.add_theme_font_size_override("font_size", font_size)
    label.add_theme_color_override("font_color", color)
    add_child(label)
