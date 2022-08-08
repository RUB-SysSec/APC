package de.rub.mobsec

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

internal class AndroidUnlockGridTests {
    @TestFactory
    fun `test if point in map == isPointInMap`() =
        positiveTestRange.flatMap { cols ->
            positiveTestRange.flatMap { rows ->
                sequenceOf(
                    dynamicTest("$cols,$rows expected false") {
                        assertEquals(false, Node[-1, 0] in AndroidUnlockGrid[cols, rows])
                    },
                    dynamicTest("$cols,$rows expected true") {
                        assertEquals(true, Node[cols - 1, rows - 1] in AndroidUnlockGrid[cols, rows])
                    })
            }
        }.iterator()

    @TestFactory
    fun `test if map#get(map#get(index)) == index`() =
        positiveTestRange.flatMap { cols ->
            positiveTestRange.flatMap { rows ->
                (0 until cols * rows).asSequence().map { index ->
                    val grid = AndroidUnlockGrid[cols, rows]

                    dynamicTest("$cols,$rows expected $index") {
                        assertEquals(index, grid[grid[index]])
                    }
                }
            }
        }.iterator()

    @TestFactory
    fun `test if map#get(point) == index`() =
        positiveTestRange.flatMap { cols ->
            positiveTestRange.flatMap { rows ->
                sequenceOf(
                    dynamicTest("#1 $cols,$rows expected 0") {
                        assertEquals(0, AndroidUnlockGrid[cols, rows][Node[0, 0]])
                    },
                    dynamicTest("#2 $cols,$rows expected ${cols - 1}") {
                        assertEquals(cols - 1, AndroidUnlockGrid[cols, rows][Node[cols - 1, 0]])
                    },
                    dynamicTest("#3 $cols,$rows expected ${(rows - 1) * cols}") {
                        assertEquals(
                            (rows - 1) * cols,
                            AndroidUnlockGrid[cols, rows][Node[0, rows - 1]]
                        )
                    },
                    dynamicTest("#4 $cols,$rows expected ${cols * rows - 1}") {
                        assertEquals(
                            cols * rows - 1,
                            AndroidUnlockGrid[cols, rows][Node[cols - 1, rows - 1]]
                        )
                    }
                )
            }
        }.iterator()
}