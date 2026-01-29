package com.project.petpoint.view

import android.app.AlertDialog
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.project.petpoint.model.LostFoundModel
import com.project.petpoint.repository.LostFoundRepoImpl
import com.project.petpoint.view.ui.theme.*
import com.project.petpoint.viewmodel.LostFoundViewModel

@Composable
fun LostAndFoundManagementScreen() {
    val viewModel = remember { LostFoundViewModel(LostFoundRepoImpl()) }
    val context = LocalContext.current

    val reports by viewModel.filteredReports.observeAsState(initial = emptyList())
    val loading by viewModel.loading.observeAsState(initial = false)
    val message by viewModel.message.observeAsState()
    val isAdmin by viewModel.isAdmin.observeAsState(initial = false)

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.checkAdminStatus()
        viewModel.getAllReports()
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Lost & Found",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = VividAzure
                        )
                        Text(
                            text = "${reports?.size ?: 0} active reports",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    FloatingActionButton(
                        onClick = {
                            if (FirebaseAuth.getInstance().currentUser == null) {
                                Toast.makeText(context, "Please sign in first", Toast.LENGTH_LONG).show()
                            } else {
                                context.startActivity(Intent(context, AddLostFoundReportActivity::class.java))
                            }
                        },
                        containerColor = VividAzure,
                        contentColor = White,
                        elevation = FloatingActionButtonDefaults.elevation(6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add report")
                    }
                }
            }

            item {
                Text(
                    text = "Filter by Status",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Black
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatusChip(
                        label = "All",
                        icon = Icons.Outlined.ViewList,
                        isSelected = viewModel.filterType.value == "All",
                        onClick = { viewModel.setFilterType("All") },
                        color = VividAzure
                    )
                    StatusChip(
                        label = "Lost",
                        icon = Icons.Outlined.SearchOff,
                        isSelected = viewModel.filterType.value == "Lost",
                        onClick = { viewModel.setFilterType("Lost") },
                        color = crimson
                    )
                    StatusChip(
                        label = "Found",
                        icon = Icons.Outlined.Check,
                        isSelected = viewModel.filterType.value == "Found",
                        onClick = { viewModel.setFilterType("Found") },
                        color = Blue
                    )
                    StatusChip(
                        label = "Rescued",
                        icon = Icons.Outlined.Favorite,
                        isSelected = viewModel.filterType.value == "Rescued",
                        onClick = { viewModel.setFilterType("Rescued") },
                        color = lightgreen
                    )
                }
            }

            when {
                loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = VividAzure,
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Loading reports...", color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                    }
                }

                reports!!.isEmpty() -> {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            colors = CardDefaults.cardColors(containerColor = White),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(48.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Outlined.SearchOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color.Gray.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "No reports found",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray
                                )
                                Text(
                                    "Try adjusting your filters",
                                    fontSize = 14.sp,
                                    color = Color.Gray.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                else -> {
                    items(reports!!.size) { index ->
                        val item = reports!![index]
                        var visible by remember { mutableStateOf(false) }

                        LostFoundAdminCard(
                            item = item,
                            isAdmin = isAdmin,
                            viewModel = viewModel,
                            onEdit = {
                                context.startActivity(
                                    Intent(context, AddLostFoundReportActivity::class.java).apply {
                                        putExtra("lostId", item.lostId)
                                    }
                                )
                            },
                            onDelete = {
                                AlertDialog.Builder(context)
                                    .setTitle("Delete Report")
                                    .setMessage(
                                        "Are you sure you want to permanently delete \"${item.title}\"?\n\n" +
                                                "This action cannot be undone."
                                    )
                                    .setPositiveButton("Delete") { _, _ ->
                                        viewModel.deleteReport(item.lostId)
                                    }
                                    .setNegativeButton("Cancel", null)
                                    .show()
                            },
                            onChangeStatus = { newStatus ->
                                AlertDialog.Builder(context)
                                    .setTitle("Change Status")
                                    .setMessage("Change status to \"$newStatus\"?")
                                    .setPositiveButton("Change") { _, _ ->
                                        viewModel.changeStatus(item.lostId, newStatus)
                                    }
                                    .setNegativeButton("Cancel", null)
                                    .show()
                            }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun StatusChip(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    color: Color
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "chip scale"
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) color else White,
        shadowElevation = if (isSelected) 4.dp else 1.dp,
        modifier = Modifier.scale(animatedScale)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (isSelected) White else color
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) White else Black
            )
        }
    }
}

@Composable
fun LostFoundAdminCard(
    item: LostFoundModel,
    isAdmin: Boolean,
    viewModel: LostFoundViewModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onChangeStatus: (String) -> Unit
) {
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid
    val isOwner = currentUid != null && item.reportedBy == currentUid
    val canManage = isOwner || isAdmin

    val typeLower = item.type.lowercase()
    val statusColor = when (typeLower) {
        "lost" -> crimson
        "found" -> Blue
        else -> Green
    }

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
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
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            when (typeLower) {
                                "lost" -> Icons.Outlined.SearchOff
                                "found" -> Icons.Outlined.Check
                                else -> Icons.Outlined.Favorite
                            },
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            item.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Black
                        )
                        Text(
                            item.category,
                            fontSize = 13.sp,
                            color = Davygrey
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        typeLower.uppercase(),
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            if (item.imageUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ReportDetailRow(Icons.Outlined.LocationOn, item.location)
            Spacer(modifier = Modifier.height(8.dp))
            ReportDetailRow(Icons.Outlined.Person, item.reportedByName ?: "Anonymous")
            Spacer(modifier = Modifier.height(8.dp))
            ReportDetailRow(Icons.Outlined.CalendarToday, item.date)

            if (canManage) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.Gray.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(16.dp))

                if (item.type.equals("Lost", ignoreCase = true)) {
                    Button(
                        onClick = { onChangeStatus("Found") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Green
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Check,
                            null,
                            modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Mark as Found", fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = VividAzure
                        )
                    ) {
                        Icon(Icons.Default.Edit,
                            null,
                            modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Edit", fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = crimson
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Delete,
                            null,
                            modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Delete", fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportDetailRow(icon: ImageVector, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            value,
            fontSize = 14.sp,
            color = Davygrey
        )
    }
}