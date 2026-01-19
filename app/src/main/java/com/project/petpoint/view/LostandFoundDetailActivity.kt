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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.project.petpoint.repository.LostFoundRepoImpl
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.VividAzure
import com.project.petpoint.view.ui.theme.White
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
                title = { Text("Item Details") },
                navigationIcon = {
                    IconButton(onClick = { context?.finish() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VividAzure,
                    titleContentColor = White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Azure)
                .padding(padding)
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = VividAzure
                )
            } else if (item == null) {
                Text(
                    "Item not found",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            } else {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    // Image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .background(Color.LightGray)
                    ) {
                        if (item!!.imageUrl.isNotBlank()) {
                            AsyncImage(
                                model = item!!.imageUrl,
                                contentDescription = item!!.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                "No photo available",
                                modifier = Modifier.align(Alignment.Center),
                                color = Color.Gray
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = item!!.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Badge(
                            text = item!!.type.uppercase(),
                            background = if (item!!.type == "Lost") Color(0xFFfee2e2) else Color(0xFFdcfce7),
                            textColor = if (item!!.type == "Lost") Color(0xFFdc2626) else Color(0xFF15803d)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        DetailRow("Category", item!!.category)
                        DetailRow("Location", item!!.location)
                        DetailRow("Date", item!!.date)
                        DetailRow("Reported by", item!!.reportedByName ?: "Anonymous")

                        if (item!!.contactInfo.isNotBlank()) {
                            DetailRow("Contact", item!!.contactInfo)
                        }

                        if (canManage) {
                            Spacer(modifier = Modifier.height(32.dp))

                            // Status change button
                            val newStatus = if (item!!.type == "Lost") "Found" else "Lost"
                            Button(
                                onClick = { showStatusDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF059669)
                                )
                            ) {
                                Text("Mark as $newStatus")
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Edit and Delete buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = {
                                        context?.startActivity(
                                            Intent(context, AddLostFoundReportActivity::class.java).apply {
                                                putExtra("lostId", item!!.lostId)
                                            }
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = VividAzure)
                                ) {
                                    Text("Edit")
                                }

                                Button(
                                    onClick = { showDeleteDialog = true },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFdc2626)
                                    )
                                ) {
                                    Text("Delete")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text("Description", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = White)
                        ) {
                            Text(
                                item!!.description.ifBlank { "No description provided." },
                                modifier = Modifier.padding(16.dp),
                                lineHeight = 22.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }

            // Delete Dialog
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Delete Permanently?") },
                    text = {
                        Text(
                            "Are you sure you want to permanently delete \"${item!!.title}\"?\n\n" +
                                    "This action CANNOT be undone."
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                viewModel.deleteReport(item!!.lostId) { success, msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    if (success) {
                                        context?.finish()
                                    }
                                }
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color(0xFFdc2626)
                            )
                        ) {
                            Text("Delete Permanently")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Status Change Dialog
            if (showStatusDialog) {
                val newStatus = if (item!!.type == "Lost") "Found" else "Lost"
                AlertDialog(
                    onDismissRequest = { showStatusDialog = false },
                    title = { Text("Change Status") },
                    text = {
                        Text("Change the status of this report to \"$newStatus\"?")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            showStatusDialog = false
                            viewModel.changeStatus(item!!.lostId, newStatus) { success, msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    viewModel.getReportById(item!!.lostId)
                                }
                            }
                        }) {
                            Text("Change to $newStatus")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showStatusDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun Badge(
    text: String,
    background: Color,
    textColor: Color
) {
    Surface(
        color = background,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 14.sp
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = "$label: ",
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f)
        )
    }
}