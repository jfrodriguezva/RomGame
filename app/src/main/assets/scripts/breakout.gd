extends Node2D
var paddle:=Vector2(480,490);var ball:=Vector2(480,440);var velocity:=Vector2(220,-250);var bricks:Array[Rect2]=[];var lives:=3;var score:=0;var level:=1;var paused:=false;var launched:=false;var over:=false
func _ready()->void:reset_game()
func reset_game()->void:lives=3;score=0;level=1;over=false;paused=false;build_level();reset_ball()
func build_level()->void:
 bricks.clear();var rows=4+level
 for y in rows:
  for x in 12:
   if not(level>=2 and (x+y)%7==0):bricks.append(Rect2(64+x*70,75+y*27,64,21))
func reset_ball()->void:ball=Vector2(paddle.x,445);velocity=Vector2(210+level*18,-245-level*16);launched=false;queue_redraw()
func _process(delta:float)->void:
 if paused or over:return
 if not launched:ball.x=paddle.x;queue_redraw();return
 var previous=ball;ball+=velocity*delta
 if ball.x<18 or ball.x>942:velocity.x=-velocity.x;ball.x=clamp(ball.x,18.0,942.0)
 if ball.y<62:velocity.y=abs(velocity.y)
 if ball.y>540:
  lives-=1
  if lives<=0:
   over=true
  else:
   reset_ball()
  queue_redraw();return
 var paddle_rect=Rect2(paddle.x-70,485,140,18)
 if velocity.y>0 and paddle_rect.has_point(ball):velocity.y=-abs(velocity.y);velocity.x+=(ball.x-paddle.x)*4.0;ball.y=480
 for i in range(bricks.size()-1,-1,-1):
  if bricks[i].grow(8).has_point(ball):
   var hit=bricks[i];bricks.remove_at(i);score+=10*level
   if previous.y<hit.position.y or previous.y>hit.end.y:
    velocity.y=-velocity.y
   else:
    velocity.x=-velocity.x
   break
 if bricks.is_empty():
  level+=1
  if level>3:
   over=true
  else:
   build_level();reset_ball()
 queue_redraw()
func _unhandled_input(event:InputEvent)->void:
 if event is InputEventMouseMotion or event is InputEventScreenDrag:
  var x=event.position.x*960.0/get_viewport_rect().size.x;paddle.x=clamp(x,80.0,880.0);queue_redraw()
 if event is InputEventScreenTouch and event.pressed:
  var p=event.position*Vector2(960.0/get_viewport_rect().size.x,540.0/get_viewport_rect().size.y)
  if p.y<55:
   if p.x<150:reset_game()
   elif p.x<300:paused=not paused
  elif over:reset_game()
  else:paddle.x=clamp(p.x,80.0,880.0);launched=true
func _draw()->void:
 draw_rect(Rect2(0,0,960,540),Color("081020"));draw_rect(Rect2(0,0,960,55),Color("55346e"));label("ROMPE LADRILLOS",Vector2(350,37),27);label("NUEVO",Vector2(28,36),18);label("PAUSA",Vector2(170,36),18);label("Puntos %d  Vidas %d  Nivel %d"%[score,lives,level],Vector2(680,36),17)
 var palette=[Color("ef5350"),Color("ff9f43"),Color("feca57"),Color("48dbb4"),Color("54a0ff"),Color("a66efa")]
 for b in bricks:draw_rect(b,palette[int((b.position.y-75)/27)%palette.size()]);draw_rect(b,Color(1,1,1,.22),false,2)
 draw_rect(Rect2(paddle.x-70,485,140,18),Color("e8edf5"));draw_circle(ball,9,Color("f7d154"))
 if not launched and not over:label("TOCA PARA LANZAR · ARRASTRA LA PALA",Vector2(275,455),19,Color("e0c23c"))
 if paused:overlay("PAUSA")
 if over:overlay("VICTORIA" if level>3 else "FIN · TOCA PARA REINICIAR")
func label(v:String,p:Vector2,s:int,c:Color=Color.WHITE)->void:draw_string(ThemeDB.fallback_font,p,v,HORIZONTAL_ALIGNMENT_LEFT,-1,s,c)
func overlay(v:String)->void:draw_rect(Rect2(190,220,580,90),Color(0.03,0.06,0.12,.94));label(v,Vector2(300,275),25,Color("e0c23c"))
