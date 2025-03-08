package reehi.board.hotarticle.service

import org.springframework.stereotype.Component
import reehi.board.hotarticle.repository.ArticleCommentCountRepository
import reehi.board.hotarticle.repository.ArticleLikeCountRepository
import reehi.board.hotarticle.repository.ArticleViewCountRepository


@Component
class HotArticleScoreCalculator(
    val articleLikeCountRepository: ArticleLikeCountRepository,
    val articleViewCountRepository: ArticleViewCountRepository,
    val articleCommentCountRepository: ArticleCommentCountRepository
    
) {
    
    companion object {
        const val ARTICLE_LIKE_COUNT_WEIGHT: Long = 3
        const val ARTICLE_COMMENT_COUNT_WEIGHT: Long = 2
        const val ARTICLE_VIEW_COUNT_WEIGHT: Long = 1
        
    }

    fun calculate(articleId: Long): Long {
        val articleLikeCount = articleLikeCountRepository.read(articleId)
        val articleViewCount = articleViewCountRepository.read(articleId)
        val articleCommentCount = articleCommentCountRepository.read(articleId)

        return articleLikeCount * ARTICLE_LIKE_COUNT_WEIGHT +
                articleViewCount * ARTICLE_VIEW_COUNT_WEIGHT +
                articleCommentCount * ARTICLE_COMMENT_COUNT_WEIGHT
    }
}