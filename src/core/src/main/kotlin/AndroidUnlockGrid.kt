package de.rub.mobsec

import de.rub.mobsec.internal.DefaultAndroidUnlockGrid

/**
 * A representation for an android unlock grid.
 *
 * The default android grid is a 3x3 matrix.
 *
 * [Node] index starts at `0`.
 */
interface AndroidUnlockGrid {
    /**
     * @return true only if the androidUnlockGrid contains this [node], false otherwise.
     */
    operator fun contains(node: Node): Boolean

    /**
     * @return the node from the [index].
     */
    operator fun get(index: Int): Node

    /**
     * @return the index of the [node] or -1 if the node is not in this androidUnlockGrid.
     */
    operator fun get(node: Node): Int

    companion object {
        /**
         * @param cols number of grid columns
         * @param rows number of grid rows
         *
         * @return a new instance with [cols] and [rows].
         */
        operator fun get(cols: Int = 3, rows: Int = cols): AndroidUnlockGrid =
            DefaultAndroidUnlockGrid(cols, rows)
    }
}