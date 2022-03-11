package com.kiven.sample.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.R
import com.kiven.sample.compose.theme.TestComposeTheme
import kotlinx.coroutines.launch

class AHComposeDemo: KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        activity.setContent {
            TestContent()
        }
    }
}

@Composable
fun TestContent(scaffoldState: ScaffoldState = rememberScaffoldState()) {
    val scope = rememberCoroutineScope()

    TestComposeTheme {
        Scaffold(scaffoldState = scaffoldState,topBar = {
            TopAppBar() {
            }
        }) {
            Column(modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Greeting("Android")
                Greeting("Android2")

                var count by remember {
                    mutableStateOf(0)
                }
                Button(onClick = { count ++ }) {
                    Text(text = "click ${count}!")
                }

                // rememberSaveable 通过将状态保存在 Bundle 中来保留状态，使其在配置更改后仍保持不变。
                var count2 by rememberSaveable {
                    mutableStateOf(0)
                }
                Button(onClick = {
                    count2 ++

                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar("click ${count2}!")
                    }
                }) {
                    Text(text = "click ${count2}!")
                }

                Image(painter = painterResource(id = R.mipmap.ic_launcher_u), contentDescription = "")
            }
        }

    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TestContent()
}