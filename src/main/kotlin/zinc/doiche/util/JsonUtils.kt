package zinc.doiche.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder

internal val gson: Gson = GsonBuilder().disableHtmlEscaping().create()

internal fun Any.serialize() = gson.toJson(this)