package reehi.board.like.repository

import org.springframework.data.jpa.repository.JpaRepository
import reehi.board.like.entity.ArticleLike

interface ArticleLikeRepository: JpaRepository<ArticleLike, Long> {

    fun findByArticleIdAndUserId(articleId: Long, userId: Long): ArticleLike?
}