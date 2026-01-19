package com.project.petpoint.view

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
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
import com.google.firebase.auth.FirebaseAuth
import com.project.petpoint.model.LostFoundModel
import com.project.petpoint.repository.LostFoundRepoImpl
import com.project.petpoint.utils.ImageUtils
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.Orange
import com.project.petpoint.view.ui.theme.VividAzure
import com.project.petpoint.view.ui.theme.White
import com.project.petpoint.viewmodel.LostFoundViewModel
import java.text.SimpleDateFormat
import java.util.*

class AddLostFoundReportActivity : ComponentActivity() {

    private lateinit var imageUtils: ImageUtils
    private var selectedImageUri by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        imageUtils = ImageUtils(this, this)
        imageUtils.registerLaunchers { uri ->
            selectedImageUri = uri
        }

        val lostId = intent.getStringExtra("lostId")

        setContent {
            AddLostFoundReportScreen(
                selectedImageUri = selectedImageUri,
                onPickImage = { imageUtils.launchImagePicker() },
                editLostId = lostId
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLostFoundReportScreen(
    selectedImageUri: Uri?,
    onPickImage: () -> Unit,
    editLostId: String? = null
) {
    val viewModel = remember { LostFoundViewModel(LostFoundRepoImpl()) }

    val context = LocalContext.current
    val activity = context as? Activity

    val currentUser = FirebaseAuth.getInstance().currentUser

    if (currentUser == null) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Please sign in to create or edit a report", Toast.LENGTH_LONG).show()
            activity?.finish()
        }
        return
    }

    val reporterName = currentUser.displayName?.takeIf { it.isNotBlank() }
        ?: currentUser.email?.split("@")?.firstOrNull()
        ?: "User"

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }
    var isLost by remember { mutableStateOf(true) }

    var isEditMode by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var existingImageUrl by remember { mutableStateOf("") }

    // Store original reporter info when editing
    var originalReportedBy by remember { mutableStateOf("") }
    var originalReportedByName by remember { mutableStateOf("") }

    val selectedItem by viewModel.selectedReport.observeAsState()
    val errorMessage by viewModel.message.observeAsState()

    LaunchedEffect(editLostId) {
        if (!editLostId.isNullOrBlank()) {
            isEditMode = true
            isLoading = true
            viewModel.getReportById(editLostId)
        }
    }

    LaunchedEffect(selectedItem) {
        selectedItem?.let { item ->
            isLoading = false
            title = item.title
            description = item.description
            location = item.location
            category = item.category
            contactInfo = item.contactInfo
            isLost = item.type.lowercase() == "lost"
            existingImageUrl = item.imageUrl

            // CRITICAL: Store original reporter info
            originalReportedBy = item.reportedBy
            originalReportedByName = item.reportedByName ?: ""

            viewModel.clearMessage()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { msg ->
            if (!msg.contains("success", ignoreCase = true)) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
            viewModel.clearMessage()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Azure)
    ) {
        item {
            Spacer(modifier = Modifier.height(40.dp))

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                if (isLoading && isEditMode) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = VividAzure)
                    }
                } else {
                    Text("Type", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        FilterChip(
                            selected = isLost,
                            onClick = { isLost = true },
                            label = { Text("Lost") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFfee2e2),
                                selectedLabelColor = Color(0xFFdc2626)
                            )
                        )
                        FilterChip(
                            selected = !isLost,
                            onClick = { isLost = false },
                            label = { Text("Rescued") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFdcfce7),
                                selectedLabelColor = Color(0xFF15803d)
                            )
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Text("Title", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("e.g. Lost Golden Retriever") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(20.dp))

                    Text("Category", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        placeholder = { Text("e.g. Dog, Cat, Bird") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(20.dp))

                    Text("Location", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        placeholder = { Text("e.g. Thamel, Kathmandu") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(20.dp))

                    Text("Description", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Details about the pet/item...") },
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 6
                    )

                    Spacer(Modifier.height(20.dp))

                    Text("Contact Info", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = contactInfo,
                        onValueChange = { contactInfo = it },
                        placeholder = { Text("Phone / WhatsApp / Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(28.dp))
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clickable(onClick = onPickImage)
                        .background(Color.LightGray, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else if (existingImageUrl.isNotBlank()) {
                        AsyncImage(
                            model = existingImageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Image, null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("Upload Image", color = Color.DarkGray)
                        }
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    if (title.trim().isEmpty()) {
                        Toast.makeText(context, "Please enter title", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (category.trim().isEmpty()) {
                        Toast.makeText(context, "Please enter category", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (location.trim().isEmpty()) {
                        Toast.makeText(context, "Please enter location", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true

                    val saveAction: (String) -> Unit = { imageUrl ->
                        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())


                        val finalReportedBy = if (isEditMode && originalReportedBy.isNotBlank()) {
                            originalReportedBy
                        } else {
                            currentUser.uid
                        }

                        val finalReportedByName = if (isEditMode && originalReportedByName.isNotBlank()) {
                            originalReportedByName
                        } else {
                            reporterName
                        }

                        val model = LostFoundModel(
                            lostId = editLostId ?: "",
                            type = if (isLost) "Lost" else "Rescued",
                            title = title.trim(),
                            category = category.trim(),
                            description = description.trim(),
                            location = location.trim(),
                            date = date,
                            reportedBy = finalReportedBy,
                            reportedByName = finalReportedByName,
                            imageUrl = imageUrl,
                            contactInfo = contactInfo.trim(),
                            isVisible = true
                        )

                        if (isEditMode) {
                            viewModel.updateReport(model) { success, msg ->
                                isLoading = false
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    activity?.setResult(Activity.RESULT_OK)
                                    activity?.finish()
                                }
                            }
                        } else {
                            viewModel.addReport(model) { success, msg ->
                                isLoading = false
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    activity?.setResult(Activity.RESULT_OK)
                                    activity?.finish()
                                }
                            }
                        }
                    }

                    when {
                        selectedImageUri != null -> {
                            viewModel.uploadImage(context, selectedImageUri!!) { url ->
                                if (url != null) {
                                    saveAction(url)
                                } else {
                                    isLoading = false
                                    Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        existingImageUrl.isNotBlank() -> {
                            saveAction(existingImageUrl)
                        }
                        else -> {
                            isLoading = false
                            Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = White)
                    Spacer(Modifier.width(12.dp))
                }
                Text(
                    text = if (isEditMode) "Update Report" else "Submit Report",
                    color = White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(60.dp))
        }
    }
}