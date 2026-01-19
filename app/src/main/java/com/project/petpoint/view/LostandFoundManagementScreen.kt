package com.project.petpoint.view

import android.app.AlertDialog
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import com.google.firebase.auth.FirebaseAuth
import com.project.petpoint.model.LostFoundModel
import com.project.petpoint.repository.LostFoundRepoImpl
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.VividAzure
import com.project.petpoint.view.ui.theme.White
import com.project.petpoint.viewmodel.LostFoundViewModel

@Composable
fun LostAndFoundManagementScreen() {
    val viewModel = remember { LostFoundViewModel(LostFoundRepoImpl()) }
    val context = LocalContext.current

    val reports by viewModel.filteredReports.observeAsState(initial = emptyList())
    val loading by viewModel.loading.observeAsState(initial = false)
    val message by viewModel.message.observeAsState()
    val isAdmin by viewModel.isAdmin.observeAsState(initial = false)

    LaunchedEffect(Unit) {
        viewModel.getAllReports(includeHidden = true)
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Azure)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Manage Lost & Found Reports${if (isAdmin) " (Admin)" else ""}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            IconButton(onClick = {
                if (FirebaseAuth.getInstance().currentUser == null) {
                    Toast.makeText(context, "Please sign in first", Toast.LENGTH_LONG).show()
                } else {
                    context.startActivity(Intent(context, AddLostFoundReportActivity::class.java))
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add report", tint = VividAzure)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = viewModel.filterType.value == "All",
                onClick = { viewModel.setFilterType("All") },
                label = { Text("All") }
            )
            FilterChip(
                selected = viewModel.filterType.value == "Lost",
                onClick = { viewModel.setFilterType("Lost") },
                label = { Text("Lost") }
            )
            FilterChip(
                selected = viewModel.filterType.value == "Found",
                onClick = { viewModel.setFilterType("Found") },
                label = { Text("Found") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = VividAzure
            )
        } else if (reports!!.isEmpty()) {
            Text(
                "No reports found",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color.Gray,
                fontSize = 16.sp
            )
        } else {
            LazyColumn {
                items(reports!!) { item ->
                    val currentUid = FirebaseAuth.getInstance().currentUser?.uid
                    val isOwner = currentUid != null && item.reportedBy == currentUid
                    val canManage = isOwner || isAdmin

                    LostFoundAdminCard(
                        item = item,
                        isAdmin = isAdmin,
                        canManage = canManage,
                        onEdit = {
                            if (canManage) {
                                context.startActivity(
                                    Intent(context, AddLostFoundReportActivity::class.java).apply {
                                        putExtra("lostId", item.lostId)
                                    }
                                )
                            } else {
                                Toast.makeText(context, "No permission to edit", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onHide = {
                            if (canManage && item.isVisible) {
                                AlertDialog.Builder(context)
                                    .setTitle(if (isAdmin) "Hide Report" else "Delete Report")
                                    .setMessage(
                                        if (isAdmin) {
                                            "Hide \"${item.title}\"?\nIt will be invisible to everyone (including the owner)."
                                        } else {
                                            "Delete \"${item.title}\"?\nThis report will be removed from your view and public lists.\nThis cannot be undone by you."
                                        }
                                    )
                                    .setPositiveButton(if (isAdmin) "Hide" else "Delete") { _, _ ->
                                        viewModel.hideReport(item.lostId) { success, msg ->
                                            Toast.makeText(
                                                context,
                                                msg ?: if (success) "Success" else "Failed",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    .setNegativeButton("Cancel", null)
                                    .show()
                            }
                        },
                        onUnhide = {
                            if (isAdmin && !item.isVisible) {
                                if (item.lostId.isBlank()) {
                                    Toast.makeText(context, "Invalid report ID", Toast.LENGTH_SHORT).show()
                                    return@LostFoundAdminCard
                                }

                                AlertDialog.Builder(context)
                                    .setTitle("Restore Report")
                                    .setMessage("Make \"${item.title}\" visible again?")
                                    .setPositiveButton("Restore") { _, _ ->
                                        viewModel.unhideReport(item.lostId) { success, msg ->
                                            Toast.makeText(
                                                context,
                                                msg ?: if (success) "Report restored" else "Failed to restore",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    .setNegativeButton("Cancel", null)
                                    .show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LostFoundAdminCard(
    item: LostFoundModel,
    isAdmin: Boolean,
    canManage: Boolean,
    onEdit: () -> Unit,
    onHide: () -> Unit,
    onUnhide: () -> Unit
) {
    val statusColor = if (item.isVisible) Color(0xFF16a34a) else Color(0xFFdc2626)
    val statusText = if (item.isVisible) "Visible" else "Deleted"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(item.title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            item.type.uppercase(),
                            color = if (item.type == "Lost") Color(0xFFdc2626) else Color(0xFF16a34a),
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "• $statusText",
                            color = statusColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (item.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text("Location: ${item.location}", fontSize = 14.sp, color = Color.DarkGray)
            Text("Category: ${item.category}", fontSize = 14.sp, color = Color.DarkGray)
            Text(
                "Reported by: ${item.reportedByName ?: "Anonymous"} • ${item.date}",
                fontSize = 13.sp,
                color = Color.Gray
            )

            if (canManage) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, null, tint = Color(0xFF2563eb))
                        Spacer(Modifier.width(4.dp))
                        Text("Edit", color = Color(0xFF2563eb))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    if (item.isVisible) {
                        TextButton(onClick = onHide) {
                            Icon(Icons.Default.Delete, null, tint = Color(0xFFdc2626))
                            Spacer(Modifier.width(4.dp))
                            Text(if (isAdmin) "Hide" else "Delete", color = Color(0xFFdc2626))
                        }
                    } else if (isAdmin) {
                        TextButton(onClick = onUnhide) {
                            Icon(Icons.Default.Visibility, null, tint = Color(0xFF16a34a))
                            Spacer(Modifier.width(4.dp))
                            Text("Restore", color = Color(0xFF16a34a))
                        }
                    } else {
                        // Owner sees only status - no action
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = Color(0xFF9ca3af),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text("Deleted", color = Color(0xFF9ca3af), fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}