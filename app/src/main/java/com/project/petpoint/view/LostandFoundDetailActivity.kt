package com.project.petpoint.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.project.petpoint.repository.LostFoundRepoImpl
import com.project.petpoint.view.ui.theme.*
import com.project.petpoint.viewmodel.LostFoundViewModel

class LostandFoundDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val lostId = intent.getStringExtra("lostId") ?: ""

        setContent {
            LostAndFoundDetailScreen(lostId)
        }
    }
}

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostAndFoundDetailScreen(lostId: String) {
    val viewModel = remember { LostFoundViewModel(LostFoundRepoImpl()) }
    val context = LocalContext.current as? ComponentActivity

    val item by viewModel.selectedReport.observeAsState()
    val loading by viewModel.loading.observeAsState(initial = false)
    val message by viewModel.message.observeAsState()
    val isAdmin by viewModel.isAdmin.observeAsState(initial = false)

    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentUid = currentUser?.uid

    val isOwner = currentUid != null && item?.reportedBy == currentUid
    val canManage = isOwner || isAdmin

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.checkAdminStatus()
    }

    LaunchedEffect(lostId) {
        if (lostId.isNotBlank()) {
            viewModel.getReportById(lostId)
        }
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
                        "Report Details",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { context?.finish() },
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
                .padding(padding)
        ) {
            when {
                loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = VividAzure,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Loading details...",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                item == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Outlined.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = Color.Gray.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Report not found",
                                color = Color.Gray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                else -> {
                    val typeLower = item!!.type.lowercase()
                    val statusColor = when (typeLower) {
                        "lost" -> crimson
                        "found" -> Blue
                        else -> Green
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Image Section with Status Badge
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
                                    if (item!!.imageUrl.isNotBlank()) {
                                        AsyncImage(
                                            model = item!!.imageUrl,
                                            contentDescription = item!!.title,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        // Gradient overlay
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(120.dp)
                                                .align(Alignment.BottomCenter)
                                                .background(
                                                    Brush.verticalGradient(
                                                        colors = listOf(
                                                            Color.Transparent,
                                                            Color.Black.copy(alpha = 0.4f)
                                                        )
                                                    )
                                                )
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(IceWhite),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Icon(
                                                    Icons.Outlined.Image,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(64.dp),
                                                    tint = Color.Gray
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    "No photo available",
                                                    color = Color.Gray,
                                                    fontSize = 14.sp
                                                )
                                            }
                                        }
                                    }

                                    // Status Badge
                                    Surface(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(16.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        color = statusColor,
                                        shadowElevation = 4.dp
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                when (typeLower) {
                                                    "lost" -> Icons.Outlined.SearchOff
                                                    "found" -> Icons.Outlined.Check
                                                    else -> Icons.Outlined.Favorite
                                                },
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp),
                                                tint = White
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = item!!.type.uppercase(),
                                                color = White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Title Card
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
                                    text = item!!.title,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Black,
                                    lineHeight = 32.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Details Card
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
                                    text = "Details",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Black
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                DetailInfoCard(
                                    icon = Icons.Outlined.Category,
                                    label = "Category",
                                    value = item!!.category,
                                    color = VividAzure
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                DetailInfoCard(
                                    icon = Icons.Outlined.LocationOn,
                                    label = "Location",
                                    value = item!!.location,
                                    color = Vividpink
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                DetailInfoCard(
                                    icon = Icons.Outlined.CalendarToday,
                                    label = "Date",
                                    value = item!!.date,
                                    color = Orange
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                DetailInfoCard(
                                    icon = Icons.Outlined.Person,
                                    label = "Reported by",
                                    value = item!!.reportedByName ?: "Anonymous",
                                    color = Green
                                )

                                if (item!!.contactInfo.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(12.dp))

                                    DetailInfoCard(
                                        icon = Icons.Outlined.Phone,
                                        label = "Contact",
                                        value = item!!.contactInfo,
                                        color = lightgreen
                                    )
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
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Black
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = item!!.description.ifBlank { "No description provided." },
                                    color = Davygrey,
                                    lineHeight = 24.sp,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Action Buttons
                        if (canManage) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                // Mark as Found Button (only for Lost items)
                                if (item!!.type.equals("Lost", ignoreCase = true)) {
                                    Button(
                                        onClick = { showStatusDialog = true },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp)
                                            .shadow(4.dp, RoundedCornerShape(16.dp)),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Green
                                        )
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Outlined.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp),
                                                tint = White
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                "Mark as Found",
                                                color = White,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                // Edit and Delete Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            context?.startActivity(
                                                Intent(context, AddLostFoundReportActivity::class.java).apply {
                                                    putExtra("lostId", item!!.lostId)
                                                }
                                            )
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(56.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = VividAzure
                                        )
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Outlined.Edit,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                "Edit",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = { showDeleteDialog = true },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(56.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = crimson
                                        )
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Outlined.Delete,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                "Delete",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }

            // Delete Dialog
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    icon = {
                        Icon(
                            Icons.Outlined.Warning,
                            contentDescription = null,
                            tint = crimson,
                            modifier = Modifier.size(48.dp)
                        )
                    },
                    title = {
                        Text(
                            "Delete Report?",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    },
                    text = {
                        Text(
                            "Are you sure you want to permanently delete \"${item!!.title}\"?\n\nThis action cannot be undone.",
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showDeleteDialog = false
                                viewModel.deleteReport(item!!.lostId) { success, msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    if (success) {
                                        context?.finish()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = crimson
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Delete", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = { showDeleteDialog = false },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel")
                        }
                    },
                    shape = RoundedCornerShape(20.dp)
                )
            }

            // Status Change Dialog
            if (showStatusDialog) {
                AlertDialog(
                    onDismissRequest = { showStatusDialog = false },
                    icon = {
                        Icon(
                            Icons.Outlined.Check,
                            contentDescription = null,
                            tint = Green,
                            modifier = Modifier.size(48.dp)
                        )
                    },
                    title = {
                        Text(
                            "Mark as Found?",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    },
                    text = {
                        Text(
                            "Change the status of this report to \"Found\"?\n\nThis action cannot be reversed.",
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showStatusDialog = false
                                viewModel.changeStatus(item!!.lostId, "Found") { success, msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    if (success) {
                                        viewModel.getReportById(item!!.lostId)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Green
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Mark as Found", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = { showStatusDialog = false },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel")
                        }
                    },
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }
    }
}

@Composable
private fun DetailInfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.08f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Black,
                    lineHeight = 20.sp
                )
            }
        }
    }
}