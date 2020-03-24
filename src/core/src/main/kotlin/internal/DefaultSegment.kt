package de.rub.mobsec.internal

import de.rub.mobsec.Node
import de.rub.mobsec.Segment

private val KNIGHT_MOVES = hashSetOf(
    Node[1, 2],
    Node[-1, 2],
    Node[1, -2],
    Node[-1, -2],
    Node[2, 1],
    Node[2, -1],
    Node[-2, 1],
    Node[-2, -1]
)

internal data class DefaultSegment(
    override val a: Node,
    override val b: Node
) : Segment {
    override val direction = b - a

    override val maximumNorm = direction.maxHorizontalOrVerticalDistance

    override val nodesBetweenAB by lazy {
        val result = mutableSetOf<Node>()

        val (minimizedMove, gdc) = direction.minimize()

        (1 until gdc).forEach {
            result += a + minimizedMove * it
        }

        result.toSet()
    }

    override val isSinglePoint = a === b

    override val isKnightMove = direction in KNIGHT_MOVES

    override val euclideanDistance = direction.euclideanDistance

    override fun isConsecutiveTo(other: Segment) = b === other.a

    override fun isValidWith(visitedNodes: List<Node>) =
        visitedNodes.containsAll(nodesBetweenAB) && a in visitedNodes && b !in visitedNodes

    override fun isValidWith(visitedNodes: List<Node>, last: Segment) =
        isValidWith(visitedNodes) && last isConsecutiveTo this

    override fun isTurn(other: Segment) = direction !== other.direction

    override fun isSunIntersecting(other: Segment): Boolean {
        if (this isConsecutiveTo other)
            return false
        if (other isConsecutiveTo this)
            return false

        val w = a - other.a
        val determinant = direction.determinant(other.direction)

        if (determinant == 0) // Segments are parallel or collinear (det == 0).
            return false

        val sI = (other.direction.determinant(w).toDouble() / determinant)
        if (sI < 0.0)
            return false
        if (sI > 1.0)
            return false

        val tI = (direction.determinant(w).toDouble() / determinant)
        if (tI < 0.0)
            return false
        if (tI > 1.0)
            return false

        return true
    }

    override fun isSongIntersecting(other: Segment): Boolean {
        if (this isConsecutiveTo other)
            return false
        if (other isConsecutiveTo this)
            return false

        val w = a - other.a
        val determinant = direction.determinant(other.direction)

        if (determinant == 0) // Segments are parallel or collinear (det == 0).
            return false

        val sI = (other.direction.determinant(w).toDouble() / determinant)
        if (sI <= 0.0)
            return false
        if (sI >= 1.0)
            return false

        val tI = (direction.determinant(w).toDouble() / determinant)
        if (tI <= 0.0)
            return false
        if (tI >= 1.0)
            return false

        return true
    }

    override fun isOverlapping(other: Segment) =
        direction.minimize().first.abs() === other.direction.minimize().first.abs() &&
                (nodesBetweenAB + a + b)
                    .asSequence()
                    .filter { it == other.a || it == other.b || it in other.nodesBetweenAB }
                    .count() >= 2

    override fun isOverlapping(node: Node) = node in nodesBetweenAB
}