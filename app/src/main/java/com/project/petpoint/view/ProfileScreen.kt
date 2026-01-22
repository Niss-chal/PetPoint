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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.project.petpoint.R
import com.project.petpoint.repository.UserRepoImpl
import com.project.petpoint.viewmodel.UserViewModel
import com.project.petpoint.view.ui.theme.*

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProfileScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {

    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("User", Context.MODE_PRIVATE)

    val userId = sharedPref.getString("userId", null)
    val userRole = sharedPref.getString("role", "buyer")

    val userViewModel = remember { UserViewModel(UserRepoImpl()) }

    var userName by remember { mutableStateOf("Guest") }
    var userEmail by remember { mutableStateOf("") }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Load user data
    if (userId != null) {
        LaunchedEffect(userId) {
            userViewModel.getUserById(userId)
        }
        val userState by userViewModel.users.observeAsState()
        userState?.let {
            userName = it.name
            userEmail = it.email
            profileImageUrl = it.profileImage
        }
    }

    Scaffold(
        topBar = {
            if (userRole == "admin") {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Profile",
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { (context as? Activity)?.finish() }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowLeft,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = VividAzure
                    )
                )
            }
        }
    ) { padding ->

        val topPadding =
            if (userRole == "admin") padding.calculateTopPadding() else 0.dp

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
                            contentDescription = null,
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
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = userEmail,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }

            // EDIT PROFILE
            item {
                Button(
                    onClick = {
                        context.startActivity(
                            Intent(context, EditProfileActivity::class.java)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VividAzure)
                ) {
                    Icon(Icons.Default.Edit, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Profile")
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // MENU
            item {
                Card(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column {

                        if (userRole == "buyer") {
                            EnhancedProfileMenuItem(
                                icon = Icons.Default.History,
                                text = "Order History",
                                iconTint = VividAzure
                            ) {
                                context.startActivity(
                                    Intent(context, OrderHistoryActivity::class.java)
                                )
                            }
                        }

                        EnhancedProfileMenuItem(
                            icon = Icons.Default.Delete,
                            text = "Delete Account",
                            iconTint = crimson
                        ) {
                            showDeleteDialog = true
                        }

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
        }
    }

    // LOGOUT CONFIRMATION
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(Icons.Default.Logout, null, tint = VividOrange)
            },
            title = { Text("Log out?") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                Button(
                    onClick = {
                        userViewModel.logout { success, message ->
                            if (success) {
                                sharedPref.edit().clear().apply()
                                context.startActivity(
                                    Intent(context, LoginActivity::class.java)
                                )
                                (context as? Activity)?.finish()
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

    // DELETE CONFIRMATION
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(Icons.Default.Warning, null, tint = Color.Red)
            },
            title = { Text("Delete Account?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        userId?.let {
                            userViewModel.deleteAccount(it) { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    sharedPref.edit().clear().apply()
                                    context.startActivity(
                                        Intent(context, LoginActivity::class.java)
                                    )
                                    (context as? Activity)?.finish()
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
                is ImageVector -> Icon(icon, null, tint = iconTint)
                is Int -> Icon(painterResource(icon), null, tint = iconTint)
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(text, fontSize = 16.sp)

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            painter = painterResource(R.drawable.keyboard_arrow_right_24px),
            contentDescription = null,
            tint = Color.Gray
        )
    }
}