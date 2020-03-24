package de.rub.mobsec

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertAll
import kotlin.math.abs
import kotlin.math.max

internal class SegmentTests {
    @TestFactory
    fun getMove() =
        testRangeNoZero.flatMap { x1 ->
            testRangeNoZero.flatMap { y1 ->
                testRangeNoZero.flatMap { x2 ->
                    testRangeNoZero.map { y2 ->
                        dynamicTest("$x1,$y1 $x2,$y2") {
                            assertEquals(Node[x2 - x1, y2 - y1], Segment[Node[x1, y1], Node[x2, y2]].direction)
                        }
                    }
                }
            }
        }.iterator()

    @TestFactory
    fun getMaxHorizontalOrVerticalDistance() =
        testRangeNoZero.flatMap { x1 ->
            testRangeNoZero.flatMap { y1 ->
                testRangeNoZero.flatMap { x2 ->
                    testRangeNoZero.map { y2 ->
                        dynamicTest("$x1,$y1 $x2,$y2") {
                            assertEquals(
                                max(abs(x2 - x1), abs(y2 - y1)),
                                Segment[Node[x1, y1], Node[x2, y2]].maximumNorm
                            )
                        }
                    }
                }
            }
        }.iterator()

    @TestFactory
    fun getPointsBetweenAB() =
        testRange.flatMap { x ->
            testRange.flatMap { y ->
                allMoves.flatMap {
                    val point = Node[x, y]
                    sequenceOf(
                        dynamicTest("1") {
                            assertIterableEquals(emptySet<Node>(), Segment[point, point + it].nodesBetweenAB)
                        },
                        dynamicTest("2") {
                            assertIterableEquals(setOf(point), Segment[point - it, point + it].nodesBetweenAB)
                        },
                        dynamicTest("3") {
                            assertIterableEquals(
                                setOf(point - it, point),
                                Segment[point - it - it, point + it].nodesBetweenAB
                            )
                        })
                }
            }
        }.iterator()

    @TestFactory
    fun isSinglePoint() =
        testRangeNoZero.flatMap { x1 ->
            testRangeNoZero.flatMap { y1 ->
                testRangeNoZero.flatMap { x2 ->
                    testRangeNoZero.map { y2 ->
                        dynamicTest("$x1,$y1 $x2,$y2") {
                            assertEquals(x1 == x2 && y1 == y2, Segment[Node[x1, y1], Node[x2, y2]].isSinglePoint)
                        }
                    }
                }
            }
        }.iterator()

    @TestFactory
    fun isKnightMove() =
        testRange.flatMap { x ->
            testRange.flatMap { y ->
                knightMoves.map {
                    dynamicTest("#1 $x,$y") {
                        assertTrue(Segment[Node[x, y], Node[x, y] + it].isKnightMove)
                    }
                } + ndMoves.map {
                    dynamicTest("#2 $x,$y") {
                        assertFalse(Segment[Node[x, y], Node[x, y] + it].isKnightMove)
                    }
                }
            }
        }.iterator()

    @TestFactory
    fun getEuclideanDistance() =
        testRange.flatMap { x ->
            testRange.flatMap { y ->
                testRangeNoZero.flatMap { factor ->
                    knightMoves.map {
                        dynamicTest("#1 $x,$y") {
                            assertEquals(
                                abs(factor * KNIGHT_DISTANCE),
                                Segment[Node[x, y], Node[x, y] + it * factor].euclideanDistance,
                                DELTA
                            )
                        }
                    } + normalMoves.map {
                        dynamicTest("#2 $x,$y") {
                            assertEquals(
                                abs(factor * NORMAL_DISTANCE),
                                Segment[Node[x, y], Node[x, y] + it * factor].euclideanDistance,
                                DELTA
                            )
                        }
                    } + diagonalMoves.map {
                        dynamicTest("#3 $x,$y") {
                            assertEquals(
                                abs(factor * DIAGONAL_DISTANCE),
                                Segment[Node[x, y], Node[x, y] + it * factor].euclideanDistance,
                                DELTA
                            )
                        }
                    }
                }
            }
        }.iterator()

