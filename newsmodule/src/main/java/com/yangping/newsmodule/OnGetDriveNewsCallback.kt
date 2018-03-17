package com.yangping.newsmodule

/**
 * Created by YuPing on 2018/3/17.
 * 警廣資料的callback
 */
interface OnGetDriveNewsCallback {
    fun onFailure(message: String)

    fun onSuccess(newsList: List<DriveNews>)
}