package com.kiven.sample.compose.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kiven.sample.R
import com.kiven.sample.compose.Nav
import com.kiven.sample.compose.PageName
import com.kiven.sample.compose.firstBaselineToTop
import com.kiven.sample.compose.theme.Purple700
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage() {
    // todo https://developer.android.google.cn/jetpack/compose/layouts/material
//  https://developer.android.google.cn/jetpack/compose/designsystems/material2-material3?hl=zh-cn

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(modifier = Modifier
                .background(Color.White)
                .width(300.dp)
                .fillMaxHeight()) {
                Button(onClick = { scope.launch { drawerState.close(); Nav.navigate(PageName.setting) } }) {
                    Text(text = "点我")
                }
            }
        },
        gesturesEnabled = true,
//            scrimColor = Color.Cyan,
        content = {
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(text = "title") },
                        actions = {
                            Text(text = "菜单")
                            Text(text = "设置", Modifier.padding(15.dp), textAlign = TextAlign.End)
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Purple700,
                            titleContentColor = Color.White,
                            actionIconContentColor = Color.White,
                        )
                    )
                },
                bottomBar = {
                    BottomAppBar(
                        actions = {
                            IconButton(onClick = { /* doSomething() */ }) {
                                Icon(Icons.Filled.Check, contentDescription = "Localized description")
                            }
                            IconButton(onClick = { /* doSomething() */ }) {
                                Icon(
                                    Icons.Filled.Edit,
                                    contentDescription = "Localized description",
                                )
                            }
                        },
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = { /* do something */ },
                                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                            ) {
                                Icon(Icons.Filled.Add, "Localized description")
                            }
                        }
                    )
                },
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        onClick = {
                            scope.launch {
                                drawerState.apply {
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
                },
            ) { contentPadding ->
                // Screen content
//            Box(modifier = Modifier.padding(contentPadding)) { /* ... */ }
                Column(
                    modifier = Modifier
                        .padding(contentPadding)
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
                            snackbarHostState.showSnackbar("click ${count2}!")
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

                    Button(onClick = { Nav.navigate(PageName.hello + "/大宝") }) {
                        Text(text = "下一页")
                    }

                    Image(
                        painter = painterResource(id = R.mipmap.ic_launcher_u),
                        contentDescription = "",
                        alignment = Alignment.Center
                    )

                    Text("padding(top = 24.dp)", Modifier.padding(top = 24.dp))
                    Text("firstBaselineToTop(24.dp)", Modifier.firstBaselineToTop(24.dp))
                    Text("paddingFrom(FirstBaseline, before = 24.dp)", Modifier.paddingFrom(
                        FirstBaseline, before = 24.dp))

                }
            }
        }
    )
}


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}