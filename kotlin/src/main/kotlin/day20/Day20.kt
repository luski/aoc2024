@file:Suppress("ktlint:standard:no-wildcard-imports")

package day20

import java.util.*
import kotlin.math.abs

typealias Board<T> = MutableList<MutableList<T>>
typealias Pt = Pair<Int, Int>

const val START = 'S'
const val END = 'E'
const val EMPTY = '.'

operator fun Pt.plus(pt: Pt): Pt = pt.first + this.first to pt.second + this.second

fun Pt.distance(pt: Pt): Int = abs(pt.first - this.first) + abs(pt.second - this.second)

fun Pt.inBounds(
    width: Int,
    height: Int,
): Boolean {
    val (row, col) = this
    return row in 0..<height && col in 0..<width
}

operator fun <T> Board<T>.get(pos: Pt) = this[pos.first][pos.second]

operator fun <T> Board<T>.set(
    pos: Pt,
    value: T,
) {
    this[pos.first][pos.second] = value
}

fun <T> Board<T>.extract(
    value: T,
    empty: T,
): Pt =
    this
        .withIndex()
        .firstNotNullOf { (row, line) ->
            line
                .indexOf(value)
                .takeIf { it != -1 }
                ?.let { col ->
                    row to col
                }
        }.also { this[it] = empty }

class Solution {
    val board =
        generateSequence(::readlnOrNull)
            .takeWhile(String::isNotBlank)
            .map(String::toMutableList)
            .toMutableList()

    val startPos: Pt = board.extract(START, EMPTY)
    val endPos: Pt = board.extract(END, EMPTY)
    val width = board[0].size
    val height = board.size

    val distances = MutableList(height) { MutableList(width) { Int.MAX_VALUE } }

    fun solve() {
        bfs()
        println("Part 1: ${revBfs(100, 2)}")
        println("Part 2: ${revBfs(100, 20)}")
    }

    fun bfs() {
        val visited = MutableList(height) { MutableList(width) { false } }
        val queue = LinkedList<Pt>()
        queue.add(endPos)
        distances[endPos] = 0
        visited[endPos] = true

        while (queue.isNotEmpty()) {
            val pt = queue.pollFirst()
            for (neighbor in neighbors(pt, board, visited)) {
                visited[neighbor] = true
                distances[neighbor] = distances[pt] + 1
                queue.add(neighbor)
            }
        }
    }

    fun neighborsInManhattanSeq(
        center: Pt,
        radius: Int,
    ): Sequence<Pt> =
        sequence {
            val (row, col) = center
            for (colOffset in -radius..radius) {
                val colReach = radius - abs(colOffset)
                for (rowOffset in -colReach..colReach) {
                    val point = row + rowOffset to col + colOffset
                    if (point.inBounds(width, height) && board[point] == EMPTY) {
                        yield(point)
                    }
                }
            }
        }

    fun revBfs(
        minImprovement: Int,
        radius: Int,
    ): Int {
        val revDistances = MutableList(height) { MutableList(width) { Int.MAX_VALUE } }
        var result = 0
        val maxDistance = distances[startPos]

        val visited = MutableList(height) { MutableList(width) { false } }
        revDistances[startPos] = 0
        val queue = LinkedList<Pt>()
        queue.add(startPos)
        visited[startPos] = true

        while (queue.isNotEmpty()) {
            val pt = queue.pollFirst()
            val nextDistance = revDistances[pt] + 1
            for (neighbor in neighbors(pt, board, visited)) {
                visited[neighbor] = true
                revDistances[neighbor] = nextDistance
                queue.add(neighbor)
                result +=
                    neighborsInManhattanSeq(pt, radius)
                        .filter {
                            distances[it] + nextDistance +
                                pt.distance(it) + minImprovement - 1 <= maxDistance
                        }.count()
            }
        }

        return result
    }

    val directions = listOf(0 to 1, 0 to -1, -1 to 0, 1 to 0)

    fun neighbors(
        pt: Pt,
        board: Board<Char>,
        visited: Board<Boolean>,
    ): List<Pt> =
        directions
            .map { pt + it }
            .filter { it.inBounds(width, height) && board[it] == EMPTY && !visited[it] }
}

fun main() {
    val solution = Solution()
    solution.solve()
}
