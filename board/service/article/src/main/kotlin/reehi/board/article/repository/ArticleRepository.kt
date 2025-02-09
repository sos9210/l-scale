package reehi.board.article.repository

import org.springframework.data.jpa.repository.JpaRepository
import reehi.board.article.entity.Article

interface ArticleRepository : JpaRepository<Article,Long> {
}