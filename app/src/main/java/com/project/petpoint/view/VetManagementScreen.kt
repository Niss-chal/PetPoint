package com.project.petpoint.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.VividAzure
import com.project.petpoint.view.ui.theme.White

@Composable
fun VetManagementScreen() {
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Azure)
            .padding(16.dp)
    ) {

        // Title
        Text(
            text = "Veterinarian Management",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,

        )

        Spacer(modifier = Modifier.height(30.dp))

        HorizontalDivider(
            color = Color.Gray.copy(alpha = 0.7f),
            thickness = 1.dp
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Add Veterinarian Button
        Button(
            onClick = {
                showDialog = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = VividAzure),
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
        ) {
            Text(" Add Veterinarian", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Placeholder for future list
//        LazyColumn {
//            items(5) {
//                VetCard(
//                    name = "Dr. Sarah Johnson",
//                    specialization = "Small Animal Surgery",
//                    email = "sarah@clinic.com",
//                    phone = "+1 234 567 8900",
//                    schedule = "Mon-Fri 9AM-5PM",
//                    address = "123 Pet Street, City"
//                )
//            }
//        }

        Spacer(modifier = Modifier.weight(1f))
    }

    // Simple placeholder dialog (optional - you can expand later)
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Veterinarian") },
            text = { Text("Form fields will go here...") },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun VetCard(
    name: String,
    specialization: String,
    email: String,
    phone: String,
    schedule: String,
    address: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(name, fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Text(specialization, color = VividAzure, fontSize = 15.sp)

            Spacer(modifier = Modifier.height(16.dp))

            DetailRow("Email", email)
            DetailRow("Phone", phone)
            DetailRow("Schedule", schedule)
            DetailRow("Address", address)

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = Color.Gray.copy(alpha = 0.7f))

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Edit
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { }
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit", fontSize = 14.sp, color = Color.Black)
                }

                // Delete
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { }
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Delete", fontSize = 14.sp, color = Color.Red)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row {
        Text("$label: ", fontWeight = FontWeight.Medium, color = Color.DarkGray)
        Text(value, color = Color.Black)
    }
}

@Composable
@Preview
fun VetManagementPreview() {
    VetManagementScreen()
}