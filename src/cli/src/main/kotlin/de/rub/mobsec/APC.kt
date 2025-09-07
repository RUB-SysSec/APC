@file:Suppress("NOTHING_TO_INLINE")

package de.rub.mobsec

import com.github.ajalt.clikt.core.*
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

typealias Meter1 = AndriotisMeter
typealias Meter2 = SunMeter
typealias Meter3 = SongMeter
typealias Pattern = AndroidUnlockPattern

private val DEFAULT_GRID = AndroidUnlockGrid[3]

class PatternConfig(val delimiter: String, val grid: AndroidUnlockGrid)

internal fun CoreCliktCommand.delimiter() = option(
    "-d",
    "--delimiter",
    help = "Changes the pattern delimiter. Dot (as in 1.2.3.4) is the default. To parse a pattern without delimiter (like 1234), use an empty string: -d \"\".",
).default(".")

internal fun CoreCliktCommand.grid() = option(
    "-g",
    "--grid",
    help = "Changes the grid dimension. Default is 3 3.",
).int().restrictTo(1).pair().transformAll { gridDimension ->
    gridDimension.lastOrNull()?.let { AndroidUnlockGrid[it.first, it.second] } ?: DEFAULT_GRID
}

internal fun CoreCliktCommand.patterns() = option(
    "-p",
    "--pattern",
    help = "An Android unlock pattern. Can be specified multiple times. By default a pattern has the form of 0.1.2.3.8.",
).multiple()

internal fun CoreCliktCommand.files() = option("-f", "--file", help = "File with one pattern per line.")
    .file(mustExist = true, mustBeReadable = true)
    .multiple()

internal inline fun String.toPattern(config: PatternConfig) = Pattern[config.grid, trim(), config.delimiter]

private val names: Map<List<String>, (Pattern) -> Number> = mapOf(
    listOf("andriotis", "andriotis-raw", "andriotis-r") to Meter1::computeScore,
    listOf("andriotis-normalized", "andriotis-n") to Meter1::computeNormalizedScore,
    listOf("andriotis-quantized", "andriotis-q") to Meter1::computeQuantizedScore,
    listOf("andriotis-quantized-normalized", "andriotis-qn") to Meter1::computeQuantizedNormalizedScore,

    listOf("sun", "sun-raw", "sun-r") to Meter2::computeScore,
    listOf("sun-normalized", "sun-n") to Meter2::computeNormalizedScore,
    listOf("sun-quantized", "sun-q") to Meter2::computeQuantizedScore,
    listOf("sun-quantized-normalized", "sun-qn") to Meter2::computeQuantizedNormalizedScore,

    listOf("song", "song-raw", "song-r") to Meter3::computeScore,
    listOf("song-normalized", "song-n") to Meter3::computeNormalizedScore,
    listOf("song-quantized", "song-q") to Meter3::computeQuantizedScore,
    listOf("song-quantized-normalized", "song-qn") to Meter3::computeQuantizedNormalizedScore,

    listOf("andriotis-node-index") to Meter1::startNode,
    listOf("andriotis-length") to Meter1::length,
    listOf("andriotis-direction-changes", "andriotis-turns") to Meter1::directionChanges,

    listOf("andriotis-knight-moves", "knight-moves", "k-moves") to Pattern::numberOfKnightMoves,
    listOf(
        "andriotis-overlapping-nodes",
        "overlapping-nodes",
        "revisited-nodes",
        "revisited-points",
        "non-adjacent",
    ) to Pattern::numberOfRevisitedNodes,

    listOf("sun-overlapping-segments", "overlapping-segments") to Pattern::numberOfOverlappingSegments,
    listOf("sun-length", "node-length", "point-length", "pattern-length") to Pattern::nodeLength,
    listOf(
        "sun-physical-length",
        "physical-length",
        "sum-of-euclidean-distance",
        "euclidean-distance",
    ) to Pattern::sumOfEuclideanDistances,
    listOf("sun-intersections", "all-intersections") to Pattern::numberOfSunIntersections,

    listOf(
        "song-length",
        "song-length-in-maximum-norm",
        "song-maximum-norm",
        "song-maximum-norm-length",
        "length-in-maximum-norm",
        "maximum-norm-length",
        "maximum-norm",
        "sum-of-max-horizontal-or-vertical-distance",
        "max-horizontal-or-vertical-distance",
    ) to Pattern::sumOfMaximumNorms,
    listOf("song-intersections") to Pattern::numberOfSongIntersections,
    listOf(
        "song-ratio-of-non-repeated-segments",
        "ratio-of-non-repeated-segments",
        "non-repeated-segments",
        "non-repeated",
    ) to Pattern::ratioOfNonRepeatedSegments,

    listOf("start-node-index", "start-point-index", "start-index") to Pattern::startNodeIndex,
    listOf("direction-changes", "turns") to Pattern::numberOfDirectionChanges,
    listOf("non-repeated-palindrome") to Pattern::ratioOfNonRepeatedSegmentsWithPalindrome
)

