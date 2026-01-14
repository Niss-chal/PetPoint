package com.project.petpoint.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.google.firebase.auth.FirebaseAuth
import com.project.petpoint.model.CartModel
import com.project.petpoint.repository.CartRepoImpl
import com.project.petpoint.repository.ProductRepoImpl
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.Green
import com.project.petpoint.view.ui.theme.VividAzure
import com.project.petpoint.view.ui.theme.White
import com.project.petpoint.viewmodel.CartViewModel
import com.project.petpoint.viewmodel.ProductViewModel

class CheckoutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CheckoutScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen() {
    val cartViewModel = remember { CartViewModel(CartRepoImpl()) }
    val productViewModel = remember { ProductViewModel(ProductRepoImpl()) }
    val context = LocalContext.current
    val activity = context as? Activity

    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val cartItems by cartViewModel.cartItems.observeAsState(initial = emptyList())
    val totalPrice by cartViewModel.totalPrice.observeAsState(initial = 0.0)
    val loading by cartViewModel.loading.observeAsState(initial = false)

    // Form states
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf("Cash on Delivery") }
    var expandedSection by remember { mutableStateOf("delivery") }
    var isPlacingOrder by remember { mutableStateOf(false) }

    val deliveryCharge = 100.0
    val grandTotal = totalPrice + deliveryCharge

    LaunchedEffect(Unit) {
        if (userId.isNotEmpty()) {
            cartViewModel.getCartItems(userId) { _, _, _ -> }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
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
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Delivery Information Section
                    item {
                        CheckoutSection(
                            title = "Delivery Information",
                            icon = Icons.Default.LocationOn,
                            isExpanded = expandedSection == "delivery",
                            onToggle = { expandedSection = if (expandedSection == "delivery") "" else "delivery" }
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = fullName,
                                    onValueChange = { fullName = it },
                                    label = { Text("Full Name") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Person, contentDescription = null)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = White,
                                        unfocusedContainerColor = White,
                                        focusedIndicatorColor = VividAzure,
                                        unfocusedIndicatorColor = Color.Gray
                                    )
                                )

                                OutlinedTextField(
                                    value = phone,
                                    onValueChange = { phone = it },
                                    label = { Text("Phone Number") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Phone, contentDescription = null)
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = White,
                                        unfocusedContainerColor = White,
                                        focusedIndicatorColor = VividAzure,
                                        unfocusedIndicatorColor = Color.Gray
                                    )
                                )

                                OutlinedTextField(
                                    value = address,
                                    onValueChange = { address = it },
                                    label = { Text("Address") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Home, contentDescription = null)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 2,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = White,
                                        unfocusedContainerColor = White,
                                        focusedIndicatorColor = VividAzure,
                                        unfocusedIndicatorColor = Color.Gray
                                    )
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedTextField(
                                        value = city,
                                        onValueChange = { city = it },
                                        label = { Text("City") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = White,
                                            unfocusedContainerColor = White,
                                            focusedIndicatorColor = VividAzure,
                                            unfocusedIndicatorColor = Color.Gray
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Payment Method Section
                    item {
                        CheckoutSection(
                            title = "Payment Method",
                            icon = Icons.Default.Payment,
                            isExpanded = expandedSection == "payment",
                            onToggle = { expandedSection = if (expandedSection == "payment") "" else "payment" }
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                PaymentMethodOption(
                                    method = "Cash on Delivery",
                                    icon = Icons.Default.Money,
                                    isSelected = selectedPaymentMethod == "Cash on Delivery",
                                    onClick = { selectedPaymentMethod = "Cash on Delivery" }
                                )
                            }
                        }
                    }

                    // Order Summary Section
                    item {
                        CheckoutSection(
                            title = "Order Summary (${cartItems?.size ?: 0} items)",
                            icon = Icons.Default.ShoppingCart,
                            isExpanded = expandedSection == "summary",
                            onToggle = { expandedSection = if (expandedSection == "summary") "" else "summary" }
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                cartItems?.forEach { item ->
                                    OrderItemCard(item)
                                }

                                Divider(modifier = Modifier.padding(vertical = 8.dp))

                                PriceSummaryRow("Subtotal", totalPrice)
                                PriceSummaryRow("Delivery Charge", deliveryCharge)

                                Divider(modifier = Modifier.padding(vertical = 8.dp))

                                PriceSummaryRow(
                                    "Total Amount",
                                    grandTotal,
                                    isTotal = true
                                )
                            }
                        }
                    }

                    // Add spacing at bottom
                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }

                // Place Order Button (Fixed at bottom)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    shadowElevation = 8.dp,
                    color = White
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Button(
                            onClick = {
                                if (fullName.isEmpty() || phone.isEmpty() || address.isEmpty() || city.isEmpty()) {
                                    Toast.makeText(context, "Please fill all delivery information", Toast.LENGTH_SHORT).show()
                                } else if (isPlacingOrder) {
                                    // Prevent double submission
                                    return@Button
                                } else {
                                    isPlacingOrder = true

                                    // Update stock for each product
                                    var updatedCount = 0
                                    val totalItems = cartItems?.size ?: 0

                                    cartItems?.forEach { cartItem ->
                                        // Get full product details first
                                        productViewModel.getProductById(cartItem.productId)
                                    }

                                    // Wait for products to load
                                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                        cartItems?.forEach { cartItem ->
                                            productViewModel.getProductById(cartItem.productId)

                                            // Small delay to let product load
                                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                                val currentProduct = productViewModel.selectedProduct.value

                                                if (currentProduct != null) {
                                                    // Only update stock, keep everything else same
                                                    val newStock = currentProduct.stock - cartItem.quantity
                                                    val updatedProduct = currentProduct.copy(stock = newStock)

                                                    productViewModel.updateProduct(updatedProduct) { success, msg ->
                                                        updatedCount++

                                                        // When all products are updated
                                                        if (updatedCount == totalItems) {
                                                            // Clear cart
                                                            cartViewModel.clearCart(userId) { clearSuccess, _ ->
                                                                isPlacingOrder = false
                                                                if (clearSuccess) {
                                                                    Toast.makeText(
                                                                        context,
                                                                        "Order placed successfully!",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()
                                                                    activity?.finish()
                                                                } else {
                                                                    Toast.makeText(
                                                                        context,
                                                                        "Order placed but failed to clear cart",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    // If product not found, still increment counter
                                                    updatedCount++
                                                    if (updatedCount == totalItems) {
                                                        cartViewModel.clearCart(userId) { _, _ ->
                                                            isPlacingOrder = false
                                                            Toast.makeText(
                                                                context,
                                                                "Order placed!",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            activity?.finish()
                                                        }
                                                    }
                                                }
                                            }, (300 * (cartItems?.indexOf(cartItem) ?: 0)).toLong()) // Stagger the updates
                                        }
                                    }, 500)

                                    if (totalItems == 0) {
                                        isPlacingOrder = false
                                        Toast.makeText(context, "Cart is empty", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Green),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isPlacingOrder
                        ) {
                            if (isPlacingOrder) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Processing...")
                            } else {
                                Icon(Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Place Order - Rs. $grandTotal",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CheckoutSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(VividAzure.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = VividAzure,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            // Expandable Content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun PaymentMethodOption(
    method: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) VividAzure.copy(alpha = 0.1f) else White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = if (isSelected) VividAzure else Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) VividAzure else Color.Gray
                )
                Text(
                    text = method,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) VividAzure else Color.Black
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = VividAzure)
            )
        }
    }
}

@Composable
fun OrderItemCard(item: CartModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Image
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            if (item.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalPlatformContext.current)
                        .data(item.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Details
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = item.name,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
            Text(
                text = "Qty: ${item.quantity}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // Price
        Text(
            text = "Rs. ${item.getTotalPrice()}",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = VividAzure
        )
    }
}

@Composable
fun PriceSummaryRow(label: String, amount: Double, isTotal: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = if (isTotal) 18.sp else 14.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            color = if (isTotal) Color.Black else Color.Gray
        )
        Text(
            text = "Rs. $amount",
            fontSize = if (isTotal) 20.sp else 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isTotal) VividAzure else Color.Black
        )
    }
}