package com.project.petpoint.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.petpoint.view.ui.theme.VividAzure

class OrderHistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
                OrderHistoryBody(
                    orders = emptyList(),
                    onClearHistory = {
                        // TODO: Clear orders from database
                    }
                )
            }
        }
    }

data class OrderModel(
    val imageUrl: String = "",
    val title: String = "",
    val quantity: Int = 0,
    val date: String = "",
)

@Composable
fun OrderHistoryBody(
    orders: List<OrderModel>,
    onClearHistory: () -> Unit
) {
    Scaffold { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE7F0F1))
                .padding(padding),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .background(
                            VividAzure,
                            RoundedCornerShape(
                                bottomStart = 20.dp,
                                bottomEnd = 20.dp
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Order History",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onClearHistory) {
                        Text("Clear History", color = Color.Gray)
                    }
                }

                Divider(color = Color.Gray.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(6.dp))
            }

            if (orders.isNotEmpty()) {

                item {
                    Text(
                        text = "History",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0D47A1)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                items(orders) { order ->
                    OrderItemCard(order)
                }

            } else {

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No order history available",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemCard(item: OrderModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            // 🔹 Image from URL (enable later)
            /*
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier.height(60.dp)
            )
            */

            Spacer(modifier = Modifier.padding(10.dp))

            Column {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Qty: ${item.quantity}",
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = item.date,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
