package reehi.board.comment

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EntityScan(basePackages = ["reehi.board"])
@EnableJpaRepositories(basePackages = ["reehi.board"])
@SpringBootApplication
class CommentApplication
fun main(args: Array<String>) {
    runApplication<CommentApplication>(*args)
}
