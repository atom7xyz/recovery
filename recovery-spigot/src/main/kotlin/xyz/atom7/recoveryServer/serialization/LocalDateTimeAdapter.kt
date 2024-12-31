package xyz.atom7.recoveryServer.serialization

import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeAdapter : JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime>
{
    private val formatter = DateTimeFormatter.ISO_DATE_TIME

    override fun serialize(src: LocalDateTime, typeOfSrc: Type, context: JsonSerializationContext): JsonElement
    {
        return JsonPrimitive(src.format(formatter)) // Convert LocalDateTime to ISO 8601 string
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDateTime
    {
        return LocalDateTime.parse(json.asString, formatter) // Parse ISO 8601 string to LocalDateTime
    }
}