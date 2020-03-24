package de.rub.mobsec

import de.rub.mobsec.internal.DefaultSegment

/**
 * A representation for a line segment in an [AndroidUnlockGrid].
 *
 * A line connecting two [nodes][Node] is considered a line segment.
 */
interface Segment {
    /**
     * Returns the [starting node][Node] of a [segment][Segment].
     */
    val a: Node

    /**
     * Returns the [ending node][Node] of a [segment][Segment].
     */
    val b: Node

    /**
     * Returns the direction of a [segment][Segment].
     *
     * Since [a] is the start and [b] the end of a segment, the direction MUST be calculated with:
     * ```
     * direction = b - a
     * ```
     */
    val direction: Node

    /**
     * Returns the maximum norm of [direction].
     *
     * The maximum norm is defined as the absolute maximum of [direction] x/y coordinate:
     *
     * `maximum norm = max{direction.x, direction.y}`
     *
     * Song calls this "Pattern Length".
     */
    val maximumNorm: Int

    /**
     * Returns a set of nodes that are between this segments start [a] and end [b] (both exclusive).
     *
     * This is used in the calculation of what Andriotis calls "overlapping nodes".
     */
    val nodesBetweenAB: Set<Node>

    /**
     * Returns `true` only if [a] === [b], `false` otherwise.
     */
    val isSinglePoint: Boolean

    /**
     * Returns `true` only if [direction] is a knight move, `false` otherwise.
     *
     * The following directions are valid knight moves:
     * ```
     * Node[1, 2], Node[-1, 2], Node[1, -2], Node[-1, -2],
     * Node[2, 1], Node[2, -1], Node[-2, 1], Node[-2, -1]
     * ```
     *
     * Knight move refers to a move in chess.
     */
    val isKnightMove: Boolean

    /**
     * Returns the euclidean distance of this segments [direction].
     */
    val euclideanDistance: Double

    /**
     * Returns `true` only if the other segment follows this one, `false` otherwise.
     *
     * This means that the ending node of this segment is the same as the starting node of the other segment.
     *
     * @param other segment
     *
     * @return `true` only if this segments [b] === other segments [a], `false` otherwise.
     */
    infix fun isConsecutiveTo(other: Segment): Boolean

    /**
     * This segment is valid if:
     * 1. [nodesBetweenAB] are all in [visitedNodes]
     * 2. [a] is in [visitedNodes]
     * 3. [b] is **not** in [visitedNodes]
     * 4. [a] !== [b] (this is given by 2. and 3.)
     *
     * @param visitedNodes list of already visited nodes in a pattern.
     *
     * @return `true` only if this is a valid **first** segment of a [pattern][AndroidUnlockPattern] with
     * [visitedNodes], `false` otherwise.
     */
    infix fun isValidWith(visitedNodes: List<Node>): Boolean

    /**
     * This segment is valid if:
     * 1. [nodesBetweenAB] are all in [visitedNodes]
     * 2. [a] is in [visitedNodes]
     * 3. [b] is **not** in [visitedNodes]
     * 4. [a] !== [b] (this is given by 2. and 3.)
     * 5. this [is consecutive to][isConsecutiveTo] last segment
     *
     * @param visitedNodes list of already visited nodes in a pattern.
     * @param last segment of a pattern (read: the one before this one).
     *
     * @return `true` only if this is a valid segment of a [pattern][AndroidUnlockPattern] with
     * [visitedNodes] and [last], `false` otherwise.
     */
    fun isValidWith(visitedNodes: List<Node>, last: Segment): Boolean

    /**
     * @param other segment
     *
     * @return `true` only if this and [other] forms a turn (direction change), `false` otherwise.
     */
    infix fun isTurn(other: Segment): Boolean

    /**
     * A segment intersects another one if the lines one can draw between [a] an [b] of each segment are intersecting.
     *
     * @param other segment
     *
     * @return `true` only if this segment intersects the [other] segment, `false` otherwise.
     */
    infix fun isSunIntersecting(other: Segment): Boolean

    /**
     * A segment intersects another one if the lines one can draw between [a] an [b] of each segment are intersecting.
     *
     * @param other segment
     *
     * @return `true` only if this segment intersects the [other] segment, `false` otherwise.
     */
    infix fun isSongIntersecting(other: Segment): Boolean

    /**
     * @param other segment
     *
     * @return `true` only if this segment overlaps the [other] segment, `false` otherwise.
     */
    infix fun isOverlapping(other: Segment): Boolean

    /**
     * @param node to test overlapping.
     *
     * @return `true` only if [node] is in [nodesBetweenAB], `false` otherwise.
     */
    infix fun isOverlapping(node: Node): Boolean

    companion object {
        /**
         * The internal [Segment] cache.
         */
        private val cache = HashMap<Pair<Node, Node>, Segment>()

        /**
         * This method caches newly created segments and returns the cached ones on subsequent calls.
         *
         * @param a starting node
         * @param b ending node
         *
         * @return a new [Segment] with [a] and [b].
         */
        operator fun get(a: Node, b: Node): Segment = cache.getOrPut(a to b) { DefaultSegment(a, b) }
    }
}