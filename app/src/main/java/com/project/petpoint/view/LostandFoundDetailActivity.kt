package com.project.petpoint.view

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostAndFoundDetailScreen(lostId: String) {
    val viewModel = remember { LostFoundViewModel(LostFoundRepoImpl()) }
    val context = LocalContext.current as? ComponentActivity

    val item by viewModel.selectedReport.observeAsState()
    val loading by viewModel.loading.observeAsState(initial = false)
    val message by viewModel.message.observeAsState()

    LaunchedEffect(lostId) {
        if (lostId.isNotBlank()) viewModel.getReportById(lostId)
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
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = VividAzure)
            } else if (item == null) {
                Text("Item not found", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
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
                            Text("No photo", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(item!!.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Badge(
                                text = item!!.type.uppercase(),
                                background = if (item!!.type == "Lost") Color(0xFFfee2e2) else Color(0xFFdcfce7),
                                textColor = if (item!!.type == "Lost") Color(0xFFdc2626) else Color(0xFF15803d)
                            )
                            Badge(
                                text = item!!.status.uppercase(),
                                background = Color(0xFFfef3c7),
                                textColor = Color(0xFFb45309)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        DetailRow("Category", item!!.category)
                        DetailRow("Location", item!!.location)
                        DetailRow("Date", item!!.date)
                        DetailRow("Reported by", item!!.reportedBy)
                        if (item!!.contactInfo.isNotBlank()) {
                            DetailRow("Contact", item!!.contactInfo)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

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
        }
    }
}

@Composable
fun Badge(text: String, background: Color, textColor: Color) {
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
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text("$label: ", fontWeight = FontWeight.Medium, color = Color.Gray)
        Text(value, modifier = Modifier.weight(1f))
    }
}