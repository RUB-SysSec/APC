package de.rub.mobsec

import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.main
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

class ExceptionsTest {

    private val outContent = mutableListOf<String>()
    private val errContent = mutableListOf<String>()
    private val apc = APC().context {
        echoMessage = { _, message, _, err ->
            if (err) errContent.add(message.toString()) else outContent.add(message.toString())
        }
    }

    @ParameterizedTest
    @CsvSource(
        textBlock = """
COMMAND   , LINE_START
          , 'Usage: apc [<options>] [<features>]...'
-h        , 'Usage: apc [<options>] [<features>]...'
--help    , 'Usage: apc [<options>] [<features>]...'
--features, '[andriotis, andriotis-raw, andriotis-r]'
--version , 'Android Pattern Classifier (apc) 1.0.5 -- 2025-09-07'
""", useHeadersInDisplayName = true
    )
    fun `info commands print lineStart`(command: String?, lineStart: String) {
         apc.main(listOfNotNull(command))
        assertAll(
            { assertEquals(listOf(), errContent) },
            { assertEquals(lineStart, outContent.first().substringBefore('\n')) },
        )
    }

    @Test
    fun `empty pattern file prints help`() {
       apc.main(listOf("--file", "./src/test/resources/empty-pattern.csv"))
        assertAll(
            { assertEquals(listOf(), errContent) },
            { assertEquals("Usage: apc [<options>] [<features>]...", outContent.first().substringBefore('\n')) },
        )
    }
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PrintStreamTest {

    private val outContent = mutableListOf<String>()
    private val errContent = mutableListOf<String>()
    private val apc = APC().context {
        echoMessage = { _, message, _, err ->
            if (err) errContent.add(message.toString()) else outContent.add(message.toString())
        }
    }

    @AfterEach
    fun afterEach() {
        outContent.clear()
        errContent.clear()
    }

    private val validPatterns = listOf("0.1.2.4.6.7.8.3.5", "4.0.8.1.7.2.6.5.3")
    private val invalidPatterns = listOf("0.1.2.4.6.7.8.3.5.9", "4.0.8.1.7.2.6.5.3.0", "0.0.0.0", "0.2.3.5")
    private val delimiters = listOf("\t", "-", "", "x", "öleunw", ".")
    private val headers = listOf("--header-off", "--header-on", null)


    private fun testArgs(): List<Arguments> {
        return headers.flatMap { header ->
            delimiters.flatMap { delimiter ->
                validPatterns.map { pattern ->
                    arguments(pattern.replace(".", delimiter), "VALID", delimiter, header)
                } + invalidPatterns.map { pattern ->
                    arguments(pattern.replace(".", delimiter), "INVALID", delimiter, header)
                }
            } + validPatterns.map { pattern ->
                arguments(pattern, "VALID", null, header)
            } + invalidPatterns.map { pattern ->
                arguments(pattern, "INVALID", null, header)
            }
        }
    }

    @ParameterizedTest
    @MethodSource("testArgs")
    fun `test verify sub-command with non-default delimiter`(
        patternString: String,
        isValid: String,
        delimiter: String?,
        header: String?,
    ) {
        apc.main(listOfNotNull(delimiter?.let { "-d" }, delimiter, "-p", patternString, header))
        val out = if (header == "--header-off") outContent.first() else outContent[1]
        assertAll(
            { "$patternString\t1\t$isValid".let { assertEquals(it, out.take(it.length)) } },
            { assertEquals(listOf(), errContent) }
        )
    }
}
