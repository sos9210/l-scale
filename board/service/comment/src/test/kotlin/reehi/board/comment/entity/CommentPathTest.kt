package reehi.board.comment.entity

import kotlin.test.DefaultAsserter
import kotlin.test.Test
import kotlin.test.assertFailsWith

class CommentPathTest {

    @Test
    fun createChildCommentTest() {

        //00000 <- 생성
        createChildCommentTest(CommentPath.create(""),null,"00000")

        //00000
        //  00000 <- 생성
        createChildCommentTest(CommentPath.create("00000"),null,"0000000000")

        //00000
        //00001 <- 생성
        createChildCommentTest(CommentPath.create(""),"00000","00001")

        //0000z
        //  abcdz
        //      zzzzz
        //          zzzzz
        //  abce0   <- 생성
        createChildCommentTest(CommentPath.create("0000z"),"0000zabcdzzzzzzzzzzz","0000zabce0")
    }

    fun createChildCommentTest(commentPath: CommentPath, descendantsTopPath: String?, expectedChildPath: String) {
        val childCommentPath = commentPath.createChildCommentPath(descendantsTopPath)
        DefaultAsserter.assertEquals("childPath",expectedChildPath, childCommentPath.path)

    }

    @Test
    fun createChildCommentPathIfMaxDepthTest() {
        // 깊이 5를 초과해서 댓글생성은 불가능하다 .
        assertFailsWith<IllegalStateException> {
            CommentPath.create("zzzzz".repeat(5)).createChildCommentPath(null)
        }
    }

    @Test
    fun createChildCommentPathIfChunkOverflowTest() {
        // zzzzz이상 댓글을 생성할 수 없다.
        // given
        var commentPath:CommentPath = CommentPath.create("");

        // when, then
        assertFailsWith<IllegalStateException> {
            commentPath.createChildCommentPath("zzzzz")
        }
    }
}