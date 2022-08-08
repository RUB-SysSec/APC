package de.rub.mobsec

import de.rub.mobsec.internal.DefaultAndroidUnlockPattern

/**
 * A representation for an android unlock pattern.
 *
 * Normally a pattern string is in the form of "0.1.2.3", where the numbers represent indexes in an [AndroidUnlockGrid]
 * and the `.` is the delimiter.
 *
 * An implementation of this interface SHOULD be linked permanently to a single instance of type AndroidUnlockGrid,
 * since most properties of this interface would change in value if the grid changes.
 */
interface AndroidUnlockPattern {
    /**
     * Returns `true` only if this pattern is valid, `false` otherwise.
     *
     * A pattern for a 3x3 (default size) [grid][AndroidUnlockGrid] is only valid if all the
     * following statements are true:
     * * minimal length is 4 [nodes][Node] (inclusive)
     * * each node can only be included once in a pattern (it can be revisited)
     * * a [segment][Segment] does not skip over a node in the gird, except if it revisits it
     *
     * Example of a valid patterns:
     * ```
     * "0.1.2.3"
     * "3.4.6.0.8"
     * ```
     * Example of invalid patterns:
     * ```
     * "0.1.2"   // to short
     * "0.1.0.4" // nodes included more than once
     * "0.2.3.4" // skipped node at index 1 without revisiting it
     * ```
     *
     * @see [numberOfRevisitedNodes] for a definition of revisited nodes.
     */
    val isValid: Boolean

    /**
     * Returns the index of this patterns starting [node][Node]. The index is defined by the used
     * [grid][AndroidUnlockGrid].
     */
    val startNodeIndex: Int

    /**
     * Returns the length of this pattern in [nodes][Node].
     *
     * The length is defined as the number of nodes in a pattern.
     *
     * The minimum node length is 4.
     * The maximum node length is 9.
     *
     * Some Examples:
     *
     * * 0.1.2.5 has 4 nodes, thus `nodeLength = 4`
     * * 0.1.2.5.4 has 5 nodes, thus `nodeLength = 5`
     * * 4.0.8.7.1.2.5.6.3 has 9 nodes, thus `nodeLength = 9`
     */
    val nodeLength: Int

    /**
     * Returns the number of non-consecutive intersecting [segments][Segment], as defined by Sun et al.
     *
     * Sun counts all intersections, with no regards to where these intersections happen. We call this the intuitive way
     * of counting intersections. Intersections are independent of a segments' direction. The number of intersections is
     * increased by one, if two segments intersect. The maximum number of intersection is 18.
     *
     * Some examples with two segments:
     *
     * * `0.4` and `1.3` do intersect (they form a cross with 90deg angles), `numberOfSunIntersections = 1`
     * * `0.4` and `1.2` do NOT intersect, `numberOfSunIntersections = 0`
     * * `1.4` and `0.2`  do intersect (the intersection is exactly on node 1), `numberOfSunIntersections = 1`
     */
    val numberOfSunIntersections: Int

    /**
     * Returns the number of non-consecutive intersecting [segments][Segment], as defined by Song et al.
     *
     * Song does NOT count all intersections, they omit intersections that happen directly on the end of a line
     * segment. Intersections are independent of a segments' direction. The number of intersections is increased by one,
     * if two segments intersect. The maximum number of intersection is 15.
     *
     * Some examples with two segments:
     *
     * * `0.4` and `1.3` do intersect (they form a cross with 90deg angles), `numberOfSongIntersections = 1`
     * * `0.4` and `1.2` do NOT intersect, `numberOfSongIntersections = 0`
     * * `1.4` and `0.2`  do NOT intersect (Sun counts these intersections), `numberOfSongIntersections = 0`
     */
    val numberOfSongIntersections: Int

    /**
     * Returns the number of revisited [nodes][Node].
     *
     * The number is increased by one each time a node is revisited. This means that if a node is revisited twice, the
     * number is increased by two in total.
     *
     * Some Examples:
     *
     * The pattern `1.0.2` has one revisited node at index 1, `numberOfRevisitedNodes = 1`:
     * ```
     * 0 <-- 1     2
     *   -------->
     * ```
     *
     * The pattern `0.4.8.1.7` has one node at index 4, which is revisited two times, `numberOfRevisitedNodes = 2`.
     *
     * The pattern `7.4.1.0.2.6` has two revisited nodes at index 1 and 4, `numberOfRevisitedNodes = 2`.
     *
     * The maximum number of revisited nodes is 5, for example the pattern `4.1.7.0.2.6.8.3.5`.
     */
    val numberOfRevisitedNodes: Int

