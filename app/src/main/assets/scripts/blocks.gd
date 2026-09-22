extends Node2D
const COLS:=10;const ROWS:=20;const CELL:=22;const ORIGIN:=Vector2(350,55)
const SHAPES := [[Vector2i(-1,0),Vector2i(0,0),Vector2i(1,0),Vector2i(2,0)],[Vector2i(0,0),Vector2i(1,0),Vector2i(0,1),Vector2i(1,1)],[Vector2i(-1,0),Vector2i(0,0),Vector2i(1,0),Vector2i(0,1)],[Vector2i(-1,0),Vector2i(0,0),Vector2i(1,0),Vector2i(1,1)],[Vector2i(-1,1),Vector2i(-1,0),Vector2i(0,0),Vector2i(1,0)],[Vector2i(-1,0),Vector2i(0,0),Vector2i(0,1),Vector2i(1,1)],[Vector2i(-1,1),Vector2i(0,1),Vector2i(0,0),Vector2i(1,0)]]
const COLORS := [Color("31c7ef"),Color("f7d308"),Color("ad4d9c"),Color("f28e2b"),Color("3d67d9"),Color("47b95c"),Color("e84b3c")]
var board:Array=[];var shape:Array=[];var piece_type:=0;var next_type:=0;var piece_pos:=Vector2i(4,0);var score:=0;var lines:=0;var level:=1;var fall_time:=0.0;var paused:=false;var over:=false
func _ready()->void:reset()
func reset()->void:
 board.clear()
 for y in ROWS:
  var row=[];row.resize(COLS);row.fill(-1);board.append(row)
 score=0;lines=0;level=1;paused=false;over=false;next_type=randi_range(0,SHAPES.size()-1);spawn_piece();queue_redraw()
func spawn_piece()->void:piece_type=next_type;next_type=randi_range(0,SHAPES.size()-1);shape=SHAPES[piece_type].duplicate();piece_pos=Vector2i(4,0);over=not valid(piece_pos,shape)
func valid(at:Vector2i,cells:Array)->bool:
 for offset in cells:
  var p=at+offset
  if p.x<0 or p.x>=COLS or p.y>=ROWS:return false
  if p.y>=0 and board[p.y][p.x]!=-1:return false
 return true
func move(delta:Vector2i)->bool:
 if valid(piece_pos+delta,shape):piece_pos+=delta;queue_redraw();return true
 return false
func rotate_piece()->void:
 var rotated:Array=[]
 for p in shape:rotated.append(Vector2i(-p.y,p.x))
 for kick in [Vector2i.ZERO,Vector2i(-1,0),Vector2i(1,0),Vector2i(-2,0),Vector2i(2,0)]:
  if valid(piece_pos+kick,rotated):piece_pos+=kick;shape=rotated;queue_redraw();return
func lock_piece()->void:
 for offset in shape:
  var p=piece_pos+offset
  if p.y>=0:board[p.y][p.x]=piece_type
 clear_lines();spawn_piece();queue_redraw()
func clear_lines()->void:
 var removed:=0;var y:=ROWS-1
 while y>=0:
  if board[y].all(func(v):return v!=-1):board.remove_at(y);var row=[];row.resize(COLS);row.fill(-1);board.push_front(row);removed+=1
  else:y-=1
 if removed>0:lines+=removed;score+=[0,100,300,500,800][removed]*level;level=1+lines/10
func hard_drop()->void:
 var distance:=0
 while move(Vector2i(0,1)):distance+=1
 score+=distance*2;lock_piece()
func _process(delta:float)->void:
 if paused or over:return
 fall_time+=delta
 if fall_time>=max(0.08,0.72-(level-1)*0.055):fall_time=0;if not move(Vector2i(0,1)):lock_piece()
func _unhandled_input(event:InputEvent)->void:
 if event is InputEventKey and event.pressed:
  if event.keycode==KEY_LEFT:move(Vector2i.LEFT)
  elif event.keycode==KEY_RIGHT:move(Vector2i.RIGHT)
  elif event.keycode==KEY_UP:rotate_piece()
  elif event.keycode==KEY_DOWN:move(Vector2i.DOWN)
  elif event.keycode==KEY_SPACE:hard_drop()
 if event is InputEventScreenTouch and event.pressed:
  var p=event.position*Vector2(960.0/get_viewport_rect().size.x,540.0/get_viewport_rect().size.y)
  if p.y<52:
   if p.x<150:reset()
   elif p.x<300:paused=not paused
  elif over:reset()
  elif p.y>455:
   if p.x<190:move(Vector2i.LEFT)
   elif p.x<380:move(Vector2i.RIGHT)
   elif p.x<570:rotate_piece()
   elif p.x<760:move(Vector2i.DOWN)
   else:hard_drop()
func _draw()->void:
 draw_rect(Rect2(0,0,960,540),Color("081221"));draw_rect(Rect2(0,0,960,52),Color("20385f"));label("BLOQUES",Vector2(395,35),28);label("NUEVO",Vector2(28,34),18);label("PAUSA",Vector2(170,34),18)
 draw_rect(Rect2(ORIGIN-Vector2(4,4),Vector2(COLS*CELL+8,ROWS*CELL+8)),Color("33435d"))
 for y in ROWS:
  for x in COLS:draw_rect(Rect2(ORIGIN+Vector2(x,y)*CELL,Vector2(CELL-1,CELL-1)),Color("111d30") if board[y][x]==-1 else COLORS[board[y][x]])
 if not over:
  for p in shape:cell(piece_pos+p,COLORS[piece_type])
 label("PUNTOS  %d"%score,Vector2(650,125),22);label("LÍNEAS  %d"%lines,Vector2(650,165),22);label("NIVEL  %d"%level,Vector2(650,205),22);label("SIGUIENTE",Vector2(650,265),18,Color("e0c23c"))
 for p in SHAPES[next_type]:draw_rect(Rect2(Vector2(710,300)+Vector2(p.x,p.y)*20,Vector2(18,18)),COLORS[next_type])
 var controls=["◀","▶","GIRAR","▼","CAER"]
 for i in controls.size():draw_rect(Rect2(i*192,460,190,70),Color("294873"));label(controls[i],Vector2(i*192+55,505),20)
 if paused:overlay("PAUSA")
 if over:overlay("FIN · TOCA PARA REINICIAR")
func cell(p:Vector2i,color:Color)->void:
 if p.y>=0:draw_rect(Rect2(ORIGIN+Vector2(p.x,p.y)*CELL,Vector2(CELL-1,CELL-1)),color)
func label(value:String,at:Vector2,size:int,color:Color=Color.WHITE)->void:draw_string(ThemeDB.fallback_font,at,value,HORIZONTAL_ALIGNMENT_LEFT,-1,size,color)
func overlay(value:String)->void:draw_rect(Rect2(170,210,620,110),Color(0.03,0.06,0.12,0.94));label(value,Vector2(245,275),26,Color("e0c23c"))
