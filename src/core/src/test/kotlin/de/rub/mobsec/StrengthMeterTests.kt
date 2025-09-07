package de.rub.mobsec

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource

/**
 * This is a self-test, which means that the scores from selected-scores.csv where computed by the code that is tested.
 * Useful to find errors in changed code.
 */
class StrengthMeterTests {
    private fun String.parsePattern() = AndroidUnlockPattern[map, this]

    @ParameterizedTest
    @CsvFileSource(resources = ["/selected-scores.csv"], numLinesToSkip = 1)
    fun `test meter result`(
        patternString: String,
        andriotis: Double,
        andriotisN: Double,
        andriotisQ: Int,
        andriotisQN: Int,
        sun: Double,
        sunN: Double,
        sunQ: Int,
        sunQN: Int,
        song: Double,
        songN: Double,
        songQ: Int,
        songQN: Int
    ) {
        val pattern = patternString.parsePattern()
        assertAll(
            { assertEquals(andriotis, AndriotisMeter.computeScore(pattern), DELTA, "andriotis") },
            { assertEquals(andriotisN, AndriotisMeter.computeNormalizedScore(pattern), DELTA, "andriotis-n") },
            { assertEquals(andriotisQ, AndriotisMeter.computeQuantizedScore(pattern), "andriotis-q") },
            { assertEquals(andriotisQN, AndriotisMeter.computeQuantizedNormalizedScore(pattern), "andriotis-qn") },

            { assertEquals(sun, SunMeter.computeScore(pattern), DELTA, "sun") },
            { assertEquals(sunN, SunMeter.computeNormalizedScore(pattern), DELTA, "sun-n") },
            { assertEquals(sunQ, SunMeter.computeQuantizedScore(pattern), "sun-q") },
            { assertEquals(sunQN, SunMeter.computeQuantizedNormalizedScore(pattern), "sun-qn") },

            { assertEquals(song, SongMeter.computeScore(pattern), DELTA, "song") },
            { assertEquals(songN, SongMeter.computeNormalizedScore(pattern), DELTA, "song-n") },
            { assertEquals(songQ, SongMeter.computeQuantizedScore(pattern), "song-q") },
            { assertEquals(songQN, SongMeter.computeQuantizedNormalizedScore(pattern), "song-qn") }
        )
    }
}
