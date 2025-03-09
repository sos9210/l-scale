package reehi.board.common.outboxmessagerelay

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Component
class MessageRelay(
    val outboxRepository: OutboxRepository,
    val messageRelayCoordinator: MessageRelayCoordinator,
    val messageRelayKafkaTemplate: KafkaTemplate<String, String>
) {

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun createOutbox(outboxEvent: OutboxEvent) {
        log.info("[MessageRelay.createOutbox] outboxEvent={}", outboxEvent)
        outboxRepository.save(outboxEvent.outbox)
    }

    @Async("messageRelayPublishEventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun publishEvent(outboxEvent: OutboxEvent) {
        publishEvent(outboxEvent.outbox)
    }

    private fun publishEvent(outbox: Outbox) {
        try {
            messageRelayKafkaTemplate.send(
                outbox.eventType.topic,
                outbox.shardKey.toString(),
                outbox.payload
            ).get(1, TimeUnit.SECONDS)
            outboxRepository.delete(outbox)
        } catch (e: Exception) {
            log.error("[MessageRelay.publishEvent] outbox={}", outbox, e)
        }
    }

    @Scheduled(
        fixedDelay = 10,
        initialDelay = 5,
        timeUnit = TimeUnit.SECONDS,
        scheduler = "messageRelayPublishPendingEventExecutor"
    )
    fun publishPendingEvent() {
        val assignedShard = messageRelayCoordinator.assignShards()
        log.info("[MessageRelay.publishPendingEvent] assignedShard size={}", assignedShard.shards?.size)
        for (shard in assignedShard.shards!!) {
            val outboxes = outboxRepository.findAllByShardKeyAndCreatedAtLessThanEqualOrderByCreatedAtAsc(
                shard,
                LocalDateTime.now().minusSeconds(10),
                Pageable.ofSize(100)
            )
            for (outbox in outboxes!!) {
                publishEvent(outbox!!)
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(MessageRelay::class.java)
    }
}
