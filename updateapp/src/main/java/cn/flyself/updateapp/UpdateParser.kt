package cn.flyself.updateapp

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class UpdateParser {
    private var updateJsonUrl: String? = null

    companion object {
        @JvmStatic
        private var updateParser: UpdateParser? = null

        @JvmStatic
        fun get(): UpdateParser {
            if (updateParser == null) {
                updateParser = UpdateParser()
            }
            return updateParser!!
        }
    }

    /**
     * 设置服务器端的更新信息json url
     * @param updateJsonUrl 服务器端的更新信息json url
     * @return UpdateParser
     * */
    fun setUpdateJsonUrl(updateJsonUrl: String): UpdateParser {
        this.updateJsonUrl = updateJsonUrl
        return this
    }

    /**
     * 获取解析的UpdateData对象
     * @param
     * @return UpdateData
     * */
    fun parser(): UpdateEntity {
        return getUpdateData()
    }

    private fun getUpdateData(): UpdateEntity {
        val gson = Gson()
        val default = "{\n" +
                "  \"versionCode\": 0,\n" +
                "  \"versionName\": \"0.0.0\",\n" +
                "  \"versionInfo\":\"\",\n" +
                "  \"downloadUrl\":\"\",\n" +
                "  \"apkSize\":0,\n" +
                "  \"MD5\":\"\"\n" +
                "}"
        val updateData = try {
            getJson(updateJsonUrl!!) ?: default
        } catch (e: Exception) {
            e.printStackTrace()
            default
        }
        return gson.fromJson(updateData, UpdateEntity::class.java)
    }

    @Throws(Exception::class)
    private fun getJson(url: String): String? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return if (response.body() != null) response.body()!!.string() else null
        }
    }
}