    @TestFactory
    fun isConsecutive() =
        testRangeNoZero.flatMap { x1 ->
            testRangeNoZero.flatMap { y1 ->
                testRangeNoZero.flatMap { x2 ->
                    testRangeNoZero.flatMap { y2 ->
                        sequenceOf(
                            dynamicTest("1 true") {
                                assertTrue(Segment[Node[x1, y1], Node[x2, y2]] isConsecutiveTo Segment[Node[x2, y2], Node[0, 0]])
                            },
                            dynamicTest("2 false") {
                                assertFalse(Segment[Node[x2, y2], Node[0, 0]] isConsecutiveTo Segment[Node[x1, y1], Node[x2, y2]])
                            },
                            dynamicTest("3 false") {
                                assertFalse(Segment[Node[x1, y1], Node[x2, y2]] isConsecutiveTo Segment[Node[0, 0], Node[x2, y2]])
                            }
                        )
                    }
                }
            }
        }.iterator()

    @TestFactory
    fun isValidWith() =
        testRangeNoZero.flatMap { x ->
            testRangeNoZero.flatMap { y ->
                allMoves.flatMap {
                    val segment = Segment[Node[x, y], Node[x, y] + it * 2]
                    sequenceOf(
                        dynamicTest("1") {
                            assertFalse(Segment[Node[x, y], Node[x, y] + it] isValidWith emptyList())
                        },
                        dynamicTest("2") {
                            assertTrue(
                                Segment[Node[x, y], Node[x, y] + it] isValidWith listOf(
                                    Node[x, y]
                                )
                            )
                        },
                        dynamicTest("3") {
                            assertTrue(segment isValidWith listOf(Node[x, y], Node[x, y] + it))
                        },
                        dynamicTest("4") {
                            assertFalse(segment isValidWith listOf(Node[x, y]))
                        },
                        dynamicTest("5") {
                            assertFalse(segment isValidWith listOf(Node[x, y] + it))
                        }
                    )
                }
            }
        }.iterator()

    @TestFactory
    fun isValidWithLast() =
        testRangeNoZero.flatMap { x ->
            testRangeNoZero.flatMap { y ->
                allMoves.flatMap {
                    val segment = Segment[Node[x, y], Node[x, y] + it * 2]
                    val consecutive = Segment[Node[x, y] - it, Node[x, y]]
                    sequenceOf(
                        dynamicTest("1") {
                            assertFalse(segment.isValidWith(listOf(Node[x, y], Node[x, y] + it), segment))
                        },
                        dynamicTest("2") {
                            assertTrue(segment.isValidWith(listOf(Node[x, y], Node[x, y] + it), consecutive))
                        },
                        dynamicTest("3") {
                            assertFalse(segment.isValidWith(listOf(Node[x, y]), consecutive))
                        },
                        dynamicTest("4") {
                            assertFalse(segment.isValidWith(listOf(Node[x, y] + it), consecutive))
                        }
                    )
                }
            }
        }.iterator()

