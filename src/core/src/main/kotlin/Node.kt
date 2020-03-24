package de.rub.mobsec

import de.rub.mobsec.internal.DefaultNode

/**
 * A representation for a node in an [AndroidUnlockGrid].
 *
 * In Android a node is visualized as a dot, and depending on the Android version a circle border around it.
 */
interface Node {
    /**
     * Returns the x coordinate of this node.
     */
    val x: Int

    /**
     * Returns the y coordinate of this node.
     */
    val y: Int

    /**
     * Returns the greater one of [x] and [y].
     */
    val maxHorizontalOrVerticalDistance: Int

    /**
     * Returns the euclidean distance.
     *
     * The euclidean distance is defined as:
     * ```
     * sqrt(x^2 + y^2)
     * ```
     */
    val euclideanDistance: Double

    /**
     * @param other node
     *
     * @return the node resulting by adding x/y coordinates respectively.
     */
    operator fun plus(other: Node): Node

    /**
     * @param other node
     *
     * @return the node resulting by subtracting x/y coordinates respectively.
     */
    operator fun minus(other: Node): Node

    /**
     * @param scalar integer
     *
     * @return the node resulting by multiplying x/y with [scalar].
     */
    operator fun times(scalar: Int): Node

    /**
     * @param scalar integer
     *
     * @return the node resulting by dividing x/y with [scalar].
     */
    operator fun div(scalar: Int): Node

    /**
     * @return the absolute version of this node.
     */
    fun abs(): Node

    /**
     * @return a node with [x] and [y] divided by their gcd and the gcd.
     */
    fun minimize(): Pair<Node, Int>

    /**
     * The determinant is defined as:
     * ```
     * x1 * y2 - y1 * x2
     * ```
     *
     * @param other [Node].
     *
     * @return the determinant with [other].
     */
    fun determinant(other: Node): Int

    companion object {
        /**
         * The internal [Node] cache.
         */
        private val cache = HashMap<String, Node>()

        /**
         * This method caches newly created nodes and returns the cached ones on subsequent calls.
         *
         * @param x coordinate
         * @param y coordinate
         *
         * @return a [Node] with [x] and [y].
         */
        operator fun get(x: Int, y: Int): Node = cache.getOrPut("$x,$y") { DefaultNode(x, y) }
    }
}