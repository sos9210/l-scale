package reehi.board.view.service

import org.springframework.stereotype.Service
import reehi.board.view.repository.ArticleViewCountRepository
import reehi.board.view.repository.ArticleViewDistributedLockRepository
import java.time.Duration


@Service
class ArticleViewService (
    val articleViewCountRepository: ArticleViewCountRepository,
    val articleViewCountBackUpProcessor: ArticleViewCountBackUpProcessor,
    val articleViewDistributedLockRepository: ArticleViewDistributedLockRepository
){

    fun increase(articleId: Long, userId: Long): Long {
        if (!articleViewDistributedLockRepository.lock(articleId, userId, TTL)) {
            return articleViewCountRepository.read(articleId)
        }

        val count = articleViewCountRepository.increase(articleId)
        if (count % BACK_UP_BACH_SIZE == 0L) {
            articleViewCountBackUpProcessor.backUp(articleId, count)
        }
        return count
    }

    fun count(articleId: Long): Long {
        return articleViewCountRepository.read(articleId)
    }
    companion object {
        const val BACK_UP_BACH_SIZE: Int = 100
        val TTL: Duration = Duration.ofMinutes(10)

    }
}