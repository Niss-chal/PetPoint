package com.project.petpoint.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.petpoint.model.VetModel
import com.project.petpoint.repository.VetRepoImpl
import com.project.petpoint.view.ui.theme.VividAzure
import com.project.petpoint.view.ui.theme.White
import com.project.petpoint.viewmodel.VetViewModel

class VetDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val vetId = intent.getStringExtra("vetId") ?: ""

        setContent {
            VetDetailScreen(vetId = vetId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetDetailScreen(vetId: String) {
    val viewModel = remember { VetViewModel(VetRepoImpl()) }

    val vet: VetModel? by viewModel.selectedDoctor.observeAsState(null)
    val isLoading: Boolean by viewModel.loading.observeAsState(initial = false)
    val error: String? by viewModel.errorMessage.observeAsState(initial = null)

    val context = LocalContext.current

    LaunchedEffect(vetId) {
        if (vetId.isNotBlank()) {
            viewModel.getDoctorById(vetId)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Veterinarian Details") },
                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFE3F2FD))
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null -> {
                    Text(
                        text = "Error: $error",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                vet == null -> {
                    Text(
                        text = "Veterinarian not found",
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = vet!!.name,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = vet!!.specialization,
                            color = Color(0xFF0288D1),
                            fontSize = 20.sp
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        DetailItem("Email", vet!!.email)
                        DetailItem("Phone", vet!!.phonenumber)
                        DetailItem("Schedule", vet!!.schedule)
                        DetailItem("Address", vet!!.address)

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = {
                                context.startActivity(Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${vet!!.phonenumber}")
                                })
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(Color(0xFF0288D1)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Call Now", color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                context.startActivity(Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:${vet!!.email}")
                                })
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(Color(0xFF4CAF50)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Send Email", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray
        )
        Text(
            text = value,
            fontSize = 16.sp
        )
    }
}