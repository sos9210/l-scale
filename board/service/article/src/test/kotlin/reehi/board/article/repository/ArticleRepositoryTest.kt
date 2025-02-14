package reehi.board.article.repository

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test

private val log: Logger = LoggerFactory.getLogger("ArticleRepositoryTest")
@SpringBootTest
class ArticleRepositoryTest (
    @Autowired val articleRepository: ArticleRepository
){
    @Test
    fun findAllTest() {
        val articles = articleRepository.findAll(1L, 1499970L, 30L);
        log.info("article.size = ${articles.size}")

        for (article in articles){
            log.info("article = $article")
        }
    }

    @Test
    fun countTest() {
        val count = articleRepository.count(1L, 10000L)
        log.info("count = $count")
    }

}