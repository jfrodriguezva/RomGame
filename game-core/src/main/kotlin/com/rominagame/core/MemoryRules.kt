package com.rominagame.core

import kotlin.random.Random

enum class MemoryMode(val id:String,val title:String){
    PAIRS("pairs","Parejas"), MISSING("missing","Qué falta"), TURNS("turns","Contra CPU"), SEARCH("search","Búsqueda visual")
}

data class MemoryDifficulty(val rows:Int,val cols:Int,val previewSeconds:Float,val targets:Int){val cells:Int get()=rows*cols}

fun memoryDifficulty(mode:MemoryMode,level:Int):MemoryDifficulty{
    val n=level.coerceIn(1,20)
    return when(mode){
        MemoryMode.PAIRS,MemoryMode.TURNS->when{n<=4->MemoryDifficulty(2,3,1.2f,3);n<=9->MemoryDifficulty(2,4,1f,4);n<=14->MemoryDifficulty(3,4,.8f,6);else->MemoryDifficulty(4,4,.6f,8)}
        MemoryMode.MISSING->when{n<=5->MemoryDifficulty(2,3,2.5f,1);n<=12->MemoryDifficulty(2,4,2f,1);else->MemoryDifficulty(3,4,1.5f,1)}
        MemoryMode.SEARCH->when{n<=5->MemoryDifficulty(3,4,0f,2);n<=12->MemoryDifficulty(4,4,0f,3);else->MemoryDifficulty(4,5,0f,4)}
    }
}

fun pairDeck(pairs:Int,random:Random=Random.Default): List<Char> = (('A'..'Z').take(pairs).flatMap{listOf(it,it)}).shuffled(random)

data class SearchDeck(val symbols:List<Char>,val target:Char,val targetIndexes:Set<Int>)
fun searchDeck(cells:Int,targets:Int,random:Random=Random.Default):SearchDeck{
    val target=('A'..'F').random(random);val indexes=(0 until cells).shuffled(random).take(targets).toSet()
    val symbols=List(cells){i->if(i in indexes)target else ('G'..'Z').random(random)}
    return SearchDeck(symbols,target,indexes)
}
