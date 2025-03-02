package reehi.board.view.repository

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import reehi.board.view.entity.ArticleViewCount
import kotlin.test.Test
import kotlin.test.assertEquals


@SpringBootTest
class ArticleViewCountBackUpRepositoryTest (
    @Autowired
    val articleViewCountBackUpRepository: ArticleViewCountBackUpRepository,

    @Autowired
    val entityManager: EntityManager
){

    @Test
    @Transactional
    fun updateViewCountTest() {
        // given
        articleViewCountBackUpRepository.save(
            ArticleViewCount.init(1L, 0L)
        )
        entityManager.flush()
        entityManager.clear()

        // when
        val result1: Int = articleViewCountBackUpRepository.updateViewCount(1L, 100L)
        val result2: Int = articleViewCountBackUpRepository.updateViewCount(1L, 300L)
        val result3: Int = articleViewCountBackUpRepository.updateViewCount(1L, 200L)

        // then
        assertEquals(result1,1)
        assertEquals(result2,1)
        assertEquals(result3,0)

        val articleViewCount: ArticleViewCount = articleViewCountBackUpRepository.findById(1L).get()
        assertEquals(articleViewCount.viewCount,300L)
    }
}