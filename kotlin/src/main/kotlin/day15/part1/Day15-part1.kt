package day15.part1

private const val EMPTY = '.'
private const val WALL = '#'
private const val ROBOT = '@'
private const val BOX = 'O'

typealias Board = MutableList<MutableList<Char>>
typealias Pt = Pair<Int, Int>

data class InputData(val board: Board, val moves: List<Char>, val initialPosition: Pt)

operator fun Pt.plus(other: Pt): Pt =
    this.first + other.first to this.second + other.second

operator fun Board.get(pos: Pt): Char = this[pos.first][pos.second]

fun Board.swap(source: Pt, target: Pt) {
    val tmp = this[source.first][source.second]
    this[source.first][source.second] = this[target.first][target.second]
    this[target.first][target.second] = tmp
}

fun readInput(): InputData {
    val board: Board = mutableListOf<MutableList<Char>>()
    var initialPosition = 0 to 0

    var row = 0
    while (true) {
        val line = readlnOrNull() ?: break
        if (line.isBlank()) break
        val col = line.indexOf(ROBOT)
        if (col != -1) {
            initialPosition = row to col
        }
        board.add(line.replace(ROBOT, EMPTY).toMutableList())
        row++
    }

    val moves = generateSequence(::readlnOrNull)
        .takeWhile(String::isNotBlank)
        .flatMap(String::asSequence)
        .toList()

    return InputData(board, moves, initialPosition)
}

fun main() {
    val (board, moves, initialPosition) = readInput()
    var position = initialPosition
    for (direction in moves) {
        position = performMove(board, position, direction)
    }
//    day15.part1.day15.part2.printBoard(board)
    println(calculate(board))
}

private val OFFSETS: Map<Char, Pt> = mapOf(
    '<' to (0 to -1),
    '>' to (0 to 1),
    '^' to (-1 to 0),
    'v' to (1 to 0),
)


fun nextPosition(position: Pt, direction: Char): Pt {
    val offset = OFFSETS[direction] ?: error("Unexpected move character '$direction'")
    return position + offset
}

fun performMove(board: Board, position: Pt, direction: Char): Pt {
    val nextRobotPosition = nextPosition(position, direction)
    var nextFreePos = nextRobotPosition

    while (board[nextFreePos] != EMPTY) {
        val element = board[nextFreePos]
        if (element == WALL) {
            return position
        }
        nextFreePos = nextPosition(nextFreePos, direction)
    }

    if (nextFreePos != nextRobotPosition) {
        board.swap(nextRobotPosition, nextFreePos)
    }
    return nextRobotPosition
}

fun calculate(board: Board): Int {
    return board.withIndex().sumOf { (y, row) ->
        row.withIndex()
            .filter { (_, char) -> char == BOX }
            .sumOf { (x, _) -> 100 * y + x }
    }

}

fun printBoard(board: Board) {
    for (row in board) {
        println(row.joinToString(separator = ""))
    }
}