    @TestFactory
    fun isTurn() =
        testRangeNoZero.flatMap { x ->
            testRangeNoZero.flatMap { y ->
                normalMoves.flatMap {
                    normalMoves.map { next ->
                        dynamicTest("3") {
                            assertEquals(
                                it !== next,
                                Segment[Node[x, y], Node[x, y] + it] isTurn Segment[Node[x, y] + it, Node[x, y] + it + next]
                            )
                        }
                    } + dkMoves.map { turn ->
                        dynamicTest("3") {
                            assertTrue(Segment[Node[x, y], Node[x, y] + it] isTurn Segment[Node[x, y] + it, Node[x, y] + it + turn])
                        }
                    }
                } + diagonalMoves.flatMap {
                    diagonalMoves.map { next ->
                        dynamicTest("3") {
                            assertEquals(
                                it !== next,
                                Segment[Node[x, y], Node[x, y] + it] isTurn Segment[Node[x, y] + it, Node[x, y] + it + next]
                            )
                        }
                    } + nkMoves.map { turn ->
                        dynamicTest("7") {
                            assertTrue(Segment[Node[x, y], Node[x, y] + it] isTurn Segment[Node[x, y] + it, Node[x, y] + it + turn])
                        }
                    }
                } + knightMoves.flatMap {
                    knightMoves.map { next ->
                        dynamicTest("3") {
                            assertEquals(
                                it !== next,
                                Segment[Node[x, y], Node[x, y] + it] isTurn Segment[Node[x, y] + it, Node[x, y] + it + next]
                            )
                        }
                    } + ndMoves.map { turn ->
                        dynamicTest("11") {
                            assertTrue(Segment[Node[x, y], Node[x, y] + it] isTurn Segment[Node[x, y] + it, Node[x, y] + it + turn])
                        }
                    }
                }
            }
        }.iterator()

