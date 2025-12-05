package com.project.petpoint.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.project.petpoint.view.ui.theme.Azure

@Composable
fun ShopScreen(){
    Column(
        modifier = Modifier.fillMaxSize()
            .background(Azure)
    ) {
        Text("Shopping Screen")
    }
}