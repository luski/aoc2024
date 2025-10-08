package day24

typealias Rules = List<Rule>

private fun Rules.findRule(
    a: Rule,
    b: Rule,
    operation: Operation,
) = find { it.hasAll(a.output, b.output) && it.op == operation }

private fun Rules.findBrokenRules(
    a: Rule,
    b: Rule,
    operation: Operation,
) = filter { it.hasOneOf(a.output, b.output) && it.op == operation }

private fun Rules.findRule(
    n: Int,
    operation: Operation,
): Rule? {
    val args =
        listOf('x', 'y')
            .map {
                "$it${
                    n.toString()
                        .padStart(2, '0')
                }"
            }.toTypedArray()
    return find { rule -> rule.hasAll(*args) && rule.op == operation }
}

private fun Rules.tryToFixBrokenRule(
    a: Rule,
    b: Rule,
    operation: Operation,
): Pair<Rule, Pair<String, String>>? {
    val candidates = findBrokenRules(a, b, operation)
    if (candidates.size == 1) {
        val swappedArgs = candidates[0].fix(a.output, b.output)
        return candidates[0] to swappedArgs
    } else {
        return null
    }
}

private fun Rule.hasAll(vararg args: String) = this.arg1 in args && this.arg2 in args

private fun Rule.hasOneOf(vararg args: String) = this.arg1 in args || this.arg2 in args

private fun Rule.fix(vararg args: String) =
    when {
        !args.contains(arg1) -> {
            val removed = arg1
            arg1 = args.find { it != arg2 }!!
            arg1 to removed
        }

        !args.contains(arg2) -> {
            val removed = arg2
            arg2 = args.find { it != arg1 }!!
            arg2 to removed
        }

        else -> error("invalid args")
    }

fun main() {
    val size =
        generateSequence(::readlnOrNull)
            .takeWhile(String::isNotBlank)
            .toList()
            .count() / 2

    val rules =
        generateSequence(::readlnOrNull)
            .takeWhile(String::isNotBlank)
            .map { it.split(" ") }
            .map { Rule(it[0], it[2], Operation.valueOf(it[1]), it[4]) }
            .toMutableList()

    val offsetRule0 = rules.findRule(0, Operation.AND)
    requireNotNull(offsetRule0)
    val offsetRules = mutableListOf(offsetRule0)
    val result = mutableSetOf<String>()

    val getOrFixRule = { ruleA: Rule, ruleB: Rule, operation: Operation ->
        rules.findRule(ruleA, ruleB, operation) ?: rules
            .tryToFixBrokenRule(ruleA, ruleB, operation)
            ?.let {
                val (rule, swappedArgs) = it
                result += swappedArgs.toList()
                rule
            }
    }
    for (n in 1..<size) {
        val sumPart1 = rules.findRule(n, Operation.XOR)
        requireNotNull(sumPart1)
        val sumRule = getOrFixRule(sumPart1, offsetRules[n - 1], Operation.XOR)
        requireNotNull(sumRule)
        val offsetPart1 = rules.findRule(n, Operation.AND)
        val offsetPart2 = getOrFixRule(sumPart1, offsetRules[n - 1], Operation.AND)
        requireNotNull(offsetPart1)
        requireNotNull(offsetPart2)
        val offsetRule = getOrFixRule(offsetPart1, offsetPart2, Operation.OR)
        requireNotNull(offsetRule)
        offsetRules += offsetRule
    }

    println(
        result
            .toList()
            .sorted()
            .joinToString(","),
    )
}
