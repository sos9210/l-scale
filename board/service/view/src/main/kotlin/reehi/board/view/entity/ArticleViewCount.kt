package reehi.board.view.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "article_view_count")
class ArticleViewCount(
    @Id
    var articleId: Long, // shard key
    var viewCount: Long

) {
    companion object {

        fun init(articleId: Long, viewCount: Long): ArticleViewCount = ArticleViewCount (
            articleId = articleId,
            viewCount = viewCount
        )

    }
}