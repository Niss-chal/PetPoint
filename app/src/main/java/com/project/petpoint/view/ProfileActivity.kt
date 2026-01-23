package com.project.petpoint.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.project.petpoint.R
import com.project.petpoint.repository.UserRepoImpl
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.VividAzure
import com.project.petpoint.view.ui.theme.VividOrange
import com.project.petpoint.viewmodel.UserViewModel

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdminProfileScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProfileScreen() {

    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("User", Context.MODE_PRIVATE)
    val userId = sharedPref.getString("userId", null)

    val userViewModel = remember { UserViewModel(UserRepoImpl()) }

    var userName by remember { mutableStateOf("Admin") }
    var userEmail by remember { mutableStateOf("") }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }

    var showLogoutDialog by remember { mutableStateOf(false) }

    // Fetch fresh admin data
    LaunchedEffect(userId) {
        if (userId != null) {
            userViewModel.getUserById(userId)
        }
    }

    val userState by userViewModel.users.observeAsState()
    userState?.let {
        userName = it.name.ifBlank { "Admin" }
        userEmail = it.email
        profileImageUrl = it.profileImage
    }

    Scaffold { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        listOf(VividAzure.copy(0.1f), Azure)
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // HEADER - same style as user profile
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(VividAzure, VividAzure.copy(0.85f))
                            ),
                            shape = RoundedCornerShape(
                                bottomStart = 32.dp,
                                bottomEnd = 32.dp
                            )
                        )
                        .padding(vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = profileImageUrl ?: R.drawable.profile,
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .border(4.dp, Color.White, CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = userName,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = userEmail,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Small visual hint that this is admin
                        Box(
                            modifier = Modifier
                                .background(VividOrange.copy(alpha = 0.9f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "ADMIN",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }

            // EDIT PROFILE - same button style
            item {
                Button(
                    onClick = {
                        context.startActivity(Intent(context, EditProfileActivity::class.java))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VividAzure)
                ) {
                    Icon(Icons.Default.Edit, null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Profile", color = Color.White)
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // MENU - same card + EnhancedProfileMenuItem style
            item {
                Card(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column {
                        // You can add admin-specific menu items here later
                        // For now we keep only logout (like minimal version)

                        EnhancedProfileMenuItem(
                            icon = Icons.Default.Logout,
                            text = "Log Out",
                            iconTint = VividOrange
                        ) {
                            showLogoutDialog = true
                        }
                    }
                }
            }

            // Optional: extra space at bottom
            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }

    // LOGOUT DIALOG - same style as user version
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = { Icon(Icons.Default.Logout, null, tint = VividOrange) },
            title = { Text("Log out?") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                Button(
                    onClick = {
                        userViewModel.logout { success, message ->
                            if (success) {
                                sharedPref.edit().clear().commit()
                                val intent = Intent(context, LoginActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                context.startActivity(intent)
                                (context as? Activity)?.finishAffinity()
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                        showLogoutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VividOrange)
                ) {
                    Text("Log Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Reuse the same EnhancedProfileMenuItem composable (copy from user profile or move to common file)
