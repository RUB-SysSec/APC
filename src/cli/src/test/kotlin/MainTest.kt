package de.rub.mobsec

import com.github.ajalt.clikt.core.PrintHelpMessage
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals

class MainTest {
    private val apc = APC()

    @Test
    fun `test print help`() {
        assertAll(
            { assertThrows<PrintHelpMessage> { apc.parse(emptyList()) } },
            { assertThrows<PrintHelpMessage> { apc.parse(listOf("-h")) } },
            { assertThrows<PrintHelpMessage> { apc.parse(listOf("--help")) } }
        )
    }
}

class PrintStreamTest {
    private val apc = APC()

    private val sysOut = System.out
    private val sysErr = System.err
    private val outContent = ByteArrayOutputStream()
    private val errContent = ByteArrayOutputStream()

    @BeforeEach
    fun setUpStreams() {
        System.setOut(PrintStream(outContent))
        System.setErr(PrintStream(errContent))
    }

    @AfterEach
    fun revertStreams() {
        System.setOut(sysOut)
        System.setErr(sysErr)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "0-1-2-4-6-7-8-3-5,VALID,-",
            "012467835,VALID,",
            "4-0-8-1-7-2-6-5-3,VALID,-",
            "408172653,VALID,",
            "0-1-2-4-6-7-8-3-5-9,INVALID,-",
            "0124678359,INVALID,",
            "4-0-8-1-7-2-6-5-3-0,INVALID,-",
            "4081726530,INVALID,",
            "0-0-0-0,INVALID,-",
            "0000,INVALID,",
            "0-2-3-5,INVALID,-",
            "0235,INVALID,"
        ]
    )
    fun `test verify sub-command with non-default delimiter`(
        patternString: String,
        isValid: String,
        delimiter: String?
    ) {
        apc.parse(listOf("-d", delimiter ?: "", "-p", patternString))
        assertAll(
            //  { assertEquals("$patternString1,$isValid", outContent.toString().trim()) },
            { assertEquals("", errContent.toString()) }
        )
    }
}