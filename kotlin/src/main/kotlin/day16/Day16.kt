package day16

typealias Board<T> = MutableList<MutableList<T>>
typealias Pt = Pair<Int, Int>

const val START = 'S'
const val END = 'E'
const val EMPTY = '.'

// Board extensions
fun Board<Char>.extract(ch: Char): Pt = this.withIndex()
    .firstNotNullOf { (col, line) ->
        line.indexOf(ch)
            .takeIf { it != -1 }
            ?.let { row -> col to row }
    }
    .also { this[it] = EMPTY }

operator fun <T> List<List<T>>.get(pt: Pt): T = this[pt.first][pt.second]
operator fun <T> List<MutableList<T>>.set(pt: Pt, value: T) {
    this[pt.first][pt.second] = value
}

// Direction
enum class Direction(val offset: Pt) {
    UP(-1 to 0), DOWN(1 to 0), LEFT(0 to -1), RIGHT(0 to 1);

    val opposite
        get() = when (this) {
            UP -> DOWN
            DOWN -> UP
            LEFT -> RIGHT
            RIGHT -> LEFT
        }
}

// Pt
operator fun Pt.plus(pt: Pt): Pt = (this.first + pt.first) to (this.second + pt.second)
operator fun Pt.plus(direction: Direction): Pt = this + direction.offset

// DTOs
data class Action(val position: Pt, val cost: Int, val direction: Direction)
data class InputData(val board: Board<Char>, val start: Pt, val end: Pt)


fun readInput(): InputData {
    val board: Board<Char> = generateSequence(::readlnOrNull).takeWhile(String::isNotBlank)
        .map(String::toMutableList)
        .toMutableList()

    val start = board.extract(START)
    val end = board.extract(END)

    return InputData(board, start, end)
}

fun rec(board: Board<Char>, scoreBoard: Board<Int>, pos: Pt, end: Pt, direction: Direction) {
    if (pos == end) return

    for (action in nextActions(board, pos, direction)) {
        if (scoreBoard[action.position] <= scoreBoard[pos] + action.cost) continue
        scoreBoard[action.position] = scoreBoard[pos] + action.cost
        rec(board, scoreBoard, action.position, end, action.direction)
    }
}

fun visit(
    visitBoard: Board<Boolean>,
    currPosition: Pt,
    currDirection: Direction,
    turns: Int,
    scoreBoard: Board<Int>,
    finish: Pt,
    allowedTurns: Int
) {
    visitBoard[currPosition] = true
    if (currPosition == finish) return

    for (nextDirection in nextDirections(scoreBoard, currPosition)) {
        val nextTurns = if (nextDirection == currDirection) turns else turns + 1
        val nextPosition = currPosition + nextDirection
        val neighborTurns = scoreBoard[nextPosition] / 1000
        if (nextTurns + neighborTurns > allowedTurns) continue

        visit(visitBoard, nextPosition, nextDirection, nextTurns, scoreBoard, finish, allowedTurns)
    }
}

fun nextActions(board: Board<Char>, pos: Pt, direction: Direction): List<Action> = Direction.entries.asSequence()
    .filter { it != direction.opposite && board[pos + it] == EMPTY }
    .map { Action(pos + it, if (direction == it) 1 else 1001, it) }
    .toList()

fun nextDirections(scoreBoard: Board<Int>, pos: Pt): List<Direction> = Direction.entries.asSequence()
    .filter { (scoreBoard[pos] % 1000) - 1 == scoreBoard[pos + it] % 1000 }
    .toList()

fun calculateVisited(visitBoard: Board<Boolean>): Int = visitBoard
    .sumOf { row -> row.count { it } }

fun main() {
    val (board, start, end) = readInput()
    val scoreBoard = MutableList(board.size) { MutableList(board[0].size) { Int.MAX_VALUE } }
    scoreBoard[start] = 0
    rec(board, scoreBoard, start, end, Direction.RIGHT)
    val visitBoard = MutableList(board.size) { MutableList(board[0].size) { false } }
    val allowedTurns = scoreBoard[end] / 1000
    visit(visitBoard, end, Direction.LEFT, 0, scoreBoard, start, allowedTurns)
    visit(visitBoard, end, Direction.DOWN, 0, scoreBoard, start, allowedTurns)
    println("Part 1: ${scoreBoard[end]}")
    println("Part 2: ${calculateVisited(visitBoard)}")
}
