package de.rub.mobsec

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import org.junit.jupiter.params.provider.CsvSource

internal class AndroidUnlockPatternTests {
    @ParameterizedTest
    @CsvFileSource(resources = ["/all-10.txt"], numLinesToSkip = 370_000)
    fun isValid(patternString: String) =
        assertTrue(AndroidUnlockPattern[map, patternString].isValid)

    @ParameterizedTest
    @CsvSource("10.11.12.13", "0")
    fun isInvalid(patternString: String) =
        assertFalse(AndroidUnlockPattern[map, patternString].isValid)

    @ParameterizedTest
    // skip the first since # non-repeated unclear
    @CsvFileSource(resources = ["/selected-patterns.csv"], numLinesToSkip = 1)
    fun `test pattern analysis`(
        patternString: String,
        startPointIndex: Int,
        pointLength: Int,
        numberOfSunIntersections: Int,
        numberOfSongIntersections: Int,
        numberOfRevisitedPoints: Int,
        numberOfOverlappingSegments: Int,
        numberOfKnightMoves: Int,
        numberOfDirectionChanges: Int,
        sumOfEuclideanDistances: Double,
        sumOfMaxHorizontalOrVerticalDistances: Int,
        ratioOfNonRepeatedSegments: Double,
        ratioOfNonRepeatedSegmentsWithPalindrome: Double
    ) {
        val pattern = AndroidUnlockPattern[map, patternString]
        assertAll(
            { assertEquals(startPointIndex, pattern.startNodeIndex, "index") },
            { assertEquals(pointLength, pattern.nodeLength, "length") },
            { assertEquals(numberOfSunIntersections, pattern.numberOfSunIntersections, "sun-intersections") },
            { assertEquals(numberOfSongIntersections, pattern.numberOfSongIntersections, "song-intersections") },
            { assertEquals(numberOfRevisitedPoints, pattern.numberOfRevisitedNodes, "revisited") },
            { assertEquals(numberOfOverlappingSegments, pattern.numberOfOverlappingSegments, "overlapping-segments") },
            { assertEquals(numberOfKnightMoves, pattern.numberOfKnightMoves, "knight") },
            { assertEquals(numberOfDirectionChanges, pattern.numberOfDirectionChanges, "turns") },
            { assertEquals(sumOfEuclideanDistances, pattern.sumOfEuclideanDistances, "euclidean") },
            {
                assertEquals(
                    sumOfMaxHorizontalOrVerticalDistances,
                    pattern.sumOfMaximumNorms,
                    "max"
                )
            },
            { assertEquals(ratioOfNonRepeatedSegments, pattern.ratioOfNonRepeatedSegments, "ratio") },
            {
                assertEquals(
                    ratioOfNonRepeatedSegmentsWithPalindrome,
                    pattern.ratioOfNonRepeatedSegmentsWithPalindrome,
                    "ratio-palindrome"
                )
            }
        )
    }

    @TestFactory
    fun `test toString`() =
        sequenceOf("0.1.2.3", "9.9.9.9.9.9.9", "10.1000.1").map {
            DynamicTest.dynamicTest(it) {
                assertEquals(it, AndroidUnlockPattern[map, it].toString())
            }
        }.iterator()
}
