extends Node2D

var game_id := "escuadron-estelar"
var ship:=Vector2(480,430);var target:=Vector2(480,220);var objects:Array=[];var shots:Array=[]
var score:=0;var lives:=3;var level:=1;var distance:=0.0;var spawn_timer:=0.0;var boss_spawned:=false;var paused:=false;var over:=false;var won:=false

func _ready()->void:reset_game()
func reset_game()->void:score=0;lives=3;level=1;distance=0;boss_spawned=false;over=false;won=false;paused=false;objects.clear();shots.clear()
func fire()->void:
 if not over and not paused:shots.append({"p":ship,"target":target,"life":.42})
func _process(delta:float)->void:
 if paused or over:return
 distance+=delta*90;spawn_timer-=delta
 if spawn_timer<=0:
  var boss=distance>1100*level and not boss_spawned
  if boss:boss_spawned=true
  objects.append({"p":Vector2(randf_range(180,780),randf_range(105,290)),"z":.08,"hp":18 if boss else 1+level/2,"boss":boss,"vx":randf_range(-25,25)})
  spawn_timer=4.0 if boss else max(.28,.8-level*.12)
 for o in objects:
  o.z+=delta*(.12+level*.018);o.p.x+=o.vx*delta
  if o.z>=1.0:
   if screen_pos(o).distance_to(ship)<85:lose_life()
   o.hp=0
 for s in shots:s.life-=delta
 for s in shots:
  for o in objects:
   if o.hp>0 and screen_pos(o).distance_to(s.target)<45+o.z*35:o.hp-=1;s.life=0;score+=25
 objects=objects.filter(func(o):return o.hp>0)
 shots=shots.filter(func(s):return s.life>0)
 if boss_spawned and not objects.any(func(o):return o.boss):
  level+=1
  boss_spawned=false
  if level>3:over=true;won=true
 queue_redraw()
func screen_pos(o:Dictionary)->Vector2:
 return Vector2(480,270).lerp(o.p,clamp(o.z,0.0,1.0))
func lose_life()->void:lives-=1;if lives<=0:over=true
func _unhandled_input(event:InputEvent)->void:
 if event is InputEventKey and event.pressed:
  if event.keycode==KEY_LEFT:ship.x-=55
  elif event.keycode==KEY_RIGHT:ship.x+=55
  elif event.keycode==KEY_UP:ship.y-=35
  elif event.keycode==KEY_DOWN:ship.y+=35
  elif event.keycode==KEY_SPACE:fire()
  ship.x=clamp(ship.x,100.0,860.0);ship.y=clamp(ship.y,330.0,470.0)
 if event is InputEventScreenDrag:
  var scale=Vector2(960.0/get_viewport_rect().size.x,540.0/get_viewport_rect().size.y);target=event.position*scale;ship.x=clamp(target.x,100.0,860.0)
 if event is InputEventScreenTouch and event.pressed:
  var p=event.position*Vector2(960.0/get_viewport_rect().size.x,540.0/get_viewport_rect().size.y)
  if p.y<55:
   if p.x<145:reset_game()
   elif p.x<290:paused=not paused
  elif over:reset_game()
  else:target=p;ship.x=clamp(p.x,100.0,860.0);fire()
func _draw()->void:
 draw_rect(Rect2(0,0,960,540),Color("030717"));draw_rect(Rect2(0,0,960,55),Color("172350"))
 for i in 40:
  var x=fposmod(i*137.0-distance*2,960.0);var y=70+fposmod(i*83.0+distance,390.0);draw_circle(Vector2(x,y),1.5,Color("b7d8ff"))
 for ring in range(6):
  var t=float(ring+1)/6.0;draw_arc(Vector2(480,270),70+t*400,PI+.25,TAU-.25,28,Color(0.1,0.3,0.6,.35),2)
 label("ESCUADRÓN ESTELAR",Vector2(350,36),25);label("NUEVO",Vector2(22,35),17);label("PAUSA",Vector2(160,35),17);label("Puntos %d  Escudos %d  Sector %d"%[score,lives,level],Vector2(655,35),16)
 for o in objects:
  var p=screen_pos(o);var size=12+o.z*45;draw_circle(p,size,Color("dd4560") if not o.boss else Color("9a54db"));draw_arc(p,size+5,0,TAU,20,Color.WHITE,2)
 for s in shots:draw_line(ship,s.target,Color("61efff"),4);draw_circle(s.target,13,Color("eaffff"),false,3)
 draw_colored_polygon(PackedVector2Array([ship+Vector2(0,-28),ship+Vector2(-28,22),ship,ship+Vector2(28,22)]),Color("4db7e8"));draw_circle(target,18,Color("69eaff"),false,2)
 if paused:overlay("PAUSA")
 if over:overlay("SECTORES LIBERADOS" if won else "MISIÓN FALLIDA · TOCA")
func label(v:String,p:Vector2,s:int,c:Color=Color.WHITE)->void:draw_string(ThemeDB.fallback_font,p,v,HORIZONTAL_ALIGNMENT_LEFT,-1,s,c)
func overlay(v:String)->void:draw_rect(Rect2(175,220,610,90),Color(0.03,0.04,0.1,.95));label(v,Vector2(280,275),25,Color("65e8ff"))
