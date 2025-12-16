package com.project.petpoint.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.PurpleGrey808

@Composable
fun HomeScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Overview",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        item {
            CardItem(
                title = "Total Products",
                count = "240",
                image = painterResource(id = com.project.petpoint.R.drawable.greenbox)
            )
        }
        item {
            CardItem(
                title = "Active Doctors",
                count = "18",
                image = painterResource(id = com.project.petpoint.R.drawable.doctor)
            )
        }
        item {
            CardItem(
                title = "Lost & Found Pets",
                count = "22",
                image = painterResource(id = com.project.petpoint.R.drawable.pet)
            )
        }
        item {
            SimpleCard(
                title = "Recent Activities"
            )
        }
    }
}

@Composable
fun CardItem(title: String, count: String, image: Painter) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(PurpleGrey808, shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                Text(text = title, fontWeight = FontWeight.SemiBold)
                Text(
                    text = count,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 24.dp)
                )
            }
            Image(
                painter = image,
                contentDescription = title,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun SimpleCard(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(PurpleGrey808, shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}






