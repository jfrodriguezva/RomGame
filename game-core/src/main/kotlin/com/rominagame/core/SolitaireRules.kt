package com.rominagame.core

data class PlayingCard(val suit:Int,val rank:Int)
data class FaceCard(val card:PlayingCard,val faceUp:Boolean)
data class KlondikeState(val columns:List<List<FaceCard>>,val stock:List<PlayingCard>,val waste:List<PlayingCard>,val foundations:List<Int>)
data class SpiderState(val columns:List<List<FaceCard>>,val stock:List<PlayingCard>,val completed:Int)

fun isRed(card:PlayingCard)=card.suit==1||card.suit==2
fun fullDeck()=(0..3).flatMap{suit->(1..13).map{PlayingCard(suit,it)}}
fun validKlondikeSequence(column:List<FaceCard>,index:Int):Boolean{
 if(index !in column.indices||!column[index].faceUp)return false
 return(index until column.lastIndex).all{i->val a=column[i].card;val b=column[i+1].card;isRed(a)!=isRed(b)&&a.rank==b.rank+1}
}
fun canPlaceKlondike(card:PlayingCard,destination:List<FaceCard>):Boolean=if(destination.isEmpty())card.rank==13 else destination.last().let{it.faceUp&&isRed(it.card)!=isRed(card)&&it.card.rank==card.rank+1}
fun canPlaceFoundation(card:PlayingCard,current:Int)=card.rank==current+1
fun newKlondike(deck:List<PlayingCard> = fullDeck().shuffled()):KlondikeState{val cards=deck.toMutableList();val columns=(0..6).map{col->(0..col).map{row->FaceCard(cards.removeAt(0),row==col)}};return KlondikeState(columns,cards,emptyList(),List(4){0})}
private fun revealTop(column:List<FaceCard>)=if(column.isNotEmpty()&&!column.last().faceUp)column.dropLast(1)+column.last().copy(faceUp=true) else column
fun drawKlondike(state:KlondikeState):KlondikeState=if(state.stock.isNotEmpty())state.copy(stock=state.stock.drop(1),waste=state.waste+state.stock.first())else state.copy(stock=state.waste.reversed(),waste=emptyList())
fun moveKlondikeColumns(state:KlondikeState,from:Int,index:Int,to:Int):KlondikeState?{if(from==to||from !in state.columns.indices||to !in state.columns.indices)return null;val source=state.columns[from];if(!validKlondikeSequence(source,index))return null;val packet=source.drop(index);if(!canPlaceKlondike(packet.first().card,state.columns[to]))return null;val columns=state.columns.toMutableList();columns[from]=revealTop(source.take(index));columns[to]=columns[to]+packet;return state.copy(columns=columns)}
fun moveWasteToColumn(state:KlondikeState,to:Int):KlondikeState?{val card=state.waste.lastOrNull()?:return null;if(to !in state.columns.indices||!canPlaceKlondike(card,state.columns[to]))return null;val columns=state.columns.toMutableList();columns[to]=columns[to]+FaceCard(card,true);return state.copy(columns=columns,waste=state.waste.dropLast(1))}
fun moveKlondikeToFoundation(state:KlondikeState,from:Int):KlondikeState?{val card=if(from==-1)state.waste.lastOrNull() else state.columns.getOrNull(from)?.lastOrNull()?.takeIf{it.faceUp}?.card?:return null;if(card==null||!canPlaceFoundation(card,state.foundations[card.suit]))return null;val foundations=state.foundations.toMutableList().also{it[card.suit]=card.rank};if(from==-1)return state.copy(waste=state.waste.dropLast(1),foundations=foundations);val columns=state.columns.toMutableList();columns[from]=revealTop(columns[from].dropLast(1));return state.copy(columns=columns,foundations=foundations)}

fun validSpiderSequence(column:List<FaceCard>,index:Int):Boolean{if(index !in column.indices||!column[index].faceUp)return false;return(index until column.lastIndex).all{i->column[i].card.rank==column[i+1].card.rank+1}}
fun canPlaceSpider(card:PlayingCard,destination:List<FaceCard>)=destination.isEmpty()||destination.last().let{it.faceUp&&it.card.rank==card.rank+1}
fun spiderComplete(column:List<FaceCard>):Boolean=column.size>=13&&validSpiderSequence(column,column.size-13)&&column[column.size-13].card.rank==13&&column.last().card.rank==1
fun newSpider(deck:List<PlayingCard> = (1..8).flatMap{(1..13).map{rank->PlayingCard(0,rank)}}.shuffled()):SpiderState{val cards=deck.toMutableList();val columns=(0..9).map{col->val count=if(col<4)6 else 5;(0 until count).map{row->FaceCard(cards.removeAt(0),row==count-1)}};return SpiderState(columns,cards,0)}
fun moveSpider(state:SpiderState,from:Int,index:Int,to:Int):SpiderState?{if(from==to||from !in state.columns.indices||to !in state.columns.indices)return null;val source=state.columns[from];if(!validSpiderSequence(source,index))return null;val packet=source.drop(index);if(!canPlaceSpider(packet.first().card,state.columns[to]))return null;val columns=state.columns.toMutableList();columns[from]=revealTop(source.take(index));columns[to]=columns[to]+packet;var completed=state.completed;if(spiderComplete(columns[to])){columns[to]=revealTop(columns[to].dropLast(13));completed++};return SpiderState(columns,state.stock,completed)}
fun dealSpider(state:SpiderState):SpiderState?{if(state.stock.size<10||state.columns.any{it.isEmpty()})return null;var columns=state.columns.mapIndexed{i,col->col+FaceCard(state.stock[i],true)};var completed=state.completed;columns=columns.map{col->if(spiderComplete(col)){completed++;revealTop(col.dropLast(13))}else col};return SpiderState(columns,state.stock.drop(10),completed)}
