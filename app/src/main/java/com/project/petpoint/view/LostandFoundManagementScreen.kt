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

    LaunchedEffect(Unit) {
        viewModel.getAllReports()
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
                "Manage Lost & Found",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = {
                // Open Add screen
                context.startActivity(
                    Intent(context, AddLostFoundReportActivity::class.java)
                )
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add report", tint = VividAzure)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filters
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
            FilterChip(
                selected = viewModel.filterStatus.value == "All",
                onClick = { viewModel.setFilterStatus("All") },
                label = { Text("All Status") }
            )
            FilterChip(
                selected = viewModel.filterStatus.value == "Pending",
                onClick = { viewModel.setFilterStatus("Pending") },
                label = { Text("Pending") }
            )
            FilterChip(
                selected = viewModel.filterStatus.value == "Resolved",
                onClick = { viewModel.setFilterStatus("Resolved") },
                label = { Text("Resolved") }
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
                text = "No reports found",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color.Gray
            )
        } else {
            LazyColumn {
                items(reports!!) { item ->
                    LostFoundAdminCard(
                        item = item,
                        onEdit = {
                            // Open the same screen in edit mode
                            context.startActivity(
                                Intent(context, AddLostFoundReportActivity::class.java).apply {
                                    putExtra("lostId", item.lostId)
                                }
                            )
                        },
                        onDelete = {
                            AlertDialog.Builder(context)
                                .setTitle("Delete Report")
                                .setMessage("Are you sure you want to delete \"${item.title}\" ?")
                                .setPositiveButton("Delete") { _, _ ->
                                    viewModel.deleteReport(item.lostId) { success, msg ->
                                        // Toast is already handled in ViewModel
                                    }
                                }
                                .setNegativeButton("Cancel", null)
                                .show()
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
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
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
                    Text(
                        item.type.uppercase(),
                        color = if (item.type == "Lost") Color(0xFFdc2626) else Color(0xFF16a34a),
                        fontSize = 13.sp
                    )
                }
                Badge(
                    text = item.status,
                    background = Color(0xFFfef3c7),
                    textColor = Color(0xFFb45309)
                )
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
                "Reported by: ${item.reportedBy} • ${item.date}",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, null, tint = Color(0xFF2563eb))
                    Spacer(Modifier.width(4.dp))
                    Text("Edit", color = Color(0xFF2563eb))
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, null, tint = Color.Red)
                    Spacer(Modifier.width(4.dp))
                    Text("Delete", color = Color.Red)
                }
            }
        }
    }
}