private val returnTypes: Map<(Pattern) -> Number, Boolean> = mapOf(
    Meter1::computeScore to (Meter1::computeScore.returnType == typeOf<Int>()),
    Meter1::computeNormalizedScore to (Meter1::computeNormalizedScore.returnType == typeOf<Int>()),
    Meter1::computeQuantizedScore to (Meter1::computeQuantizedScore.returnType == typeOf<Int>()),
    Meter1::computeQuantizedNormalizedScore to (Meter1::computeQuantizedNormalizedScore.returnType == typeOf<Int>()),

    Meter2::computeScore to (Meter2::computeScore.returnType == typeOf<Int>()),
    Meter2::computeNormalizedScore to (Meter2::computeNormalizedScore.returnType == typeOf<Int>()),
    Meter2::computeQuantizedScore to (Meter2::computeQuantizedScore.returnType == typeOf<Int>()),
    Meter2::computeQuantizedNormalizedScore to (Meter2::computeQuantizedNormalizedScore.returnType == typeOf<Int>()),

    Meter3::computeScore to (Meter3::computeScore.returnType == typeOf<Int>()),
    Meter3::computeNormalizedScore to (Meter3::computeNormalizedScore.returnType == typeOf<Int>()),
    Meter3::computeQuantizedScore to (Meter3::computeQuantizedScore.returnType == typeOf<Int>()),
    Meter3::computeQuantizedNormalizedScore to (Meter3::computeQuantizedNormalizedScore.returnType == typeOf<Int>()),

    Meter1::startNode to (Meter1::startNode.returnType == typeOf<Int>()),
    Meter1::length to (Meter1::length.returnType == typeOf<Int>()),
    Meter1::directionChanges to (Meter1::directionChanges.returnType == typeOf<Int>()),

    Pattern::numberOfKnightMoves to (Pattern::numberOfKnightMoves.returnType == typeOf<Int>()),
    Pattern::numberOfRevisitedNodes to (Pattern::numberOfRevisitedNodes.returnType == typeOf<Int>()),

    Pattern::numberOfOverlappingSegments to (Pattern::numberOfOverlappingSegments.returnType == typeOf<Int>()),
    Pattern::nodeLength to (Pattern::nodeLength.returnType == typeOf<Int>()),
    Pattern::sumOfEuclideanDistances to (Pattern::sumOfEuclideanDistances.returnType == typeOf<Int>()),
    Pattern::numberOfSunIntersections to (Pattern::numberOfSunIntersections.returnType == typeOf<Int>()),

    Pattern::sumOfMaximumNorms to (Pattern::sumOfMaximumNorms.returnType == typeOf<Int>()),
    Pattern::numberOfSongIntersections to (Pattern::numberOfSongIntersections.returnType == typeOf<Int>()),
    Pattern::ratioOfNonRepeatedSegments to (Pattern::ratioOfNonRepeatedSegments.returnType == typeOf<Int>()),

    Pattern::startNodeIndex to (Pattern::startNodeIndex.returnType == typeOf<Int>()),
    Pattern::numberOfDirectionChanges to (Pattern::numberOfDirectionChanges.returnType == typeOf<Int>()),
    Pattern::ratioOfNonRepeatedSegmentsWithPalindrome to
            (Pattern::ratioOfNonRepeatedSegmentsWithPalindrome.returnType == typeOf<Int>()),
)

private val arguments = names.keys.toList()

private val indexToFun = names.values.toList()

private const val SEP = "\t"

private val df1 = DecimalFormat("0.0###############")
private val df2 = DecimalFormat("0.################")

private inline fun Collection<(Pattern) -> Number>.headerString() = joinToString(SEP) { feature ->
    names.entries.first { it.value == feature }.key.first()
}

private inline fun Collection<(Pattern) -> Number>.scoresString(pattern: Pattern) = joinToString(SEP) {
    if (returnTypes.getValue(it)) df2.format(it(pattern).toDouble()) else df1.format(it(pattern).toDouble())
}

private inline fun Pattern.validity() = if (isValid) "VALID" else "INVALID"

internal class APC : CoreCliktCommand(name = "apc") {
    override fun help(context: Context) = """
    Android Pattern Classifier (apc) $VERSION -- $VERSION_DATE

    Computes features for all provided patterns.

    Example usages:
        apc -p 1.2.3.4 -f /path/to/file
        apc -f /path/to/file -p 0.1.2.3 --header-off andriotis song-n
"""
    override val printHelpOnEmptyArgs = true

    init {
        eagerOption("--features", help = "View all possible FEATURES with aliases and exit.") {
            throw PrintMessage(arguments.joinToString("\n"))
        }
        eagerOption("--version", help = "Show the version plus license and exit.") {
            throw PrintMessage(
                """
Android Pattern Classifier (apc) $VERSION -- $VERSION_DATE
Copyright (c) 2025 Horst Goertz Institute for IT Security (Ruhr University Bochum)

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

Written by Jan Rimkus.""".trimIndent()
            )
        }
    }

    private val delimiter by delimiter()
    private val grid by grid()
    private val patterns by patterns()
    private val files by files()
    private val header by option("--header-on", help = "Print a header.").flag("--header-off", default = true)
    private val features by argument(help = "Features to compute. Default is all features.")
        .choice(names.keys.mapIndexed { index, args -> args.map { it to index } }.flatten().toMap())
        .multiple()
        .transformAll { list -> list.map { indexToFun[it] } }

    override fun run() {
        val config = PatternConfig(delimiter, grid = grid)
        val allPatterns = (patterns.map { it.toPattern(config) } +
                files.flatMap { file -> file.readLines().map { it.toPattern(config) } })
            .groupBy { it }.mapValues { it.value.size }.toList().sortedByDescending { it.second }.toMap()

        if (allPatterns.isEmpty())
            throw PrintHelpMessage(currentContext)

        val selectedFeatures = features.ifEmpty { indexToFun }
        if (header) {
            echo("pattern${SEP}count${SEP}validity$SEP${selectedFeatures.headerString()}\n")
        }

        allPatterns.forEach { (pattern, count) ->
            echo(
                "$pattern$SEP$count$SEP${pattern.validity()}$SEP${selectedFeatures.scoresString(pattern)}"
            )
        }
    }
}

private const val VERSION = "1.0.5"
private const val VERSION_DATE = "2025-09-07"
