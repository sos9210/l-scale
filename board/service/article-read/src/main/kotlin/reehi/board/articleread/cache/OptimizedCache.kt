package reehi.board.articleread.cache

import com.fasterxml.jackson.annotation.JsonIgnore
import reehi.board.common.dataserializer.DataSerializer
import java.time.Duration
import java.time.LocalDateTime


class OptimizedCache (
    var data: String,
    var expiredAt: LocalDateTime

){
    companion object {

        fun of(data: Any, ttl: Duration): OptimizedCache =
            OptimizedCache(
            data = DataSerializer.serialize(data).toString(),
            expiredAt = LocalDateTime.now().plus(ttl)
            )
    }


    @JsonIgnore
    fun isExpired(): Boolean {
        return LocalDateTime.now().isAfter(expiredAt)
    }

    fun <T> parseData(dataType: Class<T>): T? {
        return DataSerializer.deserialize(data, dataType)
    }
}