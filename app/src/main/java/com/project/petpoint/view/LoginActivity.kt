package com.project.petpoint.view

import android.R.id.message
import android.app.Activity
import android.content.Context
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.petpoint.R
import com.project.petpoint.repository.UserRepoImpl
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.GreyOrange
import com.project.petpoint.view.ui.theme.VividAzure
import com.project.petpoint.view.ui.theme.VividOrange
import com.project.petpoint.viewmodel.UserViewModel
import androidx.core.content.edit
import com.project.petpoint.model.UserModel

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetPointLoginUI()
        }
    }
}

@Composable
fun PetPointLoginUI() {

    val userViewModel = remember { UserViewModel(UserRepoImpl()) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visibility by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Azure)
                .imePadding()
                .verticalScroll(scrollState)
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 10.dp, start = 15.dp, end = 15.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    painter = painterResource(R.drawable.paw),
                    contentDescription = null,
                    tint = VividAzure,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Sign in to continue",
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp
                )
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "Pet Point",
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Azure)
                    .padding(10.dp)
            ) {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // LOGO
                    Image(
                        painter = painterResource(R.drawable.petpoint),
                        contentDescription = null,
                        modifier = Modifier
                            .size(180.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(VividAzure, RoundedCornerShape(20.dp))
                            .padding(20.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Log in",
                                color = Color.White,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Icon(
                                painter = painterResource(R.drawable.paw),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(50.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // EMAIL FIELD
                        Text("Email", color = Color.White, fontWeight = FontWeight.SemiBold)

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = { Text("Enter your email") },
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email
                            ),
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

                        Spacer(modifier = Modifier.height(15.dp))

                        // PASSWORD FIELD
                        Text("Password", color = Color.White, fontWeight = FontWeight.SemiBold)

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = { Text("Enter your password") },
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
                            ),
                            visualTransformation = if (!visibility) PasswordVisualTransformation() else VisualTransformation.None,
                            trailingIcon = {
                                IconButton(onClick = { visibility = !visibility }) {
                                    Icon(
                                        painter = if (visibility)
                                            painterResource(R.drawable.baseline_visibility_off_24)
                                        else
                                            painterResource(R.drawable.baseline_visibility_24),
                                        contentDescription = null
                                    )
                                }
                            },
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

                        Text(
                            text = "Forgot Password?",
                            color = Color.White,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .align(Alignment.End)
                                .clickable {
                                    context.startActivity(
                                        Intent(context, ResetPasswordActivity::class.java)
                                    )
                                }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

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

                                    // Observe once and navigate when we receive the data
                                    val observer = object : androidx.lifecycle.Observer<UserModel?> {
                                        override fun onChanged(user: UserModel?) {
                                            if (user == null) return

                                            val sharedPref = context.getSharedPreferences("User", Context.MODE_PRIVATE)
                                            sharedPref.edit {
                                                clear()
                                                putString("userId", userId)
                                                putString("name", user.name)
                                                putString("email", user.email)
                                                putString("phone", user.phonenumber)
                                                putString("address", user.address)
                                                putString("profileImage", user.profileImage)
                                                putString("role", user.role)
                                            }

                                            val intent = when (user.role?.lowercase()) {
                                                "admin" -> {
                                                    Toast.makeText(context, "Welcome Admin!", Toast.LENGTH_SHORT).show()
                                                    Intent(context, AdminDashboardActivity::class.java)
                                                }
                                                "buyer" -> {
                                                    Toast.makeText(context, "Welcome!", Toast.LENGTH_SHORT).show()
                                                    Intent(context, DashboardActivity::class.java)
                                                }
                                                else -> {
                                                    Toast.makeText(context, "Unknown role: ${user.role}", Toast.LENGTH_SHORT).show()
                                                    null
                                                }
                                            }

                                            intent?.let {
                                                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                context.startActivity(it)
                                                activity?.finish()
                                            }

                                            // Crucial: remove the observer after we used it once
                                            userViewModel.users.removeObserver(this)
                                        }
                                    }

                                    userViewModel.users.observeForever(observer)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VividOrange),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth()
                                .height(45.dp)
                        ) {
                            Text("Login", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(15.dp))

                        Text(
                            buildAnnotatedString {
                                append("Don't have an account? ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("Sign Up")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    context.startActivity(
                                        Intent(context, SignupActivity::class.java)
                                    )
                                },
                            color = Color.White,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}