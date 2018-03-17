package com.yangping.newsmodule

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit
import android.util.JsonReader
import java.io.InputStreamReader
import android.util.JsonToken


/**
 * Created by YuPing on 2018/3/17.
 * 取得警廣資料的工具類
 */
class DriveNewsUril {
    companion object {
        val DEFAULT_DATA_URL_RESERVE = "http://data.moi.gov.tw/MoiOD/System/DownloadFile.aspx?DATA=1E91AC2B-48B3-47A3-83B4-EB4A8AD930B9"
        val TAG = "DriveNews"


        val JSON = MediaType.parse("application/json; charset=utf-8")

        private var _Url = DEFAULT_DATA_URL_RESERVE
        private var _Client = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build()
        private var _Callback: OnGetDriveNewsCallback? = null
        /**
         * 設定資料來源URL
         */
        public fun setUrl(url: String): Companion {
            _Url = url
            return this
        }

        /**
         * 設定OKHttp的client
         */
        public fun setClient(client: OkHttpClient): Companion {
            _Client = client
            return this
        }

        /**
         * 設定呼叫的callback
         */
        public fun setCallback(callback: OnGetDriveNewsCallback): Companion {
            _Callback = callback
            return this
        }

        /**
         * 執行取資料的行動
         */
        public fun start() {
            if (_Callback == null) {
                _Callback = object : OnGetDriveNewsCallback {
                    override fun onFailure(message: String) {
                        Log.i(TAG, "onFailure: " + message)
                    }

                    override fun onSuccess(newsList: List<DriveNews>) {
                        Log.i(TAG, "Size of the data: " + newsList.size)
                    }
                }
            }

            val request = Request.Builder()
                    .url(_Url)
                    .get()
                    .build()
            _Client.newCall(request).enqueue(object : Callback {
                private val uiHandler = UiHander(_Callback!!)
                override fun onFailure(call: Call?, e: IOException?) {
                    if (e != null) {
                        if (e.message != null) {
                            uiHandler.obtainMessage(UiHander.FAILURE, e.message!!).sendToTarget()
                            return
                        }
                    }
                    uiHandler.obtainMessage(UiHander.FAILURE, "onFailure error.").sendToTarget()
                }

                override fun onResponse(call: Call?, response: Response?) {
                    if (response != null) {
                        if (response.code() == 200) {
                            val responseBody = response.body()
                            if (responseBody != null) {
                                val dataStream = responseBody.byteStream()
                                try {
                                    val datas = parseData2DriveNews(dataStream)
                                    uiHandler.obtainMessage(UiHander.SUCCESS, datas).sendToTarget()
                                } catch (e: IOException) {
                                    uiHandler.obtainMessage(UiHander.FAILURE, "Not JSON.").sendToTarget()
                                }
                            }
                        } else {
                            uiHandler.obtainMessage(UiHander.FAILURE, "http code: " + response.code()).sendToTarget()
                        }
                    } else {
                        uiHandler.obtainMessage(UiHander.FAILURE, "onResponse error.").sendToTarget()
                    }
                }
            })
        }

        private class UiHander(val callback: OnGetDriveNewsCallback) : Handler(Looper.getMainLooper()) {
            companion object {
                val FAILURE = 0
                val SUCCESS = 1
            }

            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                if (msg != null) {
                    when (msg.what) {
                        FAILURE -> {
                            callback.onFailure(msg.obj as String)
                        }
                        SUCCESS -> {
                            callback.onSuccess(msg.obj as List<DriveNews>)
                        }
                    }
                }
            }
        }

        /**
         * Parse json to DriveNews
         */
        @Throws(IOException::class)
        private fun parseData2DriveNews(input: InputStream): List<DriveNews> {
            val reader = JsonReader(InputStreamReader(input, "UTF-8"))
            reader.beginObject()
            val name = reader.nextName()
            try {
                return readDriveNewsArray(reader)
            } finally {
                reader.close()
            }
        }

        @Throws(IOException::class)
        private fun readDriveNewsArray(reader: JsonReader): List<DriveNews> {
            val dataList = ArrayList<DriveNews>()
            reader.beginArray()
            while (reader.hasNext()) {
                dataList.add(readDriveNews(reader))
            }
            reader.endArray()
            return dataList;
        }

        @Throws(IOException::class)
        private fun readDriveNews(reader: JsonReader): DriveNews {
            var region = ""
            var srcdetail = ""
            var areaNm = ""
            var uid = ""
            var direction = ""
            var y1 = 0.0
            var happentime = ""
            var roadtype = ""
            var road = ""
            var modDttm = ""
            var comment = ""
            var happendate = ""
            var x1 = 0.0

            reader.beginObject()
            while (reader.hasNext()) {
                val name = reader.nextName()
                when (name) {
                    "srcdetail" -> {
                        srcdetail = reader.nextString()
                    }
                    "region" -> {
                        region = reader.nextString()
                    }
                    "areaNm" -> {
                        areaNm = reader.nextString()
                    }
                    "UID" -> {
                        uid = reader.nextString()
                    }
                    "direction" -> {
                        direction = reader.nextString()
                    }
                    "y1" -> {
                        y1 = reader.nextDouble()
                    }
                    "happentime" -> {
                        happentime = reader.nextString()
                    }
                    "roadtype" -> {
                        roadtype = reader.nextString()
                    }
                    "road" -> {
                        road = reader.nextString()
                    }
                    "modDttm" -> {
                        modDttm = reader.nextString()
                    }
                    "comment" -> {
                        comment = reader.nextString()
                    }
                    "happendate" -> {
                        areaNm = reader.nextString()
                    }
                    "x1" -> {
                        x1 = reader.nextDouble()
                    }
                    else -> {
                        reader.skipValue()
                    }
                }
            }
            reader.endObject()
            return DriveNews(region, srcdetail, areaNm, uid, direction, y1, happentime, roadtype, road, modDttm, comment, happendate, x1)
        }


    }

}