    @TestFactory
    fun isSunIntersecting() =
        smallTestRange.flatMap { x ->
            smallTestRange.flatMap { y ->
                sequenceOf(
                    dynamicTest("1 - small x") {
                        assertAll(
                            { assertTrue(Segment[Node[x, y], Node[x + 1, y + 1]] isSunIntersecting Segment[Node[x + 1, y], Node[x, y + 1]]) },
                            { assertTrue(Segment[Node[x, y], Node[x + 1, y + 1]] isSunIntersecting Segment[Node[x, y + 1], Node[x + 1, y]]) },
                            { assertTrue(Segment[Node[x + 1, y + 1], Node[x, y]] isSunIntersecting Segment[Node[x + 1, y], Node[x, y + 1]]) },
                            { assertTrue(Segment[Node[x + 1, y + 1], Node[x, y]] isSunIntersecting Segment[Node[x, y + 1], Node[x + 1, y]]) },
                            { assertTrue(Segment[Node[x + 1, y], Node[x, y + 1]] isSunIntersecting Segment[Node[x, y], Node[x + 1, y + 1]]) },
                            { assertTrue(Segment[Node[x + 1, y], Node[x, y + 1]] isSunIntersecting Segment[Node[x + 1, y + 1], Node[x, y]]) },
                            { assertTrue(Segment[Node[x, y + 1], Node[x + 1, y]] isSunIntersecting Segment[Node[x, y], Node[x + 1, y + 1]]) },
                            { assertTrue(Segment[Node[x, y + 1], Node[x + 1, y]] isSunIntersecting Segment[Node[x + 1, y + 1], Node[x, y]]) },

                            { assertTrue(Segment[Node[x, y], Node[x - 1, y - 1]] isSunIntersecting Segment[Node[x - 1, y], Node[x, y - 1]]) },
                            { assertTrue(Segment[Node[x, y], Node[x - 1, y - 1]] isSunIntersecting Segment[Node[x, y - 1], Node[x - 1, y]]) },
                            { assertTrue(Segment[Node[x - 1, y - 1], Node[x, y]] isSunIntersecting Segment[Node[x - 1, y], Node[x, y - 1]]) },
                            { assertTrue(Segment[Node[x - 1, y - 1], Node[x, y]] isSunIntersecting Segment[Node[x, y - 1], Node[x - 1, y]]) },
                            { assertTrue(Segment[Node[x - 1, y], Node[x, y - 1]] isSunIntersecting Segment[Node[x, y], Node[x - 1, y - 1]]) },
                            { assertTrue(Segment[Node[x - 1, y], Node[x, y - 1]] isSunIntersecting Segment[Node[x - 1, y - 1], Node[x, y]]) },
                            { assertTrue(Segment[Node[x, y - 1], Node[x - 1, y]] isSunIntersecting Segment[Node[x, y], Node[x - 1, y - 1]]) },
                            { assertTrue(Segment[Node[x, y - 1], Node[x - 1, y]] isSunIntersecting Segment[Node[x - 1, y - 1], Node[x, y]]) }
                        )
                    },
                    dynamicTest("2 - big x") {
                        assertAll(
                            { assertTrue(Segment[Node[x, y], Node[x + 2, y + 2]] isSunIntersecting Segment[Node[x + 2, y], Node[x, y + 2]]) },
                            { assertTrue(Segment[Node[x, y], Node[x + 2, y + 2]] isSunIntersecting Segment[Node[x, y + 2], Node[x + 2, y]]) },
                            { assertTrue(Segment[Node[x + 2, y + 2], Node[x, y]] isSunIntersecting Segment[Node[x + 2, y], Node[x, y + 2]]) },
                            { assertTrue(Segment[Node[x + 2, y + 2], Node[x, y]] isSunIntersecting Segment[Node[x, y + 2], Node[x + 2, y]]) },
                            { assertTrue(Segment[Node[x + 2, y], Node[x, y + 2]] isSunIntersecting Segment[Node[x, y], Node[x + 2, y + 2]]) },
                            { assertTrue(Segment[Node[x + 2, y], Node[x, y + 2]] isSunIntersecting Segment[Node[x + 2, y + 2], Node[x, y]]) },
                            { assertTrue(Segment[Node[x, y + 2], Node[x + 2, y]] isSunIntersecting Segment[Node[x, y], Node[x + 2, y + 2]]) },
                            { assertTrue(Segment[Node[x, y + 2], Node[x + 2, y]] isSunIntersecting Segment[Node[x + 2, y + 2], Node[x, y]]) },

                            { assertTrue(Segment[Node[x, y], Node[x - 2, y - 2]] isSunIntersecting Segment[Node[x - 2, y], Node[x, y - 2]]) },
                            { assertTrue(Segment[Node[x, y], Node[x - 2, y - 2]] isSunIntersecting Segment[Node[x, y - 2], Node[x - 2, y]]) },
                            { assertTrue(Segment[Node[x - 2, y - 2], Node[x, y]] isSunIntersecting Segment[Node[x - 2, y], Node[x, y - 2]]) },
                            { assertTrue(Segment[Node[x - 2, y - 2], Node[x, y]] isSunIntersecting Segment[Node[x, y - 2], Node[x - 2, y]]) },
                            { assertTrue(Segment[Node[x - 2, y], Node[x, y - 2]] isSunIntersecting Segment[Node[x, y], Node[x - 2, y - 2]]) },
                            { assertTrue(Segment[Node[x - 2, y], Node[x, y - 2]] isSunIntersecting Segment[Node[x - 2, y - 2], Node[x, y]]) },
                            { assertTrue(Segment[Node[x, y - 2], Node[x - 2, y]] isSunIntersecting Segment[Node[x, y], Node[x - 2, y - 2]]) },
                            { assertTrue(Segment[Node[x, y - 2], Node[x - 2, y]] isSunIntersecting Segment[Node[x - 2, y - 2], Node[x, y]]) }
                        )
                    },
                    dynamicTest("3") {
                        assertTrue(Segment[Node[x, y], Node[x + 1, y + 2]] isSunIntersecting Segment[Node[x, y + 2], Node[x + 1, y + 1]])
                    },
                    dynamicTest("4 - sun only") {
                        assertAll(
                            { assertTrue(Segment[Node[x, y], Node[x + 2, y]] isSunIntersecting Segment[Node[x + 1, y + 1], Node[x + 1, y]]) },
                            { assertTrue(Segment[Node[x, y], Node[x + 2, y]] isSunIntersecting Segment[Node[x + 1, y], Node[x + 1, y + 1]]) },
                            { assertTrue(Segment[Node[x, y + 2], Node[x + 2, y + 2]] isSunIntersecting Segment[Node[x + 1, y + 1], Node[x + 1, y + 2]]) },
                            { assertTrue(Segment[Node[x, y + 2], Node[x + 2, y + 2]] isSunIntersecting Segment[Node[x + 1, y + 2], Node[x + 1, y + 1]]) },

                            { assertTrue(Segment[Node[x + 1, y + 1], Node[x + 1, y]] isSunIntersecting Segment[Node[x, y], Node[x + 2, y]]) },
                            { assertTrue(Segment[Node[x + 1, y], Node[x + 1, y + 1]] isSunIntersecting Segment[Node[x, y], Node[x + 2, y]]) },
                            { assertTrue(Segment[Node[x + 1, y + 1], Node[x + 1, y + 2]] isSunIntersecting Segment[Node[x, y + 2], Node[x + 2, y + 2]]) },
                            { assertTrue(Segment[Node[x + 1, y + 2], Node[x + 1, y + 1]] isSunIntersecting Segment[Node[x, y + 2], Node[x + 2, y + 2]]) }
                        )
                    },
                    dynamicTest("5 - non intersecting parallel") {
                        assertAll(
                            { assertFalse(Segment[Node[x, y], Node[x, y + 1]] isSunIntersecting Segment[Node[x + 1, y], Node[x + 1, y + 1]]) }
                        )
                    },
                    dynamicTest("6 - non intersecting collinear") {
                        assertAll(
                            { assertFalse(Segment[Node[x, y], Node[x, y + 1]] isSunIntersecting Segment[Node[x, y + 2], Node[x, y - 1]]) },
                            { assertFalse(Segment[Node[x, y], Node[x, y + 1]] isSunIntersecting Segment[Node[x, y + 2], Node[x, y + 3]]) }
                        )
                    },
                    dynamicTest("7 - non intersecting t") {
                        assertAll(
                            { assertFalse(Segment[Node[x, y], Node[x + 2, y]] isSunIntersecting Segment[Node[x + 1, y + 2], Node[x + 1, y + 1]]) },
                            { assertFalse(Segment[Node[x, y], Node[x + 2, y]] isSunIntersecting Segment[Node[x + 1, y + 1], Node[x + 1, y + 2]]) },
                            { assertFalse(Segment[Node[x, y + 2], Node[x + 2, y + 2]] isSunIntersecting Segment[Node[x + 1, y], Node[x + 1, y + 1]]) },
                            { assertFalse(Segment[Node[x, y + 2], Node[x + 2, y + 2]] isSunIntersecting Segment[Node[x + 1, y + 1], Node[x + 1, y]]) }
                        )
                    },
                    dynamicTest("8 - non intersecting s") {
                        assertAll(
                            { assertFalse(Segment[Node[x + 1, y + 2], Node[x + 1, y + 1]] isSunIntersecting Segment[Node[x, y], Node[x + 2, y]]) },
                            { assertFalse(Segment[Node[x + 1, y + 1], Node[x + 1, y + 2]] isSunIntersecting Segment[Node[x, y], Node[x + 2, y]]) },
                            { assertFalse(Segment[Node[x + 1, y], Node[x + 1, y + 1]] isSunIntersecting Segment[Node[x, y + 2], Node[x + 2, y + 2]]) },
                            { assertFalse(Segment[Node[x + 1, y + 1], Node[x + 1, y]] isSunIntersecting Segment[Node[x, y + 2], Node[x + 2, y + 2]]) }
                        )
                    }
                ) + allMoves.flatMap {
                    allMoves.map { next ->
                        dynamicTest("9 - non intersecting consecutive") {
                            assertAll(
                                { assertFalse(Segment[Node[x, y], Node[x, y] + it] isSunIntersecting Segment[Node[x, y] + it, Node[x, y] + it + next]) },
                                { assertFalse(Segment[Node[x, y] + it, Node[x, y] + it + next] isSunIntersecting Segment[Node[x, y], Node[x, y] + it]) }
                            )
                        }
                    }
                }
            }
        }.iterator()

