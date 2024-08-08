package zinc.doiche.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

internal val gson: Gson by lazyOf(GsonBuilder()
    .disableHtmlEscaping()
//    .registerTypeAdapter(FreeString::class.java, FreeStringAdaptor())
    .create())

internal val writingGson: Gson by lazyOf(GsonBuilder()
    .setPrettyPrinting()
    .disableHtmlEscaping()
//    .registerTypeAdapter(FreeString::class.java, FreeStringAdaptor())
    .create())

internal fun Any.serialize() = gson.toJson(this)

internal fun <T> String.deserialize(clazz: Class<T>) = gson.fromJson(this, clazz)

@Suppress("UNCHECKED_CAST")
internal fun String.asMapJava() = gson.fromJson(this, mapTypeOf(String::class.java, JvmType.Object::class.java)) as Map<String, JvmType.Object>

@Suppress("UNCHECKED_CAST")
internal fun String.asMap() = gson.fromJson(this, mapTypeOf(String::class.java, JvmType.Object::class.java)) as MutableMap<String, Any>

internal fun <K, V> mapTypeOf(key: Class<K>, value: Class<V>) = TypeToken.getParameterized(Map::class.java, key, value)
