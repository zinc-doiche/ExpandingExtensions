package zinc.doiche.util

import com.google.gson.reflect.TypeToken
import java.io.File

internal fun <T> File.toObject(clazz: Class<T>): T = reader().use { gson.fromJson(it, clazz) }
internal fun <T> File.toObject(typeToken: TypeToken<T>): T = reader().use { gson.fromJson(it, typeToken) }