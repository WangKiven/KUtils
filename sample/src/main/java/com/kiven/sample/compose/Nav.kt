package com.kiven.sample.compose

import androidx.navigation.*
import androidx.navigation.compose.composable
import com.kiven.sample.compose.page.Greeting
import com.kiven.sample.compose.page.HelloPage
import com.kiven.sample.compose.page.HomePage
import com.kiven.sample.compose.page.SettingPage


object Nav {
    // 提示设置NavHostController为静态变量，会导致内存泄露。在activity销毁的时候，置空就好了。
    var controller: NavHostController? = null
    fun navigate(
        route: String,
        navOptions: NavOptions? = null,
        navigatorExtras: Navigator.Extras? = null
    ) {
        controller?.navigate(route, navOptions, navigatorExtras)
    }

    fun popBackStack() {
        controller?.popBackStack()
    }
}

object PageName {
    const val home = "home"
    const val setting = "setting"
    const val hello = "hello"
}

fun NavGraphBuilder.router() {
    composable(PageName.home) { HomePage() }
    composable(PageName.setting) { SettingPage() }
    composable(PageName.hello + "/{name}", arguments = listOf(navArgument("name"){
        type = NavType.StringType
        defaultValue = "世界"
    })) {
        HelloPage(it.arguments?.getString("name") ?: "中国")
    }
}