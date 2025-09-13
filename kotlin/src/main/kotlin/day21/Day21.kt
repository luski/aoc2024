package day21

import kotlin.math.abs

typealias Pt = Pair<Int, Int>

operator fun Pt.minus(pt: Pt) = this.first - pt.first to this.second - pt.second

const val ARRAY_STATES = "<v>^C"
const val KEYPAD_STATES = "1234567890A"

const val RIGHT = '>'
const val LEFT = '<'
const val UP = '^'
const val DOWN = 'v'
const val CLICK = 'C'

data class MemoState(
    val remoteNum: Int,
    val source: Char,
    val target: Char,
    val controllerState: Char,
)

@Suppress("ktlint")
val buttonPositions =
    mapOf(
        // arrays
        'x' to (0 to 0), '^' to (0 to 1), 'C' to (0 to 2),
        '<' to (1 to 0), 'v' to (1 to 1), '>' to (1 to 2),
        // numpad
        '7' to (0 to 0), '8' to (0 to 1), '9' to (0 to 2),
        '4' to (1 to 0), '5' to (1 to 1), '6' to (1 to 2),
        '1' to (2 to 0), '2' to (2 to 1), '3' to (2 to 2),
        'x' to (3 to 0), '0' to (3 to 1), 'A' to (3 to 2),
    )
val memo = mutableMapOf<MemoState, Long>()

fun distance(
    source: Char,
    target: Char,
): Int {
    val sourcePosition = buttonPositions[source]!!
    val targetPosition = buttonPositions[target]!!

    return abs(sourcePosition.first - targetPosition.first) + abs(sourcePosition.second - targetPosition.second)
}

fun findPaths(
    source: Char,
    target: Char,
): List<List<Char>> {
    val sourcePosition = buttonPositions[source]!!
    val targetPosition = buttonPositions[target]!!

    val (rowOffset, colOffset) = targetPosition - sourcePosition

    val verticalDirection = if (rowOffset > 0) DOWN else UP
    val horizontalDirection = if (colOffset > 0) RIGHT else LEFT

    val vertical = List(abs(rowOffset)) { verticalDirection }
    val horizontal = List(abs(colOffset)) { horizontalDirection }

    if (vertical.isEmpty() || horizontal.isEmpty() || target == LEFT) {
        return listOf(vertical + horizontal)
    }
    if (source == LEFT) {
        return listOf(horizontal + vertical)
    }

    if (("741".contains(source)) && "0A".contains(target)) {
        return listOf(horizontal + vertical)
    }
    if ("741".contains(target) && "0A".contains(source)) {
        return listOf(vertical + horizontal)
    }
    return listOf(vertical + horizontal, horizontal + vertical)
}

fun buildMemo(robots: Int) {
    for (source in ARRAY_STATES) {
        for (target in ARRAY_STATES) {
            for (controllerState in ARRAY_STATES) {
                memo[MemoState(robots, source, target, controllerState)] = distance(source, target).toLong() + 1
            }
        }
    }

    for (robotNumber in (robots - 1) downTo 1) {
        for (source in ARRAY_STATES) {
            for (target in ARRAY_STATES) {
                for (controllerState in ARRAY_STATES) {
                    val paths = findPaths(source, target)
                    memo[MemoState(robotNumber, source, target, controllerState)] =
                        paths.minOf { path ->
                            val extendedPath =
                                buildList {
                                    if (path.firstOrNull() != controllerState) add(controllerState)
                                    addAll(path)
                                    add(CLICK)
                                }
                            extendedPath
                                .zipWithNext()
                                .sumOf { (from, to) -> memo[MemoState(robotNumber + 1, from, to, controllerState)]!! }
                        }
                }
            }
        }
    }

    for (source in KEYPAD_STATES) {
        for (target in KEYPAD_STATES) {
            for (controllerState in ARRAY_STATES) {
                val paths = findPaths(source, target)
                memo[MemoState(0, source, target, controllerState)] =
                    paths.minOf { path ->
                        val extendedPath =
                            buildList {
                                if (path.firstOrNull() != controllerState) add(controllerState)
                                addAll(path)
                                add(CLICK)
                            }
                        extendedPath
                            .zipWithNext()
                            .sumOf { (from, to) -> memo[MemoState(1, from, to, controllerState)]!! }
                    }
            }
        }
    }
}

fun query(code: String) =
    ("A$code")
        .zipWithNext()
        .sumOf { (source, target) ->
            val state = MemoState(0, source, target, 'C')
            val result = memo[state] ?: error("Not found $state")
            result
        }

fun codeToNumber(code: String) =
    code
        .filter { it in '0'..'9' }
        .toInt()

fun main() {
    val codes =
        generateSequence(::readlnOrNull)
            .takeWhile(String::isNotBlank)
            .toList()

    buildMemo(2)
    println("Part 1: ${codes.sumOf { codeToNumber(it) * query(it) }}")
    memo.clear()
    buildMemo(25)
    println("Part 2: ${codes.sumOf { codeToNumber(it) * query(it) }}")
}
