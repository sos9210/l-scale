package reehi.board.comment.service

import kuke.board.common.snowflake.Snowflake
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reehi.board.comment.entity.CommentPath
import reehi.board.comment.entity.CommentV2
import reehi.board.comment.repository.CommentRepositoryV2
import reehi.board.comment.service.request.CommentCreateRequestV2
import reehi.board.comment.service.response.CommentPageResponse
import reehi.board.comment.service.response.CommentResponse
import java.util.function.Predicate.not


@Service
class CommentServiceV2 (
    val commentRepository: CommentRepositoryV2
){
    val snowflake: Snowflake = Snowflake()

    @Transactional
    fun create(request: CommentCreateRequestV2): CommentResponse {
        val parent = findParent(request)
        val parentCommentPath = parent?.commentPath ?: CommentPath.create("")
        val comment = commentRepository.save(
            CommentV2.create(
                snowflake.nextId(),
                request.content,
                request.articleId,
                request.writerId,
                parentCommentPath.createChildCommentPath(
                    commentRepository.findDescendantsTopPath(request.articleId, parentCommentPath.path)
                )
            )
        )

        return CommentResponse.from(comment)
    }

    private fun findParent(request: CommentCreateRequestV2): CommentV2? {
        val parentPath = request.parentPath ?: return null
        return commentRepository.findByPath(parentPath)
            ?.takeIf { !it.deleted }
            ?: throw IllegalStateException()
    }

    fun read(commentId: Long): CommentResponse {
        return CommentResponse.from(
            commentRepository.findById(commentId).orElseThrow()
        )
    }

    @Transactional
    fun delete(commentId: Long) {
        commentRepository.findById(commentId)
            .filter(not(CommentV2::deleted))
            .ifPresent { comment: CommentV2 ->
                if (hasChildren(comment)) {
                    comment.delete()
                } else {
                    delete(comment)
                }
            }
    }

    private fun hasChildren(comment: CommentV2): Boolean {
        return !commentRepository.findDescendantsTopPath(
            comment.articleId,
            comment.commentPath.path
        ).isNullOrEmpty()
    }

    private fun delete(comment: CommentV2) {
        commentRepository.delete(comment)
        if (!comment.isRoot()) {
            commentRepository.findByPath(comment.commentPath.parentPath())
                        ?.takeIf { !it.deleted }
                        ?.takeIf { hasChildren(it)  }?.delete()
                //.ifPresent(this::delete)
        }
    }

    fun readAll(articleId: Long, page: Long, pageSize: Long): CommentPageResponse {
        return CommentPageResponse.of(
            commentRepository.findAll(articleId, (page - 1) * pageSize, pageSize)
                .map(CommentResponse::from)
                .toList(),
            commentRepository.count(articleId, PageLimitCalculator.calculatePageLimit(page, pageSize, 10L))
        )
    }

    fun readAllInfiniteScroll(articleId: Long, lastPath: String?, pageSize: Long): List<CommentResponse> {
        val comments =
            if (lastPath == null) commentRepository.findAllInfiniteScroll(articleId, pageSize)
            else commentRepository.findAllInfiniteScroll(articleId, lastPath, pageSize)

        return comments.map(CommentResponse::from).toList()
    }

}