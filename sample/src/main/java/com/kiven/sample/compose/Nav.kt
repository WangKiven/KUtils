package com.kiven.sample.compose

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.kiven.sample.R
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

@Composable
fun NavBackButton() {
    IconButton(onClick = { Nav.popBackStack() }, colors = IconButtonColors(Color.White, Color.Red, Color.Yellow, Color.Green)) {
        Icon(painter = painterResource(id = R.drawable.abc_ic_arrow_forward), contentDescription = "back", modifier = Modifier.rotate(180f))
    }
}