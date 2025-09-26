package day23

typealias Graph = MutableMap<String, MutableSet<String>>
typealias Edge = Pair<String, String>

fun Graph.contains(edge: Edge): Boolean = this[edge.first]!!.contains(edge.second)

fun Graph.clone(): Graph =
    entries
        .associate { it.key to it.value.toMutableSet() }
        .toMutableMap()

fun Graph.removeNode(node: String) {
    remove(node)
    values.forEach { it.remove(node) }
}

fun readGraph(): Graph {
    val graph: Graph = mutableMapOf()

    generateSequence(::readlnOrNull)
        .takeWhile(String::isNotBlank)
        .forEach { line ->
            val (from, to) = line.split("-")
            graph.getOrPut(from) { mutableSetOf() } += to
            graph.getOrPut(to) { mutableSetOf() } += from
        }

    return graph
}

fun bronKerboschAlgo(
    graph: Graph,
    P: Set<String>,
    R: Set<String>,
    X: Set<String>,
    maxClique: Set<String>,
): Set<String> {
    if (P.isEmpty()) {
        if (X.isEmpty()) {
            if (R.size > maxClique.size) return R
        }
        return maxClique
    }

    var maxClique2 = maxClique
    val P2 = P.toMutableSet()
    val X2 = X.toMutableSet()

    for (v in P) {
        val N = graph[v]!!
        maxClique2 = bronKerboschAlgo(graph, P2.intersect(N), R + v, X2.intersect(N), maxClique2)
        X2 += v
        P2 -= v
    }

    return maxClique2
}

fun solvePart1(graphOriginal: Graph) {
    val graph = graphOriginal.clone()
    val desiredKeys =
        graph.keys
            .filter { it.startsWith('t') }
            .toMutableList()

    var result = 0

    while (desiredKeys.isNotEmpty()) {
        val tNode = desiredKeys.removeFirst()
        val neighbors = graph[tNode]!!.toList()
        for (i in 0..<neighbors.size) {
            for (j in (i + 1)..<neighbors.size) {
                if (graph.contains(neighbors[i] to neighbors[j])) {
                    result++
                }
            }
        }
        graph.removeNode(tNode)
    }

    println("Part 1: $result")
}

fun solvePart2(graph: Graph) {
    val result =
        bronKerboschAlgo(graph, graph.keys, setOf(), setOf(), setOf())
            .toList()
            .sorted()
            .joinToString(",")

    println("Part 2: $result")
}

fun main() {
    val graph = readGraph()
    solvePart1(graph)
    solvePart2(graph)
}