    @TestFactory
    fun isSongIntersecting() =
        smallTestRange.flatMap { x ->
            smallTestRange.flatMap { y ->
                sequenceOf(
                    dynamicTest("1 - small x") {
                        assertAll(
                            { assertTrue(Segment[Node[x, y], Node[x + 1, y + 1]] isSongIntersecting Segment[Node[x + 1, y], Node[x, y + 1]]) },
                            { assertTrue(Segment[Node[x, y], Node[x + 1, y + 1]] isSongIntersecting Segment[Node[x, y + 1], Node[x + 1, y]]) },
                            { assertTrue(Segment[Node[x + 1, y + 1], Node[x, y]] isSongIntersecting Segment[Node[x + 1, y], Node[x, y + 1]]) },
                            { assertTrue(Segment[Node[x + 1, y + 1], Node[x, y]] isSongIntersecting Segment[Node[x, y + 1], Node[x + 1, y]]) },
                            { assertTrue(Segment[Node[x + 1, y], Node[x, y + 1]] isSongIntersecting Segment[Node[x, y], Node[x + 1, y + 1]]) },
                            { assertTrue(Segment[Node[x + 1, y], Node[x, y + 1]] isSongIntersecting Segment[Node[x + 1, y + 1], Node[x, y]]) },
                            { assertTrue(Segment[Node[x, y + 1], Node[x + 1, y]] isSongIntersecting Segment[Node[x, y], Node[x + 1, y + 1]]) },
                            { assertTrue(Segment[Node[x, y + 1], Node[x + 1, y]] isSongIntersecting Segment[Node[x + 1, y + 1], Node[x, y]]) }
                        )
                    },
                    dynamicTest("2 - big x") {
                        assertAll(
                            { assertTrue(Segment[Node[x, y], Node[x + 2, y + 2]] isSongIntersecting Segment[Node[x + 2, y], Node[x, y + 2]]) },
                            { assertTrue(Segment[Node[x, y], Node[x + 2, y + 2]] isSongIntersecting Segment[Node[x, y + 2], Node[x + 2, y]]) },
                            { assertTrue(Segment[Node[x + 2, y + 2], Node[x, y]] isSongIntersecting Segment[Node[x + 2, y], Node[x, y + 2]]) },
                            { assertTrue(Segment[Node[x + 2, y + 2], Node[x, y]] isSongIntersecting Segment[Node[x, y + 2], Node[x + 2, y]]) },
                            { assertTrue(Segment[Node[x + 2, y], Node[x, y + 2]] isSongIntersecting Segment[Node[x, y], Node[x + 2, y + 2]]) },
                            { assertTrue(Segment[Node[x + 2, y], Node[x, y + 2]] isSongIntersecting Segment[Node[x + 2, y + 2], Node[x, y]]) },
                            { assertTrue(Segment[Node[x, y + 2], Node[x + 2, y]] isSongIntersecting Segment[Node[x, y], Node[x + 2, y + 2]]) },
                            { assertTrue(Segment[Node[x, y + 2], Node[x + 2, y]] isSongIntersecting Segment[Node[x + 2, y + 2], Node[x, y]]) }
                        )
                    },
                    dynamicTest("3") {
                        assertTrue(Segment[Node[x, y], Node[x + 1, y + 2]] isSongIntersecting Segment[Node[x, y + 2], Node[x + 1, y + 1]])
                    },
                    dynamicTest("4 - sun only") {
                        assertAll(
                            { assertFalse(Segment[Node[x, y], Node[x + 2, y]] isSongIntersecting Segment[Node[x + 1, y + 1], Node[x + 1, y]]) },
                            { assertFalse(Segment[Node[x, y], Node[x + 2, y]] isSongIntersecting Segment[Node[x + 1, y], Node[x + 1, y + 1]]) },
                            { assertFalse(Segment[Node[x, y + 2], Node[x + 2, y + 2]] isSongIntersecting Segment[Node[x + 1, y + 1], Node[x + 1, y + 2]]) },
                            { assertFalse(Segment[Node[x, y + 2], Node[x + 2, y + 2]] isSongIntersecting Segment[Node[x + 1, y + 2], Node[x + 1, y + 1]]) },

                            { assertFalse(Segment[Node[x + 1, y + 1], Node[x + 1, y]] isSongIntersecting Segment[Node[x, y], Node[x + 2, y]]) },
                            { assertFalse(Segment[Node[x + 1, y], Node[x + 1, y + 1]] isSongIntersecting Segment[Node[x, y], Node[x + 2, y]]) },
                            { assertFalse(Segment[Node[x + 1, y + 1], Node[x + 1, y + 2]] isSongIntersecting Segment[Node[x, y + 2], Node[x + 2, y + 2]]) },
                            { assertFalse(Segment[Node[x + 1, y + 2], Node[x + 1, y + 1]] isSongIntersecting Segment[Node[x, y + 2], Node[x + 2, y + 2]]) }
                        )
                    },
                    dynamicTest("5 - non intersecting parallel") {
                        assertAll(
                            { assertFalse(Segment[Node[x, y], Node[x, y + 1]] isSongIntersecting Segment[Node[x + 1, y], Node[x + 1, y + 1]]) }
                        )
                    },
                    dynamicTest("6 - non intersecting collinear") {
                        assertAll(
                            { assertFalse(Segment[Node[x, y], Node[x, y + 1]] isSongIntersecting Segment[Node[x, y + 2], Node[x, y - 1]]) },
                            { assertFalse(Segment[Node[x, y], Node[x, y + 1]] isSongIntersecting Segment[Node[x, y + 2], Node[x, y + 3]]) }
                        )
                    },
                    dynamicTest("7 - non intersecting t") {
                        assertAll(
                            { assertFalse(Segment[Node[x, y], Node[x + 2, y]] isSongIntersecting Segment[Node[x + 1, y + 2], Node[x + 1, y + 1]]) },
                            { assertFalse(Segment[Node[x, y], Node[x + 2, y]] isSongIntersecting Segment[Node[x + 1, y + 1], Node[x + 1, y + 2]]) },
                            { assertFalse(Segment[Node[x, y + 2], Node[x + 2, y + 2]] isSongIntersecting Segment[Node[x + 1, y], Node[x + 1, y + 1]]) },
                            { assertFalse(Segment[Node[x, y + 2], Node[x + 2, y + 2]] isSongIntersecting Segment[Node[x + 1, y + 1], Node[x + 1, y]]) }
                        )
                    },
                    dynamicTest("8 - non intersecting s") {
                        assertAll(
                            { assertFalse(Segment[Node[x + 1, y + 2], Node[x + 1, y + 1]] isSongIntersecting Segment[Node[x, y], Node[x + 2, y]]) },
                            { assertFalse(Segment[Node[x + 1, y + 1], Node[x + 1, y + 2]] isSongIntersecting Segment[Node[x, y], Node[x + 2, y]]) },
                            { assertFalse(Segment[Node[x + 1, y], Node[x + 1, y + 1]] isSongIntersecting Segment[Node[x, y + 2], Node[x + 2, y + 2]]) },
                            { assertFalse(Segment[Node[x + 1, y + 1], Node[x + 1, y]] isSongIntersecting Segment[Node[x, y + 2], Node[x + 2, y + 2]]) }
                        )
                    }
                ) + allMoves.flatMap {
                    allMoves.map { next ->
                        dynamicTest("9 - non intersecting consecutive") {
                            assertAll(
                                { assertFalse(Segment[Node[x, y], Node[x, y] + it] isSongIntersecting Segment[Node[x, y] + it, Node[x, y] + it + next]) },
                                { assertFalse(Segment[Node[x, y] + it, Node[x, y] + it + next] isSongIntersecting Segment[Node[x, y], Node[x, y] + it]) }
                            )
                        }
                    }
                }
            }
        }.iterator()

