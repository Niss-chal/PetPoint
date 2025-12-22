package com.project.petpoint.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.petpoint.R
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.Green
import com.project.petpoint.view.ui.theme.VividAzure
import com.project.petpoint.view.ui.theme.VividOrange
import com.project.petpoint.view.ui.theme.White
import com.project.petpoint.view.ui.theme.Yellow

class ProductManagementActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProductManagementScreen()
        }
    }
}

@Composable
fun ProductManagementScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Azure)
            .padding(16.dp)
    ) {

        // Title
        Text(
            text = "Product Management",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))
        Row {
            Divider(
                color = Color.Gray.copy(alpha = 0.7f),
                thickness = 1.dp,
            )
        }
        Spacer(modifier = Modifier.height(30.dp))

        // Add Product Button
        Button(
            onClick = { /* Navigate to Add Product */ },
            colors = ButtonDefaults.buttonColors(containerColor = VividAzure),
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
        ) {
            Text("+ Add Product", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(30.dp))

        LazyColumn {
            item {
                ProductCard(
                    name = "Dog Collar",
                    price = "Rs. 500",
                    stock = 12,
                    status = "In Stock",
                    statusColor = Green
                )
            }

            item {
                ProductCard(
                    name = "Cat Food Bowl",
                    price = "Rs. 725",
                    stock = 6,
                    status = "Low Stock",
                    statusColor = Yellow
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

    }
}

@Composable
fun ProductCard(
    name: String,
    price: String,
    stock: Int,
    status: String,
    statusColor: Color
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(name, fontWeight = FontWeight.Bold)
                Text(status, color = statusColor, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(price)
                Text("Stock: $stock")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row {
                Divider(
                    color = Color.Gray.copy(alpha = 0.7f),
                    thickness = 1.dp,
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Edit
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Edit",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }

                // Delete
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Delete",
                        fontSize = 14.sp,
                        color = Color.Red
                    )
                }
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun ProductPreview() {
    ProductManagementScreen()
}
