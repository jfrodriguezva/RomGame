extends Node2D
const COLS:=24;const ROWS:=14;const CELL:=30;const ORIGIN:=Vector2(120,70)
var body:Array[Vector2i]=[];var direction:=Vector2i.RIGHT;var queued:=Vector2i.RIGHT;var food:=Vector2i.ZERO;var timer:=0.0;var score:=0;var level:=1;var paused:=false;var over:=false;var touch_start:=Vector2.ZERO
func _ready()->void:reset()
func reset()->void:body=[Vector2i(7,7),Vector2i(6,7),Vector2i(5,7)];direction=Vector2i.RIGHT;queued=direction;score=0;level=1;paused=false;over=false;place_food();queue_redraw()
func place_food()->void:
 var free:Array[Vector2i]=[]
 for y in ROWS:
  for x in COLS:
   if Vector2i(x,y) not in body:free.append(Vector2i(x,y))
 food=free.pick_random()
func turn(next:Vector2i)->void:
 if next+direction!=Vector2i.ZERO:queued=next
func _process(delta:float)->void:
 if paused or over:return
 timer+=delta
 if timer<max(.07,.22-(level-1)*.015):return
 timer=0;direction=queued;var head=body[0]+direction
 if head.x<0 or head.x>=COLS or head.y<0 or head.y>=ROWS or head in body:over=true;queue_redraw();return
 body.push_front(head)
 if head==food:score+=10*level;level=1+score/100;place_food()
 else:body.pop_back()
 queue_redraw()
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
   if d.length()>35:turn(Vector2i(sign(d.x),0) if abs(d.x)>abs(d.y) else Vector2i(0,sign(d.y)))
   elif over:reset()
func _draw()->void:
 draw_rect(Rect2(0,0,960,540),Color("071523"));draw_rect(Rect2(0,0,960,55),Color("244b58"));label("LA VÍBORA",Vector2(400,37),28);label("NUEVO",Vector2(28,36),18);label("PAUSA",Vector2(170,36),18);label("Puntos %d   Nivel %d"%[score,level],Vector2(715,36),18)
 draw_rect(Rect2(ORIGIN-Vector2(4,4),Vector2(COLS*CELL+8,ROWS*CELL+8)),Color("31505c"))
 for y in ROWS:
  for x in COLS:draw_rect(Rect2(ORIGIN+Vector2(x,y)*CELL,Vector2(CELL-1,CELL-1)),Color("0d2430"))
 draw_circle(ORIGIN+Vector2(food)*CELL+Vector2(.5,.5)*CELL,11,Color("ef5350"))
 for i in body.size():draw_rect(Rect2(ORIGIN+Vector2(body[i])*CELL+Vector2(3,3),Vector2(CELL-6,CELL-6)),Color("8bd450") if i else Color("d5f06b"),true)
 if paused:overlay("PAUSA")
 if over:overlay("CHOQUE · TOCA PARA REINICIAR")
func label(v:String,p:Vector2,s:int,c:Color=Color.WHITE)->void:draw_string(ThemeDB.fallback_font,p,v,HORIZONTAL_ALIGNMENT_LEFT,-1,s,c)
func overlay(v:String)->void:draw_rect(Rect2(190,220,580,90),Color(0.03,0.06,0.12,.94));label(v,Vector2(270,275),25,Color("e0c23c"))
