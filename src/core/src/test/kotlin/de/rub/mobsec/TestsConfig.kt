package de.rub.mobsec

const val DELTA = 0.0000000000001
const val NORMAL_DISTANCE = 1.0
const val DIAGONAL_DISTANCE = 1.414213562373095
const val KNIGHT_DISTANCE = 2.23606797749979

val normalMoves = sequenceOf(Node[1, 0], Node[0, 1], Node[-1, 0], Node[0, -1])
val diagonalMoves = sequenceOf(Node[1, 1], Node[-1, 1], Node[1, -1], Node[-1, -1])
val knightMoves = sequenceOf(
    Node[1, 2],
    Node[-1, 2],
    Node[1, -2],
    Node[-1, -2],
    Node[2, 1],
    Node[2, -1],
    Node[-2, 1],
    Node[-2, -1]
)

val ndMoves = normalMoves + diagonalMoves
val nkMoves = normalMoves + knightMoves
val dkMoves = diagonalMoves + knightMoves

val allMoves = normalMoves + diagonalMoves + knightMoves

val smallTestRange = (-2..2).asSequence()
val testRange = (-3..3).asSequence()
val testRangeNoZero = testRange.filter { it != 0 }
val positiveTestRange = (1..5).asSequence()

val map = AndroidUnlockGrid[3]
