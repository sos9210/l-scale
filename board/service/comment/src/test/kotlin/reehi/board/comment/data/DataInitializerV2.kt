package reehi.board.comment.data

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kuke.board.common.snowflake.Snowflake
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.support.TransactionTemplate
import reehi.board.comment.entity.CommentPath
import reehi.board.comment.entity.CommentPath.Companion
import reehi.board.comment.entity.CommentV2
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.test.Test

@SpringBootTest
class DataInitializerV2 {
    @PersistenceContext
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var transactionTemplate: TransactionTemplate

    val snowflake : Snowflake = Snowflake()
    val latch : CountDownLatch = CountDownLatch(BULK_INSERT_SIZE)

    val CHARSET: String = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    val DEPTH_CHUNK_SIZE: Int = 5


    companion object {
        val BULK_INSERT_SIZE : Int = 2000;
        val EXECUTE_COUNT : Int = 6000;
    }

    @Test
    fun initialize() {
        val executorService = Executors.newFixedThreadPool(10)
        for (i in 1..EXECUTE_COUNT) {
            val start = i * BULK_INSERT_SIZE
            val end = (i + 1) * BULK_INSERT_SIZE
            executorService.submit {
                insert(start, end)
                latch.countDown()
                println("latch.count" + latch.count)
            }
        }
        latch.await()
        executorService.shutdown()
    }
    fun insert(start:Int, end:Int) {
        transactionTemplate.executeWithoutResult({

            for (i in start ..<  end) {
                val comment = CommentV2.create(
                    snowflake.nextId(),
                    "content$i",
                    1L,
                    1L,
                    toPath(i)
                )
                entityManager.persist(comment)
            }
        })
    }

    fun toPath(pathParam: Int): CommentPath {
        var value = pathParam
        var path: String = ""
        for (i in 0..<DEPTH_CHUNK_SIZE) {
            path = CHARSET[value % CHARSET.length] + path
            value /= CHARSET.length
        }
        return CommentPath.create(path)
    }

}