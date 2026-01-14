package com.project.petpoint.view

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.project.petpoint.R
import com.project.petpoint.model.ProductModel
import com.project.petpoint.repository.CartRepoImpl
import com.project.petpoint.repository.ProductRepoImpl
import com.project.petpoint.ui.theme.Black
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.Green
import com.project.petpoint.view.ui.theme.VividAzure
import com.project.petpoint.view.ui.theme.White
import com.project.petpoint.viewmodel.CartViewModel
import com.project.petpoint.viewmodel.ProductViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen() {
    val viewModel = remember { ProductViewModel(ProductRepoImpl()) }
    val cartViewModel = remember { CartViewModel(CartRepoImpl()) }
    val context = LocalContext.current

    val searchQuery by viewModel.searchQuery.observeAsState(initial = "")
    val filteredProducts by viewModel.filteredProducts.observeAsState(initial = emptyList())
    val loading by viewModel.loading.observeAsState(initial = false)
    val message by viewModel.message.observeAsState()

    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""


    LaunchedEffect(Unit) {
        viewModel.getAllProduct()
    }

    // Show toast messages
    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Azure)
            .padding(16.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search products...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Loading Indicator
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = VividAzure)
            }
        } else if (filteredProducts?.isEmpty() ?: true) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isEmpty()) "No products available" else "No products found",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Product Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredProducts?.size ?: 0) { index ->
                    filteredProducts?.get(index)?.let { product ->
                        UserProductCard(
                            product = product,
                            onClick = {
                                val intent = Intent(context, ProductDetailActivity::class.java)
                                intent.putExtra("productId", product.productId)
                                context.startActivity(intent)
                            },
                            onAddToCart = {
                                cartViewModel.addToCart(product, userId) { success, message ->
                                    if (success) {
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserProductCard(
    product: ProductModel,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    val isInStock = product.stock > 0

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Product Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = "https://res.cloudinary.com/dlnlxghqk/image/upload/v1768210540/Snapchat-1158614951.jpg",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
//                if (product.imageUrl.isNotEmpty() && product.imageUrl != "") {
//                    AsyncImage(
//                        model = product.imageUrl,
//                        contentDescription = null,
//                        modifier = Modifier.fillMaxWidth().height(110.dp).clip(RoundedCornerShape(8.dp)),
//                        contentScale = ContentScale.Crop,
//                        error = painterResource(R.drawable.image)
//                    )
//                } else {
//                    // Placeholder when no image
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Icon(
//                            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
//                            contentDescription = "No Image",
//                            tint = Color.Gray,
//                            modifier = Modifier.size(48.dp)
//                        )
//                        Spacer(modifier = Modifier.height(4.dp))
//                        Text(
//                            text = "No Image",
//                            fontSize = 12.sp,
//                            color = Color.Gray
//                        )
//                    }
//                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Product Name
            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(40.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Price
            Text(
                text = "Rs. ${product.price}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Stock Status
            Text(
                text = if (isInStock) "In Stock" else "Out of Stock",
                fontSize = 12.sp,
                color = if (isInStock) Green else Color.Red,
                fontWeight = FontWeight.Medium
            )
        }
    }
}