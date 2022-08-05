@file:Suppress("NOTHING_TO_INLINE")

package de.rub.mobsec

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintHelpMessage
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.transformAll
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.restrictTo
import java.text.DecimalFormat
import kotlin.reflect.typeOf

private val DEFAULT_GRID = AndroidUnlockGrid[3]

class PatternConfig(val delimiter: String, val grid: AndroidUnlockGrid)

internal fun CliktCommand.delimiter() = option(
    "-d",
    "--delimiter",
    help = "Changes the pattern delimiter. Dot (as in 1.2.3.4) is the default. To parse a pattern without delimiter (like 1234), use an empty string: -d \"\"."
).default(".")

internal fun CliktCommand.grid() = option(
    "-g",
    "--grid",
    help = "Changes the grid dimension. Default is 3 3."
).int().restrictTo(1).pair().transformAll { gridDimension ->
    gridDimension.lastOrNull()?.let { AndroidUnlockGrid[it.first, it.second] } ?: DEFAULT_GRID
}

internal fun CliktCommand.patterns() = option(
    "-p",
    "--pattern",
    help = "An Android unlock pattern. Can be specified multiple times. By default a pattern has the form of 0.1.2.3.8."
).multiple()

internal fun CliktCommand.files() = option(
    "-f", "--file", help = "File with one pattern per line."
).file(mustExist = true, mustBeReadable = true).multiple()

internal inline fun String.toPattern(config: PatternConfig) =
    AndroidUnlockPattern[config.grid, trim(), config.delimiter]

private val names: Map<List<String>, (AndroidUnlockPattern) -> Number> = mapOf(
    listOf("andriotis", "andriotis-raw", "andriotis-r") to AndriotisMeter::computeScore,
    listOf("andriotis-normalized", "andriotis-n") to AndriotisMeter::computeNormalizedScore,
    listOf("andriotis-quantized", "andriotis-q") to AndriotisMeter::computeQuantizedScore,
    listOf("andriotis-quantized-normalized", "andriotis-qn") to AndriotisMeter::computeQuantizedNormalizedScore,
    listOf("sun", "sun-raw", "sun-r") to SunMeter::computeScore,
    listOf("sun-normalized", "sun-n") to SunMeter::computeNormalizedScore,
    listOf("sun-quantized", "sun-q") to SunMeter::computeQuantizedScore,
    listOf("sun-quantized-normalized", "sun-qn") to SunMeter::computeQuantizedNormalizedScore,
    listOf("song", "song-raw", "song-r") to SongMeter::computeScore,
    listOf("song-normalized", "song-n") to SongMeter::computeNormalizedScore,
    listOf("song-quantized", "song-q") to SongMeter::computeQuantizedScore,
    listOf("song-quantized-normalized", "song-qn") to SongMeter::computeQuantizedNormalizedScore,

    listOf("andriotis-node-index") to AndriotisMeter::startNode,
    listOf("andriotis-length") to AndriotisMeter::length,
    listOf("andriotis-direction-changes", "andriotis-turns") to AndriotisMeter::directionChanges,
    listOf("andriotis-knight-moves", "knight-moves", "k-moves") to AndroidUnlockPattern::numberOfKnightMoves,
    listOf(
        "andriotis-overlapping-nodes",
        "overlapping-nodes",
        "revisited-nodes",
        "revisited-points",
        "non-adjacent"
    ) to AndroidUnlockPattern::numberOfRevisitedNodes,

    listOf("sun-overlapping-segments", "overlapping-segments") to AndroidUnlockPattern::numberOfOverlappingSegments,
    listOf(
        "sun-length",
        "node-length",
        "point-length",
        "pattern-length"
    ) to AndroidUnlockPattern::nodeLength,
    listOf(
        "sun-physical-length",
        "physical-length",
        "sum-of-euclidean-distance",
        "euclidean-distance"
    ) to AndroidUnlockPattern::sumOfEuclideanDistances,
    listOf("sun-intersections", "all-intersections") to AndroidUnlockPattern::numberOfSunIntersections,

    listOf(
        "song-length",
        "song-length-in-maximum-norm",
        "song-maximum-norm",
        "song-maximum-norm-length",
        "length-in-maximum-norm",
        "maximum-norm-length",
        "maximum-norm",
        "sum-of-max-horizontal-or-vertical-distance",
        "max-horizontal-or-vertical-distance"
    ) to AndroidUnlockPattern::sumOfMaximumNorms,
    listOf("song-intersections") to AndroidUnlockPattern::numberOfSongIntersections,
    listOf(
        "song-ratio-of-non-repeated-segments",
        "ratio-of-non-repeated-segments",
        "non-repeated-segments",
        "non-repeated"
    ) to AndroidUnlockPattern::ratioOfNonRepeatedSegments,

    listOf(
        "start-node-index",
        "start-point-index",
        "start-index"
    ) to AndroidUnlockPattern::startNodeIndex,
    listOf("direction-changes", "turns") to AndroidUnlockPattern::numberOfDirectionChanges,
    listOf("non-repeated-palindrome") to AndroidUnlockPattern::ratioOfNonRepeatedSegmentsWithPalindrome
)


