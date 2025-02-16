package reehi.board.comment.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import reehi.board.comment.entity.Comment

interface CommentRepository : JpaRepository<Comment, Long> {

    @Query(
        value = "select count(*) from (" +
                "   select comment_id from comment " +
                "   where artcile_id = :articleId and parent_comment_id = :parentCommentId " +
                "   limit :limit" +
                ") t"
        , nativeQuery = true
    )
    fun countBy(
        @Param("articleId") articleId: Long,
        @Param("parentCommentId") parentCommentId: Long,
        @Param("limit") limit: Long
        ): Long
}