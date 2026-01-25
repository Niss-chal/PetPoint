package com.project.petpoint.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.petpoint.R
import com.project.petpoint.repository.UserRepoImpl
import com.project.petpoint.view.ui.theme.*
import com.project.petpoint.viewmodel.UserViewModel

class ResetPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetPointResetPasswordUI()
        }
    }
}

@Composable
fun PetPointResetPasswordUI() {
    var email by remember { mutableStateOf("") }
    val userViewModel = remember { UserViewModel(UserRepoImpl()) }
    val context = LocalContext.current
    val activity = context as? Activity
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Azure)
            .verticalScroll(scrollState)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Icon
        Icon(
            painter = painterResource(R.drawable.paw),
            contentDescription = null,
            tint = VividAzure,
            modifier = Modifier
                .padding(top = 10.dp, end = 15.dp)
                .size(80.dp)
                .align(Alignment.End)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // App Title
        Text(
            text = "Pet Point",
            style = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Logo
        Image(
            painter = painterResource(R.drawable.petpoint),
            contentDescription = "Pet Point Logo",
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Reset Password Form
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .background(VividAzure, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reset Password",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    painter = painterResource(R.drawable.paw),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Info Text
            Text(
                text = "Enter your email address and we'll send you a link to reset your password.",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Email Field
            Text(
                text = "Email",
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Enter your email") },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = GreyOrange,
                    focusedContainerColor = GreyOrange,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Reset Button
            Button(
                onClick = {
                    if (email.isBlank()) {
                        Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    userViewModel.forgotPassword(email) { success, message ->
                        if (success) {
                            Toast.makeText(
                                context,
                                "Reset link sent to $email. Check your inbox.",
                                Toast.LENGTH_LONG
                            ).show()
                            activity?.finish()
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = VividOrange),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Send Reset Link",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Back to Login Link
            Text(
                buildAnnotatedString {
                    append("Back to ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Login")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        context.startActivity(Intent(context, LoginActivity::class.java))
                        activity?.finish()
                    },
                color = Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}