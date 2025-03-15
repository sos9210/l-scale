package reehi.board.common.outboxmessagerelay

import java.util.stream.LongStream

class AssignedShard {
    var shards: List<Long>? = null
    companion object {
        fun of(appId: String, appIds: List<String>, shardCount: Long): AssignedShard {
            val assignedShard = AssignedShard()
            assignedShard.shards = assign(appId, appIds, shardCount)
            return assignedShard
        }

        private fun assign(appId: String, appIds: List<String>, shardCount: Long): List<Long> {
            val appIndex = findAppIndex(appId, appIds)
            if (appIndex == -1) {
                return listOf()
            }

            val start = appIndex * shardCount / appIds.size
            val end = (appIndex + 1) * shardCount / appIds.size - 1

            return LongStream.rangeClosed(start, end).boxed().toList()
        }

        private fun findAppIndex(appId: String, appIds: List<String>): Int {
            for (i in appIds.indices) {
                if (appIds[i] == appId) {
                    return i
                }
            }
            return -1
        }

    }
}