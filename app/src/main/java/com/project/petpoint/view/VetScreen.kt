package com.project.petpoint.view

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.petpoint.model.VetModel
import com.project.petpoint.repository.VetRepoImpl
import com.project.petpoint.viewmodel.VetViewModel

@Composable
fun VetScreen() {
    val viewModel = remember { VetViewModel(VetRepoImpl()) }

    // Observing LiveData from your ViewModel
    val vets: List<VetModel> by viewModel.allDoctors.observeAsState(initial = emptyList())
    val isLoading: Boolean by viewModel.loading.observeAsState(initial = false)
    val error: String? by viewModel.errorMessage.observeAsState(initial = null)
    val selectedVet: VetModel? by viewModel.selectedDoctor.observeAsState(initial = null)

    var searchQuery by remember { mutableStateOf("") }
    var selectedVetId by remember { mutableStateOf<String?>(null) }

    // Load initial data
    LaunchedEffect(Unit) {
        viewModel.getAllDoctors()
    }

    // Load selected veterinarian when ID changes
    LaunchedEffect(selectedVetId) {
        selectedVetId?.let { id ->
            viewModel.getDoctorById(id)
        } ?: viewModel.clearSelectedDoctor()
    }

    val filteredVets = vets.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.specialization.contains(searchQuery, ignoreCase = true)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE3F2FD)) // Light blue
                .padding(16.dp)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search veterinarians...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Main content
            AnimatedVisibility(visible = selectedVetId == null) {
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Error: $error",
                                color = Color.Red,
                                fontSize = 16.sp
                            )
                        }
                    }

                    filteredVets.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No veterinarians found",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }

                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredVets) { vet ->
                                VetCard(
                                    vet = vet,
                                    onClick = { selectedVetId = vet.vetId }
                                )
                            }
                        }
                    }
                }
            }

            // Detail view
            AnimatedVisibility(visible = selectedVetId != null) {
                VetDetailContent(
                    vet = selectedVet,
                    isLoading = isLoading,
                    error = error,
                    onBack = { selectedVetId = null }
                )
            }
        }
    }
}

@Composable
private fun VetCard(
    vet: VetModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = vet.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = vet.specialization,
                color = Color(0xFF0288D1),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("📞 ${vet.phonenumber}", fontSize = 13.sp)
            Text("🕒 ${vet.schedule}", fontSize = 12.sp, color = Color.DarkGray)
        }
    }
}

@Composable
private fun VetDetailContent(
    vet: VetModel?,
    isLoading: Boolean,
    error: String?,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3F2FD))
            .padding(20.dp)
    ) {
        // Header with back button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back to list")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Veterinarian Details",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: $error", color = Color.Red)
                }
            }

            vet == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Veterinarian not found", color = Color.Gray)
                }
            }

            else -> {
                Column {
                    Text(vet.name, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text(vet.specialization, color = Color(0xFF0288D1), fontSize = 18.sp)

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("📧 ${vet.email}", fontSize = 16.sp)
                    Text("📞 ${vet.phonenumber}", fontSize = 16.sp)
                    Text("🕒 ${vet.schedule}", fontSize = 16.sp)
                    Text("📍 ${vet.address}", fontSize = 16.sp)

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${vet.phonenumber}")
                            })
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Call Now", color = Color.White, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:${vet.email}")
                            })
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Send Email", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}