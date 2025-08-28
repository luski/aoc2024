package day17

class Day17 {
    var regA = 0L
    var regB = 0L
    var regC = 0L
    lateinit var ops: List<Int>

    fun readInput() {
        regA = parseRegister(readln())
        regB = parseRegister(readln())
        regC = parseRegister(readln())

        readln()
        ops = parseOperations(readln())
    }

    fun solvePart1() {
        var i = 0
        val output = mutableListOf<Long>()

        while (i < ops.size) {
            val operand = ops[i + 1]
            when (ops[i]) {
                0 -> regA = dv(operand)
                1 -> regB = (regB xor operand.toLong())
                2 -> regB = combo(operand) % 8
                3 ->
                    if (regA != 0L) {
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

        println("Part1: ${output.joinToString(",")}")
    }

    fun rec(
        acc: Long,
        operations: List<Int>,
        index: Int,
    ): Long? {
        if (index == operations.size) return acc

        for (candidate in 0..7) {
            val newAcc = nextAcc(candidate, acc, operations[index]) ?: continue

            val result = rec(newAcc, operations, index + 1)
            if (result != null) {
                return result
            }
        }

        return null
    }

    fun nextAcc(
        candidate: Int,
        acc: Long,
        op: Int,
    ): Long? {
        val regA = (acc shl 3) or candidate.toLong()

        // k = (A % 8) xor 5
        val k = ((regA and 7L).toInt() xor 5)

        // C = A / 2^k  → przesunięciem
        val shifted = regA ushr k

        // B = k xor C xor 6
        val b = (k.toLong() xor shifted xor 6L)

        if ((b and 7L).toInt() == op) {
            return regA
        }
        return null
    }

    fun solvePart2() {
        val result = rec(0, ops.asReversed(), 0)
        println("Part 2: $result")
    }

    fun dv(operand: Int) = regA / (1 shl combo(operand).toInt())

    fun combo(operand: Int): Long =
        when (operand) {
            in 0..3 -> operand.toLong()
            4 -> regA
            5 -> regB
            6 -> regC
            else -> error("Unexpected combo operand: $operand")
        }

    fun parseRegister(line: String) =
        line
            .split(":")[1]
            .trim()
            .toLong()

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
    solution.solvePart1()
    solution.solvePart2()
}
