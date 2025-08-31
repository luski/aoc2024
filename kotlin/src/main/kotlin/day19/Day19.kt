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
            val child = node.children[c]
            if (child != null) {
                node = child
            } else {
                val newNode = TreeNode()
                node.children[c] = newNode
                node = newNode
            }
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

    val cache = hashMapOf<Pair<String, TreeNode>, Boolean>()

    fun matches(
        design: String,
        node: TreeNode = tree.root,
    ): Boolean {
        if (cache.contains(design to node)) return cache.get(design to node)!!
        if (design.isEmpty()) return node.isEnd

        val c = design.first()
        val rest = design.substring(1)

        var nextNode = node.children[c]

        if (nextNode != null) {
            if (matches(rest, nextNode)) {
                cache[design to node] = true
                return true
            }
        }

        if (node.isEnd) {
            nextNode = tree.root.children[c]
            if (nextNode == null) {
                cache[design to node] = false
                return false
            }
            val result = matches(rest, nextNode)
            cache[design to node] = result
            return result
        }
        cache[design to node] = false
        return false
    }

    fun solve() {
        val result =
            designs
                .filter(::matches)
                .size
        println("Part 1: $result")
    }
}

fun main() {
    val solution = Solution()
    solution.solve()
}
