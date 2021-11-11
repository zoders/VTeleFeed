package ru.technopark.vtelefeed

import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import org.json.JSONException
import org.json.JSONObject

data class Attachment(
    val photo: Photo? = null,
    val type: String = "") {

    data class Photo(
        val date: Long,
        val id: Int,
        val sizes: List<Size>,
        val text: String) {
        companion object {
            fun parse(json: JSONObject): Photo{
                try {
                    val date = json.getLong("date")
                    val id = json.getInt("id")
                    val text = json.getString("text")
                    val ja = json.optJSONArray("sizes")
                    val sizes = ArrayList<Size>(ja.length())
                    for (i in 0 until ja.length()) {
                        val size = Size.parse(ja.optJSONObject(i))
                        sizes.add(size)
                    }
                    return Photo(date, id, sizes, text)
                } catch (ex: JSONException) {
                    throw VKApiIllegalResponseException(ex)
                }
            }
        }

        data class Size(
            val height: Int = 0,
            val type: String = "",
            val url: String = "",
            val width: Int = 0
        ) {
            companion object {
                fun parse(json: JSONObject) =
                    Size(height = json.getInt("height"),
                    type = json.getString("type"),
                    url = json.getString("url"),
                    width = json.getInt("width"))
                }
            }
        }

    companion object {
        fun parse(json: JSONObject) =
            Attachment(type=json.getString("type"),
            photo=Photo.parse(json.optJSONObject("photo")))
    }
}
