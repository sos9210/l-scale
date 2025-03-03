package reehi.board.hotarticle.repository

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class HotArticleListRepositoryTest(
    private val hotArticleListRepository: HotArticleListRepository
) {

    @Test
    @Throws(InterruptedException::class)
    fun addTest() {
        // given
        val time = LocalDateTime.of(2024, 7, 23, 0, 0)
        val limit: Long = 3

        // when
        hotArticleListRepository.add(1L, time, 2L, limit, Duration.ofSeconds(3))
        hotArticleListRepository.add(2L, time, 3L, limit, Duration.ofSeconds(3))
        hotArticleListRepository.add(3L, time, 1L, limit, Duration.ofSeconds(3))
        hotArticleListRepository.add(4L, time, 5L, limit, Duration.ofSeconds(3))
        hotArticleListRepository.add(5L, time, 4L, limit, Duration.ofSeconds(3))
        val articleIds = hotArticleListRepository.readAll("20240723")


        // then
        assertEquals(articleIds.size, limit.toInt())
        assertEquals(articleIds[0],4)
        assertEquals(articleIds[1],5)
        assertEquals(articleIds[2],2)

        TimeUnit.SECONDS.sleep(5)

        assertTrue(hotArticleListRepository.readAll("20240723").isEmpty())
    }
}