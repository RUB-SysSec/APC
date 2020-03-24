package de.rub.mobsec.internal

import de.rub.mobsec.Node
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

internal class DefaultNode(
    override val x: Int,
    override val y: Int
) : Node {
    private val internalToString = "($x,$y)"
    private val internalHashCode = internalToString.hashCode()
    private val gcd by lazy { gcd(abs(x), abs(y)) }

    private tailrec fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)

    override val maxHorizontalOrVerticalDistance = max(abs(x), abs(y))
    override val euclideanDistance = sqrt((x * x + y * y).toDouble())

    override operator fun plus(other: Node) = Node[x + other.x, y + other.y]
    override operator fun minus(other: Node) = Node[x - other.x, y - other.y]

    override operator fun times(scalar: Int) = Node[x * scalar, y * scalar]
    override operator fun div(scalar: Int) = Node[x / scalar, y / scalar]

    override fun abs() = Node[abs(x), abs(y)]

    override fun minimize() = (if (gcd > 1) this / gcd else this) to gcd

    override fun determinant(other: Node) = x * other.y - y * other.x

    override fun equals(other: Any?) = this === other

    override fun hashCode() = internalHashCode

    override fun toString() = internalToString
}