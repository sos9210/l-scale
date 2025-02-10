package reehi.board.article.data

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kuke.board.common.snowflake.Snowflake
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.support.TransactionTemplate
import reehi.board.article.entity.Article
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.test.Test

@SpringBootTest
class DataInitializer {
    @PersistenceContext
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var transactionTemplate: TransactionTemplate

    val snowflake : Snowflake = Snowflake()
    val latch : CountDownLatch = CountDownLatch(BULK_INSERT_SIZE)


    companion object {
        val BULK_INSERT_SIZE : Int = 2000;
        val EXECUTE_COUNT : Int = 6000;
    }

    @Test
    fun initialize() {
        val executorService = Executors.newFixedThreadPool(10)
        for (i in 1..EXECUTE_COUNT) {
            executorService.submit({
                insert()
                latch.countDown()
                println("latch.count" + latch.count)
            })
        }
        latch.await()
        executorService.shutdown()
    }
    fun insert() {
        transactionTemplate.executeWithoutResult({
            for (i in 1..BULK_INSERT_SIZE) {
                val article = Article.create(
                    snowflake.nextId(),
                    "title$i",
                    "content$i",
                    1L,
                    1L,
                )
                entityManager.persist(article)
            }
        })
    }
}