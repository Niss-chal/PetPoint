package com.project.petpoint.view

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.style.TextOverflow
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostAndFoundScreen() {
    val viewModel = remember { LostFoundViewModel(LostFoundRepoImpl()) }
    val context = LocalContext.current

    val reports by viewModel.filteredReports.observeAsState(initial = emptyList())
    val loading by viewModel.loading.observeAsState(initial = false)
    val searchQuery by viewModel.searchQuery.observeAsState(initial = "")

    LaunchedEffect(Unit) {
        viewModel.getAllReports()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (FirebaseAuth.getInstance().currentUser == null) {
                        Toast.makeText(context, "Please sign in to report", Toast.LENGTH_LONG).show()
                    } else {
                        context.startActivity(Intent(context, AddLostFoundReportActivity::class.java))
                    }
                },
                containerColor = VividAzure,
                contentColor = White,
                elevation = FloatingActionButtonDefaults.elevation(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Report lost or found pet/item"
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Azure)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search lost & found items...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = White,
                    unfocusedContainerColor = White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(selected = viewModel.filterType.value == "All", onClick = { viewModel.setFilterType("All") }, label = { Text("All") })
                FilterChip(selected = viewModel.filterType.value == "Lost", onClick = { viewModel.setFilterType("Lost") }, label = { Text("Lost") })
                FilterChip(selected = viewModel.filterType.value == "Found", onClick = { viewModel.setFilterType("Found") }, label = { Text("Found") })
                FilterChip(selected = viewModel.filterStatus.value == "All", onClick = { viewModel.setFilterStatus("All") }, label = { Text("All Status") })
                FilterChip(selected = viewModel.filterStatus.value == "Pending", onClick = { viewModel.setFilterStatus("Pending") }, label = { Text("Pending") })
                FilterChip(selected = viewModel.filterStatus.value == "Resolved", onClick = { viewModel.setFilterStatus("Resolved") }, label = { Text("Resolved") })
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = VividAzure)
            } else if (reports!!.isEmpty()) {
                Text(
                    text = if (searchQuery.isEmpty()) "No reports available" else "No items found",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(reports!!.size) { index ->
                        val item = reports!![index]
                        LostFoundUserCard(
                            item = item,
                            onClick = {
                                context.startActivity(
                                    Intent(context, LostandFoundDetailActivity::class.java).apply {
                                        putExtra("lostId", item.lostId)
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LostFoundUserCard(
    item: LostFoundModel,
    onClick: () -> Unit
) {
    val isLost = item.type.lowercase() == "lost"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                if (item.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("No photo", color = Color.Gray, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Badge(
                    text = item.type.uppercase(),
                    background = if (isLost) Color(0xFFfee2e2) else Color(0xFFdcfce7),
                    textColor = if (isLost) Color(0xFFdc2626) else Color(0xFF15803d)
                )
                Badge(
                    text = item.status.uppercase(),
                    background = Color(0xFFfef3c7),
                    textColor = Color(0xFFb45309)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.location,
                fontSize = 13.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Reported by ${item.reportedByName ?: "Anonymous"}",
                fontSize = 12.sp,
                color = Color(0xFF555555),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun LostFoundViewModel.onSearchQueryChanged(it: String) {}