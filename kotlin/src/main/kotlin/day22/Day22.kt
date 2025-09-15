package day22

fun nextNumberV1(number: Long): Long {
    val step1 = number shl 6 // multiply by 64
    val step2 = step1.xor(number) // mix
    val step3 = step2 and 0xFFFFFF // prune
    return step3
}

fun nextNumberV2(number: Long): Long {
    val step1 = number shr 5 // divide by 32
    val step2 = step1.xor(number) // mix
    val step3 = step2 and 0xFFFFFF // prune
    return step3
}

fun nextNumberV3(number: Long): Long {
    val step1 = number shl 11 // multiply by 2048
    val step2 = step1.xor(number) // mix
    val step3 = step2 and 0xFFFFFF // prune
    return step3
}

fun nextNumber(number: Long): Long = nextNumberV3(nextNumberV2(nextNumberV1(number)))

data class Seq(
    val a: Int,
    val b: Int,
    val c: Int,
    val d: Int,
)

fun first2000(initialNumber: Long): Long {
    var result = initialNumber
    for (i in 1..2000) {
        result = nextNumber(result)
    }
    return result
}

fun first2000Sequence(initialNumber: Long): Sequence<Long> =
    sequence {
        var result = initialNumber
        for (i in 1..2000) {
            result = nextNumber(result)
            yield(result)
        }
    }

fun List<Int>.toSeq(): Seq =
    Seq(
        this[1] - this[0],
        this[2] - this[1],
        this[3] - this[2],
        this[4] - this[3],
    )

fun main() {
    val initialSecretNumbers =
        generateSequence(::readlnOrNull)
            .takeWhile(String::isNotBlank)
            .map(String::toLong)
            .toList()

    val result =
        initialSecretNumbers.sumOf(::first2000)
    println("Part 1: $result")

    val globalMapping = mutableMapOf<Seq, Long>()

    for (initialNumber in initialSecretNumbers) {
        val mapping = mutableMapOf<Seq, Int>()
        first2000Sequence(initialNumber)
            .map { (it % 10).toInt() }
            .windowed(5, 1, false)
            .forEach { window ->
                val key = window.toSeq()
                if (!mapping.contains(key)) {
                    mapping[key] = window.last()
                }
            }
        for ((key, value) in mapping) {
            globalMapping[key] = (globalMapping[key] ?: 0) + value
        }
    }

    val result2 = globalMapping.maxOf { it.value }
    println("Part 2: $result2")
}
