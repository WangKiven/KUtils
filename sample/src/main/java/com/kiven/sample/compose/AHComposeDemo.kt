package com.kiven.sample.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.compose.theme.TestComposeTheme

class AHComposeDemo : KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        activity.setContent {
            TestComposeTheme { MainNavHost() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Nav.controller = null
    }
}

@Preview(showBackground = true)
@Composable
fun MainNavHost() {
    val navController = rememberNavController()
    Nav.controller = navController

    val graph = navController.createGraph(PageName.home) {
        router()
    }
    NavHost(navController, graph)
}

/*@Composable
fun TestContent(scaffoldState: ScaffoldState = rememberScaffoldState()) {
    val scope = rememberCoroutineScope()


    TestComposeTheme {
        Scaffold(scaffoldState = scaffoldState, topBar = {
            TopAppBar {
                Text(text = "菜单", Modifier.weight(1f))
                Text(text = "title", Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(text = "设置", Modifier.weight(1f), textAlign = TextAlign.End)
            }
        }, bottomBar = {
            BottomAppBar(cutoutShape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50))) {

            }
        }, floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    scope.launch {
                        scaffoldState.drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                },
                icon = {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = "Favorite"
                    )
                },
                text = { Text("Like") }
            )
        }, isFloatingActionButtonDocked = true,
        drawerContent = {}, drawerGesturesEnabled = true) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Greeting("Android1")
                Greeting("Android2")

                var count by remember {
                    mutableStateOf(0)
                }
                Button(onClick = { count++ }) {
                    Text(text = "click ${count}!")
                }

                // rememberSaveable 通过将状态保存在 Bundle 中来保留状态，使其在配置更改后仍保持不变。
                var count2 by rememberSaveable {
                    mutableStateOf(0)
                }
                Button(onClick = {
                    count2++

                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar("click ${count2}!")
                    }
                }) {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = "Favorite",
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = "click ${count2}!")
                }

                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_u),
                    contentDescription = ""
                )

                Text("padding(top = 24.dp)", Modifier.padding(top = 24.dp))
                Text("firstBaselineToTop(24.dp)", Modifier.firstBaselineToTop(24.dp))
                Text("paddingFrom(FirstBaseline, before = 24.dp)", Modifier.paddingFrom(FirstBaseline, before = 24.dp))
            }
        }

    }
}*/

fun Modifier.firstBaselineToTop(
    firstBaselineToTop: Dp
) = layout { measurable, constraints ->
    // Measure the composable
    val placeable = measurable.measure(constraints)

    // Check the composable has a first baseline
    check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
    val firstBaseline = placeable[FirstBaseline]

    // Height of the composable with padding - first baseline
    val placeableY = firstBaselineToTop.roundToPx() - firstBaseline
    val height = placeable.height + placeableY
    layout(placeable.width, height) {
        // Where the composable gets placed
        placeable.placeRelative(0, placeableY)
    }
}