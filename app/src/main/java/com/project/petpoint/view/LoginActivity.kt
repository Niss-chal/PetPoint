package com.project.petpoint.view

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.petpoint.R
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
            PetPointLoginUI()
        }
    }
}

@Composable
fun PetPointLoginUI() {

    var userViewModel = remember { UserViewModel(UserRepoImpl()) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visibility by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity

    val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
    val localEmail = sharedPreferences.getString("email", "")
    val localPassword = sharedPreferences.getString("password", "")

    LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Azure)
        ) {

            item {
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
            }

            item { Spacer(modifier = Modifier.height(10.dp)) }

            item { Text(
                text = "Sign in to continue",
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp
                )
            )

                Spacer(modifier = Modifier.height(15.dp))
            }


            item { Text(
                text = "Pet Point",
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold
                )
            ) }


            item {
                // MAIN CONTENT
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
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = GreyOrange,
                                    focusedContainerColor = GreyOrange,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent
                                ),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),


                                )

                            Spacer(modifier = Modifier.height(15.dp))

                            // PASSWORD FIELD
                            Text("Password", color = Color.White, fontWeight = FontWeight.SemiBold)

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                placeholder = { Text("Enter your password") },
                                shape = RoundedCornerShape(12.dp),
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
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            )

                            Text(
                                text = "Forgot Password?",
                                color = Color.White,
                                fontSize = 13.sp,
                                modifier = Modifier.align(Alignment.End)
                                    .clickable{
                                        val intent = Intent(context, ResetPasswordActivity::class.java)
                                        context.startActivity(intent)

                                    }
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    if (email.isNotBlank() && password.isNotBlank()) {

                                        userViewModel.login(email, password) { success, message ->
                                            if (success) {
                                                val userId = userViewModel.getCurrentUser()?.uid
                                                if (userId != null) {
                                                    userViewModel.checkUserRole(userId) { role ->
                                                        when (role) {
                                                            "admin" -> {
                                                                Toast.makeText(
                                                                    context,
                                                                    "Welcome!",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                                context.startActivity(
                                                                    Intent(context, AdminDashboardActivity::class.java)
                                                                )
                                                                activity?.finish()
                                                            }
                                                            "buyer" -> {
                                                                Toast.makeText(
                                                                    context,
                                                                    "Welcome!",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                                context.startActivity(
                                                                    Intent(context, DashboardActivity::class.java)
                                                                )
                                                                activity?.finish()
                                                            }
                                                            else -> {
                                                                Toast.makeText(
                                                                    context,
                                                                    "User role not found",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "User ID not found",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            } else {
                                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Please enter email and password",
                                            Toast.LENGTH_SHORT
                                        ).show()
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
                                    .clickable{
                                        val intent = Intent(context, SignupActivity::class.java)
                                        context.startActivity(intent)

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


@Preview
@Composable
fun LoginPreview() {
    PetPointLoginUI()
}
