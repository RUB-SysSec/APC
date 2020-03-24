package de.rub.mobsec

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.sign

internal class NodeTests {
    @TestFactory
    fun getMaxHorizontalOrVerticalDistance() =
        (testRange.flatMap { x ->
            testRange.map { y ->
                DynamicTest.dynamicTest("$x,$y expected ${max(abs(x), abs(y))}") {
                    Assertions.assertEquals(max(abs(x), abs(y)), Node[x, y].maxHorizontalOrVerticalDistance)
                }
            }
        } + DynamicTest.dynamicTest("100,-8 expected 100") {
            Assertions.assertEquals(100, Node[100, -8].maxHorizontalOrVerticalDistance)
        } + DynamicTest.dynamicTest("-8,100 expected 100") {
            Assertions.assertEquals(100, Node[-8, 100].maxHorizontalOrVerticalDistance)
        }).iterator()

    @TestFactory
    fun getEuclideanDistance() =
        (testRangeNoZero.flatMap { factor ->
            normalMoves.map { point ->
                DynamicTest.dynamicTest("#1 $factor expected ${abs(factor * NORMAL_DISTANCE)}") {
                    Assertions.assertEquals(
                        abs(factor * NORMAL_DISTANCE),
                        (point * factor).euclideanDistance,
                        DELTA
                    )
                }
            } + diagonalMoves.map { point ->
                DynamicTest.dynamicTest("+2 $factor expected ${abs(factor * DIAGONAL_DISTANCE)}") {
                    Assertions.assertEquals(
                        abs(factor * DIAGONAL_DISTANCE),
                        (point * factor).euclideanDistance,
                        DELTA
                    )
                }
            } + knightMoves.map { point ->
                DynamicTest.dynamicTest("#3 $factor expected ${abs(factor * KNIGHT_DISTANCE)}") {
                    Assertions.assertEquals(
                        abs(factor * KNIGHT_DISTANCE),
                        (point * factor).euclideanDistance,
                        DELTA
                    )
                }
            }
        } + DynamicTest.dynamicTest("expected 0") {
            Assertions.assertEquals(0.0, Node[0, 0].euclideanDistance)
        } + DynamicTest.dynamicTest("expected 10.770329614269008") {
            Assertions.assertEquals(10.770329614269008, Node[-10, 4].euclideanDistance)
        }).iterator()

    @TestFactory
    fun plus() =
        testRange.flatMap { x1 ->
            testRange.flatMap { y1 ->
                testRange.flatMap { x2 ->
                    testRange.map { y2 ->
                        DynamicTest.dynamicTest("$x1,$y1 + $x2,$y2") {
                            Assertions.assertEquals(Node[x1 + x2, y1 + y2], Node[x1, y1] + Node[x2, y2])
                        }
                    }
                }
            }
        }.iterator()

    @TestFactory
    fun minus() =
        testRange.flatMap { x1 ->
            testRange.flatMap { y1 ->
                testRange.flatMap { x2 ->
                    testRange.map { y2 ->
                        DynamicTest.dynamicTest("$x1,$y1 - $x2,$y2") {
                            Assertions.assertEquals(Node[x1 - x2, y1 - y2], Node[x1, y1] - Node[x2, y2])
                        }
                    }
                }
            }
        }.iterator()

    @TestFactory
    fun times() =
        testRange.flatMap { x ->
            testRange.flatMap { y ->
                testRange.map { scalar ->
                    DynamicTest.dynamicTest("$x,$y * $scalar") {
                        Assertions.assertEquals(Node[x * scalar, y * scalar], Node[x, y] * scalar)
                    }
                }
            }
        }.iterator()

    @TestFactory
    fun div() =
        testRange.flatMap { x ->
            testRange.flatMap { y ->
                testRangeNoZero.map { scalar ->
                    DynamicTest.dynamicTest("$x,$y / $scalar") {
                        Assertions.assertEquals(Node[x / scalar, y / scalar], Node[x, y] / scalar)
                    }
                }
            }
        }.iterator()

    @TestFactory
    fun abs() =
        testRange.flatMap { x ->
            testRange.map { y ->
                DynamicTest.dynamicTest("$x,$y") {
                    Assertions.assertEquals(Node[abs(x), abs(y)], Node[x, y].abs())
                }
            }
        }.iterator()

    @TestFactory
    fun minimize() =
        (testRangeNoZero.flatMap { factor ->
            normalMoves.map {
                DynamicTest.dynamicTest("#1 $it") {
                    Assertions.assertEquals(it * factor.sign to factor.absoluteValue, (it * factor).minimize())
                }
            } + diagonalMoves.map {
                DynamicTest.dynamicTest("#2 $it") {
                    Assertions.assertEquals(it * factor.sign to factor.absoluteValue, (it * factor).minimize())
                }
            } + knightMoves.map {
                DynamicTest.dynamicTest("#3 $it") {
                    Assertions.assertEquals(it * factor.sign to factor.absoluteValue, (it * factor).minimize())
                }
            }
        } + DynamicTest.dynamicTest("0") {
            Assertions.assertEquals(Node[0, 0] to 0, Node[0, 0].minimize())
        }).iterator()

    @TestFactory
    fun determinant() =
        (testRange.flatMap { x1 ->
            testRange.flatMap { y1 ->
                testRange.flatMap { x2 ->
                    testRange.map { y2 ->
                        DynamicTest.dynamicTest("$x1,$y1 det $x2,$y2") {
                            Assertions.assertEquals(x1 * y2 - y1 * x2, Node[x1, y1].determinant(Node[x2, y2]))
                        }
                    }
                }
            }
        } + DynamicTest.dynamicTest("$8,-100 det 8,100") {
            Assertions.assertEquals(1600, Node[8, -100].determinant(Node[8, 100]))
        } + DynamicTest.dynamicTest("$8,100 det 8,100") {
            Assertions.assertEquals(0, Node[8, 100].determinant(Node[8, 100]))
        }).iterator()
}