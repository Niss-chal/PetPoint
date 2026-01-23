package com.project.petpoint.view

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

        if (currentUser == null) {
            openLogin()
        } else {
            // user logged in, check role
            FirebaseDatabase.getInstance().getReference("users")
                .child(currentUser.uid)
                .get()
                .addOnSuccessListener { snapshot ->
                    val role = snapshot.child("role").value.toString()
                    if (role == "admin") openAdminDashboard() else openUserDashboard()
                }
                .addOnFailureListener {
                    openLogin()
                }
        }
    }

    private fun openLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun openAdminDashboard() {
        startActivity(Intent(this, AdminDashboardActivity::class.java))
        finish()
    }

    private fun openUserDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
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
