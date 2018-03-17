package com.yangping.newsmodule

/**
 * Created by YuPing on 2018/3/17.
 * 警廣資料object
 */
data class DriveNews constructor(val region: String, val srcdetail: String, val areaNm: String, val UID: String
                                 , val direction: String, val y1: Double, val happentime: String, val roadtype: String, val road: String
                                 , val modDttm: String, val comment: String, val happendate: String, val x1: Double)