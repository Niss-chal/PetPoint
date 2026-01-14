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
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.petpoint.model.VetModel
import com.project.petpoint.repository.VetRepoImpl
import com.project.petpoint.view.ui.theme.*
import com.project.petpoint.viewmodel.VetViewModel

@Composable
fun VetManagementScreen() {
    val viewModel = remember { VetViewModel(VetRepoImpl()) }
    val context = LocalContext.current

    val allDoctors by viewModel.allDoctors.observeAsState(initial = emptyList())
    val isLoading by viewModel.loading.observeAsState(initial = false)
    val errorMessage by viewModel.errorMessage.observeAsState(initial = null)

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    // Fields for add/edit
    var name by remember { mutableStateOf("") }
    var specialization by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phonenumber by remember { mutableStateOf("") }
    var schedule by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // For editing - keep track of current vet being edited
    var editingVetId by remember { mutableStateOf<String?>(null) }

    // Load doctors when screen appears
    LaunchedEffect(Unit) {
        viewModel.getAllDoctors()
    }

    // When editing starts, fill the fields
    LaunchedEffect(editingVetId) {
        if (editingVetId != null) {
            viewModel.getDoctorById(editingVetId!!)
        }
    }

    // Observe selected doctor to fill edit fields
    val selectedDoctor by viewModel.selectedDoctor.observeAsState(null)
    LaunchedEffect(selectedDoctor) {
        selectedDoctor?.let { vet ->
            name = vet.name
            specialization = vet.specialization
            email = vet.email
            phonenumber = vet.phonenumber
            schedule = vet.schedule
            address = vet.address
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Azure)
            .padding(16.dp)
    ) {
        Text(
            text = "Veterinarian Management",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Loading / Error state
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Text(
                    text = "Error: $errorMessage",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            else -> {
                Button(
                    onClick = {
                        // Reset fields for new doctor
                        name = ""
                        specialization = ""
                        email = ""
                        phonenumber = ""
                        schedule = ""
                        address = ""
                        showAddDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VividAzure),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("+ Add New Veterinarian", color = White)
                }

                Spacer(modifier = Modifier.height(24.dp))

                LazyColumn {
                    items(allDoctors) { vet ->
                        VetAdminCard(
                            vet = vet,
                            onEdit = {
                                editingVetId = vet.vetId
                                showEditDialog = true
                            },
                            onDelete = {
                                AlertDialog.Builder(context)
                                    .setTitle("Delete Veterinarian")
                                    .setMessage("Are you sure you want to delete ${vet.name}?")
                                    .setPositiveButton("Delete") { _, _ ->
                                        viewModel.deleteDoctor(vet.vetId) { success, msg ->
                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                            if (success) {
                                                viewModel.getAllDoctors()
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
        }
    }

    // ── Add Doctor Dialog ───────────────────────────────────────
    if (showAddDialog) {
        VetFormDialog(
            title = "Add Veterinarian",
            name = name,
            specialization = specialization,
            email = email,
            phonenumber = phonenumber,
            schedule = schedule,
            address = address,
            onNameChange = { name = it },
            onSpecializationChange = { specialization = it },
            onEmailChange = { email = it },
            onPhoneChange = { phonenumber = it },
            onScheduleChange = { schedule = it },
            onAddressChange = { address = it },
            onSave = {
                val model = VetModel(
                    name = name.trim(),
                    specialization = specialization.trim(),
                    email = email.trim(),
                    phonenumber = phonenumber.trim(),
                    schedule = schedule.trim(),
                    address = address.trim()
                )
                viewModel.addDoctor(model) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        showAddDialog = false
                        viewModel.getAllDoctors()
                    }
                }
            },
            onDismiss = { showAddDialog = false }
        )
    }

    // ── Edit Doctor Dialog ───────────────────────────────────────
    if (showEditDialog && selectedDoctor != null) {
        VetFormDialog(
            title = "Edit Veterinarian",
            name = name,
            specialization = specialization,
            email = email,
            phonenumber = phonenumber,
            schedule = schedule,
            address = address,
            onNameChange = { name = it },
            onSpecializationChange = { specialization = it },
            onEmailChange = { email = it },
            onPhoneChange = { phonenumber = it },
            onScheduleChange = { schedule = it },
            onAddressChange = { address = it },
            onSave = {
                val model = VetModel(
                    vetId = selectedDoctor!!.vetId,
                    name = name.trim(),
                    specialization = specialization.trim(),
                    email = email.trim(),
                    phonenumber = phonenumber.trim(),
                    schedule = schedule.trim(),
                    address = address.trim()
                )
                viewModel.updateDoctor(model) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        showEditDialog = false
                        editingVetId = null
                        viewModel.clearSelectedDoctor()
                        viewModel.getAllDoctors()
                    }
                }
            },
            onDismiss = {
                showEditDialog = false
                editingVetId = null
                viewModel.clearSelectedDoctor()
            }
        )
    }
}

@Composable
private fun VetFormDialog(
    title: String,
    name: String,
    specialization: String,
    email: String,
    phonenumber: String,
    schedule: String,
    address: String,
    onNameChange: (String) -> Unit,
    onSpecializationChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onScheduleChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = specialization,
                    onValueChange = onSpecializationChange,
                    label = { Text("Specialization") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phonenumber,
                    onValueChange = onPhoneChange,
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = schedule,
                    onValueChange = onScheduleChange,
                    label = { Text("Schedule") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = onAddressChange,
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun VetAdminCard(
    vet: VetModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(vet.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(vet.specialization, color = VividAzure, fontSize = 15.sp)

            Spacer(modifier = Modifier.height(12.dp))

            DetailRow("Email", vet.email)
            DetailRow("Phone", vet.phonenumber)
            DetailRow("Schedule", vet.schedule)
            DetailRow("Address", vet.address)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onEdit() }
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit", color = Color.Black)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onDelete() }
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Delete", color = Color.Red)
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