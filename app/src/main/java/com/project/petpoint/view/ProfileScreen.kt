package com.project.petpoint.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

import com.project.petpoint.R
import com.project.petpoint.repository.UserRepoImpl
import com.project.petpoint.viewmodel.UserViewModel
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.VividAzure

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProfileScreen()
        }
    }
}

@Composable
fun ProfileScreen() {

    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("User", Context.MODE_PRIVATE)
    val userId = sharedPref.getString("userId", null)

    val userViewModel = remember { UserViewModel(UserRepoImpl()) }

    // Observe user LiveData
    var userName by remember { mutableStateOf(sharedPref.getString("name", "Guest")) }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }

    if (userId != null) {
        LaunchedEffect(userId) {
            userViewModel.getUserById(userId)
        }
        val userState by userViewModel.users.observeAsState()
        userState?.let { user ->
            userName = user.name
            profileImageUrl = user.profileImage
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Azure)
                .padding(top = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {

            item { Spacer(modifier = Modifier.height(20.dp)) }

            item {
                if (!profileImageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = profileImageUrl,
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = userName ?: "Guest",
                    fontSize = 22.sp,
                    color = Color.Black
                )
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        context.startActivity(
                            Intent(context, EditProfileActivity::class.java)
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VividAzure),
                    modifier = Modifier.width(160.dp)
                ) {
                    Text("Edit Profile", color = Color.White, fontSize = 16.sp)
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
                Divider(
                    color = Color.Gray.copy(alpha = 0.3f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 40.dp)
                )
                Spacer(modifier = Modifier.height(25.dp))
            }

            item {
                ProfileMenuItem(icon = R.drawable.baseline_history_24, text = "Order History") {
                    // Navigate to OrderHistoryActivity
                    context.startActivity(Intent(context, OrderHistoryActivity::class.java))
                }
            }

            item {
                ProfileMenuItem(icon = R.drawable.baseline_logout_24, text = "Log Out") {
                    // TODO: Log out user
                }
            }
        }
    }
}

// Updated ProfileMenuItem to accept onClick
@Composable
fun ProfileMenuItem(icon: Int, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 18.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = VividAzure,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Text(text = text, fontSize = 18.sp, color = Color.Black)
    }
}
