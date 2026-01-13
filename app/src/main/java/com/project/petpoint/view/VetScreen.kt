package com.project.petpoint.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.project.petpoint.view.ui.theme.*
import com.project.petpoint.viewmodel.VetViewModel

/* ---------------------------------------------------
   VET SCREEN (Grid + Search)
--------------------------------------------------- */

@Composable
fun VetScreen() {

    val viewModel = remember { VetViewModel(VetRepoImpl()) }
    val vets by viewModel.allDoctors.observeAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.getAllDoctors()
    }


    val filteredVets = vets.filter { vet ->
        vet.name.contains(searchQuery, ignoreCase = true) ||
                vet.specialization.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Azure)
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search veterinarians...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredVets.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No veterinarians found", color = Color.Gray)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredVets) { vet ->
                    VetUserCard(vet)
                }
            }
        }
    }
}

/* ---------------------------------------------------
   VET CARD
--------------------------------------------------- */

@Composable
fun VetUserCard(vet: VetModel) {

    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(context, VetDetailActivity::class.java)
                intent.putExtra("vetId", vet.vetId)
                context.startActivity(intent)
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Text(
                vet.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                vet.specialization,
                color = VividAzure,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text("📞 ${vet.phonenumber}", fontSize = 13.sp, color = Black)
            Text("🕒 ${vet.schedule}", fontSize = 12.sp, color = Color.DarkGray)
        }
    }
}

/* ---------------------------------------------------
   VET DETAIL ACTIVITY
--------------------------------------------------- */

class VetDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val vetId = intent.getStringExtra("vetId") ?: ""

        setContent {
            VetDetailScreen(vetId)
        }
    }
}

/* ---------------------------------------------------
   VET DETAIL SCREEN
--------------------------------------------------- */

@Composable
fun VetDetailScreen(vetId: String) {

    val viewModel = remember { VetViewModel(VetRepoImpl()) }
    val vet by viewModel.doctor.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.getDoctorById(vetId)
    }

    vet?.let {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Azure)
                .padding(20.dp)
        ) {

            Text(it.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(it.specialization, color = VividAzure, fontSize = 18.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Text("📧 ${it.email}")
            Text("📞 ${it.phonenumber}")
            Text("🕒 ${it.schedule}")
            Text("📍 ${it.address}")

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:${it.phonenumber}")
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = VividAzure),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Call Vet", color = White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data = Uri.parse("mailto:${it.email}")
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Green),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Email Vet", color = White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    // Appointment booking later
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Book Appointment", color = White)
            }
        }
    }
}