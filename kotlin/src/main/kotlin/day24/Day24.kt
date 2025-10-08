package day24

enum class Operation {
    AND,
    OR,
    XOR,
}

data class Rule(
    var arg1: String,
    var arg2: String,
    val op: Operation,
    val output: String,
)

fun Rule.calculate(
    val1: Long,
    val2: Long,
): Long =
    when (this.op) {
        Operation.AND -> val1 and val2
        Operation.OR -> val1 or val2
        Operation.XOR -> val1 xor val2
    }

fun main() {
    val values =
        generateSequence(::readlnOrNull)
            .takeWhile(String::isNotBlank)
            .map { it.split(": ") }
            .map { it[0] to it[1].toLong() }
            .toMap()
            .toMutableMap()

    val rules =
        generateSequence(::readlnOrNull)
            .takeWhile(String::isNotBlank)
            .map { it.split(" ") }
            .map { Rule(it[0], it[2], Operation.valueOf(it[1]), it[4]) }
            .toMutableSet()

    while (rules.isNotEmpty()) {
        val iterator = rules.iterator()
        while (iterator.hasNext()) {
            val rule = iterator.next()
            val arg1Val = values[rule.arg1]
            val arg2Val = values[rule.arg2]
            if (arg1Val != null && arg2Val != null) {
                values[rule.output] = rule.calculate(arg1Val, arg2Val)
                iterator.remove()
            }
        }
    }

    val output =
        values.entries
            .filter { it.key.startsWith("z") }
            .sortedBy { it.key }
            .map { it.value }
            .toList()

    var result = 0L
    output
        .withIndex()
        .forEach { (index, item) ->
            result += item * 1 shl index
        }

    println(result)
}
