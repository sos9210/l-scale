package reehi.board.comment.service

import kuke.board.common.snowflake.Snowflake
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reehi.board.comment.entity.Comment
import reehi.board.comment.repository.CommentRepository
import reehi.board.comment.service.request.CommentCreateRequest
import reehi.board.comment.service.response.CommentPageResponse
import reehi.board.comment.service.response.CommentResponse
import java.util.*
import java.util.function.Predicate.not

@Service
class CommentService(
    val commentRepository: CommentRepository,
) {
    val snowflake: Snowflake = Snowflake()

    @Transactional
    fun create(request: CommentCreateRequest) : CommentResponse {
        val parent :Comment? = findParent(request);
        val comment = commentRepository.save(
            Comment.create(
                snowflake.nextId(),
                request.content,
                parent?.parentCommentId,
                request.articleId,
                request.writerId,
            )
        )

        return CommentResponse.from(comment)
    }

    private fun findParent(request: CommentCreateRequest): Comment? {
        if(request.parentCommentId == null) {
            return null;
        }
        return  commentRepository.findById(request.parentCommentId)
                .filter(not(Comment::deleted))
                .filter(Comment::isRoot)
                .orElseThrow();

    }

    fun read(commentId: Long) : CommentResponse {
        val comment = commentRepository.findById(commentId).orElseThrow()
        return CommentResponse.from(comment)
    }

    @Transactional
    fun delete(commentId: Long) {
        val comment = commentRepository.findByIdOrNull(commentId)
            ?.takeIf { !it.deleted }
            ?.let {comment ->
                if(hasChildren(comment)) {
                    comment.delete()
                } else {
                    delete(comment)
                }
            }
    }
    //자식코멘트가 있는지 확인
    private fun hasChildren(comment: Comment): Boolean =
        commentRepository.countBy(comment.articleId,comment.commentId, 2L) == 2L //(자신과 자식코멘트)


    private fun delete(comment: Comment) {
        commentRepository.delete(comment)
        if(!comment.isRoot()) {
            commentRepository.findByIdOrNull(comment.parentCommentId)
            ?.takeIf { it.deleted }
            ?.let {
                if(!hasChildren(it)) {
                    delete(it)
                }
            }
        }
    }

    fun readAll(articleId: Long, page: Long, pageSize: Long): CommentPageResponse =
        CommentPageResponse.of(
            commentRepository.findAll(articleId, (page - 1) * pageSize, pageSize)
                .map { CommentResponse.from(it) }.toList(),
            commentRepository.count(articleId, PageLimitCalculator.calculatePageLimit(page, pageSize, 10L))
        )

    fun readAll (articleId: Long, lastParentCommentId: Long?, lastCommentId: Long?, limit: Long):List<CommentResponse> {
        val result =
            if(lastParentCommentId == null|| lastCommentId == null) commentRepository.findAllInfiniteScroll(articleId, limit)
            else commentRepository.findAllInfiniteScroll(articleId,lastParentCommentId,lastCommentId,limit)

        return result.map { CommentResponse.from(it) }.toList()
    }
}