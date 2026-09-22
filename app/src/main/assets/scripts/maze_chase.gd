extends Node2D
const MAP=["###################","#o.......#.......o#","#.###.##.#.##.###.#","#.................#","#.###.#.###.#.###.#","#.....#..#..#.....#","#####.## # ##.#####","    #.#     #.#    ","#####.# ### #.#####","#.......#.........#","#.###.#.###.#.###.#","#o..#........#...o#","###################"]
const CELL:=32;const ORIGIN:=Vector2(176,70)
var walls:Dictionary={};var pellets:Dictionary={};var powers:Dictionary={};var player:=Vector2i(1,1);var direction:=Vector2i.RIGHT;var queued:=Vector2i.RIGHT;var ghosts:Array[Vector2i]=[];var ghost_dirs:Array[Vector2i]=[];var timer:=0.0;var frightened:=0.0;var score:=0;var lives:=3;var level:=1;var paused:=false;var over:=false;var touch_start:=Vector2.ZERO
func _ready()->void:reset_game()
func reset_game()->void:lives=3;score=0;level=1;over=false;paused=false;load_level()
func load_level()->void:
 walls.clear();pellets.clear();powers.clear()
 for y in MAP.size():
  for x in MAP[y].length():
   var p=Vector2i(x,y);var ch=MAP[y][x]
   if ch=="#":walls[p]=true
   elif ch==".":pellets[p]=true
   elif ch=="o":powers[p]=true
 player=Vector2i(1,1);direction=Vector2i.RIGHT;queued=direction;ghosts=[Vector2i(9,7),Vector2i(8,7),Vector2i(10,7)];ghost_dirs=[Vector2i.LEFT,Vector2i.RIGHT,Vector2i.UP];queue_redraw()
func open(p:Vector2i)->bool:return not walls.has(p) and p.y>=0 and p.y<MAP.size()
func turn(next:Vector2i)->void:queued=next
func _process(delta:float)->void:
 if paused or over:return
 frightened=max(0.0,frightened-delta);timer+=delta
 if timer<max(.09,.18-(level-1)*.012):return
 timer=0
 if open(player+queued):direction=queued
 if open(player+direction):player+=direction
 player.x=posmod(player.x,MAP[0].length())
 if pellets.erase(player):score+=10
 if powers.erase(player):score+=50;frightened=7.0
 move_ghosts();collide()
 if pellets.is_empty() and powers.is_empty():
  level+=1
  if level>3:
   over=true
  else:
   load_level()
 queue_redraw()
func move_ghosts()->void:
 for i in ghosts.size():
  var options:Array[Vector2i]=[]
  for d in [Vector2i.LEFT,Vector2i.RIGHT,Vector2i.UP,Vector2i.DOWN]:
   if d+ghost_dirs[i]!=Vector2i.ZERO and open(ghosts[i]+d):options.append(d)
  if options.is_empty():options.append(-ghost_dirs[i])
  var target=Vector2i(9,7) if frightened>0 else player
  options.sort_custom(func(a,b):return (ghosts[i]+a).distance_squared_to(target)>(ghosts[i]+b).distance_squared_to(target) if frightened>0 else (ghosts[i]+a).distance_squared_to(target)<(ghosts[i]+b).distance_squared_to(target))
  ghost_dirs[i]=options[0];ghosts[i]+=ghost_dirs[i];ghosts[i].x=posmod(ghosts[i].x,MAP[0].length())
func collide()->void:
 for i in range(ghosts.size()-1,-1,-1):
  if ghosts[i]==player:
    if frightened>0:
     score+=200
     ghosts[i]=Vector2i(9,7)
    else:
     lives-=1
     player=Vector2i(1,1)
     if lives<=0:
      over=true
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
    if p.x<150:
     reset_game()
    elif p.x<300:
     paused=not paused
  else:
   var d=p-touch_start
   if d.length()>30:turn(Vector2i(sign(d.x),0) if abs(d.x)>abs(d.y) else Vector2i(0,sign(d.y)))
   elif over:reset_game()
func _draw()->void:
 draw_rect(Rect2(0,0,960,540),Color("030713"));draw_rect(Rect2(0,0,960,55),Color("16275a"));label("COMEPUNTOS",Vector2(400,37),27);label("NUEVO",Vector2(25,36),18);label("PAUSA",Vector2(165,36),18);label("Puntos %d  Vidas %d  Nivel %d"%[score,lives,level],Vector2(670,36),17)
 for p in walls:draw_rect(Rect2(ORIGIN+Vector2(p)*CELL,Vector2(CELL-2,CELL-2)),Color("2553c7"))
 for p in pellets:draw_circle(ORIGIN+Vector2(p)*CELL+Vector2(CELL/2,CELL/2),3,Color("f5db91"))
 for p in powers:draw_circle(ORIGIN+Vector2(p)*CELL+Vector2(CELL/2,CELL/2),7,Color("fff0b0"))
 draw_circle(ORIGIN+Vector2(player)*CELL+Vector2(CELL/2,CELL/2),12,Color("ffd630"))
 var colors=[Color("ef4b54"),Color("ef77bc"),Color("4ad5e8")]
 for i in ghosts.size():draw_circle(ORIGIN+Vector2(ghosts[i])*CELL+Vector2(CELL/2,CELL/2),11,Color("3158d4") if frightened>0 else colors[i])
 if paused:overlay("PAUSA")
 if over:overlay("LABERINTOS COMPLETOS" if level>3 else "FIN · TOCA PARA REINICIAR")
func label(v:String,p:Vector2,s:int,c:Color=Color.WHITE)->void:draw_string(ThemeDB.fallback_font,p,v,HORIZONTAL_ALIGNMENT_LEFT,-1,s,c)
func overlay(v:String)->void:draw_rect(Rect2(180,220,600,90),Color(0.03,0.06,0.12,.94));label(v,Vector2(280,275),25,Color("e0c23c"))
