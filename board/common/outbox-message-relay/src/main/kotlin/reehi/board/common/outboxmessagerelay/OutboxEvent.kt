package reehi.board.common.outboxmessagerelay

class OutboxEvent (
    var outbox: Outbox

){
    companion object {
        fun of(outbox: Outbox): OutboxEvent = OutboxEvent(
            outbox = outbox

        )
    }

}