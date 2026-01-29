package com.project.petpoint.view

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import com.project.petpoint.view.ui.theme.Black
import com.project.petpoint.view.ui.theme.Davygrey
import com.project.petpoint.view.ui.theme.Green
import com.project.petpoint.view.ui.theme.Orange
import com.project.petpoint.view.ui.theme.VividAzure
import com.project.petpoint.view.ui.theme.VividOrange
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

    var isAddingToCart by remember { mutableStateOf(false) }
    var showSuccessAnimation by remember { mutableStateOf(false) }

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
                title = {
                    Text(
                        "Product Details",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { activity?.finish() },
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = White
                        )
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
                Surface(
                    shadowElevation = 8.dp,
                    color = White
                ) {
                    Button(
                        onClick = {
                            if (userId.isEmpty()) {
                                Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isAddingToCart = true
                            cartViewModel.addToCart(it, userId) { success, msg ->
                                isAddingToCart = false
                                if (success) {
                                    showSuccessAnimation = true
                                }
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp)
                            .shadow(4.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (it.stock > 0) VividAzure else Color.Gray,
                            disabledContainerColor = Color.Gray
                        ),
                        enabled = it.stock > 0 && !isAddingToCart
                    ) {
                        AnimatedContent(
                            targetState = isAddingToCart,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            },
                            label = "button content"
                        ) { adding ->
                            if (adding) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = White,
                                    strokeWidth = 3.dp
                                )
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (it.stock > 0) "Add to Cart" else "Out of Stock",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Azure,
                            White
                        )
                    )
                )
        ) {
            when {
                loading -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = VividAzure,
                        strokeWidth = 4.dp
                    )
                }

                product == null -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Product not found",
                            fontSize = 18.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(padding)
                ) {
                    // Product Image with gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(380.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(360.dp)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Box {
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
                                    // Gradient overlay at bottom
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp)
                                            .align(Alignment.BottomCenter)
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(
                                                        Color.Transparent,
                                                        Color.Black.copy(alpha = 0.3f)
                                                    )
                                                )
                                            )
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.LightGray),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("No Image", color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }

                    // Product Info Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            // Product Name
                            Text(
                                text = product!!.name,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = Black
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Price with badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = VividAzure.copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        text = "Rs. ${product!!.price}",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VividAzure,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            val isLowStock = product!!.stock in 1..8
                            if (isLowStock) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = VividOrange.copy(alpha = 0.12f)
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Outlined.Info,
                                            contentDescription = null,
                                            tint = Orange,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Low Stock Alert",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = Orange
                                            )
                                            Text(
                                                text = "Only ${product!!.stock} left - Order soon!",
                                                fontSize = 13.sp,
                                                color = Color.Gray,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Stock Status Card
                            val inStock = product!!.stock > 0
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (inStock)
                                        Green.copy(alpha = 0.12f)
                                    else
                                        Color.Red.copy(alpha = 0.12f)
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (inStock) {
                                        Icon(
                                            Icons.Outlined.CheckCircle,
                                            contentDescription = null,
                                            tint = Green,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (inStock) "In Stock" else "Out of Stock",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = if (inStock) Green else Color.Red
                                        )
                                        if (inStock) {
                                            Text(
                                                text = "${product!!.stock} units available",
                                                fontSize = 13.sp,
                                                color = Color.Gray,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Description",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Black
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = product!!.description,
                                color = Davygrey,
                                lineHeight = 24.sp,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }

            // Success Animation Overlay
            AnimatedVisibility(
                visible = showSuccessAnimation,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = White)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val scale by animateFloatAsState(
                                targetValue = 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                label = "scale"
                            )

                            Icon(
                                Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = Green,
                                modifier = Modifier
                                    .size(64.dp)
                                    .scale(scale)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Added to Cart!",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(1500)
                    showSuccessAnimation = false
                }
            }
        }
    }
}