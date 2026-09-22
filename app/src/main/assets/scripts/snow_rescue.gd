extends Node2D
var hero:=Vector2(100,450);var velocity:=Vector2.ZERO;var facing:=1;var enemies:Array=[];var shots:Array=[];var balls:Array=[];var platforms=[Rect2(0,500,960,40),Rect2(80,390,300,18),Rect2(520,390,350,18),Rect2(260,280,430,18),Rect2(60,170,270,18),Rect2(630,170,270,18)];var lives:=3;var score:=0;var level:=1;var paused:=false;var over:=false;var left_pressed:=false;var right_pressed:=false
func _ready()->void:reset_game()
func reset_game()->void:lives=3;score=0;level=1;over=false;paused=false;spawn_level()
func spawn_level()->void:
 hero=Vector2(100,450);velocity=Vector2.ZERO;shots.clear();balls.clear();enemies.clear()
 for i in 3+level:enemies.append({"p":Vector2(220+i*130,130+(i%3)*110),"v":40.0 if i%2==0 else -40.0,"snow":0})
 queue_redraw()
func on_floor()->bool:
 for platform in platforms:
  if abs((hero.y+22)-platform.position.y)<7 and hero.x>=platform.position.x and hero.x<=platform.end.x:return true
 return false
func shoot()->void:shots.append({"p":hero+Vector2(facing*24,-5),"v":Vector2(facing*360,0)})
func _process(delta:float)->void:
 if paused or over:return
 velocity.x=(-180 if left_pressed else 180 if right_pressed else move_toward(velocity.x,0,700*delta));velocity.y+=700*delta
 var old=hero;hero+=velocity*delta;hero.x=clamp(hero.x,18.0,942.0)
 for platform in platforms:
  if velocity.y>=0 and old.y+22<=platform.position.y and hero.y+22>=platform.position.y and hero.x>=platform.position.x and hero.x<=platform.end.x:hero.y=platform.position.y-22;velocity.y=0
 if hero.y>560:lose_life()
 for shot in shots:shot.p+=shot.v*delta
 shots=shots.filter(func(s):return s.p.x>-20 and s.p.x<980)
 for e in enemies:
  e.p.x+=e.v*delta
  if e.p.x<30 or e.p.x>930:e.v=-e.v
  for shot in shots:
   if e.p.distance_to(shot.p)<24:
    e.snow+=1
    shots.erase(shot)
    if e.snow>=3:
     balls.append({"p":e.p,"v":Vector2(sign(e.v)*280,0)})
     e.p=Vector2(-999,-999)
     score+=50
    break
 enemies=enemies.filter(func(e):return e.p.x>-100)
 for ball in balls:
  ball.p+=ball.v*delta
  for e in enemies:
   if ball.p.distance_to(e.p)<30:e.p=Vector2(-999,-999);score+=100
 balls=balls.filter(func(b):return b.p.x>-40 and b.p.x<1000);enemies=enemies.filter(func(e):return e.p.x>-100)
 for e in enemies:
  if e.p.distance_to(hero)<28:lose_life();break
 if enemies.is_empty():
  level+=1
  if level>3:
   over=true
  else:
   spawn_level()
 queue_redraw()
func lose_life()->void:
 lives-=1
 hero=Vector2(100,450)
 velocity=Vector2.ZERO
 if lives<=0:
  over=true
func _unhandled_input(event:InputEvent)->void:
 if event is InputEventKey:
  if event.keycode==KEY_LEFT:left_pressed=event.pressed;facing=-1
  elif event.keycode==KEY_RIGHT:right_pressed=event.pressed;facing=1
  elif event.pressed and event.keycode==KEY_UP and on_floor():velocity.y=-390
  elif event.pressed and event.keycode==KEY_SPACE:shoot()
 if event is InputEventScreenTouch:
  var p=event.position*Vector2(960.0/get_viewport_rect().size.x,540.0/get_viewport_rect().size.y)
  if p.y<55 and event.pressed:
   if p.x<150:reset_game()
   elif p.x<300:paused=not paused
  elif p.y>455:
   if p.x<220:left_pressed=event.pressed;facing=-1
   elif p.x<440:right_pressed=event.pressed;facing=1
   elif event.pressed and p.x<680 and on_floor():velocity.y=-390
   elif event.pressed:shoot()
  elif event.pressed and over:reset_game()
func _draw()->void:
 draw_rect(Rect2(0,0,960,540),Color("102039"));draw_rect(Rect2(0,0,960,55),Color("285a7a"));label("RESCATE DE NIEVE",Vector2(350,37),27);label("NUEVO",Vector2(25,36),18);label("PAUSA",Vector2(165,36),18);label("Puntos %d  Vidas %d  Nivel %d"%[score,lives,level],Vector2(675,36),17)
 for p in platforms:draw_rect(p,Color("c8edff"));draw_rect(Rect2(p.position,p.size*Vector2(1,.3)),Color.WHITE)
 draw_circle(hero,18,Color.WHITE);draw_circle(hero+Vector2(0,-20),13,Color("e8fbff"));draw_circle(hero+Vector2(facing*6,-23),2,Color("162030"))
 for e in enemies:
  draw_circle(e.p,17,Color("b5485b") if e.snow==0 else Color("d8f4ff"));label(str(e.snow),e.p+Vector2(-5,6),15,Color.WHITE)
 for s in shots:draw_circle(s.p,6,Color("dff8ff"))
 for b in balls:draw_circle(b.p,20,Color("ecfbff"));draw_arc(b.p,20,0,TAU,18,Color("8ccbe8"),2)
 var controls=["◀","▶","SALTAR","NIEVE"]
 for i in controls.size():draw_rect(Rect2(i*240,465,238,70),Color("2b5271"));label(controls[i],Vector2(i*240+65,508),19)
 if paused:overlay("PAUSA")
 if over:overlay("RESCATE COMPLETO" if level>3 else "FIN · TOCA PARA REINICIAR")
func label(v:String,p:Vector2,s:int,c:Color=Color.WHITE)->void:draw_string(ThemeDB.fallback_font,p,v,HORIZONTAL_ALIGNMENT_LEFT,-1,s,c)
func overlay(v:String)->void:draw_rect(Rect2(180,220,600,90),Color(0.03,0.06,0.12,.94));label(v,Vector2(285,275),25,Color("e0c23c"))
