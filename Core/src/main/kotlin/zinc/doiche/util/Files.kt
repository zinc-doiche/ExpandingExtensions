package zinc.doiche.util

import com.google.gson.reflect.TypeToken
import java.io.File

internal fun <T> File.toObject(clazz: Class<T>): T = reader().use { gson.fromJson(it, clazz) }
internal fun <T> File.toObject(typeToken: TypeToken<T>): T = reader().use { gson.fromJson(it, typeToken) }

@Suppress("UNCHECKED_CAST")
internal fun <K, V> File.toMapOf(
    keyClass: Class<K>, valueClass: Class<V>
): Map<K, V> = reader().use {
    val typeToken = TypeToken.getParameterized(Map::class.java, keyClass, valueClass)
    gson.fromJson(it, typeToken)
} as? Map<K, V> ?: mutableMapOf()

internal fun File.writeJson(obj: Any) = writer().use { writingGson.toJson(obj, it) }
