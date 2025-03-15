package reehi.board.view

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EntityScan(basePackages = ["reehi.board"])
@EnableJpaRepositories(basePackages = ["reehi.board"])
@SpringBootApplication
class ViewApplication
fun main(args: Array<String>) {
    runApplication<ViewApplication>(*args)
}
