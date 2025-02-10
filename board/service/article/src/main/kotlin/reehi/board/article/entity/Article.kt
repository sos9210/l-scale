package reehi.board.article.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "article")
class Article (
    @Id
    val articleId: Long,
    var title: String,
    var content: String,
    var boardId: Long,
    val writerId: Long,
    val createdAt: LocalDateTime,
    var modifiedAt: LocalDateTime
){

    fun update (title: String, content: String){
        this.title = title;
        this.content = content;
        this.modifiedAt = LocalDateTime.now();
    }
    companion object {
        fun create(articleId: Long, title: String, content: String, boardId: Long, writerId: Long): Article =
            Article(articleId, title, content, boardId, writerId,LocalDateTime.now(),LocalDateTime.now())
    }
}