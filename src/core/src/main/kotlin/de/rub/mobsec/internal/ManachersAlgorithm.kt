package de.rub.mobsec.internal

import de.rub.mobsec.Node

/**
 * Code copied from wikipedia:
 * ```
 * https://en.wikipedia.org/wiki/Longest_palindromic_substring
 * ```
 *
 * Modified to support a list of [nodes][Node].
 */
internal object ManachersAlgorithm {
    private fun addBoundaries(nodes: List<Node>) =
        List(nodes.size * 2 + 1) { index -> if (index % 2 == 0) Node[0, 0] else nodes[index / 2] }

    private fun removeBoundaries(nodes: List<Node>) =
        List((nodes.size - 1) / 2) { index -> nodes[index * 2 + 1] }

    fun computeLongestPalindrome(s: List<Node>): List<Node> {
        val s2 = addBoundaries(s)
        val p = IntArray(s2.size)
        var c = 0
        var r = 0 // Here the first element in s2 has been processed.
        var m: Int
        var n = 0 // The walking indices to compare if two elements are the same
        for (i in 1 until s2.size) {
            if (i > r) {
                p[i] = 0
                m = i - 1
                n = i + 1
            } else {
                val i2 = c * 2 - i
                if (p[i2] < r - i - 1) {
                    p[i] = p[i2]
                    m = -1 // This signals bypassing the while loop below.
                } else {
                    p[i] = r - i
                    n = r + 1
                    m = i * 2 - n
                }
            }
            while (m >= 0 && n < s2.size && s2[m] == s2[n]) {
                p[i]++
                m--
                n++
            }
            if (i + p[i] > r) {
                c = i
                r = i + p[i]
            }
        }
        var len = 0
        c = 0
        for (i in 1 until s2.size) {
            if (len < p[i]) {
                len = p[i]
                c = i
            }
        }
        return removeBoundaries(s2.subList(c - len, c + len + 1))
    }
}