    @TestFactory
    fun isOverlapping() =
        testRangeNoZero.flatMap { x ->
            testRangeNoZero.flatMap { y ->
                allMoves.flatMap {
                    sequenceOf(
                        dynamicTest("1") {
                            assertFalse(Segment[Node[x, y], Node[x, y] + it] isOverlapping Node[x, y])
                        },
                        dynamicTest("2") {
                            assertFalse(Segment[Node[x, y] + it, Node[x, y]] isOverlapping Node[x, y])
                        },
                        dynamicTest("3") {
                            assertTrue(Segment[Node[x, y] - it, Node[x, y] + it] isOverlapping Node[x, y])
                        }
                    )
                }
            }
        }.iterator()

    @TestFactory
    fun isOverlapping1() =
        (testRange.flatMap { x ->
            testRange.flatMap { y ->
                sequenceOf(
                    dynamicTest("1") {
                        assertFalse(Segment[Node[x, y], Node[x + 1, y]] isOverlapping Segment[Node[x + 2, y], Node[x + 2, y + 1]])
                    },
                    dynamicTest("2") {
                        assertFalse(Segment[Node[x + 1, y], Node[x + 1, y + 2]] isOverlapping Segment[Node[x, y + 1], Node[x + 1, y + 1]])
                    },
                    dynamicTest("3 parallel") {
                        assertFalse(Segment[Node[x + 1, y], Node[x + 2, y]] isOverlapping Segment[Node[x + 1, y + 1], Node[x + 2, y + 1]])
                    },
                    dynamicTest("4 collinear") {
                        assertFalse(Segment[Node[x, y], Node[x + 1, y]] isOverlapping Segment[Node[x + 1, y], Node[x + 2, y]])
                    },
                    dynamicTest("5 single point") {
                        assertFalse(Segment[Node[x, y], Node[x + 2, y + 2]] isOverlapping Segment[Node[x + 1, y + 1], Node[x + 1, y + 1]])
                    },
                    dynamicTest("6") {
                        assertTrue(Segment[Node[x, y], Node[x, y + 1]] isOverlapping Segment[Node[x, y], Node[x, y + 1]])
                    },
                    dynamicTest("7") {
                        assertTrue(Segment[Node[x, y + 1], Node[x + 2, y + 1]] isOverlapping Segment[Node[x, y + 1], Node[x + 1, y + 1]])
                    },
                    dynamicTest("8") {
                        assertTrue(Segment[Node[x, y], Node[x + 2, y + 2]] isOverlapping Segment[Node[x + 1, y + 1], Node[x + 2, y + 2]])
                    }
                ) + allMoves.flatMap {
                    sequenceOf(
                        dynamicTest("9") {
                            assertFalse(Segment[Node[x, y], Node[x, y] + it] isOverlapping Segment[Node[x, y] + it, Node[x, y] + it * 2])
                        },
                        dynamicTest("10") {
                            assertTrue(Segment[Node[x, y], Node[x, y] + it * 2] isOverlapping Segment[Node[x, y] + it, Node[x, y] + it * 2])
                        },
                        dynamicTest("11") {
                            assertTrue(Segment[Node[x, y], Node[x, y] + it * 2] isOverlapping Segment[Node[x, y] + it, Node[x, y] + it * 3])
                        }
                    )
                }
            }
        } + dynamicTest("12") {
            assertTrue(Segment[Node[1, 1], Node[0, 2]] isOverlapping Segment[Node[0, 2], Node[2, 0]])
        }).iterator()
}