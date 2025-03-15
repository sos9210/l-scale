package reehi.board.comment.service

import kuke.board.common.snowflake.Snowflake
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reehi.board.comment.entity.ArticleCommentCount
import reehi.board.comment.entity.CommentPath
import reehi.board.comment.entity.CommentV2
import reehi.board.comment.repository.ArticleCommentCountRepository
import reehi.board.comment.repository.CommentRepositoryV2
import reehi.board.comment.service.request.CommentCreateRequestV2
import reehi.board.comment.service.response.CommentPageResponse
import reehi.board.comment.service.response.CommentResponse
import reehi.board.common.event.EventType
import reehi.board.common.event.payload.CommentCreatedEventPayload
import reehi.board.common.event.payload.CommentDeletedEventPayload
import reehi.board.common.outboxmessagerelay.OutboxEventPublisher
import java.util.function.Predicate.not


@Service
class CommentServiceV2 (
    val commentRepository: CommentRepositoryV2,
    val articleCommentCountRepository: ArticleCommentCountRepository,
    val outboxEventPublisher: OutboxEventPublisher
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
        val result = articleCommentCountRepository.increase(request.articleId)
        if (result == 0) {
            articleCommentCountRepository.save(ArticleCommentCount.init(request.articleId, 1L))
        }

        outboxEventPublisher.publish(
            EventType.COMMENT_CREATED,
            CommentCreatedEventPayload(
                commentId = comment.commentId,
                content = comment.content,
                articleId = comment.articleId,
                writerId = comment.writerId,
                deleted = comment.deleted,
                createdAt = comment.createdAt,
                articleCommentCount = count(comment.articleId),
                path = null
            ),
            comment.articleId
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
                outboxEventPublisher.publish(
                    EventType.COMMENT_DELETED,
                    CommentDeletedEventPayload(
                        commentId = comment.commentId,
                        content = comment.content,
                        articleId = comment.articleId,
                        writerId = comment.writerId,
                        deleted = comment.deleted,
                        createdAt = comment.createdAt,
                        articleCommentCount = count(comment.articleId),
                        path = null
                    ),
                    comment.articleId
                )
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
        articleCommentCountRepository.decrease(comment.articleId)
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


    fun count(articleId: Long): Long =
        articleCommentCountRepository.findByIdOrNull(articleId)?.commentCount ?: 0L

}