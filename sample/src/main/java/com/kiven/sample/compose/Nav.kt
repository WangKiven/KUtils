package com.kiven.sample.compose

import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator


object Nav {
    // 提示设置为静态变量，会导致内存泄露。在activity销毁的时候，置空就好了。
    var controller: NavHostController? = null
    fun navigate(
        route: String,
        navOptions: NavOptions? = null,
        navigatorExtras: Navigator.Extras? = null
    ) {
        controller?.navigate(route, navOptions, navigatorExtras)
    }
}