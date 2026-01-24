package com.project.petpoint.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.project.petpoint.R
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.VividAzure
import kotlinx.coroutines.delay

class FrontpageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SplashScreen {
                checkLoginAndNavigate()
            }
        }
    }

    private fun checkLoginAndNavigate() {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val sharedPref = getSharedPreferences("User", Context.MODE_PRIVATE)
        val savedUserId = sharedPref.getString("userId", null)

        // Check if user is logged in AND SharedPreferences match
        if (currentUser != null && savedUserId == currentUser.uid) {
            // Valid session - check role and navigate
            FirebaseDatabase.getInstance().getReference("users")
                .child(currentUser.uid)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val role = snapshot.child("role").value.toString()

                        // Update SharedPreferences with latest data
                        val editor = sharedPref.edit()
                        editor.putString("userId", currentUser.uid)
                        editor.putString("name", snapshot.child("name").value.toString())
                        editor.putString("email", snapshot.child("email").value.toString())
                        editor.putString("phone", snapshot.child("phonenumber").value.toString())
                        editor.putString("address", snapshot.child("address").value.toString())
                        editor.putString("profileImage", snapshot.child("profileImage").value.toString())
                        editor.putString("role", role)
                        editor.apply()

                        // Navigate based on role
                        if (role == "admin") {
                            openAdminDashboard()
                        } else {
                            openUserDashboard()
                        }
                    } else {
                        // User exists in Auth but not in Database - logout and go to login
                        auth.signOut()
                        sharedPref.edit().clear().apply()
                        openLogin()
                    }
                }
                .addOnFailureListener {
                    // Failed to fetch user data - logout and go to login
                    auth.signOut()
                    sharedPref.edit().clear().apply()
                    openLogin()
                }
        } else {
            // No valid session - clear everything and go to login
            auth.signOut()
            sharedPref.edit().clear().apply()
            openLogin()
        }
    }

    private fun openLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun openAdminDashboard() {
        val intent = Intent(this, AdminDashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun openUserDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.5f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 600,
                easing = FastOutSlowInEasing
            )
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 600,
                easing = FastOutSlowInEasing
            )
        )

        delay(800)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Azure),
        contentAlignment = Alignment.Center
    ) {

        Icon(
            painter = painterResource(R.drawable.paw),
            contentDescription = null,
            tint = VividAzure,
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.TopEnd)
                .alpha(alpha.value)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alpha.value)
        ) {
            Text(
                "Welcome to",
                textAlign = TextAlign.Center,
                fontSize = 25.sp
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                "Pet Point",
                textAlign = TextAlign.Center,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(30.dp))

            Image(
                painter = painterResource(R.drawable.petpoint),
                contentDescription = "Pet Point Logo",
                modifier = Modifier
                    .size(300.dp)
                    .clip(CircleShape)
                    .scale(scale.value),
                contentScale = ContentScale.Crop
            )
        }
    }
}