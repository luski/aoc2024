package day15.part2

import day15.part2.Direction.*

private const val EMPTY = '.'
private const val WALL = '#'
private const val ROBOT = '@'
private const val BOX = 'O'
private const val L_BOX = '['
private const val R_BOX = ']'

typealias Board = MutableList<MutableList<Char>>
typealias Pt = Pair<Int, Int>

enum class Direction(val offset: Pt, val symbol: Char) {
    Up(-1 to 0, '^'),
    Right(0 to 1, '>'),
    Left(0 to -1, '<'),
    Down(1 to 0, 'v');

    val opposite: Direction
        get() = when (this) {
            Up -> Down
            Down -> Up
            Right -> Left
            Left -> Right
        }

    companion object {
        private val fromChar = mapOf(
            Up.symbol to Up,
            Down.symbol to Down,
            Left.symbol to Left,
            Right.symbol to Right
        )

        fun from(ch: Char) = fromChar[ch] ?: error("invalid character $ch")
    }
}

data class InputData(val board: Board, val moves: List<Char>, val initialPosition: Pt)

operator fun Pt.plus(other: Pt): Pt = this.first + other.first to this.second + other.second
operator fun Pt.plus(direction: Direction): Pt = this + direction.offset
operator fun Pt.minus(direction: Direction): Pt = this.plus(direction.opposite)

operator fun Board.get(pos: Pt): Char = this[pos.first][pos.second]
operator fun Board.set(pos: Pt, value: Char) {
    this[pos.first][pos.second] = value
}

fun Board.swap(source: Pt, target: Pt) {
    val tmp = this[source.first][source.second]
    this[source.first][source.second] = this[target.first][target.second]
    this[target.first][target.second] = tmp
}

fun readInput(): InputData {
    val board: Board = generateSequence(::readlnOrNull)
        .takeWhile(String::isNotBlank)
        .map { line ->
            line.asSequence().flatMap { ch ->
                when (ch) {
                    BOX -> sequenceOf(L_BOX, R_BOX)
                    WALL -> sequenceOf(WALL, WALL)
                    ROBOT -> sequenceOf(ROBOT, EMPTY)
                    else -> sequenceOf(EMPTY, EMPTY)
                }
            }.toMutableList()
        }.toMutableList()

    val initialPosition = board
        .withIndex()
        .firstNotNullOfOrNull { (row, line) ->
            line.indexOf(ROBOT)
                .takeIf { it != -1 }?.let { col -> row to col }
        } ?: error("")

    board[initialPosition] = EMPTY

    val moves = generateSequence(::readlnOrNull)
        .takeWhile(String::isNotBlank)
        .flatMap(String::asSequence)
        .toList()

    return InputData(board, moves, initialPosition)
}

fun main() {
    val (board, moves, initialPosition) = readInput()
    printBoard(board)
    var position = initialPosition
    for (direction in moves) {
        position = performMove(board, position, Direction.from(direction))
    }
    println(calculate(board))
}

fun getBoxPosition(board: Board, pt: Pt): Pt {
    if (board[pt] == L_BOX) return pt
    val (row, col) = pt
    return row to (col - 1)
}

fun findNextBoxesToMoveVert(board: Board, boxes: List<Pt>, direction: Direction): List<Pt>? {
    val result = mutableSetOf<Pt>()

    for (box in boxes) {
        val nextLeftPos = box + direction
        val nextRightPos = nextLeftPos + Right

        when (board[nextLeftPos]) {
            L_BOX -> result += nextLeftPos
            R_BOX -> result += nextLeftPos + Left
            WALL -> return null
        }
        when (board[nextRightPos]) {
            L_BOX -> result += nextRightPos
            WALL -> return null
        }

    }

    return result.toList()
}

fun performMove(board: Board, position: Pt, direction: Direction): Pt = when (direction) {
    Up, Down -> performMoveVert(board, position, direction)
    Left, Right -> performMoveHor(board, position, direction)
}

fun performMoveVert(board: Board, position: Pt, direction: Direction): Pt {
    val nextRobotPosition = position + direction.offset
    when (board[nextRobotPosition]) {
        EMPTY -> return nextRobotPosition
        WALL -> return position
    }

    val boxesToMove = mutableListOf(listOf(getBoxPosition(board, nextRobotPosition)))

    while (true) {
        val nextBoxesToMove = findNextBoxesToMoveVert(board, boxesToMove.last(), direction) ?: return position
        if (nextBoxesToMove.isEmpty()) break
        boxesToMove.add(nextBoxesToMove)
    }

    for (boxes in boxesToMove.asReversed()) {
        for (box in boxes) {
            board.swap(box, box + direction)
            board.swap(box + Right, box + direction + Right)
        }
    }

    return nextRobotPosition
}

fun performMoveHor(board: Board, position: Pt, direction: Direction): Pt {
    val nextRobotPosition = position + direction.offset
    var nextFreePos = nextRobotPosition

    while (board[nextFreePos] != EMPTY) {
        val element = board[nextFreePos]
        if (element == WALL) {
            return position
        }
        nextFreePos += direction.offset
    }

    while (nextFreePos != nextRobotPosition) {
        board.swap(nextFreePos, nextFreePos - direction)
        nextFreePos -= direction
    }
    return nextRobotPosition
}

fun calculate(board: Board): Int {
    return board.withIndex().sumOf { (y, row) ->
        row.withIndex().filter { (_, char) -> char == L_BOX }.sumOf { (x, _) -> 100 * y + x }
    }

}

fun printBoard(board: Board) {
    board.forEach { println(it.joinToString("")) }
}