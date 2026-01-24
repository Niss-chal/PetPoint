package com.project.petpoint.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.project.petpoint.R
import com.project.petpoint.model.UserModel
import com.project.petpoint.repository.UserRepoImpl
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.GreyOrange
import com.project.petpoint.view.ui.theme.VividAzure
import com.project.petpoint.view.ui.theme.VividOrange
import com.project.petpoint.viewmodel.UserViewModel

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen()
        }
    }
}

@Composable
fun LoginScreen() {
    val context = LocalContext.current
    val activity = context as? Activity
    val scrollState = rememberScrollState()

    val userViewModel = remember { UserViewModel(UserRepoImpl()) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Azure)
            .imePadding()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top paw icon
        Icon(
            painter = painterResource(R.drawable.paw),
            contentDescription = null,
            tint = VividAzure,
            modifier = Modifier
                .padding(top = 16.dp, end = 16.dp)
                .size(70.dp)
                .align(Alignment.End)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Welcome text
        Text(
            text = "Sign in to continue",
            fontSize = 26.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Pet Point",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Logo
        Image(
            painter = painterResource(R.drawable.petpoint),
            contentDescription = "Pet Point Logo",
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login Form Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .background(VividAzure, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            // Header with paw
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Log in",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    painter = painterResource(R.drawable.paw),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(45.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Email field
            Text(
                text = "Email",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Enter your email", fontSize = 14.sp) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = GreyOrange,
                    focusedContainerColor = GreyOrange,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            Text(
                text = "Password",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Enter your password", fontSize = 14.sp) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            painter = painterResource(
                                if (isPasswordVisible) R.drawable.baseline_visibility_off_24
                                else R.drawable.baseline_visibility_24
                            ),
                            contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = GreyOrange,
                    focusedContainerColor = GreyOrange,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Forgot password
            Text(
                text = "Forgot Password?",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable {
                        context.startActivity(Intent(context, ResetPasswordActivity::class.java))
                    }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Login button
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    userViewModel.login(email, password) { success, message ->
                        if (!success) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            return@login
                        }

                        val userId = userViewModel.getCurrentUser()?.uid
                        if (userId == null) {
                            Toast.makeText(context, "User ID not found", Toast.LENGTH_SHORT).show()
                            return@login
                        }

                        // Fetch user data
                        userViewModel.getUserById(userId)

                        // Observe user data once
                        val observer = object : androidx.lifecycle.Observer<UserModel?> {
                            override fun onChanged(user: UserModel?) {
                                if (user == null) return

                                // Normalize role to lowercase and trim whitespace
                                val normalizedRole = user.role?.trim()?.lowercase() ?: "buyer"

                                // Determine target activity
                                val targetActivity = if (normalizedRole == "admin") {
                                    AdminDashboardActivity::class.java
                                } else {
                                    DashboardActivity::class.java
                                }

                                Log.d("LOGIN_DEBUG", "User role: '$normalizedRole' → ${targetActivity.simpleName}")

                                // Save user data to SharedPreferences with normalized role
                                context.getSharedPreferences("User", Context.MODE_PRIVATE).edit {
                                    clear() // Clear any old data
                                    putString("userId", userId)
                                    putString("name", user.name)
                                    putString("email", user.email)
                                    putString("phone", user.phonenumber)
                                    putString("address", user.address)
                                    putString("profileImage", user.profileImage)
                                    putString("role", normalizedRole) // Save normalized role
                                }

                                Toast.makeText(
                                    context,
                                    if (normalizedRole == "admin") "Welcome Admin!" else "Welcome!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Navigate to appropriate dashboard
                                val intent = Intent(context, targetActivity).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                context.startActivity(intent)
                                activity?.finish()

                                // Clean up observer
                                userViewModel.users.removeObserver(this)
                            }
                        }

                        userViewModel.users.observeForever(observer)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = VividOrange),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Login",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign up link
            Text(
                text = buildAnnotatedString {
                    append("Don't have an account? ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Sign Up")
                    }
                },
                color = Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        context.startActivity(Intent(context, SignupActivity::class.java))
                    }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}