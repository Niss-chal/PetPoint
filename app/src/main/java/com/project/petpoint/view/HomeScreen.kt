package com.project.petpoint.view

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.petpoint.R
import com.project.petpoint.model.LostFoundModel
import com.project.petpoint.model.ProductModel
import com.project.petpoint.model.VetModel
import com.project.petpoint.repository.LostFoundRepoImpl
import com.project.petpoint.repository.ProductRepoImpl
import com.project.petpoint.repository.VetRepoImpl
import com.project.petpoint.view.ui.theme.*
import com.project.petpoint.viewmodel.HomeViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen() {
    val viewModel = remember {
        HomeViewModel(
            productRepo = ProductRepoImpl(),
            vetRepo = VetRepoImpl(),
            lostFoundRepo = LostFoundRepoImpl()
        )
    }

    val totalProducts by viewModel.totalProducts.observeAsState(0)
    val activeDoctors by viewModel.activeDoctors.observeAsState(0)
    val lostFoundPets by viewModel.lostFoundPets.observeAsState(0)
    val recentProducts by viewModel.recentProducts.observeAsState(emptyList())
    val recentVets by viewModel.recentVets.observeAsState(emptyList())
    val recentReports by viewModel.recentReports.observeAsState(emptyList())
    val loading by viewModel.loading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.loadOverviewData()
    }

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
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Dashboard",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = VividAzure
                    )
                    Text(
                        text = "Welcome back! Here's your overview",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            if (loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = VividAzure,
                            strokeWidth = 3.dp
                        )
                    }
                }
            }

            errorMessage?.let { error ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFfee2e2)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "⚠️ $error",
                            color = Color(0xFFdc2626),
                            modifier = Modifier.padding(16.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Quick Stats",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1a1a1a)
                )
            }

            item {
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(100)
                    visible = true
                }

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
                ) {
                    ImprovedCardItem(
                        title = "Total Products",
                        count = totalProducts.toString(),
                        image = painterResource(id = R.drawable.greenbox),
                        backgroundColor = Color(0xFF10b981),
                        delay = 0
                    )
                }
            }

            item {
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(150)
                    visible = true
                }

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
                ) {
                    ImprovedCardItem(
                        title = "Active Doctors",
                        count = activeDoctors.toString(),
                        image = painterResource(id = R.drawable.doctor),
                        backgroundColor = Color(0xFF3b82f6),
                        delay = 50
                    )
                }
            }

            item {
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(200)
                    visible = true
                }

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
                ) {
                    ImprovedCardItem(
                        title = "Lost & Found Pets",
                        count = lostFoundPets.toString(),
                        image = painterResource(id = R.drawable.pet),
                        backgroundColor = Color(0xFFf59e0b),
                        delay = 100
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                RecentActivitiesCard(
                    recentProducts = recentProducts,
                    recentVets = recentVets,
                    recentReports = recentReports
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ImprovedCardItem(
    title: String,
    count: String,
    image: Painter,
    backgroundColor: Color,
    delay: Long = 0
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        color = Color(0xFF666666)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = count,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = backgroundColor
                    )
                }

                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(backgroundColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = image,
                        contentDescription = title,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RecentActivitiesCard(
    recentProducts: List<ProductModel>,
    recentVets: List<VetModel>,
    recentReports: List<LostFoundModel>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(VividAzure.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📋", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Recent Activities",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1a1a1a)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val hasActivities = recentProducts.isNotEmpty() ||
                    recentVets.isNotEmpty() ||
                    recentReports.isNotEmpty()

            if (!hasActivities) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("💤", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No recent activities",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    recentProducts.forEach { product ->
                        ProductActivityItem(product = product)
                    }

                    recentVets.forEach { vet ->
                        VetActivityItem(vet = vet)
                    }

                    recentReports.forEach { report ->
                        ReportActivityItem(report = report)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductActivityItem(product: ProductModel) {
    ActivityItemCard(
        icon = Icons.Default.ShoppingCart,
        iconColor = VividAzure,
        title = product.name,
        subtitle = "Product Added",
        additionalInfo = "Rs. ${product.price}"
    )
}

@Composable
fun VetActivityItem(vet: VetModel) {
    ActivityItemCard(
        icon = Icons.Default.LocalHospital,
        iconColor = Green,
        title = vet.name,
        subtitle = "Veterinarian Added",
        additionalInfo = vet.specialization
    )
}

@Composable
fun ReportActivityItem(report: LostFoundModel) {
    val isLost = report.type == "Lost"
    ActivityItemCard(
        icon = if (isLost) Icons.Default.Search else Icons.Default.Pets,
        iconColor = if (isLost) Color(0xFFdc2626) else Color(0xFF16a34a),
        title = report.title,
        subtitle = "${report.type} Pet",
        additionalInfo = report.location,
        date = report.date
    )
}

@Composable
fun ActivityItemCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    additionalInfo: String,
    date: String? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF8F9FA),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = subtitle,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(0xFF1a1a1a)
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    color = Color(0xFF666666),
                    fontSize = 12.sp
                )
                if (additionalInfo.isNotEmpty()) {
                    Text(
                        text = additionalInfo,
                        color = iconColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            date?.let {
                Text(
                    text = it,
                    color = Color(0xFF999999),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}