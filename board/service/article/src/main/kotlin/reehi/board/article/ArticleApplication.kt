package reehi.board.article

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@EntityScan(basePackages = ["reehi.board"])
@EnableJpaRepositories(basePackages = ["reehi.board"])
@SpringBootApplication
@EnableScheduling
class ArticleApplication
fun main(args: Array<String>) {
    runApplication<ArticleApplication>(*args)
}
