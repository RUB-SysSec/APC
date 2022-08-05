package de.rub.mobsec.internal

import de.rub.mobsec.AndroidUnlockGrid
import de.rub.mobsec.AndroidUnlockPattern
import de.rub.mobsec.Segment
import de.rub.mobsec.internal.ManachersAlgorithm.computeLongestPalindrome

private const val INDEX_OF_FIRST = 0

internal data class DefaultAndroidUnlockPattern(
    private val segments: List<Segment>,
    private val patternString: String,
    private val androidUnlockGrid: AndroidUnlockGrid
) : AndroidUnlockPattern {
    override val isValid by lazy {
        if (segments.size <= 2)
            return@lazy false

        val visited = mutableListOf(segments.first().a)

        if (segments.first().a !in androidUnlockGrid)
            return@lazy false

        segments.filterIndexed { index, segment ->
            if (index == 0) {
                val isNotValid = !(segment isValidWith visited && segment.b in androidUnlockGrid)
                visited += segment.b
                isNotValid
            } else {
                val isNotValid = !(segment.isValidWith(visited, segments[index - 1]) && segment.b in androidUnlockGrid)
                visited += segment.b
                isNotValid
            }
        }.isEmpty()
    }

    override val startNodeIndex by lazy { androidUnlockGrid[segments.first().a] }

    override val nodeLength by lazy { segments.size + 1 }

    override val numberOfSunIntersections by lazy {
        val visited = mutableListOf<Segment>()
        val visitedBuffer = mutableListOf<Segment>()

        segments.sumOf { segment ->
            val count = visited.asSequence().filter(segment::isSunIntersecting).count()
            if (visitedBuffer.isNotEmpty())
                visited += visitedBuffer.removeAt(INDEX_OF_FIRST)
            visitedBuffer += segment
            count
        }
    }

    override val numberOfSongIntersections by lazy {
        val visited = mutableListOf<Segment>()
        val visitedBuffer = mutableListOf<Segment>()

        segments.sumOf { segment ->
            visited.filter(segment::isSongIntersecting).also {
                if (visitedBuffer.isNotEmpty())
                    visited += visitedBuffer.removeAt(INDEX_OF_FIRST)
                visitedBuffer += segment
            }.count()
        }
    }

    override val numberOfRevisitedNodes by lazy {
        val visited = mutableSetOf(segments.first().a)

        segments.sumOf { segment ->
            visited.filter(segment::isOverlapping).also { visited += segment.b }.count()
        }
    }

    override val numberOfOverlappingSegments by lazy {
        val visited = mutableSetOf<Segment>()

        segments.sumOf { segment ->
            visited.filter(segment::isOverlapping).also { visited += segment }.count()
        }
    }

    override val numberOfKnightMoves by lazy { segments.count { it.isKnightMove } }

    override val numberOfDirectionChanges by lazy {
        segments.asSequence().zipWithNext().filter { it.first isTurn it.second }.count()
    }

    override val sumOfEuclideanDistances by lazy { segments.sumOf { it.euclideanDistance } }

    override val sumOfMaximumNorms by lazy { segments.sumOf { it.maximumNorm } }

    override val ratioOfNonRepeatedSegments by lazy {
        segments.map { it.direction.abs() }.toSet().count() / segments.size.toDouble()
    }

    override val ratioOfNonRepeatedSegmentsWithPalindrome by lazy {
        (segments.size - computeLongestPalindrome(segments.map(Segment::direction)).size) / segments.size.toDouble()
    }

    override fun toString() = patternString
}