private val returnTypes: Map<(AndroidUnlockPattern) -> Number, Boolean> = mapOf(
    AndriotisMeter::computeScore to (AndriotisMeter::computeScore.returnType == typeOf<Int>()),
    AndriotisMeter::computeNormalizedScore to (AndriotisMeter::computeNormalizedScore.returnType == typeOf<Int>()),
    AndriotisMeter::computeQuantizedScore to (AndriotisMeter::computeQuantizedScore.returnType == typeOf<Int>()),
    AndriotisMeter::computeQuantizedNormalizedScore to (AndriotisMeter::computeQuantizedNormalizedScore.returnType == typeOf<Int>()),
    SunMeter::computeScore to (SunMeter::computeScore.returnType == typeOf<Int>()),
    SunMeter::computeNormalizedScore to (SunMeter::computeNormalizedScore.returnType == typeOf<Int>()),
    SunMeter::computeQuantizedScore to (SunMeter::computeQuantizedScore.returnType == typeOf<Int>()),
    SunMeter::computeQuantizedNormalizedScore to (SunMeter::computeQuantizedNormalizedScore.returnType == typeOf<Int>()),
    SongMeter::computeScore to (SongMeter::computeScore.returnType == typeOf<Int>()),
    SongMeter::computeNormalizedScore to (SongMeter::computeNormalizedScore.returnType == typeOf<Int>()),
    SongMeter::computeQuantizedScore to (SongMeter::computeQuantizedScore.returnType == typeOf<Int>()),
    SongMeter::computeQuantizedNormalizedScore to (SongMeter::computeQuantizedNormalizedScore.returnType == typeOf<Int>()),

    AndriotisMeter::startNode to (AndriotisMeter::startNode.returnType == typeOf<Int>()),
    AndriotisMeter::length to (AndriotisMeter::length.returnType == typeOf<Int>()),
    AndriotisMeter::directionChanges to (AndriotisMeter::directionChanges.returnType == typeOf<Int>()),
    AndroidUnlockPattern::numberOfKnightMoves to (AndroidUnlockPattern::numberOfKnightMoves.returnType == typeOf<Int>()),
    AndroidUnlockPattern::numberOfRevisitedNodes to (AndroidUnlockPattern::numberOfRevisitedNodes.returnType == typeOf<Int>()),

    AndroidUnlockPattern::numberOfOverlappingSegments to (AndroidUnlockPattern::numberOfOverlappingSegments.returnType == typeOf<Int>()),
    AndroidUnlockPattern::nodeLength to (AndroidUnlockPattern::nodeLength.returnType == typeOf<Int>()),
    AndroidUnlockPattern::sumOfEuclideanDistances to (AndroidUnlockPattern::sumOfEuclideanDistances.returnType == typeOf<Int>()),
    AndroidUnlockPattern::numberOfSunIntersections to (AndroidUnlockPattern::numberOfSunIntersections.returnType == typeOf<Int>()),

    AndroidUnlockPattern::sumOfMaximumNorms to (AndroidUnlockPattern::sumOfMaximumNorms.returnType == typeOf<Int>()),
    AndroidUnlockPattern::numberOfSongIntersections to (AndroidUnlockPattern::numberOfSongIntersections.returnType == typeOf<Int>()),
    AndroidUnlockPattern::ratioOfNonRepeatedSegments to (AndroidUnlockPattern::ratioOfNonRepeatedSegments.returnType == typeOf<Int>()),

    AndroidUnlockPattern::startNodeIndex to (AndroidUnlockPattern::startNodeIndex.returnType == typeOf<Int>()),
    AndroidUnlockPattern::numberOfDirectionChanges to (AndroidUnlockPattern::numberOfDirectionChanges.returnType == typeOf<Int>()),
    AndroidUnlockPattern::ratioOfNonRepeatedSegmentsWithPalindrome to (AndroidUnlockPattern::ratioOfNonRepeatedSegmentsWithPalindrome.returnType == typeOf<Int>())
)

