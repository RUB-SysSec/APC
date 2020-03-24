package de.rub.mobsec.internal

import de.rub.mobsec.AndroidUnlockGrid
import de.rub.mobsec.Node

internal data class DefaultAndroidUnlockGrid(
    private val cols: Int,
    private val rows: Int
) : AndroidUnlockGrid {

    init {
        require(cols >= 1)
        require(rows >= 1)
    }

    private val map = Array(rows) { row -> Array(cols) { col -> Node[col, row] } }

    private val indexedMapPoints = map.flatMap { it.asList() }

    private val mapPoints = LinkedHashSet(indexedMapPoints)

    override operator fun contains(node: Node) = node in mapPoints

    override operator fun get(index: Int) = indexedMapPoints.getOrElse(index) { Node[-1, -1] }

    override operator fun get(node: Node) = indexedMapPoints.indexOf(node)
}