package com.project.petpoint.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.project.petpoint.R
import com.project.petpoint.model.UserModel
import com.project.petpoint.repository.UserRepoImpl
import com.project.petpoint.ui.theme.VividAzure
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.viewmodel.UserViewModel

class EditProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EditProfileBody()
        }
    }
}

@Composable
fun EditProfileBody() {

    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("User", Context.MODE_PRIVATE)
    val userViewModel = remember { UserViewModel(UserRepoImpl()) }

    val userId = sharedPref.getString("userId", null)

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // Image state
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Image picker
    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImageUri = uri
        }

    // Fetch current user data from Firebase (not SharedPreferences!)
    LaunchedEffect(userId) {
        if (userId != null) {
            userViewModel.getUserById(userId)
        }
    }



    // Observe user data and update state
    DisposableEffect(Unit) {
        val observer = androidx.lifecycle.Observer<UserModel?> { user ->
            user?.let {
                name = it.name ?: ""
                email = it.email ?: ""
                phone = it.phonenumber ?: ""
                address = it.address ?: ""
                profileImageUrl = it.profileImage
                isLoading = false
            }
        }
        userViewModel.users.observeForever(observer)
        onDispose {
            userViewModel.users.removeObserver(observer)
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Azure),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .background(
                    VividAzure,
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Edit Profile", color = Color.White, fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Profile Image Box
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.BottomEnd
        ) {

            AsyncImage(
                model = selectedImageUri ?: profileImageUrl ?: R.drawable.userprofile,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .border(3.dp, Color(0xFF9EC760), CircleShape),
                contentScale = ContentScale.Crop
            )

            IconButton(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White, CircleShape)
                    .border(1.dp, Color.Gray, CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_camera_alt_24),
                    contentDescription = "Change Profile Picture",
                    tint = VividAzure
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // User info fields
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(Color(0xFFE7F0F3), shape = RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {

            Text(text = "Name", fontSize = 18.sp)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Email", fontSize = 18.sp)
            OutlinedTextField(
                value = email,
                onValueChange = {},
                readOnly = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Phone", fontSize = 18.sp)
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Address", fontSize = 18.sp)
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    if (userId != null) {
                        val updatedUser = UserModel(
                            userId = userId,
                            name = name,
                            email = email,
                            address = address,
                            phonenumber = phone
                        )

                        if (selectedImageUri != null) {
                            userViewModel.uploadProfileImage(context, selectedImageUri!!) { imageUrl ->
                                if (imageUrl != null) {
                                    userViewModel.updateProfileImage(userId, imageUrl)
                                    profileImageUrl = imageUrl

                                    // Update SharedPreferences
                                    val editor = sharedPref.edit()
                                    editor.putString("profileImage", imageUrl)
                                    editor.apply()
                                }
                            }
                        }

                        userViewModel.updateProfile(userId, updatedUser) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                            if (success) {
                                // Update SharedPreferences with new data
                                val editor = sharedPref.edit()
                                editor.putString("name", name)
                                editor.putString("phone", phone)
                                editor.putString("address", address)
                                editor.apply()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VividAzure,
                    contentColor = Color.White
                )
            ) {
                Text("Save", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    (context as? Activity)?.finish()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VividAzure,
                    contentColor = Color.White
                )
            ) {
                Text("Back", fontSize = 18.sp)
            }
        }
    }
}