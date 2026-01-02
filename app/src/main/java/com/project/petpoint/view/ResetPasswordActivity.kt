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

    // Initial state is now empty strings
    var email by remember { mutableStateOf("") }
    val userViewModel = remember { UserViewModel(UserRepoImpl()) }
    val context = LocalContext.current
    val activity = context as? Activity


    Scaffold { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Azure)
        ) {

            // PAW ICON TOP & Title Area
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
            Text(
                text = "Pet Point",
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            // MAIN CONTENT
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Azure)
                    .padding(5.dp)
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

                        Spacer(modifier = Modifier.height(30.dp))

                        // EMAIL FIELD
                        Text(
                            "Email",
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.SemiBold
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = { Text("") }, // Updated placeholder
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = GreyOrange,
                                focusedContainerColor = GreyOrange,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color.Black
                            ),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // RESET BUTTON
                        Box(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth()
                                .background(VividOrange, RoundedCornerShape(25.dp))
                                .height(45.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = {
                                    if (email.isEmpty()) {
                                        Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }

                                    // Send reset email
                                    userViewModel.forgotPassword(email) { success, message ->
                                        if (success) {
                                            Toast.makeText(
                                                context,
                                                "Reset email sent to $email. Check your inbox.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            activity?.finish() // optional: go back to login
                                        } else {
                                            Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                shape = RoundedCornerShape(20.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    "Reset",
                                    color = Color.White,
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = VividOrange,     // FIXED: Solid dark orange
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold // FIXED: Bold
                            )
                        ) {
                            append("Back to login")
                        }
                    },
                        modifier = Modifier
                            .clickable {
                                val intent = Intent(context,
                                    LoginActivity::class.java)
                                context.startActivity(intent)
                            }
                        ,style = TextStyle(fontSize = 16.sp))


                }
            }
        }
    }
}
@Preview
@Composable
fun ResetPasswordPreview() {
    PetPointResetPasswordUI()
}