package com.kiven.sample.util

import java.io.File

object Const {
    val FILEPROVIDER_AUTHORITY = "com.kiven.sample.fileprovider"
    val IMAGE_DIR = "KUtilSampleFile" + File.separator + "testImage"
    val IMAGES = listOf(
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1564053855675&di=d65370e4ea7fa5a0af9c3cb8c849ac84&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201703%2F03%2F20170303212118_uSryj.thumb.700_0.jpeg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1564045468923&di=9316f56ad4bb4d9232658d249f94f11f&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fblog%2F201406%2F16%2F20140616171020_TiEXR.thumb.700_0.jpeg",
            "http://b.hiphotos.baidu.com/image/pic/item/908fa0ec08fa513db777cf78376d55fbb3fbd9b3.jpg",
            "http://file5.gucn.com/file2/ShopLogoFile/20120413/Gucn_20120413327888131819Logo.jpg",

            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1564113347031&di=94df480cbdfe79062f62ea21afc3e0b8&imgtype=0&src=http%3A%2F%2Fimg4q.duitang.com%2Fuploads%2Fitem%2F201501%2F25%2F20150125024328_xVf4t.jpeg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1564113347030&di=7b54e7630b7ced44d477abab6a9aa913&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2Ff1450573c09b0128c64b7c8f94c412d399a1503817779e-GqjnkT_fw658",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1564113347030&di=19e8d4c7df5e687e2c1937ee41e767d7&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201607%2F18%2F20160718092005_5SHs4.jpeg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1564113347030&di=28d252727338e89c6a9eaa1fdc3d9dd0&imgtype=0&src=http%3A%2F%2Fpic.rmb.bdstatic.com%2Ffce5771dd26eb65b08ad1ad099b74129.jpeg"
    )

    fun randomImage() = IMAGES[(Math.random() * 1000).toInt() % IMAGES.size]
//    fun randomImage2() = IMAGES[(Math.random() * 1000).toInt() % (IMAGES.size - 4) + 4]
}