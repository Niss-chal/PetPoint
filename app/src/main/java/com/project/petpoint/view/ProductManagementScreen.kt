package com.project.petpoint.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.petpoint.model.ProductModel
import com.project.petpoint.repository.ProductRepoImpl
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.Green
import com.project.petpoint.view.ui.theme.GreyOrange
import com.project.petpoint.view.ui.theme.VividAzure
import com.project.petpoint.view.ui.theme.VividOrange
import com.project.petpoint.view.ui.theme.White
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

        item {
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showDialog = false
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            var model = ProductModel(
                                product .value!!.productId,
                                name,price.toDouble(),description,"","",stock.toInt()
                            )
                            productViewModel.updateProduct(model){
                                    success,message ->
                                if(success){
                                    showDialog = false
                                }else{
                                    Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
                                }
                            }
                        }) { Text("Update") }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showDialog = false
                        }) { Text("Cancel") }
                    },
                    title = {Text("Update Product")},
                    text = {
                        Column {
                            Spacer(modifier = Modifier.height(50.dp))

                            OutlinedTextField(
                                value = name,
                                onValueChange = { data ->
                                    name = data
                                },
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                placeholder = {
                                    Text("Enter the product name")
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = GreyOrange,
                                    unfocusedContainerColor = GreyOrange,
                                    focusedIndicatorColor = Blue,
                                    unfocusedIndicatorColor = Color.Transparent
                                )
                            )

                            Spacer(modifier = Modifier.height(15.dp))


                            OutlinedTextField(
                                value = price,
                                onValueChange = { data ->
                                    price = data
                                },
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                placeholder = {
                                    Text("Enter the price")
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = GreyOrange,
                                    unfocusedContainerColor = GreyOrange,
                                    focusedIndicatorColor = Blue,
                                    unfocusedIndicatorColor = Color.Transparent
                                )
                            )

                            Spacer(modifier = Modifier.height(15.dp))


                            OutlinedTextField(
                                value = description,
                                onValueChange = { data ->
                                    description = data
                                },
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                placeholder = {
                                    Text("Enter the description")
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = GreyOrange,
                                    unfocusedContainerColor = GreyOrange,
                                    focusedIndicatorColor = Blue,
                                    unfocusedIndicatorColor = Color.Transparent
                                )
                            )

                            Spacer(modifier = Modifier.height(15.dp))


                            OutlinedTextField(
                                value = stock,
                                onValueChange = { data ->
                                    stock = data
                                },
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                placeholder = {
                                    Text("Enter the stock")
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = GreyOrange,
                                    unfocusedContainerColor = GreyOrange,
                                    focusedIndicatorColor = Blue,
                                    unfocusedIndicatorColor = Color.Transparent
                                )
                            )


                        }
                    }
                )
            }
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
                statusColor = statusColor,
                onEdit = {
                    productViewModel.getProductById(data.productId)
                    showDialog = true
                },
                onDelete={
                    AlertDialog.Builder(context)
                        .setTitle("Delete Product")
                        .setMessage("Are you sure you want to delete ${data.name}?")
                        .setPositiveButton("Delete"){_,_ ->
                            productViewModel.deleteProduct(data.productId){
                                success,message->
                                if(success){
                                    Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        .setNegativeButton("Cancel",null)
                        .show()
                }
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
    statusColor: Color,
    onEdit: () -> Unit,
    onDelete: () -> Unit
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
                Text(text = "Rs. $price")
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
                    modifier = Modifier.clickable {
                       onEdit()
                    }
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
                    modifier = Modifier.clickable { onDelete()}
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