package com.rominagame.core
import org.junit.Assert.*
import org.junit.Test
class PathBoardRulesTest{
 @Test fun `normal move preserves rolled target`(){assertEquals(PathMove(8,8,null),pathMove(5,3,40,emptyMap()))}
 @Test fun `goose applies forward and backward specials`(){assertEquals(12,pathMove(3,3,50,gooseJumps)!!.finalTarget);assertEquals(6,pathMove(16,3,50,gooseJumps)!!.finalTarget)}
 @Test fun `snakes and ladders use their real direction`(){assertEquals(14,pathMove(1,3,40,snakesLaddersJumps)!!.finalTarget);assertEquals(4,pathMove(14,3,40,snakesLaddersJumps)!!.finalTarget)}
 @Test fun `finish requires exact roll`(){assertNull(pathMove(38,4,40,snakesLaddersJumps));assertEquals(40,pathMove(38,2,40,snakesLaddersJumps)!!.finalTarget)}
 @Test fun `board alternates row direction`(){assertEquals(pathCellCenter(10,40).first,pathCellCenter(11,40).first,0.01f);assertTrue(pathCellCenter(11,40).first>pathCellCenter(12,40).first)}
 @Test fun `challenge die retains forty activities`(){assertEquals(40,movementChallenges.distinct().size)}
}
