package com.rominagame.core

data class LotteryCard(val name:String,val symbol:String)
val mexicanLotteryDeck=listOf(
 "El gallo","La dama","El paraguas","La sirena","La escalera","El barril","El árbol","El melón","La pera","La bandera","La garza","El pájaro","La mano","La bota","La luna","El cotorro","El corazón","La sandía","El tambor","El camarón","La araña","La estrella","El mundo","El nopal","La rosa","La campana","El cantarito","El venado","El sol","La corona","La chalupa","El pino","El pescado","La palma","La maceta","La rana"
).mapIndexed{i,n->LotteryCard(n,(i+1).toString())}
val bingoDeck=listOf("Perro","Gato","Conejo","Mariposa","Flor","Estrella","Manzana","Auto","Globo")

fun lotteryBoard(deck:List<LotteryCard> = mexicanLotteryDeck.shuffled())=deck.take(16)
fun canMarkLottery(board:List<LotteryCard>,called:LotteryCard,index:Int,marked:Set<Int>)=index in board.indices&&index !in marked&&board[index].name==called.name
fun lotteryComplete(marked:Set<Int>)=marked.size==16
fun bingoLine(marked:Set<Int>):Boolean{
 val lines=listOf(setOf(0,1,2),setOf(3,4,5),setOf(6,7,8),setOf(0,3,6),setOf(1,4,7),setOf(2,5,8),setOf(0,4,8),setOf(2,4,6))
 return lines.any{marked.containsAll(it)}
}
