package day17

class Day17 {
    var regA = 0
    var regB = 0
    var regC = 0
    lateinit var ops: List<Int>

    fun readInput() {
        regA = parseRegister(readln())
        regB = parseRegister(readln())
        regC = parseRegister(readln())

        readln()
        ops = parseOperations(readln())
    }

    fun solve() {
        var i = 0
        val output = mutableListOf<Int>()

        while (i < ops.size) {
            val operand = ops[i + 1]
            when (ops[i]) {
                0 -> regA = dv(operand)
                1 -> regB = (regB xor operand)
                2 -> regB = combo(operand) % 8
                3 ->
                    if (regA != 0) {
                        i = operand
                        continue
                    }

                4 -> regB = regB xor regC
                5 -> output.add(combo(operand) % 8)
                6 -> regB = dv(operand)
                7 -> regC = dv(operand)
            }
            i += 2
        }

        println(output.joinToString(","))
    }

    fun dv(operand: Int) = regA / (1 shl combo(operand))

    fun combo(operand: Int): Int =
        when (operand) {
            in 0..3 -> operand
            4 -> regA
            5 -> regB
            6 -> regC
            else -> error("Unexpected combo operand: $operand")
        }

    fun parseRegister(line: String) =
        line
            .split(":")[1]
            .trim()
            .toInt()

    fun parseOperations(line: String) =
        line
            .split(":")[1]
            .trim()
            .split(",")
            .map(String::toInt)
}

fun main() {
    val solution = Day17()
    solution.readInput()
    solution.solve()
}
