extends Node2D

var game_id := "vaqueros"
var hero := Vector2(180, 410)
var velocity := Vector2.ZERO
var world_x := 0.0
var enemies: Array = []
var bullets: Array = []
var enemy_bullets: Array = []
var boss := {}
var score := 0
var lives := 3
var level := 1
var left_pressed := false
var right_pressed := false
var paused := false
var over := false
var won := false

func _ready() -> void:
 reset_game()

func reset_game() -> void:
 score=0;lives=3;level=1;over=false;won=false;paused=false
 start_level()

func start_level() -> void:
 hero=Vector2(180,410);velocity=Vector2.ZERO;world_x=0;bullets.clear();enemy_bullets.clear();enemies.clear();boss={}
 for i in range(10+level*2):
  enemies.append({"x":700.0+i*320.0,"y":410.0 if i%3 else 300.0,"hp":1+level/2,"cool":1.0+(i%4)*.25})

func on_floor() -> bool:
 return hero.y>=410 or (hero.y>=300 and hero.y<=307 and world_x>420 and world_x<900)

func shoot() -> void:
 if not over and not paused:
  bullets.append({"p":hero+Vector2(28,-10),"v":Vector2(620,0)})

func jump() -> void:
 if on_floor():velocity.y=-430

func _process(delta:float) -> void:
 if paused or over:return
 var motion=(-1 if left_pressed else 1 if right_pressed else 0)
 velocity.x=move_toward(velocity.x,motion*230.0,900*delta);velocity.y+=920*delta
 hero+=velocity*delta
 if hero.y>=410:hero.y=410;velocity.y=0
 hero.x=clamp(hero.x,80.0,520.0)
 if hero.x>420 and velocity.x>0:
  world_x+=velocity.x*delta;hero.x=420
 for b in bullets:b.p+=b.v*delta
 bullets=bullets.filter(func(b):return b.p.x<1000)
 for b in enemy_bullets:b.p+=b.v*delta
 enemy_bullets=enemy_bullets.filter(func(b):return b.p.x>-30 and b.p.x<990)
 for e in enemies:
  var screen_x=e.x-world_x
  if screen_x>30 and screen_x<930:
   e.cool-=delta
   if e.cool<=0:
    enemy_bullets.append({"p":Vector2(screen_x,e.y-10),"v":Vector2(-260-level*25,0)})
    e.cool=1.4+randf()
  for b in bullets:
   if Vector2(screen_x,e.y).distance_to(b.p)<30:
    e.hp-=1;b.p.x=1200
    if e.hp<=0:score+=100
 enemies=enemies.filter(func(e):return e.hp>0)
 for b in enemy_bullets:
  if b.p.distance_to(hero)<25:b.p.x=-100;lose_life()
 if world_x>3900 and boss.is_empty():boss={"p":Vector2(790,355),"hp":12+level*4,"cool":.8}
 update_boss(delta)
 if enemies.is_empty() and not boss.is_empty() and boss.hp<=0:
  level+=1
  if level>3:over=true;won=true
  else:start_level()
 queue_redraw()

func update_boss(delta:float) -> void:
 if boss.is_empty() or boss.hp<=0:return
 boss.p.y=350+sin(Time.get_ticks_msec()/350.0)*70;boss.cool-=delta
 if boss.cool<=0:
  enemy_bullets.append({"p":boss.p,"v":(hero-boss.p).normalized()*310})
  boss.cool=.55
 for b in bullets:
  if b.p.distance_to(boss.p)<45:boss.hp-=1;b.p.x=1200;score+=25

func lose_life() -> void:
 lives-=1;hero=Vector2(150,410);velocity=Vector2.ZERO
 if lives<=0:over=true

func _unhandled_input(event:InputEvent) -> void:
 if event is InputEventKey:
  if event.keycode==KEY_LEFT:left_pressed=event.pressed
  elif event.keycode==KEY_RIGHT:right_pressed=event.pressed
  elif event.pressed and event.keycode==KEY_UP:jump()
  elif event.pressed and event.keycode==KEY_SPACE:shoot()
 if event is InputEventScreenTouch:
  var p=event.position*Vector2(960.0/get_viewport_rect().size.x,540.0/get_viewport_rect().size.y)
  if p.y<55 and event.pressed:
   if p.x<145:reset_game()
   elif p.x<290:paused=not paused
  elif p.y>455:
   if p.x<220:left_pressed=event.pressed
   elif p.x<440:right_pressed=event.pressed
   elif event.pressed and p.x<680:jump()
   elif event.pressed:shoot()
  elif event.pressed and over:reset_game()

func _draw() -> void:
 draw_rect(Rect2(0,0,960,540),Color("6bb5d4"));draw_rect(Rect2(0,350,960,190),Color("d4a45b"));draw_rect(Rect2(0,0,960,55),Color("653723"))
 for x in range(-int(world_x)%180,1000,180):draw_rect(Rect2(x,320,80,90),Color("a75935"));draw_rect(Rect2(x+18,342,18,24),Color("281b18"))
 label("VAQUEROS DEL OCASO",Vector2(335,36),25);label("NUEVO",Vector2(22,35),17);label("PAUSA",Vector2(160,35),17);label("Puntos %d  Vidas %d  Nivel %d"%[score,lives,level],Vector2(680,35),16)
 draw_rect(Rect2(hero-Vector2(14,32),Vector2(28,45)),Color("2767a8"));draw_circle(hero-Vector2(0,40),13,Color("d69b70"));draw_rect(Rect2(hero+Vector2(-22,-55),Vector2(44,8)),Color("8b4b25"))
 for e in enemies:
  var sx=e.x-world_x
  if sx>-40 and sx<1000:draw_rect(Rect2(sx-14,e.y-30,28,42),Color("8e3042"));draw_circle(Vector2(sx,e.y-38),12,Color("d49a70"))
 for b in bullets:draw_circle(b.p,5,Color("ffe157"))
 for b in enemy_bullets:draw_circle(b.p,6,Color("ef493f"))
 if not boss.is_empty() and boss.hp>0:draw_rect(Rect2(boss.p-Vector2(35,45),Vector2(70,90)),Color("552446"));label("JEFE %d"%boss.hp,boss.p+Vector2(-35,-55),15)
 for i in 4:draw_rect(Rect2(i*240,465,238,70),Color("593622"));label(["◀","▶","SALTAR","DISPARAR"][i],Vector2(i*240+62,507),18)
 if paused:overlay("PAUSA")
 if over:overlay("CAMPAÑA COMPLETA" if won else "FIN · TOCA PARA REINICIAR")

func label(v:String,p:Vector2,s:int,c:Color=Color.WHITE)->void:draw_string(ThemeDB.fallback_font,p,v,HORIZONTAL_ALIGNMENT_LEFT,-1,s,c)
func overlay(v:String)->void:draw_rect(Rect2(175,220,610,90),Color(0.03,0.03,0.05,.94));label(v,Vector2(280,275),25,Color("f0c84a"))
