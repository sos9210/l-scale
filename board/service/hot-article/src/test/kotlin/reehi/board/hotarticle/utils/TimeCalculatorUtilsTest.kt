package reehi.board.hotarticle.utils

import org.junit.jupiter.api.Assertions.*
import java.time.Duration
import kotlin.test.Test

class TimeCalculatorUtilsTest {

    @Test
    fun test() {
        val duration: Duration = TimeCalculatorUtils.calculateDurationToMidnight()
        println("duration.getSeconds() / 60 = " + duration.getSeconds() / 60)
    }
}
