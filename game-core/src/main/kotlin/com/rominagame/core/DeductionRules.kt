package com.rominagame.core

data class DominoTile(val id:Int,val a:Int,val b:Int){ fun matches(value:Int)=a==value||b==value }
enum class DominoEnd{LEFT,RIGHT}
data class DominoChain(val tiles:List<DominoTile> = emptyList(),val left:Int=-1,val right:Int=-1)

fun doubleSixSet():List<DominoTile>{var id=0;return buildList{for(a in 0..6)for(b in a..6)add(DominoTile(id++,a,b))}}
fun dominoCanPlay(tile:DominoTile,chain:DominoChain,end:DominoEnd)=chain.tiles.isEmpty()||tile.matches(if(end==DominoEnd.LEFT)chain.left else chain.right)
fun placeDomino(chain:DominoChain,tile:DominoTile,end:DominoEnd):DominoChain{
 require(dominoCanPlay(tile,chain,end))
 if(chain.tiles.isEmpty())return DominoChain(listOf(tile),tile.a,tile.b)
 return if(end==DominoEnd.LEFT){val outer=if(tile.a==chain.left)tile.b else tile.a;DominoChain(listOf(DominoTile(tile.id,outer,chain.left))+chain.tiles,outer,chain.right)}
 else{val outer=if(tile.a==chain.right)tile.b else tile.a;DominoChain(chain.tiles+DominoTile(tile.id,chain.right,outer),chain.left,outer)}
}
fun playableDominoEnds(tile:DominoTile,chain:DominoChain)=DominoEnd.entries.filter{dominoCanPlay(tile,chain,it)}
fun dominoBlocked(hands:List<List<DominoTile>>,pool:List<DominoTile>,chain:DominoChain)=chain.tiles.isNotEmpty()&&pool.isEmpty()&&hands.all{h->h.none{playableDominoEnds(it,chain).isNotEmpty()}}

data class GuessCharacter(val id:Int,val name:String,val symbol:String,val color:String,val size:String,val habitat:String)
val guessCharacters=listOf(
 GuessCharacter(0,"Perro","P","café","grande","casa"),GuessCharacter(1,"Gato","G","gris","chico","casa"),
 GuessCharacter(2,"Conejo","C","blanco","chico","campo"),GuessCharacter(3,"Rana","R","verde","chico","agua"),
 GuessCharacter(4,"Cerdo","O","rosa","grande","campo"),GuessCharacter(5,"Koala","K","gris","grande","bosque"),
 GuessCharacter(6,"León","L","café","grande","campo"),GuessCharacter(7,"Hámster","H","café","chico","casa"),
 GuessCharacter(8,"Pingüino","N","negro","chico","agua"),GuessCharacter(9,"Oso","B","café","grande","bosque"),
 GuessCharacter(10,"Zorro","Z","naranja","chico","bosque"),GuessCharacter(11,"Pato","D","amarillo","chico","agua")
)
enum class GuessAttribute{COLOR,SIZE,HABITAT}
fun guessValue(character:GuessCharacter,attribute:GuessAttribute)=when(attribute){GuessAttribute.COLOR->character.color;GuessAttribute.SIZE->character.size;GuessAttribute.HABITAT->character.habitat}
fun answerQuestion(secret:GuessCharacter,attribute:GuessAttribute,value:String)=guessValue(secret,attribute)==value
fun eliminateByAnswer(characters:List<GuessCharacter>,attribute:GuessAttribute,value:String,answer:Boolean)=characters.filter{answerQuestion(it,attribute,value)!=answer}.map{it.id}.toSet()
