package com.project.petpoint.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.petpoint.R
import com.project.petpoint.model.ProductModel
import com.project.petpoint.repository.ProductRepoImpl
import com.project.petpoint.utils.ImageUtils
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.Green
import com.project.petpoint.view.ui.theme.VividAzure
import com.project.petpoint.view.ui.theme.VividOrange
import com.project.petpoint.view.ui.theme.White
import com.project.petpoint.view.ui.theme.Yellow
import com.project.petpoint.viewmodel.ProductViewModel


@Composable
fun ProductManagementScreen() {
    val productViewModel = remember { ProductViewModel(ProductRepoImpl()) }

    val context = LocalContext.current
    val activity = context as? Activity

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    val allProducts = productViewModel.allProducts.observeAsState(initial = emptyList())

    val product = productViewModel.products.observeAsState(initial = null)

    LaunchedEffect(product.value) {
        productViewModel.getAllProduct()

        product.value?.let {
            name = it.name
            price = it.price.toString()
            description = it.description
            stock = it.stock.toString()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Azure)
            .padding(16.dp)
    ) {

        item {
            // Title
            Text(
                text = "Product Management",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))
        }

        item {
            Row {
                Divider(
                    color = Color.Gray.copy(alpha = 0.7f),
                    thickness = 1.dp,
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
        }

        item {
            // Add Product Button
            Button(
                onClick = {
                    val intent = Intent(context,
                        AddProductActivity :: class.java)
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = VividAzure),
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
            ) {
                Text("+ Add Product", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        items(allProducts.value?.size ?: 0) { index ->
            val data = allProducts.value!![index]
            val (status, statusColor) = when {
                data.stock <= 0 -> "Out of Stock" to Color.Red
                data.stock < 10 -> "Low Stock" to VividOrange
                else -> "Available" to Green
            }
            ProductCard(
                name = data.name,
                price = data.price,
                description = data.description,
                stock = data.stock,
                status = status,
                statusColor = statusColor
            )
        }
    }
}

@Composable
fun ProductCard(
    name: String,
    price: Double,
    description: String,
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "$$price")
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
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* TODO: Add edit functionality */ }
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
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* TODO: Add delete functionality */ }
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


@Composable
@Preview
fun ProductPreview() {
    ProductManagementScreen()
}