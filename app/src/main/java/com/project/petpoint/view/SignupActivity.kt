package com.project.petpoint.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.project.petpoint.model.UserModel
import com.project.petpoint.repository.UserRepoImpl
import com.project.petpoint.view.ui.theme.*
import com.project.petpoint.viewmodel.UserViewModel

class SignupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SignupBody()
        }
    }
}

@Composable
fun SignupBody() {
    val userViewModel = remember { UserViewModel(UserRepoImpl()) }
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visibility by remember { mutableStateOf(false) }

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

        // Welcome Text
        Text(
            text = "Welcome to",
            style = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = 24.sp
            )
        )

        Text(
            text = "Pet Point",
            style = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Logo
        Image(
            painter = painterResource(R.drawable.petpoint),
            contentDescription = "Pet Point Logo",
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Signup Form
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .background(VividAzure, RoundedCornerShape(25.dp))
                .padding(20.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sign Up",
                    fontSize = 28.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    painter = painterResource(R.drawable.paw),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Email Field
            Text("Email", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Enter your email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GreyOrange,
                    unfocusedContainerColor = GreyOrange,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // Name Field
            Text("Name", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Enter your name") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GreyOrange,
                    unfocusedContainerColor = GreyOrange,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // Phone Field
            Text("Phone", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                placeholder = { Text("Enter your phone number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GreyOrange,
                    unfocusedContainerColor = GreyOrange,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // Address Field
            Text("Address", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                placeholder = { Text("Enter your address") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GreyOrange,
                    unfocusedContainerColor = GreyOrange,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // Password Field
            Text("Password", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Enter your password") },
                visualTransformation = if (visibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { visibility = !visibility }) {
                        Icon(
                            painter = if (visibility)
                                painterResource(R.drawable.baseline_visibility_off_24)
                            else
                                painterResource(R.drawable.baseline_visibility_24),
                            contentDescription = "Toggle password visibility"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GreyOrange,
                    unfocusedContainerColor = GreyOrange,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Signup Button
            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank() && name.isNotBlank()) {
                        userViewModel.register(email, password) { success, message, userId ->
                            if (success) {
                                val model = UserModel(
                                    userId = userId,
                                    email = email,
                                    name = name,
                                    address = address,
                                    phonenumber = phone,
                                    role = "buyer"
                                )
                                userViewModel.addUserToDatabase(userId, model) { dbSuccess, dbMessage ->
                                    Toast.makeText(context, dbMessage, Toast.LENGTH_SHORT).show()
                                    if (dbSuccess) {
                                        val intent = Intent(context, LoginActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        context.startActivity(intent)
                                        activity?.finish()
                                    }
                                }
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VividOrange)
            ) {
                Text("Sign Up", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Login Link
            Text(
                buildAnnotatedString {
                    append("Already have an account? ")
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