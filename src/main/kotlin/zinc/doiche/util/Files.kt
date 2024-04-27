package zinc.doiche.util

import com.google.gson.Gson
import java.io.File

internal fun <T> File.toObject(clazz: Class<T>): T = reader().use { Gson().fromJson(it, clazz) }