package reehi.board.articleread.cache

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import java.time.Duration
import kotlin.test.Test

class OptimizedCacheTTLTest {
    @Test
    fun ofTest() {
        // given
        val ttlSeconds: Long = 10

        // when
        val optimizedCacheTTL = OptimizedCacheTTL.of(ttlSeconds)

        // then
        assertThat(optimizedCacheTTL.logicalTTL).isEqualTo(Duration.ofSeconds(ttlSeconds))
        assertThat(optimizedCacheTTL.physicalTTL).isEqualTo(
            Duration.ofSeconds(ttlSeconds).plusSeconds(OptimizedCacheTTL.PHYSICAL_TTL_DELAY_SECONDS)
        )
    }
}