    /**
     * Returns the number of overlapping [segments][Segment].
     *
     * The number is increased by one when two segments overlap, thus only counting the overlaps, not the segments
     * involved by them.
     *
     * For example the pattern "1.0.2" has one overlap:
     * ```
     * 0 <-- 1     2
     *   -------->
     * ```
     *
     * The maximum number of overlapping segments is 3, for example the pattern `1.0.2.3.4.5.7.6.8`.
     */
    val numberOfOverlappingSegments: Int

    /**
     * Returns the number of [segments][Segment] that are classified as a knight move.
     *
     * A segment is a knight direction if its [direction][Segment.direction] either has a vertical direction of 1 and a
     * horizontal direction of 2 or, a vertical direction of 2 and a horizontal direction of 1.
     *
     * This leads to 8 possible knight moves:
     * ```
     * (1, 2), (-1, 2), (1, -2), (-1, -2)
     * (2, 1), (2, -1), (-2, 1), (-2, -1)
     * ```
     */
    val numberOfKnightMoves: Int

    /**
     * Returns the number of direction changes of consecutive [segments][Segment].
     *
     * A direction change occurs if the [directions][Segment.direction] of two consecutive segments are not the same.
     */
    val numberOfDirectionChanges: Int

    /**
     * Returns the sum of all [segments euclidean distance][Segment.euclideanDistance].
     */
    val sumOfEuclideanDistances: Double

    /**
     * Returns the sum of all [segments maximum norm][Segment.maximumNorm].
     */
    val sumOfMaximumNorms: Int

    /**
     * Returns the ratio of non-repeated [segments][Segment].
     *
     * Since we are not exactly sure what this means and the author of the paper did not answer our questions we assume
     * that it means:
     *
     * The ratio of |unique segment absolute directions| to |P|.
     *
     * Segment direction is described as a vector, assuming the upper left corner is (0,0).
     *
     * Some examples directions:
     *
     * * The segment 0.1 has absolute direction (1,0)
     * * The segment 0.3 has absolute direction (0,1)
     * * The segment 4.0 has absolute direction (1,1)
     * * The segment 6.5 has absolute direction (2,1)
     *
     * Some full examples:
     *
     * * The pattern 0.3.6.1.4.7.8 has 3 unique directions and 6 segments = 3/6 = 1/2
     * * The pattern 0.1.2.4.6.7.8 has 2 unique directions and 6 segments = 2/6 = 1/3
     * * The pattern 4.0.3.5 has 3 unique directions and 3 segments = 3/3 = 1
     *
     */
    val ratioOfNonRepeatedSegments: Double

    /**
     * Returns the ratio of non-repeated [segments][Segment].
     *
     * This is an alternate version for [ratioOfNonRepeatedSegments]. It uses the longest palindrome of segment
     * directions instead of unique segment directions:
     *
     * (|P| - |longest palindrome of segment directions|) / |P|
     *
     * See [ratioOfNonRepeatedSegments] for the definition of segment directions.
     *
     * Some examples:
     * ```
     * | pattern       | segments | palindrome | ratio             |
     * |---------------|----------|------------|-------------------|
     * | 0.3.6.1.4.7.8 | 6        | 5          | (6-5)/6 = 1/6     |
     * | 0.1.2.4.6.7.8 | 6        | 6          | (6-6)/6 = 0/6 = 0 |
     * | 4.0.3.5       | 3        | 1          | (3-1)/3 = 2/3     |
     * ```
     *
     * As one can see the problem here is that the minimal longest palindrome is 1, thus never allowing the highest
     * score of 1.
     */
    val ratioOfNonRepeatedSegmentsWithPalindrome: Double

    companion object {
        /**
         * @param grid which the pattern is connected to.
         * @param patternString string that resembles an android unlock pattern.
         * @param delimiter which delimits the indexes in the [patternString]. (defaults to `"."`)
         *
         * @return a new instance with the given parameters.
         */
        operator fun get(
            grid: AndroidUnlockGrid,
            patternString: String,
            delimiter: String = "."
        ): AndroidUnlockPattern =
            DefaultAndroidUnlockPattern(
                patternString.toSegmentList(grid, delimiter),
                patternString,
                grid
            )

        /**
         * @receiver android unlock pattern as [String]
         * @param androidUnlockGrid
         * @param delimiter used to split the receiver
         *
         * @return a list of [segments][Segment].
         *
         * @throws NumberFormatException if this string is not a (delimited) sequence of [integers][Int].
         */
        private fun String.toSegmentList(androidUnlockGrid: AndroidUnlockGrid, delimiter: String) =
            this.splitToSequence(delimiter)
                .filter(String::isNotEmpty)
                .map { androidUnlockGrid[it.toInt()] }
                .zipWithNext { a, b -> Segment[a, b] }
                .toList()
    }
}