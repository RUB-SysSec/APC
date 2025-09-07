package de.rub.mobsec.internal

import de.rub.mobsec.AndroidUnlockGrid
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertAll
import kotlin.test.assertEquals

private const val DELIMITERS = "."
private val grid = AndroidUnlockGrid[3]

private fun String.toNodes() = split(DELIMITERS).map { grid[it.toInt()] }

internal class ManachersAlgorithmTest {
    @TestFactory
    fun computeLongestPalindromeSize() =
        sequenceOf(
            "1.2.3.2.1".toNodes() to "1.2.3.2.1".toNodes(),
            "0.1.2.3.2.1".toNodes() to "1.2.3.2.1".toNodes(),
            "0.1.2.3.2.1.2".toNodes() to "1.2.3.2.1".toNodes(),
            "1.1".toNodes() to "1.1".toNodes(),
            "0.1.0.1.0.3.2.2.1.1.1.2.2".toNodes() to "2.2.1.1.1.2.2".toNodes(),
            "1.2.2.1".toNodes() to "1.2.2.1".toNodes(),
            "1.2.3.4.5.6.7.8.0.1".toNodes() to "1".toNodes()
        ).map { (points, palindrome) ->
            DynamicTest.dynamicTest("$points") {
                assertAll(
                    { assertEquals(palindrome.size, ManachersAlgorithm.computeLongestPalindrome(points).size) },
                    { assertEquals(palindrome, ManachersAlgorithm.computeLongestPalindrome(points)) }
                )
            }
        }.iterator()

}
