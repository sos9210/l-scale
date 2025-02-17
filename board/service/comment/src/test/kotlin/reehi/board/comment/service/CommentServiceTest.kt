package reehi.board.comment.service

import io.mockk.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import reehi.board.comment.entity.Comment
import reehi.board.comment.repository.CommentRepository
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class CommentServiceTest {

    private val commentRepository = mockk<CommentRepository>()
    private val commentService = CommentService(commentRepository)
    @Test
    @DisplayName("삭제할 댓글의 자식이 있으면, 삭제 표시만 한다.")
    fun deleteShouldMarkDeletedIfHashChildren() {
        //given
        val articleId: Long = 1L
        val commentId: Long = 2L
        val comment = createComment(articleId, commentId)
        every { commentRepository.findByIdOrNull(commentId)} returns comment

        every { commentRepository.countBy(articleId,commentId,2L)} returns 2L

        //when
        commentService.delete(commentId);

        //then
        verify {comment.delete()}

    }
    @Test
    @DisplayName("하위 댓글이 삭제되고, 삭제되지 않은 부모면, 하위 댓글만 삭제한다.")
    fun deleteShouldDeleteChildOnlyIfNotDeletedParent() {
        //given
        val articleId: Long = 1L
        val commentId: Long = 2L
        val parentCommentId: Long = 1L
        val comment = createComment(articleId, commentId, parentCommentId)
        every { comment.isRoot() } returns false

        val parentComment = mockk<Comment>(relaxed  = true)
        every { parentComment.deleted } returns false



        every { commentRepository.findByIdOrNull(commentId)} returns comment
        every { commentRepository.countBy(articleId,commentId,2L)} returns 1L
        every {commentRepository.findByIdOrNull(parentCommentId)} returns parentComment
        every {commentRepository.delete(comment)} just runs

        //when
        commentService.delete(commentId);

        //then
        verify {commentRepository.delete(comment)}
        verify(exactly = 0) {commentRepository.delete(parentComment) }

    }
    @Test
    @DisplayName("하위 댓글이 삭제되고, 삭제된 부모면, 재귀적으로 부모까지 모두 삭제한다.")
    fun deleteShouldDeleteAllRecursivelyIfDeletedParent() {
        //given
        val articleId: Long = 1L
        val commentId: Long = 2L
        val parentCommentId: Long = 1L
        val comment = createComment(articleId, commentId, parentCommentId)
        every { comment.isRoot() } returns false

        val parentComment = createComment(articleId, parentCommentId)
        every { parentComment.isRoot() } returns true
        every { parentComment.deleted } returns true



        every { commentRepository.findByIdOrNull(commentId)} returns comment
        every { commentRepository.countBy(articleId,commentId,2L)} returns 1L

        every {commentRepository.findByIdOrNull(parentCommentId)} returns parentComment
        every { commentRepository.countBy(articleId,parentCommentId,2L)} returns 1L

        every {commentRepository.delete(comment)} just runs
        every {commentRepository.delete(parentComment)} just runs

        //when
        commentService.delete(commentId);

        //then
        verify {commentRepository.delete(comment)}
        verify {commentRepository.delete(parentComment) }

    }

    private fun createComment(articleId: Long, commentId: Long): Comment {
        return mockk<Comment>(relaxed  = true).also {
            every { it.articleId} returns articleId
            every { it.commentId} returns commentId
            every { it.delete() } just runs

        }
    }
    private fun createComment(articleId: Long, commentId: Long, parentCommentId: Long): Comment {
        return createComment(articleId, commentId).also {
            every { it.parentCommentId } returns parentCommentId
        }
    }

}
