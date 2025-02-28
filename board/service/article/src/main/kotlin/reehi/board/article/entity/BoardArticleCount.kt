package reehi.board.article.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "board_article_count")
class BoardArticleCount (
    @Id
    var boardId: Long, // shard key
    var articleCount: Long

){
    companion object {
        fun init(boardId: Long, articleCount: Long): BoardArticleCount =
            BoardArticleCount(
                boardId = boardId,
                articleCount = articleCount
            )
    }

}