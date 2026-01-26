import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.project.petpoint.R
import com.project.petpoint.model.ProductModel
import com.project.petpoint.repository.ProductRepoImpl
import com.project.petpoint.view.ProductDetailActivity
import com.project.petpoint.viewmodel.ProductViewModel
import com.project.petpoint.view.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen() {

    val viewModel = remember { ProductViewModel(ProductRepoImpl()) }
    val context = LocalContext.current

    val searchQuery by viewModel.searchQuery.observeAsState("")
    val filteredProducts by viewModel.filteredProducts.observeAsState(emptyList())
    val loading by viewModel.loading.observeAsState(false)
    val message by viewModel.message.observeAsState()

    var selectedCategory by remember { mutableStateOf("All") }

    LaunchedEffect(Unit) {
        viewModel.getAllProduct()
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    val categories = listOf("All", "Food", "Toys", "Accessories", "Clothes", "Medicine", "Other")

    val productsToDisplay = if (selectedCategory == "All") {
        filteredProducts
    } else {
        filteredProducts!!.filter { it.categoryId == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Azure)
            .padding(16.dp)
    ) {

        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search products") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Categories
        LazyRow {
            items(categories.size) { index ->
                val category = categories[index]
                Button(
                    onClick = { selectedCategory = category },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedCategory == category) VividAzure else White,
                        contentColor = if (selectedCategory == category) White else Black
                    ),
                    modifier = Modifier.padding(end = 6.dp)
                ) {
                    Text(category, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = VividAzure)
            }
        } else if (productsToDisplay!!.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (searchQuery.isEmpty()) "No products available" else "No products found",
                    color = Color.Gray
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(productsToDisplay.size) { index ->
                    val product = productsToDisplay[index]

                    UserProductCard(
                        product = product,
                        onClick = {
                            val intent = Intent(context, ProductDetailActivity::class.java)
                            intent.putExtra("productId", product.productId)
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun UserProductCard(
    product: ProductModel,
    onClick: () -> Unit
) {
    val isInStock = product.stock > 0

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                if (product.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = painterResource(R.drawable.image)
                    )
                } else {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Rs. ${product.price}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Text(
                text = if (isInStock) "In Stock" else "Out of Stock",
                color = if (isInStock) Green else Color.Red,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = onClick,
                enabled = isInStock,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                Text("View Details", color = White, fontSize = 12.sp)
            }
        }
    }
}
