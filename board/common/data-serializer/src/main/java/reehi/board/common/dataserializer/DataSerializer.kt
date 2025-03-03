package reehi.board.common.dataserializer

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

class DataSerializer private constructor() {
    companion object {
        private val objectMapper: ObjectMapper = initialize()

        private fun initialize(): ObjectMapper {
            return ObjectMapper()
                .registerModule(JavaTimeModule())
                .registerKotlinModule()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

        fun <T> deserialize(data: String, clazz: Class<T>): T? {
            try {
                return objectMapper.readValue(data, clazz)
            } catch (e: JsonProcessingException) {
                println("[DataSerializer.deserialize] data=$data, clazz=$clazz")
                return null
            }
        }

        fun <T> deserialize(data: Any, clazz: Class<T>): T {
            return objectMapper.convertValue(data, clazz)
        }

        fun serialize(obj: Any): String? {
            try {
                return objectMapper.writeValueAsString(obj)
            } catch (e: JsonProcessingException) {
                println("[DataSerializer.serialize] object=$obj")
                return null
            }
        }
    }
}