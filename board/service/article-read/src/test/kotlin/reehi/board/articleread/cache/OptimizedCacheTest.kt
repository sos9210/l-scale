package reehi.board.articleread.cache

import org.assertj.core.api.Assertions.assertThat
import java.time.Duration
import kotlin.test.Test

class OptimizedCacheTest {
    @Test
    fun parseDataTest() {
        parseDataTest("data", 10)
        parseDataTest(3L, 10)
        parseDataTest(3, 10)
        parseDataTest(TestClass("hihi"), 10)
    }

    fun parseDataTest(data: Any, ttlSeconds: Long) {
        // given
        val optimizedCache: OptimizedCache = OptimizedCache.of(data, Duration.ofSeconds(ttlSeconds))
        println("optimizedCache = $optimizedCache")

        // when
        val resolvedData = optimizedCache.parseData(data.javaClass)

        // then
        println("resolvedData = $resolvedData")
        assertThat(resolvedData).isEqualTo(data)
    }

    @Test
    fun isExpiredTest() {
        assertThat(OptimizedCache.of("data", Duration.ofDays(-30)).isExpired()).isTrue()
        assertThat(OptimizedCache.of("data", Duration.ofDays(30)).isExpired()).isFalse()
    }
    companion object {
        class TestClass (
            var testData: String? = null
        )
    }
}