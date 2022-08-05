@file:Suppress("NOTHING_TO_INLINE")

package de.rub.mobsec

import kotlin.math.log2
import kotlin.math.min

/**
 * Sealed class for android unlock pattern strength meter.
 */
sealed class StrengthMeter {
    /**
     * The returned score is not normalized, see the implementations for min/max values of valid patterns.
     *
     * @param pattern to analyse.
     *
     * @return the score of [pattern].
     */
    abstract fun computeScore(pattern: AndroidUnlockPattern): Double

    /**
     * The returned score is normalized to be in range of 0 to 1.
     *
     * @param pattern to analyse.
     *
     * @return the normalized score of [pattern].
     */
    abstract fun computeNormalizedScore(pattern: AndroidUnlockPattern): Double

    /**
     * The returned score is quantized according to their paper.
     *
     * @param pattern to analyse.
     *
     * @return the quantized score of [pattern].
     */
    abstract fun computeQuantizedScore(pattern: AndroidUnlockPattern): Int

    /**
     * The returned score is quantized according to their paper, but is normalized to range from [1,5].
     *
     * This means with the actual quantization has 5 steps nothing changes and if it has 3 steps the values are changed
     * to 1 -> 1, 2 -> 3, and 3 -> 5, resulting in 1,3,5.
     *
     * @param pattern to analyse.
     *
     * @return the quantized score of [pattern].
     */
    abstract fun computeQuantizedNormalizedScore(pattern: AndroidUnlockPattern): Int
}

/**
 * Based on the paper:
 *
 * **Complexity Metrics and User Strength Perceptions of the Pattern-Lock Graphical Authentication Method**
 *
 * This meter only returns integers.
 *
 * Minimal score value: `0`
 *
 * Maximal score value: `13`
 */
object AndriotisMeter : StrengthMeter() {

    inline fun startNode(pattern: AndroidUnlockPattern) = if (pattern.startNodeIndex != 0) 1 else 0
    inline fun length(pattern: AndroidUnlockPattern) = if (pattern.nodeLength >= 6) pattern.nodeLength - 5 else 0
    inline fun directionChanges(pattern: AndroidUnlockPattern) = if (pattern.numberOfDirectionChanges >= 2) 1 else 0

    override fun computeScore(pattern: AndroidUnlockPattern): Double {
        val x1 = startNode(pattern)
        val x2 = length(pattern)
        val x3 = directionChanges(pattern)
        val x4 = pattern.numberOfKnightMoves
        val x5 = pattern.numberOfRevisitedNodes

        return (x1 + x2 + x3 + x4 + x5).toDouble()
    }

    private const val MAX_SCORE = 13.0

    override fun computeNormalizedScore(pattern: AndroidUnlockPattern): Double = computeScore(pattern) / MAX_SCORE

    override fun computeQuantizedScore(pattern: AndroidUnlockPattern): Int =
        when (computeScore(pattern).toInt()) {
            0, 1 -> 1
            2 -> 2
            else -> 3
        }

    override fun computeQuantizedNormalizedScore(pattern: AndroidUnlockPattern): Int =
        when (computeScore(pattern).toInt()) {
            0, 1 -> 1
            2 -> 3
            else -> 5
        }
}

/**
 * Based on the paper:
 *
 * **Dissecting pattern unlock: The effect of pattern strength meter on pattern selection**
 *
 * Minimal score value: `6.339850002884625`
 *
 * Maximal score value: `46.80738907682584`
 */
object SunMeter : StrengthMeter() {
    override fun computeScore(pattern: AndroidUnlockPattern) =
        pattern.nodeLength *
                log2(
                    pattern.sumOfEuclideanDistances +
                            pattern.numberOfSunIntersections +
                            pattern.numberOfOverlappingSegments
                )

    private const val MIN_SCORE = 6.339850002884625
    private const val MAX_SCORE = 46.80738907682584 - MIN_SCORE

    override fun computeNormalizedScore(pattern: AndroidUnlockPattern): Double =
        (computeScore(pattern) - MIN_SCORE) / MAX_SCORE

    private const val Q1 = 1 / 5.toDouble()
    private const val Q2 = 2 / 5.toDouble()
    private const val Q3 = 3 / 5.toDouble()
    private const val Q4 = 4 / 5.toDouble()

    override fun computeQuantizedScore(pattern: AndroidUnlockPattern): Int {
        val score = computeNormalizedScore(pattern)

        return when {
            score <= Q1 -> 1
            score <= Q2 -> 2
            score <= Q3 -> 3
            score <= Q4 -> 4
            else -> 5
        }
    }

    override fun computeQuantizedNormalizedScore(pattern: AndroidUnlockPattern): Int = computeQuantizedScore(pattern)
}

/**
 * Based on the paper:
 *
 * **On the Effectiveness of Pattern Lock Strength Meters - Measuring the Strength of Real World Pattern Locks**
 *
 * Minimal score value: `0.162` - Since the minimum length in segments is 3 and since non-repeated is weighted so low
 * it is 1/3. This leads to 0.81*(3/15)+0.04*1/3=263/1500
 *
 * Maximal score value: `0.99` - Since the pattern with the highest score does not have a ratio of non-repeated
 * segments of 1.
 *
 * Technically the score should range from `0` to `1` (inclusive), thus, we provide a function to compute the
 * normalized score with the correct range.
 */
object SongMeter : StrengthMeter() {
    private const val MAX_HORIZONTAL_OR_VERTICAL_DISTANCE_WEIGHT = 0.81
    private const val MAX_HORIZONTAL_OR_VERTICAL_DISTANCE = 15.0

    private const val NON_REPEATED_WEIGHT = 0.04

    private const val CROSSES_WEIGHT = 0.15
    private const val MAX_VIABLE_CROSSES = 5.0

    override fun computeScore(pattern: AndroidUnlockPattern) =
        (MAX_HORIZONTAL_OR_VERTICAL_DISTANCE_WEIGHT *
                (pattern.sumOfMaximumNorms / MAX_HORIZONTAL_OR_VERTICAL_DISTANCE)) +
                (NON_REPEATED_WEIGHT * pattern.ratioOfNonRepeatedSegments) +
                (CROSSES_WEIGHT * (min(
                    pattern.numberOfSongIntersections.toDouble(),
                    MAX_VIABLE_CROSSES
                ) / MAX_VIABLE_CROSSES))

    private const val MIN_SCORE = 263 / 1500.0
    private const val MAX_SCORE = 0.99 - MIN_SCORE

    override fun computeNormalizedScore(pattern: AndroidUnlockPattern): Double =
        (computeScore(pattern) - MIN_SCORE) / MAX_SCORE

    private const val Q0 = 0.40
    private const val Q1 = 0.56

    override fun computeQuantizedScore(pattern: AndroidUnlockPattern): Int {
        val score = computeNormalizedScore(pattern)

        return when {
            score <= Q0 -> 1
            score <= Q1 -> 2
            else -> 3
        }
    }

    override fun computeQuantizedNormalizedScore(pattern: AndroidUnlockPattern): Int {
        val score = computeNormalizedScore(pattern)

        return when {
            score <= Q0 -> 1
            score <= Q1 -> 3
            else -> 5
        }
    }
}