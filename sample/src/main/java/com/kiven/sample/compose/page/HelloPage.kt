package com.kiven.sample.compose.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.kiven.sample.compose.Nav
import com.kiven.sample.compose.NavBackButton
import com.kiven.sample.compose.theme.Purple700

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun HelloPage(name: String = "Compose") {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "问候") },
                navigationIcon = { NavBackButton()},
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Purple700,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White,
                )
            )
        }
    ) {
        Column(modifier = Modifier
            .padding(it)
            .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "您好，$name ！")
            Button(onClick = { Nav.popBackStack() }) {
                Text(text = "返回")
            }
        }
    }
}