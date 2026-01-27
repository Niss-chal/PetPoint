package com.project.petpoint.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.petpoint.model.VetModel
import com.project.petpoint.repository.VetRepoImpl
import com.project.petpoint.view.ui.theme.*
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
                title = {
                    Text(
                        "Veterinarian Details",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { (context as? ComponentActivity)?.finish() },
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Azure,
                            White
                        )
                    )
                )
        ) {
            when {
                isLoading -> {
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
                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                Icons.Outlined.ErrorOutline,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = Color.Red.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Error loading details",
                                color = Color.Gray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = error ?: "Unknown error",
                                color = Color.Gray.copy(alpha = 0.7f),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
                vet == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Outlined.PersonSearch,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = Color.Gray.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Veterinarian not found",
                                color = Color.Gray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Profile Header Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = White),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Avatar
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    VividAzure.copy(alpha = 0.3f),
                                                    VividAzure.copy(alpha = 0.6f)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Outlined.LocalHospital,
                                        contentDescription = null,
                                        tint = VividAzure,
                                        modifier = Modifier.size(50.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Name
                                Text(
                                    text = vet!!.name,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Black,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Specialization Badge
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = VividAzure.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = vet!!.specialization,
                                        color = VividAzure,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        // Contact Information Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = White),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Text(
                                    text = "Contact Information",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Black
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                DetailInfoCard(
                                    icon = Icons.Outlined.Email,
                                    label = "Email",
                                    value = vet!!.email,
                                    color = lightgreen
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                DetailInfoCard(
                                    icon = Icons.Outlined.Phone,
                                    label = "Phone",
                                    value = vet!!.phonenumber,
                                    color = Green
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                DetailInfoCard(
                                    icon = Icons.Outlined.Schedule,
                                    label = "Schedule",
                                    value = vet!!.schedule,
                                    color = Orange
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                DetailInfoCard(
                                    icon = Icons.Outlined.LocationOn,
                                    label = "Address",
                                    value = vet!!.address,
                                    color = Vividpink
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Action Buttons
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
                            // Call Button
                            Button(
                                onClick = {
                                    context.startActivity(Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:${vet!!.phonenumber}")
                                    })
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = VividAzure
                                )
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Outlined.Phone,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = White
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        "Call Now",
                                        color = White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Email Button
                            OutlinedButton(
                                onClick = {
                                    context.startActivity(Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:${vet!!.email}")
                                    })
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    width = 2.dp
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = VividAzure
                                )
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Outlined.Email,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = VividAzure
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        "Send Email",
                                        color = VividAzure,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
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