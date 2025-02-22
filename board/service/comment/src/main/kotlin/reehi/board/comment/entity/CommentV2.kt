package reehi.board.comment.entity

import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Table(name = "comment_v2")
@Entity
class CommentV2 (
    @Id
    val commentId: Long,
    var content: String,
    val articleId: Long, //shard key
    val writerId: Long,
    @Embedded
    val commentPath: CommentPath,
    var deleted: Boolean,
    val createdAt: LocalDateTime,
){
}