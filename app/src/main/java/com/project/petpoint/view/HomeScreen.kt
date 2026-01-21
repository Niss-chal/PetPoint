package com.project.petpoint.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.project.petpoint.view.ui.theme.Green
import com.project.petpoint.view.ui.theme.PurpleGrey808
import com.project.petpoint.view.ui.theme.VividAzure
import com.project.petpoint.view.ui.theme.White
import com.project.petpoint.view.ui.theme.crimson
import com.project.petpoint.view.ui.theme.lightgreen
import com.project.petpoint.viewmodel.HomeViewModel

@Composable
fun HomeScreen() {
    // Initialize ViewModel with repositories
    val viewModel = remember {
        HomeViewModel(
            productRepo = ProductRepoImpl(),
            vetRepo = VetRepoImpl(),
            lostFoundRepo = LostFoundRepoImpl()
        )
    }

    // Observe LiveData
    val totalProducts by viewModel.totalProducts.observeAsState(0)
    val activeDoctors by viewModel.activeDoctors.observeAsState(0)
    val lostFoundPets by viewModel.lostFoundPets.observeAsState(0)
    val recentProducts by viewModel.recentProducts.observeAsState(emptyList())
    val recentVets by viewModel.recentVets.observeAsState(emptyList())
    val recentReports by viewModel.recentReports.observeAsState(emptyList())
    val loading by viewModel.loading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()

    // Load data when screen appears
    LaunchedEffect(Unit) {
        viewModel.loadOverviewData()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Overview",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        item {
            Divider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Show loading indicator
        if (loading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = VividAzure)
                }
            }
        }

        // Show error message if any
        errorMessage?.let { error ->
            item {
                Text(
                    text = "Error: $error",
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        // Display cards with dynamic data
        item {
            CardItem(
                title = "Total Products",
                count = totalProducts.toString(),
                image = painterResource(id = R.drawable.greenbox)
            )
        }

        item {
            CardItem(
                title = "Active Doctors",
                count = activeDoctors.toString(),
                image = painterResource(id = R.drawable.doctor)
            )
        }

        item {
            CardItem(
                title = "Lost & Found Pets",
                count = lostFoundPets.toString(),
                image = painterResource(id = R.drawable.pet)
            )
        }

        // Recent Activities Section
        item {
            RecentActivitiesCard(
                recentProducts = recentProducts,
                recentVets = recentVets,
                recentReports = recentReports
            )
        }
    }
}

@Composable
fun CardItem(title: String, count: String, image: Painter) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(PurpleGrey808, shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                Text(text = title, fontWeight = FontWeight.SemiBold)
                Text(
                    text = count,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 24.dp)
                )
            }
            Image(
                painter = image,
                contentDescription = title,
                modifier = Modifier.size(48.dp)
            )
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
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PurpleGrey808)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Recent Activities",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            val hasActivities = recentProducts.isNotEmpty() ||
                    recentVets.isNotEmpty() ||
                    recentReports.isNotEmpty()

            if (!hasActivities) {
                Text(
                    text = "No recent activities",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                // Show recent products
                recentProducts.forEach { product ->
                    ProductActivityItem(product = product)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Show recent vets
                recentVets.forEach { vet ->
                    VetActivityItem(vet = vet)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Show recent reports
                recentReports.forEach { report ->
                    ReportActivityItem(report = report)
                    Spacer(modifier = Modifier.height(8.dp))
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
        iconColor = if (isLost) crimson else lightgreen,
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with colored background
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = subtitle,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Activity details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                if (additionalInfo.isNotEmpty()) {
                    Text(
                        text = additionalInfo,
                        color = Color.Gray,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Date (if available)
            date?.let {
                Text(
                    text = it,
                    color = Color.Gray,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
