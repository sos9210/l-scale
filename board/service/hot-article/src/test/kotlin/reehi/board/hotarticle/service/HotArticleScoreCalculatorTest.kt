package reehi.board.hotarticle.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reehi.board.hotarticle.repository.ArticleCommentCountRepository
import reehi.board.hotarticle.repository.ArticleLikeCountRepository
import reehi.board.hotarticle.repository.ArticleViewCountRepository
import kotlin.random.Random

@ExtendWith(MockKExtension::class)
class HotArticleScoreCalculatorTest {
    @InjectMockKs
    lateinit var hotArticleScoreCalculator: HotArticleScoreCalculator
    @MockK
    lateinit var articleLikeCountRepository: ArticleLikeCountRepository
    @MockK
    lateinit var articleViewCountRepository: ArticleViewCountRepository
    @MockK
    lateinit var articleCommentCountRepository: ArticleCommentCountRepository

    @Test
    fun calculateTest() {
        // given
        val articleId = 1L
        val likeCount = Random.nextLong(100)
        val commentCount = Random.nextLong(100)
        val viewCount = Random.nextLong(100)
        every { articleLikeCountRepository.read(articleId) } returns likeCount
        every { articleViewCountRepository.read(articleId) } returns viewCount
        every { articleCommentCountRepository.read(articleId) } returns commentCount

        // when
        val score = hotArticleScoreCalculator.calculate(articleId)

        // then
        assertThat(score)
            .isEqualTo(3 * likeCount + 2 * commentCount + viewCount)
    }
}
