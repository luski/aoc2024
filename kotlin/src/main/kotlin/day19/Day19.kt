package day19

val MAPPING = arrayOf('r', 'g', 'b', 'u', 'w')

operator fun Array<TreeNode?>.get(ch: Char): TreeNode? = this[MAPPING.indexOf(ch)]

operator fun Array<TreeNode?>.set(
    ch: Char,
    node: TreeNode?,
) {
    this[MAPPING.indexOf(ch)] = node
}

class TreeNode(
    val children: Array<TreeNode?> = arrayOfNulls(MAPPING.size),
) {
    var isEnd: Boolean = false
}

class Tree(
    patterns: List<String>,
) {
    val root = TreeNode()

    init {
        patterns.forEach(::addPattern)
    }

    fun addPattern(pattern: String) {
        var node = root
        for (c in pattern) {
            node = node.children[c] ?: TreeNode().also { node.children[c] = it }
        }
        node.isEnd = true
    }
}

class Solution {
    val patterns =
        readln()
            .split(", ")
            .also { readln() }

    val tree = Tree(patterns)

    val designs =
        generateSequence(::readlnOrNull)
            .takeWhile(String::isNotBlank)
            .toList()

    val cache = hashMapOf<Pair<String, TreeNode>, Long>()

    fun matches(
        design: String,
        node: TreeNode = tree.root,
    ): Long {
        if (cache.contains(design to node)) return cache.get(design to node)!!
        if (design.isEmpty()) return if (node.isEnd) 1 else 0

        val c = design.first()
        val rest = design.substring(1)

        var result = 0L
        node.children[c]?.let { result += matches(rest, it) }
        if (node.isEnd) {
            tree.root.children[c]?.let { result += matches(rest, it) }
        }
        cache[design to node] = result
        return result
    }

    fun solve() {
        val result1 = designs.count { matches(it) > 0 }
        println("Part 1: $result1")

        val result2 = designs.sumOf(::matches)
        println("Part 2: $result2")
    }
}

fun main() {
    val solution = Solution()
    solution.solve()
}
