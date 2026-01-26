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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.project.petpoint.repository.CartRepoImpl
import com.project.petpoint.repository.ProductRepoImpl
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.Green
import com.project.petpoint.view.ui.theme.VividAzure
import com.project.petpoint.view.ui.theme.White
import com.project.petpoint.viewmodel.CartViewModel
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
    val cartViewModel = remember { CartViewModel(CartRepoImpl()) }
    val context = LocalContext.current
    val activity = context as? Activity

    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VividAzure,
                    titleContentColor = White,
                    navigationIconContentColor = White
                )
            )
        },
        bottomBar = {
            product?.let {
                Button(
                    onClick = {
                        if (userId.isEmpty()) {
                            Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        cartViewModel.addToCart(it, userId) { _, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VividAzure),
                    enabled = it.stock > 0
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (it.stock > 0) "Add to Cart" else "Out of Stock",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Azure)
        ) {
            when {
                loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = VividAzure
                )

                product == null -> Text(
                    text = "Product not found",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )

                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                            .padding(16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!product!!.imageUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(product!!.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = product!!.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = product!!.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Rs. ${product!!.price}",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = VividAzure
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val inStock = product!!.stock > 0
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (inStock)
                                    Green.copy(alpha = 0.15f)
                                else
                                    Color.Red.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (inStock) "✓ In Stock" else "✗ Out of Stock",
                                    fontWeight = FontWeight.Medium,
                                    color = if (inStock) Green else Color.Red
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text("Available: ${product!!.stock}", color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Description",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = product!!.description,
                                modifier = Modifier.padding(16.dp),
                                color = Color.Gray,
                                lineHeight = 22.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}
