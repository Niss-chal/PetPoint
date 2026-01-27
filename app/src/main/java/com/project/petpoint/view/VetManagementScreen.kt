package com.project.petpoint.view

import android.app.AlertDialog
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.project.petpoint.model.VetModel
import com.project.petpoint.repository.VetRepoImpl
import com.project.petpoint.view.ui.theme.*
import com.project.petpoint.viewmodel.VetViewModel
import kotlinx.coroutines.delay

@Composable
fun VetManagementScreen() {
    val viewModel = remember { VetViewModel(VetRepoImpl()) }
    val context = LocalContext.current

    val allDoctors by viewModel.allDoctors.observeAsState(initial = emptyList())
    val isLoading by viewModel.loading.observeAsState(initial = false)
    val errorMessage by viewModel.errorMessage.observeAsState(initial = null)

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var specialization by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phonenumber by remember { mutableStateOf("") }
    var schedule by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var editingVetId by remember { mutableStateOf<String?>(null) }

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.getAllDoctors()
    }

    LaunchedEffect(editingVetId) {
        if (editingVetId != null) {
            viewModel.getDoctorById(editingVetId!!)
        }
    }

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Azure,
                        White
                    )
                )
            )
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Veterinarians",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = VividAzure
                        )
                        Text(
                            text = "${allDoctors.size} doctors registered",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    FloatingActionButton(
                        onClick = {
                            name = ""
                            specialization = ""
                            email = ""
                            phonenumber = ""
                            schedule = ""
                            address = ""
                            showAddDialog = true
                        },
                        containerColor = VividAzure,
                        contentColor = White,
                        elevation = FloatingActionButtonDefaults.elevation(6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            }

            when {
                isLoading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = VividAzure,
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Loading veterinarians...", color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                    }
                }

                errorMessage != null -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFfee2e2)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "⚠️ $errorMessage",
                                color = Color(0xFFdc2626),
                                modifier = Modifier.padding(16.dp),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                allDoctors.isEmpty() -> {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            colors = CardDefaults.cardColors(containerColor = White),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(48.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Outlined.MedicalServices,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color.Gray.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "No veterinarians yet",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray
                                )
                                Text(
                                    "Add your first veterinarian to get started",
                                    fontSize = 14.sp,
                                    color = Color.Gray.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                else -> {
                    items(allDoctors.size) { index ->
                        val vet = allDoctors[index]
                        var visible by remember { mutableStateOf(false) }

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


            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

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
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(VividAzure.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.MedicalServices,
                                contentDescription = null,
                                tint = VividAzure,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1a1a1a)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = onNameChange,
                        label = { Text("Name *") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Person, null, tint = VividAzure)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VividAzure,
                            focusedLabelColor = VividAzure
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = specialization,
                        onValueChange = onSpecializationChange,
                        label = { Text("Specialization *") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Vaccines, null, tint = VividAzure)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VividAzure,
                            focusedLabelColor = VividAzure
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        label = { Text("Email *") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Email, null, tint = VividAzure)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VividAzure,
                            focusedLabelColor = VividAzure
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = phonenumber,
                        onValueChange = onPhoneChange,
                        label = { Text("Phone Number *") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Phone, null, tint = VividAzure)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VividAzure,
                            focusedLabelColor = VividAzure
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = schedule,
                        onValueChange = onScheduleChange,
                        label = { Text("Schedule") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Schedule, null, tint = VividAzure)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VividAzure,
                            focusedLabelColor = VividAzure
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = address,
                        onValueChange = onAddressChange,
                        label = { Text("Address") },
                        leadingIcon = {
                            Icon(Icons.Outlined.LocationOn, null, tint = VividAzure)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VividAzure,
                            focusedLabelColor = VividAzure
                        )
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = onSave,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = VividAzure),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VetAdminCard(
    vet: VetModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Green.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.MedicalServices,
                        contentDescription = null,
                        tint = Green,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        vet.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF1a1a1a)
                    )
                    Text(
                        vet.specialization,
                        color = Green,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            VetDetailRow(Icons.Outlined.Email, "Email", vet.email)
            Spacer(modifier = Modifier.height(8.dp))
            VetDetailRow(Icons.Outlined.Phone, "Phone", vet.phonenumber)
            Spacer(modifier = Modifier.height(8.dp))
            VetDetailRow(Icons.Outlined.Schedule, "Schedule", vet.schedule)
            Spacer(modifier = Modifier.height(8.dp))
            VetDetailRow(Icons.Outlined.LocationOn, "Address", vet.address)

            Spacer(modifier = Modifier.height(20.dp))

            Divider(color = Color.Gray.copy(alpha = 0.2f))

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = VividAzure
                    )
                ) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit", fontWeight = FontWeight.Medium)
                }

                Button(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFdc2626)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Delete", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun VetDetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                label,
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Text(
                value,
                fontSize = 14.sp,
                color = Color(0xFF1a1a1a)
            )
        }
    }
}