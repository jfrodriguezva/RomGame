extends Node2D
const COLS:=30;const ROWS:=17;const CELL:=26;const ORIGIN:=Vector2(90,65)
var safe:Dictionary={};var trail:Array[Vector2i]=[];var player:=Vector2i(0,0);var direction:=Vector2i.RIGHT;var enemy:=Vector2(15,8);var enemy_velocity:=Vector2(3.2,2.6);var step_time:=0.0;var captured:=0.0;var lives:=3;var level:=1;var paused:=false;var over:=false;var touch_start:=Vector2.ZERO
func _ready()->void:reset()
func reset()->void:
 safe.clear();trail.clear();player=Vector2i(0,0);direction=Vector2i.RIGHT;enemy=Vector2(15,8);lives=3;level=1;paused=false;over=false
 for x in COLS:safe[Vector2i(x,0)]=true;safe[Vector2i(x,ROWS-1)]=true
 for y in ROWS:safe[Vector2i(0,y)]=true;safe[Vector2i(COLS-1,y)]=true
 update_percent();queue_redraw()
func turn(next:Vector2i)->void:
 if next!=Vector2i.ZERO:direction=next
func _process(delta:float)->void:
 if paused or over:return
 enemy+=enemy_velocity*delta
 if enemy.x<1 or enemy.x>COLS-2:enemy_velocity.x=-enemy_velocity.x;enemy.x=clamp(enemy.x,1.0,COLS-2.0)
 if enemy.y<1 or enemy.y>ROWS-2:enemy_velocity.y=-enemy_velocity.y;enemy.y=clamp(enemy.y,1.0,ROWS-2.0)
 for p in trail:
  if Vector2(p).distance_to(enemy)<.7:lose_life();return
 step_time+=delta
 if step_time>.075:
  step_time=0;advance()
 queue_redraw()
func advance()->void:
 var next=player+direction
 if next.x<0 or next.x>=COLS or next.y<0 or next.y>=ROWS:return
 if next in trail:lose_life();return
 var was_drawing=not trail.is_empty();player=next
 if safe.has(player):
  if was_drawing:capture_area()
 else:trail.append(player)
func lose_life()->void:
 lives-=1;trail.clear();player=nearest_safe(player)
 if lives<=0:over=true
func nearest_safe(from:Vector2i)->Vector2i:
 var best:=Vector2i.ZERO;var distance:=99999
 for p in safe:
  var d=abs(p.x-from.x)+abs(p.y-from.y)
  if d<distance:distance=d;best=p
 return best
func capture_area()->void:
 for p in trail:safe[p]=true
 trail.clear()
 var reachable:Dictionary={};var queue:Array[Vector2i]=[Vector2i(roundi(enemy.x),roundi(enemy.y))]
 while not queue.is_empty():
  var p=queue.pop_front()
  if reachable.has(p) or safe.has(p) or p.x<0 or p.x>=COLS or p.y<0 or p.y>=ROWS:continue
  reachable[p]=true
  queue.append_array([p+Vector2i.LEFT,p+Vector2i.RIGHT,p+Vector2i.UP,p+Vector2i.DOWN])
 for y in ROWS:
  for x in COLS:
   var p=Vector2i(x,y)
   if not safe.has(p) and not reachable.has(p):safe[p]=true
 update_percent()
 if captured>=75.0:
  level+=1
  enemy_velocity*=1.18
  if level>3:
   over=true
func update_percent()->void:captured=100.0*float(safe.size())/float(COLS*ROWS)
func _unhandled_input(event:InputEvent)->void:
 if event is InputEventKey and event.pressed:
  if event.keycode==KEY_LEFT:turn(Vector2i.LEFT)
  elif event.keycode==KEY_RIGHT:turn(Vector2i.RIGHT)
  elif event.keycode==KEY_UP:turn(Vector2i.UP)
  elif event.keycode==KEY_DOWN:turn(Vector2i.DOWN)
 if event is InputEventScreenTouch:
  var p=event.position*Vector2(960.0/get_viewport_rect().size.x,540.0/get_viewport_rect().size.y)
  if event.pressed:
   touch_start=p
   if p.y<55:
    if p.x<150:reset()
    elif p.x<300:paused=not paused
  else:
   var d=p-touch_start
   if d.length()>30:turn(Vector2i(sign(d.x),0) if abs(d.x)>abs(d.y) else Vector2i(0,sign(d.y)))
   elif over:reset()
func _draw()->void:
 draw_rect(Rect2(0,0,960,540),Color("07111f"));draw_rect(Rect2(0,0,960,55),Color("54365f"));label("MOSAICO SORPRESA",Vector2(335,37),26);label("NUEVO",Vector2(25,36),18);label("PAUSA",Vector2(165,36),18);label("Territorio %.0f%%  Vidas %d  Nivel %d"%[captured,lives,level],Vector2(660,36),17)
 for y in ROWS:
  for x in COLS:
   var p=Vector2i(x,y);var color=Color("203553") if safe.has(p) else Color("111b31")
   draw_rect(Rect2(ORIGIN+Vector2(p)*CELL,Vector2(CELL-1,CELL-1)),color)
 for p in trail:draw_rect(Rect2(ORIGIN+Vector2(p)*CELL+Vector2(5,5),Vector2(CELL-10,CELL-10)),Color("efc84a"))
 draw_circle(ORIGIN+Vector2(player)*CELL+Vector2(CELL/2,CELL/2),9,Color("61e5ff"));draw_circle(ORIGIN+enemy*CELL+Vector2(CELL/2,CELL/2),11,Color("f05b68"))
 if paused:overlay("PAUSA")
 if over:overlay("GALERÍA COMPLETA" if level>3 else "FIN · TOCA PARA REINICIAR")
func label(v:String,p:Vector2,s:int,c:Color=Color.WHITE)->void:draw_string(ThemeDB.fallback_font,p,v,HORIZONTAL_ALIGNMENT_LEFT,-1,s,c)
func overlay(v:String)->void:draw_rect(Rect2(175,220,610,90),Color(0.03,0.06,0.12,.94));label(v,Vector2(275,275),25,Color("e0c23c"))