private val arguments = names.keys.toList()

private val indexToFun = names.values.toList()

private const val SEPARATOR = "\t"

private val df1 = DecimalFormat("0.0###############")
private val df2 = DecimalFormat("0.################")

private inline fun Collection<(AndroidUnlockPattern) -> Number>.scoresString(pattern: AndroidUnlockPattern) =
    joinToString(SEPARATOR) {
        if (returnTypes.getValue(it)) df2.format(it(pattern).toDouble()) else df1.format(it(pattern).toDouble())
    }

internal class APC : CliktCommand(
    name = "apc",
    help = """
        Computes features for all provided patterns.

        Example usages:

        -p 1.2.3.4 -f /path/to/file

        -f /path/to/file -p 0.1.2.3 --header-off andriotis song-n
        """
) {
    init {
        registerOption(
            EagerOption(
                "--features",
                help = "View all possible FEATURES with aliases and exit."
            ) {
                throw PrintMessage(arguments.joinToString("\n"))
            })
        registerOption(
            EagerOption(
                names = arrayOf("--version"),
                help = "Show the version plus license and exit.",
            ) {
                throw PrintMessage(
                    """
Android Pattern Classifier (apc) $VERSION -- $VERSION_DATE
Copyright (c) 2022 Horst Goertz Institute for IT Security (Ruhr University Bochum)

MIT License (MIT)
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

Written by Jan Rimkus.
                """.trimIndent()
                )
            })
    }

    private val delimiter by delimiter()
    private val grid by grid()
    private val patterns by patterns()
    private val files by files()
    private val header by option("--header-on", help = "Print a header.").flag("--header-off", default = true)
    private val features by argument(help = "Features to compute. Default is all features.").choice(names.keys.mapIndexed { index, args -> args.map { it to index } }
        .flatten().toMap())
        .multiple().transformAll { list -> list.map { indexToFun[it] } }


    private inline fun AndroidUnlockPattern.echoPatternWithFeatures(count: Int) {
        if (features.isNotEmpty())
            echo(
                "${toString()}$SEPARATOR$count$SEPARATOR${if (isValid) "VALID" else "INVALID"}$SEPARATOR${
                    features.scoresString(this)
                }"
            )
        else
            echo(
                "${toString()}$SEPARATOR$count$SEPARATOR${if (isValid) "VALID" else "INVALID"}$SEPARATOR${
                    indexToFun.scoresString(this)
                }"
            )
    }


    @ExperimentalStdlibApi
    override fun run() {
        val config = PatternConfig(delimiter, grid = grid)
        val allPatterns = (patterns.map { it.toPattern(config) } +
                files.flatMap { file -> file.readLines().map { it.toPattern(config) } }).groupBy { it }
            .mapValues { it.value.size }.toList().sortedByDescending { it.second }.toMap()

        if (allPatterns.isEmpty())
            throw PrintHelpMessage(this)

        if (header) {
            if (features.isNotEmpty()) echo(
                "pattern${SEPARATOR}count${SEPARATOR}validity$SEPARATOR${
                    features.joinToString(SEPARATOR) { feature ->
                        names.entries.first { it.value == feature }.key.first()
                    }
                }"
            )
            else echo(
                "pattern${SEPARATOR}count${SEPARATOR}validity$SEPARATOR${
                    indexToFun.joinToString(SEPARATOR) { feature ->
                        names.entries.first { it.value == feature }.key.first()
                    }
                }"
            )
        }

        allPatterns.forEach { (pattern, count) ->
            pattern.echoPatternWithFeatures(count)
        }
    }
}

private const val VERSION = "1.0.4"
private const val VERSION_DATE = "2022-08-05"

fun main(args: Array<String>) = APC().context {
    this.helpFormatter = CliktHelpFormatter(
        usageTitle = """
        Android Pattern Classifier (apc) $VERSION -- $VERSION_DATE

        Usage:
    """.trimIndent()
    )
}.main(args)