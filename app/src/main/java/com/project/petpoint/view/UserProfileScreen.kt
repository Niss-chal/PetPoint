package com.project.petpoint.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.project.petpoint.R
import com.project.petpoint.repository.UserRepoImpl
import com.project.petpoint.view.ui.theme.*
import com.project.petpoint.viewmodel.UserViewModel

@Composable
fun UserProfileScreen() {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("User", Context.MODE_PRIVATE)
    val userId = sharedPref.getString("userId", null)
    val userViewModel = remember { UserViewModel(UserRepoImpl()) }

    var userName by remember { mutableStateOf("Guest") }
    var userEmail by remember { mutableStateOf("") }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Fetch user data
    LaunchedEffect(userId) {
        userId?.let { userViewModel.getUserById(it) }
    }

    val userState by userViewModel.users.observeAsState()
    userState?.let {
        userName = it.name
        userEmail = it.email
        profileImageUrl = it.profileImage
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(VividAzure.copy(0.1f), Azure)
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // HEADER
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(VividAzure, VividAzure.copy(0.85f))
                        ),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(vertical = 24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = profileImageUrl ?: R.drawable.profile,
                        contentDescription = "Profile Picture",
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
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // EDIT PROFILE BUTTON
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
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Profile", color = Color.White)
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // MENU CARD
        item {
            Card(
                modifier = Modifier.padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column {
                    EnhancedProfileMenuItem(
                        icon = Icons.Default.History,
                        text = "Order History",
                        iconTint = VividAzure
                    ) {
                        context.startActivity(Intent(context, OrderHistoryActivity::class.java))
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.Gray.copy(alpha = 0.2f)
                    )

                    EnhancedProfileMenuItem(
                        icon = Icons.Default.Delete,
                        text = "Delete Account",
                        iconTint = crimson
                    ) {
                        showDeleteDialog = true
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.Gray.copy(alpha = 0.2f)
                    )

                    EnhancedProfileMenuItem(
                        icon = R.drawable.baseline_logout_24,
                        text = "Log Out",
                        iconTint = VividOrange
                    ) {
                        showLogoutDialog = true
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }

    // LOGOUT DIALOG
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = { Icon(Icons.Default.Logout, contentDescription = null, tint = VividOrange) },
            title = { Text("Log out?") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                Button(
                    onClick = {
                        userViewModel.logout { success, message ->
                            if (success) {
                                sharedPref.edit().clear().apply()
                                val intent = Intent(context, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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

    // DELETE ACCOUNT DIALOG
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red) },
            title = { Text("Delete Account?") },
            text = { Text("This action cannot be undone. All your data will be permanently deleted.") },
            confirmButton = {
                Button(
                    onClick = {
                        userId?.let {
                            userViewModel.deleteAccount(it) { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    sharedPref.edit().clear().apply()
                                    val intent = Intent(context, LoginActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    context.startActivity(intent)
                                    (context as? Activity)?.finishAffinity()
                                }
                            }
                        }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun EnhancedProfileMenuItem(
    icon: Any,
    text: String,
    iconTint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconTint.copy(0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            when (icon) {
                is ImageVector -> Icon(icon, contentDescription = null, tint = iconTint)
                is Int -> Icon(painterResource(icon), contentDescription = null, tint = iconTint)
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(text, fontSize = 16.sp)

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            painter = painterResource(R.drawable.keyboard_arrow_right_24px),
            contentDescription = "Navigate",
            tint = Color.Gray
        )
    }
}