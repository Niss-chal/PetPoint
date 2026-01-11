package com.project.petpoint.view

import android.app.AlertDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.petpoint.model.VetModel
import com.project.petpoint.repository.VetRepoImpl
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.Black
import com.project.petpoint.view.ui.theme.GreyOrange
import com.project.petpoint.view.ui.theme.VividAzure
import com.project.petpoint.view.ui.theme.White
import com.project.petpoint.viewmodel.VetViewModel

@Composable
fun VetManagementScreen() {
    val vetViewModel = remember { VetViewModel(VetRepoImpl()) }

    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var specialization by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phonenumber by remember { mutableStateOf("") }
    var schedule by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedVetId by remember { mutableStateOf("") }

    val allDoctors = vetViewModel.allDoctors.observeAsState(initial = emptyList())
    val doctor = vetViewModel.doctor.observeAsState(initial = null)

    LaunchedEffect(Unit) {
        vetViewModel.getAllDoctors()
    }

    LaunchedEffect(doctor.value) {
        doctor.value?.let {
            name = it.name
            specialization = it.specialization
            email = it.email
            phonenumber = it.phonenumber
            schedule = it.schedule
            address = it.address
        }
    }

    // Add Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
            },
            confirmButton = {
                TextButton(onClick = {
                    val model = VetModel(
                        name = name,
                        specialization = specialization,
                        email = email,
                        phonenumber = phonenumber,
                        schedule = schedule,
                        address = address
                    )
                    vetViewModel.addDoctor(model) { success, message ->
                        if (success) {
                            showAddDialog = false
                            Toast.makeText(context, "Doctor added successfully", Toast.LENGTH_SHORT).show()
                            vetViewModel.getAllDoctors()
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                }) { Text("Cancel") }
            },
            title = { Text("Add Veterinarian") },
            text = {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { data ->
                            name = data
                        },
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Enter the name")
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = GreyOrange,
                            unfocusedContainerColor = GreyOrange,
                            focusedIndicatorColor = Blue,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = specialization,
                        onValueChange = { data ->
                            specialization = data
                        },
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Enter the specialization")
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = GreyOrange,
                            unfocusedContainerColor = GreyOrange,
                            focusedIndicatorColor = Blue,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { data ->
                            email = data
                        },
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Enter the email")
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = GreyOrange,
                            unfocusedContainerColor = GreyOrange,
                            focusedIndicatorColor = Blue,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phonenumber,
                        onValueChange = { data ->
                            phonenumber = data
                        },
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Enter the phone number")
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = GreyOrange,
                            unfocusedContainerColor = GreyOrange,
                            focusedIndicatorColor = Blue,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = schedule,
                        onValueChange = { data ->
                            schedule = data
                        },
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Enter the schedule")
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = GreyOrange,
                            unfocusedContainerColor = GreyOrange,
                            focusedIndicatorColor = Blue,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { data ->
                            address = data
                        },
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Enter the address")
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = GreyOrange,
                            unfocusedContainerColor = GreyOrange,
                            focusedIndicatorColor = Blue,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }
            }
        )
    }

    // Edit Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = {
                showEditDialog = false
            },
            confirmButton = {
                TextButton(onClick = {
                    val model = VetModel(
                        vetId = selectedVetId,
                        name = name,
                        specialization = specialization,
                        email = email,
                        phonenumber = phonenumber,
                        schedule = schedule,
                        address = address
                    )
                    vetViewModel.updateDoctor(model) { success, message ->
                        if (success) {
                            showEditDialog = false
                            Toast.makeText(context, "Doctor updated successfully", Toast.LENGTH_SHORT).show()
                            vetViewModel.getAllDoctors()
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }) { Text("Update") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showEditDialog = false
                }) { Text("Cancel") }
            },
            title = { Text("Update Veterinarian") },
            text = {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { data ->
                            name = data
                        },
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Enter the name")
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = GreyOrange,
                            unfocusedContainerColor = GreyOrange,
                            focusedIndicatorColor = Blue,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = specialization,
                        onValueChange = { data ->
                            specialization = data
                        },
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Enter the specialization")
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = GreyOrange,
                            unfocusedContainerColor = GreyOrange,
                            focusedIndicatorColor = Blue,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { data ->
                            email = data
                        },
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Enter the email")
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = GreyOrange,
                            unfocusedContainerColor = GreyOrange,
                            focusedIndicatorColor = Blue,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phonenumber,
                        onValueChange = { data ->
                            phonenumber = data
                        },
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Enter the phone number")
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = GreyOrange,
                            unfocusedContainerColor = GreyOrange,
                            focusedIndicatorColor = Blue,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = schedule,
                        onValueChange = { data ->
                            schedule = data
                        },
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Enter the schedule")
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = GreyOrange,
                            unfocusedContainerColor = GreyOrange,
                            focusedIndicatorColor = Blue,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { data ->
                            address = data
                        },
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Enter the address")
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = GreyOrange,
                            unfocusedContainerColor = GreyOrange,
                            focusedIndicatorColor = Blue,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Azure)
            .padding(16.dp)
    ) {
        item {
            // Title
            Text(
                text = "Veterinarian Management",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))
        }

        item {
            HorizontalDivider(
                color = Color.Gray.copy(alpha = 0.7f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(30.dp))
        }

        item {
            // Add Veterinarian Button
            Button(
                onClick = {
                    // Reset fields for add
                    name = ""
                    specialization = ""
                    email = ""
                    phonenumber = ""
                    schedule = ""
                    address = ""
                    showAddDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = VividAzure),
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
            ) {
                Text("+ Add Veterinarian", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // Doctor list
        items(allDoctors.value?.size ?: 0) { index ->
            val data = allDoctors.value!![index]
            VetCard(
                name = data.name,
                specialization = data.specialization,
                email = data.email,
                phone = data.phonenumber,
                schedule = data.schedule,
                address = data.address,
                onEdit = {
                    selectedVetId = data.vetId
                    vetViewModel.getDoctorById(data.vetId)
                    showEditDialog = true
                },
                onDelete = {
                    AlertDialog.Builder(context)
                        .setTitle("Delete Veterinarian")
                        .setMessage("Are you sure you want to delete ${data.name}?")
                        .setPositiveButton("Delete") { _, _ ->
                            vetViewModel.deleteDoctor(data.vetId) { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    vetViewModel.getAllDoctors()
                                }
                            }
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            )
        }
    }
}

@Composable
fun VetCard(
    name: String,
    specialization: String,
    email: String,
    phone: String,
    schedule: String,
    address: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
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
                    modifier = Modifier.clickable { onEdit() }
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit", fontSize = 14.sp, color = Color.Black)
                }

                // Delete
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onDelete() }
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