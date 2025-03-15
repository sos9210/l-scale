package reehi.board.articleread.client

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test

@SpringBootTest
class ViewClientTest(

    @Autowired
    val viewClient: ViewClient

) {

    @Test
    @Throws(InterruptedException::class)
    fun readCacheableTest() {
        viewClient.count(1L) // 로그 출력
        viewClient.count(1L) // 로그 미출력
        viewClient.count(1L) // 로그 미출력

        TimeUnit.SECONDS.sleep(3)
        viewClient.count(1L) // 로그 출력
    }

    @Test
    @Throws(InterruptedException::class)
    fun readCacheableMultiThreadTest() {
        val executorService = Executors.newFixedThreadPool(5)

        viewClient.count(1L) // init cache

        for (i in 0..4) {
            val latch = CountDownLatch(5)
            for (j in 0..4) {
                executorService.submit {
                    viewClient.count(1L)
                    latch.countDown()
                }
            }
            latch.await()
            TimeUnit.SECONDS.sleep(2)
            println("=== cache expired ===")
        }
    }
}