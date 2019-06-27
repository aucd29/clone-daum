@file:Suppress("NOTHING_TO_INLINE", "unused")

package brigitte

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.InputStream
import java.io.Serializable

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2017. 7. 6.. <p/>
 *
 * <p>https://github.com/FasterXML/jackson-module-kotlin/blob/master/src/test/kotlin/com/fasterxml/jackson/module/kotlin/test/GithubDatabind1329.kt</p>
 */
object Json {
    val mapper = jacksonObjectMapper()

    init {
        mapper.apply {
            configure(JsonParser.Feature.ALLOW_COMMENTS, true)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)

            //https://stackoverflow.com/questions/5455014/ignoring-new-fields-on-json-objects-using-jackson
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

    fun stringify(obj: Any): String = mapper.writeValueAsString(obj)
}

// to object

inline fun <reified T: Any> InputStream.jsonParse() = Json.mapper.readValue<T>(this)
inline fun <reified T: Any> String.jsonParse() = Json.mapper.readValue<T>(this)
inline fun <reified T: Any> ByteArray.jsonParse() = Json.mapper.readValue<T>(this)

// to json string

inline fun Serializable.jsonStringify() = Json.stringify(this)