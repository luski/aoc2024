package day18

import java.util.*

typealias Pt = Pair<Int, Int>
typealias Board<T> = MutableList<MutableList<T>>

const val EMPTY = '.'
const val WALL = '#'

operator fun Pt.plus(pt: Pt): Pt = pt.first + this.first to pt.second + this.second

fun Pt.inBounds(
    w: Int,
    h: Int,
): Boolean {
    val (x, y) = this
    return x in 0..<w && y in 0..<h
}

class Day18(
    val W: Int,
    val H: Int,
) {
    val points: List<Pt> =
        generateSequence(::readlnOrNull)
            .takeWhile(String::isNotBlank)
            .map {
                it
                    .split(",")
                    .map(String::toInt)
                    .let { (x, y) -> x to y }
            }.toList()

    fun findPath(size: Int): Int {
        val board = MutableList(H) { MutableList(W) { EMPTY } }
        val visited = MutableList(H) { MutableList(W) { EMPTY } }
        val distances = MutableList(H) { MutableList(W) { 0 } }

        for ((x, y) in points.subList(0, size)) {
            board[y][x] = WALL
        }

        val queue = LinkedList<Pt>()
        queue.push(0 to 0)

        while (queue.isNotEmpty()) {
            val pt = queue.pollFirst()
            val (x, y) = pt
            val nextDistance = distances[y][x] + 1
            for (neighbor in neighbors(pt, board, visited)) {
                val (nx, ny) = neighbor
                if (visited[ny][nx] == WALL) continue
                visited[ny][nx] = WALL
                distances[ny][nx] = nextDistance
                queue.add(neighbor)
            }
        }
        return distances[H - 1][W - 1]
    }

    fun solve1() = findPath(1024)

    fun solve2(): Pt {
        val (index, pt) =
            points
                .withIndex()
                .toList()
                .firstOrNull { (index) -> findPath(index) == 0 } ?: error("Not found")

        return points[index - 1]
    }

    val directions = listOf(0 to 1, 0 to -1, -1 to 0, 1 to 0)

    fun neighbors(
        pt: Pt,
        board: Board<Char>,
        visited: Board<Char>,
    ): List<Pt> =
        directions
            .map { pt + it }
            .filter { it.inBounds(W, H) }
            .filter { board[it.second][it.first] == EMPTY }
            .filter { visited[it.second][it.first] == EMPTY }
}

fun main() {
    val solution = Day18(71, 71)
    println("Part 1: ${solution.solve1()}")
    println("Part 2: ${solution.solve2()}")
}
