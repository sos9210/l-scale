package reehi.board.articleread.learning

import java.math.BigDecimal
import kotlin.test.Test

class LongToDoubleTest {
    @Test
    fun longToDoubleTest() {
        // long은 64비트로 정수
        // double은 64비트로 부동소수점
        val longValue = 111111111111111111L
        println("longValue = $longValue")
        val doubleValue = longValue.toDouble()
        println("doubleValue = " + BigDecimal(doubleValue).toString())
        val longValue2 = doubleValue.toLong()
        println("longValue2 = $longValue2")
    }
}