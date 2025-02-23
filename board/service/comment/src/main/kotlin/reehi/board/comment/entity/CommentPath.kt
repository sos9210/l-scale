package reehi.board.comment.entity

import jakarta.persistence.Embeddable

@Embeddable
class CommentPath(
    val path: String,

) {
    private fun depth(): Int = calDepth(path)

    fun isRoot(): Boolean = calDepth(path) == 1

    // 00000 00000 00000(path) -> 00000 00000(parent Path)
    fun parentPath(): String = path.substring(0,path.length - DEPTH_CHUNK_SIZE)

    private fun increase(path: String): String {
        // 00000 00000 -> 00000 00001
        val lastChunk = path.substring(path.length - DEPTH_CHUNK_SIZE)
        if (isChunkOverflowed(lastChunk)){
            throw IllegalStateException("chunk overflowed")
        }

        val charsetLength: Int = CHARSET.length
        var value: Int = 0
        for (ch in lastChunk.toCharArray()) {
            value = value * charsetLength + CHARSET.indexOf(ch)
        }

        value += 1

        var result: String = ""
        for (i in 0..< DEPTH_CHUNK_SIZE) {
            result = CHARSET[value % charsetLength] + result
            value /= charsetLength
        }
        // path.substring(0, path.length - DEPTH_CHUNK_SIZE) -> 상위댓글경로정보 00000 00000
        // result -> lastChunk에서 1을 더한 값
        return path.substring(0, path.length - DEPTH_CHUNK_SIZE) + result
    }

    private fun isChunkOverflowed(lastChunk: String): Boolean =
        MAX_CHUNK == lastChunk

    private fun findChildrenTopPath(descendantsTopPath: String): String =
        descendantsTopPath.substring(0, (depth() + 1 ) * DEPTH_CHUNK_SIZE)


    fun createChildCommentPath(descendantsTopPath: String?): CommentPath {
        if(descendantsTopPath == null) {
            return create(path + MIN_CHUNK)
        }
        val childrenTopPath: String = findChildrenTopPath(descendantsTopPath)
        return create(increase(childrenTopPath))
    }
    companion object {
        private const val CHARSET: String = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        private const val DEPTH_CHUNK_SIZE: Int = 5
        private const val MAX_DEPTH: Int = 5

        // MIN_CHUNK = "00000", MAX_CHUNK = "ZZZZZ"
        val MIN_CHUNK: String = CHARSET[0].toString().repeat(DEPTH_CHUNK_SIZE)
        val MAX_CHUNK: String = CHARSET[CHARSET.length - 1].toString().repeat(DEPTH_CHUNK_SIZE)

        fun create(path: String): CommentPath {
            if(isDepthOverflowed(path)){
                throw IllegalStateException("Depth overflow: $path")
            }
            return CommentPath(path)
        }

        private fun isDepthOverflowed(path: String): Boolean = calDepth(path) > MAX_DEPTH

        //25 / 5 = 5 (5 depth)
        private fun calDepth(path: String): Int = path.length / DEPTH_CHUNK_SIZE
    }
}