package reehi.board.article.service

import kotlin.test.Test
import kotlin.test.assertEquals

class PageLimitCalculatorTest {

    @Test
    fun calculatePageLimitTest() {
        calculatePageLimitTest(1L,30L, 10L, 301L);
        calculatePageLimitTest(7L,30L, 10L, 301L);
        calculatePageLimitTest(10L,30L, 10L, 301L);
        calculatePageLimitTest(11L,30L, 10L, 601L);
        calculatePageLimitTest(12L,30L, 10L, 601L);
    }

    fun calculatePageLimitTest(page: Long, pageSize: Long, moveablePageCount: Long, expected: Long) {
        val result = PageLimitCalculator.calculatePageLimit(page, pageSize, moveablePageCount)

        assertEquals(expected, result)
    }
}