package reehi.board.like.service

import kuke.board.common.snowflake.Snowflake
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reehi.board.like.entity.ArticleLike
import reehi.board.like.entity.ArticleLikeCount
import reehi.board.like.repository.ArticleLikeCountRepository
import reehi.board.like.repository.ArticleLikeRepository
import reehi.board.like.service.response.ArticleLikeResponse

@Service
class ArticleLikeService (
    val articleLikeRepository: ArticleLikeRepository,
    val articleLikeCountRepository: ArticleLikeCountRepository
){
    val snowflake: Snowflake = Snowflake()

    fun read(articleId: Long, userId: Long): ArticleLikeResponse {
        return articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            ?.let { ArticleLikeResponse.from(it) }
            ?: throw NoSuchElementException("ArticleLike not found")
    }

    /**
     * update 구문
     */
    @Transactional
    fun likePessimisticLock1(articleId: Long, userId: Long) {
        articleLikeRepository.save(
            ArticleLike.create(
                snowflake.nextId(),
                articleId,
                userId
            )
        )

        val result = articleLikeCountRepository.increase(articleId)
        if(result == 0) {
            // 최초 요청 시에는 update 도니느 레코드가 없으므로, 1로 초기화 한다.
            // 트래픽이 순식간에 몰릴 수 있는 상황에는 유실될 수 있으므로, 게시글 생성 시점에 미리 0으로 초기화 할 수 있다.
            articleLikeCountRepository.save(
                ArticleLikeCount.init(articleId,1L)
            )
        }
    }

    @Transactional
    fun unlikePessimisticLock1(articleId: Long, userId: Long) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            ?.let {
                articleLikeRepository.delete(it)
                articleLikeCountRepository.decrease(articleId)
            }
    }

    /**
     * select for update ... update 구문
     */
    @Transactional
    fun likePessimisticLock2(articleId: Long, userId: Long) {
        articleLikeRepository.save(
            ArticleLike.create(
                snowflake.nextId(),
                articleId,
                userId
            )
        )
        val articleLikeCount =
            articleLikeCountRepository.findLockedByArticleId(articleId)?: ArticleLikeCount.init(articleId, 0L)
        articleLikeCount.increase()
            articleLikeCountRepository.save(articleLikeCount)
    }


    @Transactional
    fun unlikePessimisticLock2(articleId: Long, userId: Long) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            ?.let {
                articleLikeRepository.delete(it)
                val articleLikeCount = articleLikeCountRepository.findLockedByArticleId(articleId)
                articleLikeCount ?: throw NoSuchElementException("ArticleLike not found")
                articleLikeCount.decrease()
            }
    }


    @Transactional
    fun likeOptimisticLock(articleId: Long, userId: Long) {
        articleLikeRepository.save(
            ArticleLike.create(
                snowflake.nextId(),
                articleId,
                userId
            )
        )

        val articleLikeCount = articleLikeCountRepository.findByIdOrNull(articleId) ?: ArticleLikeCount.init(articleId,0L)
        articleLikeCount.increase()
        articleLikeCountRepository.save(articleLikeCount)
    }

    @Transactional
    fun unlikeOptimisticLock(articleId: Long, userId: Long) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            ?.let {
                articleLikeRepository.delete(it)
                val articleLikeCount = articleLikeCountRepository.findByIdOrNull(articleId)
                articleLikeCount ?: throw NoSuchElementException("ArticleLike not found")
                articleLikeCount.decrease()
            }
    }

    fun count(articleId: Long): Long =
        articleLikeCountRepository.findByIdOrNull(articleId)?.likeCount ?: 0L
}