package com.kiven.sample.util

import java.io.File

object Const {
    val FILEPROVIDER_AUTHORITY = "com.kiven.sample.fileprovider"
    val IMAGE_DIR = "KUtilSampleFile" + File.separator + "testImage"
    val IMAGES = listOf(
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1564053855675&di=d65370e4ea7fa5a0af9c3cb8c849ac84&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201703%2F03%2F20170303212118_uSryj.thumb.700_0.jpeg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1564045468923&di=9316f56ad4bb4d9232658d249f94f11f&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fblog%2F201406%2F16%2F20140616171020_TiEXR.thumb.700_0.jpeg",
            "http://b.hiphotos.baidu.com/image/pic/item/908fa0ec08fa513db777cf78376d55fbb3fbd9b3.jpg",
            "http://file5.gucn.com/file2/ShopLogoFile/20120413/Gucn_20120413327888131819Logo.jpg"
    )

    fun randomImage() = IMAGES[(Math.random() * 1000).toInt() % IMAGES.size]
}