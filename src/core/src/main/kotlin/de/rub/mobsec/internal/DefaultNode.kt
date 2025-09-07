package de.rub.mobsec.internal

import de.rub.mobsec.Node
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

internal class DefaultNode(
    override val x: Int,
    override val y: Int,
) : Node {
    private val internalToString = "($x,$y)"
    private val internalHashCode = internalToString.hashCode()
    private val gcd by lazy { gcd(abs(x), abs(y)) }

    private tailrec fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)

    override val maxHorizontalOrVerticalDistance = max(abs(x), abs(y))
    override val euclideanDistance = sqrt((x * x + y * y).toDouble())

    override fun minimize() = (if (gcd > 1) this / gcd else this) to gcd

    override fun equals(other: Any?) = this === other

    override fun hashCode() = internalHashCode

    override fun toString() = internalToString
}
