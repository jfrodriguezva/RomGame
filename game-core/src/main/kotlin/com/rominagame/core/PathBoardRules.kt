package com.rominagame.core

data class PathMove(val rolledTarget:Int,val finalTarget:Int,val message:String?)

val gooseJumps=mapOf(6 to 12,19 to 6,31 to 12,42 to 30)
val snakesLaddersJumps=mapOf(4 to 14,17 to 4,22 to 32,36 to 20)

fun pathMove(position:Int,roll:Int,last:Int,jumps:Map<Int,Int>):PathMove?{
    require(roll in 1..6)
    val target=position+roll
    if(target>last)return null
    val final=jumps[target]?:target
    val message=when{
        final>target->"Salta de $target a $final"
        final<target->"Regresa de $target a $final"
        else->null
    }
    return PathMove(target,final,message)
}

fun pathCellCenter(index:Int,total:Int):Pair<Float,Float>{
    require(index in 0..total)
    val zero=(index-1).coerceAtLeast(0);val row=zero/10;val offset=zero%10
    val col=if(row%2==0)offset else 9-offset
    return (270f+col*58f) to (115f+row*70f)
}

val movementChallenges=listOf(
    "Salta 3 veces","Da 2 vueltas","Toca tus pies","Aplaude 5 veces","Camina de puntitas","Haz como un avión",
    "Salta como conejo 5 veces","Camina como pato","Haz 3 sentadillas","Camina atrás 4 pasos","Ruge como león",
    "Vuela como mariposa","Marcha 10 pasos","Haz una reverencia","Gira los brazos","Da 3 abrazos",
    "Di por favor y gracias","Respira hondo 3 veces","Quédate como estatua","Salta como rana 4 veces",
    "Camina como cangrejo","Toca tu nariz","Aplaude lento y rápido","Haz como que nadas","Imita un tren",
    "Salta adelante y atrás","Toca algo azul","Toca algo rojo","Cuenta hasta 5","Di algo bonito",
    "Haz pose de superhéroe","Camina como si flotaras","Equilibrio 5 segundos","Estírate muy alto",
    "Sonríe a alguien","Toca el suelo","Mueve hombros 5 veces","Da 4 pasos gigantes","Baila 5 segundos","Descansa y sonríe",
)
