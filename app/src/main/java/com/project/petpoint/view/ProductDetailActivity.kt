package com.project.petpoint.view

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.project.petpoint.repository.ProductRepoImpl
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.Green
import com.project.petpoint.view.ui.theme.VividAzure
import com.project.petpoint.view.ui.theme.White
import com.project.petpoint.viewmodel.ProductViewModel

class ProductDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val productId = intent.getStringExtra("productId")
            if (productId != null) {
                ProductDescriptionScreen(productId)
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDescriptionScreen(productId: String) {
    val viewModel = remember { ProductViewModel(ProductRepoImpl()) }
    val context = LocalContext.current
    val activity = context as? Activity

    val product by viewModel.selectedProduct.observeAsState()
    val loading by viewModel.loading.observeAsState(initial = false)
    val message by viewModel.message.observeAsState()

    LaunchedEffect(productId) {
        viewModel.getProductById(productId)
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VividAzure,
                    titleContentColor = White,
                    navigationIconContentColor = White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Azure)
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = VividAzure
                )
            } else if (product == null) {
                Text(
                    text = "Product not found",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Product Image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!product!!.imageUrl.isNullOrEmpty() && product!!.imageUrl != "") {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalPlatformContext.current)
                                    .data(product!!.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = product!!.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                error = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_report_image)
                            )
                        } else {
                            Icon(
                                painter = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_gallery),
                                contentDescription = "No Image",
                                tint = Color.Gray,
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    }

                    // Product Details
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Product Name
                        Text(
                            text = product!!.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Price
                        Text(
                            text = "Rs. ${product!!.price}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = VividAzure
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Stock Status
                        val isInStock = product!!.stock > 0
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isInStock) Green.copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isInStock) "✓ In Stock" else "✗ Out of Stock",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isInStock) Green else Color.Red
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "Available: ${product!!.stock}",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Description Section
                        Text(
                            text = "Description",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            colors = CardDefaults.cardColors(containerColor = White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = product!!.description,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(16.dp),
                                lineHeight = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(80.dp)) // Space for button
                    }
                }

                // Add to Cart Button (Fixed at bottom)
                if (product != null) {
                    Button(
                        onClick = {
                            viewModel.addToCart(product!!)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VividAzure,
                            disabledContainerColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = product!!.stock > 0
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (product!!.stock > 0) "Add to Cart" else "Out of Stock",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}