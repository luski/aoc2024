package day16.part1

typealias Board = MutableList<MutableList<Char>>
typealias ScoreBoard = MutableList<MutableList<Int>>
typealias Pt = Pair<Int, Int>

const val START = 'S'
const val END = 'E'
const val EMPTY = '.'

// Board extensions
fun Board.extract(ch: Char): Pt = this.withIndex()
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
data class InputData(val board: Board, val start: Pt, val end: Pt)

fun main() {
    val (board, start, end) = readInput()
    val scoreBoard = MutableList(board.size) { MutableList(board[0].size) { Int.MAX_VALUE } }
    scoreBoard[start] = 0
    rec(board, scoreBoard, start, end, Direction.RIGHT)
    println(scoreBoard[end])
}

fun readInput(): InputData {
    val board: Board = generateSequence(::readlnOrNull).takeWhile(String::isNotBlank)
        .map(String::toMutableList)
        .toMutableList()

    val start = board.extract(START)
    val end = board.extract(END)

    return InputData(board, start, end)
}

fun rec(board: Board, scoreBoard: ScoreBoard, pos: Pt, end: Pt, direction: Direction) {
    if (pos == end) return

    for (action in nextActions(board, pos, direction)) {
        if (scoreBoard[action.position] <= scoreBoard[pos] + action.cost) continue
        scoreBoard[action.position] = scoreBoard[pos] + action.cost
        rec(board, scoreBoard, action.position, end, action.direction)
    }
}

fun nextActions(board: Board, pos: Pt, direction: Direction): List<Action> = Direction.entries.asSequence()
    .filter { it != direction.opposite && board[pos + it] == EMPTY }
    .map { Action(pos + it, if (direction == it) 1 else 1001, it) }
    .toList()

fun printBoard(board: Board) {
    board.forEach { println(it.joinToString("")) }
}