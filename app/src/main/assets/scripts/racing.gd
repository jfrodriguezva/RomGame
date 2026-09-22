extends Node2D

var game_id := "gran-premio"
var car_x:=0.0;var speed:=0.0;var road_curve:=0.0;var distance:=0.0;var score:=0;var lap:=1;var race_position:=6;var lives:=3;var level:=1;var time_left:=70.0
var rivals:Array=[];var steer:=0.0;var throttle:=false;var brake:=false;var paused:=false;var over:=false;var won:=false

func _ready()->void:reset_game()
func reset_game()->void:
 car_x=0;speed=0;distance=0;score=0;lap=1;race_position=6;lives=3;level=1;time_left=70;paused=false;over=false;won=false;rivals.clear()
 for i in 8:rivals.append({"lane":float((i%3)-1)*.55,"d":150.0+i*125.0,"speed":125.0+i*7})
func _process(delta:float)->void:
 if paused or over:return
 speed=move_toward(speed,310.0 if throttle else 0.0,110*delta)
 if brake:speed=move_toward(speed,0.0,260*delta)
 road_curve=sin(distance/420.0)*.65+sin(distance/970.0)*.35
 car_x=clamp(car_x+steer*delta*(1.1+speed/180.0),-1.15,1.15)
 if abs(car_x)>1.0:speed=move_toward(speed,70.0,230*delta)
 distance+=speed*delta;score=int(distance)
 for r in rivals:
  r.d+=r.speed*delta
  var relative=r.d-distance
  if relative>0 and relative<24 and abs(r.lane-car_x)<.3:speed*=.45;lives-=1;r.d+=90
 if game_id=="gran-premio":update_circuit()
 else:update_traffic(delta)
 if lives<=0:over=true
 queue_redraw()
func update_circuit()->void:
 var circuit=2600.0+level*350
 lap=int(distance/circuit)+1
 race_position=1
 for r in rivals:
  if r.d>distance:race_position+=1
 if lap>3:
  level+=1
  if level>3:over=true;won=true
  else:
   distance=0;lap=1
   for i in rivals.size():rivals[i].d=120+i*145;rivals[i].speed+=14
func update_traffic(delta:float)->void:
 time_left-=delta
 for r in rivals:
  if r.d<distance-40:r.d=distance+randf_range(650,1250);r.lane=[-.65,0.0,.65].pick_random();r.speed=randf_range(80,185)
 if distance>level*3200:
  level+=1;time_left+=30
  if level>3:over=true;won=true
 if time_left<=0:over=true
func _unhandled_input(event:InputEvent)->void:
 if event is InputEventKey:
  if event.keycode==KEY_LEFT:steer=-1.0 if event.pressed else 0.0
  elif event.keycode==KEY_RIGHT:steer=1.0 if event.pressed else 0.0
  elif event.keycode==KEY_UP:throttle=event.pressed
  elif event.keycode==KEY_DOWN:brake=event.pressed
 if event is InputEventScreenTouch:
  var p=event.position*Vector2(960.0/get_viewport_rect().size.x,540.0/get_viewport_rect().size.y)
  if p.y<55 and event.pressed:
   if p.x<145:reset_game()
   elif p.x<290:paused=not paused
  elif p.y>430:
   if p.x<240:steer=-1.0 if event.pressed else 0.0
   elif p.x<480:steer=1.0 if event.pressed else 0.0
   elif p.x<720:brake=event.pressed
   else:throttle=event.pressed
  elif event.pressed and over:reset_game()
func road_x(y:float)->float:
 var depth=(430.0-y)/350.0
 return 480+road_curve*depth*260-car_x*depth*210
func project_rival(r:Dictionary)->Dictionary:
 var rel=fposmod(r.d-distance,1400.0);var depth=1.0-clamp(rel/1400.0,0.0,1.0);var y=85+depth*345;var center=road_x(y);return {"p":Vector2(center+r.lane*(80+depth*230),y),"s":10+depth*42}
func _draw()->void:
 draw_rect(Rect2(0,0,960,540),Color("65b5df"));draw_rect(Rect2(0,235,960,305),Color("4a9348"));draw_rect(Rect2(0,0,960,55),Color("202c49"))
 var road=PackedVector2Array([Vector2(410,80),Vector2(550,80),Vector2(920,430),Vector2(40,430)]);draw_colored_polygon(road,Color("42454d"))
 for i in 10:
  var y=90+i*36.0;var center=road_x(y);var half=75+(y-80)*1.05;draw_line(Vector2(center-half,y),Vector2(center-half-15,y+25),Color.WHITE,4);draw_line(Vector2(center+half,y),Vector2(center+half+15,y+25),Color.WHITE,4)
  if i%2==0:draw_line(Vector2(center,y),Vector2(center,y+20),Color("f5e060"),3)
 for r in rivals:
  var q=project_rival(r);draw_rect(Rect2(q.p-Vector2(q.s*.45,q.s),Vector2(q.s*.9,q.s*1.45)),Color("e4494f"))
 draw_rect(Rect2(Vector2(480+car_x*260,405)-Vector2(30,42),Vector2(60,72)),Color("2b72dd"));draw_rect(Rect2(465+car_x*260,375,30,20),Color("9de6ff"))
 var title="GRAN PREMIO" if game_id=="gran-premio" else "CARRERAS DE TRÁFICO";label(title,Vector2(370,36),25);label("NUEVO",Vector2(22,35),17);label("PAUSA",Vector2(160,35),17)
 var status="Vuelta %d/3  Pos %d/9"%[min(lap,3),race_position] if game_id=="gran-premio" else "Tiempo %.0f  Tramo %d"%[max(time_left,0),level];label("%s  %d km/h  Golpes %d"%[status,int(speed),lives],Vector2(620,35),15)
 for i in 4:draw_rect(Rect2(i*240,460,238,75),Color("263650"));label(["◀","▶","FRENO","ACELERAR"][i],Vector2(i*240+56,505),18)
 if paused:overlay("PAUSA")
 if over:overlay("CAMPEONATO COMPLETO" if won and game_id=="gran-premio" else "RUTA COMPLETA" if won else "FIN · TOCA PARA REINICIAR")
func label(v:String,p:Vector2,s:int,c:Color=Color.WHITE)->void:draw_string(ThemeDB.fallback_font,p,v,HORIZONTAL_ALIGNMENT_LEFT,-1,s,c)
func overlay(v:String)->void:draw_rect(Rect2(175,220,610,90),Color(0.03,0.04,0.08,.95));label(v,Vector2(275,275),25,Color("f4dc50"))
