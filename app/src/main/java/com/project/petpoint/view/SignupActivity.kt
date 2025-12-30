package com.project.petpoint.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.White
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
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.GreyOrange
import com.project.petpoint.view.ui.theme.VividAzure
import com.project.petpoint.view.ui.theme.VividOrange
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

    val sharedPreference = context.getSharedPreferences("User", Context.MODE_PRIVATE)

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Azure)
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
                    modifier = Modifier.size(80.dp),
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Welcome to",
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp
                )
            )
            Text(
                "Pet Point",
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,

                    )
            )
            Spacer(
                modifier = Modifier.height(15.dp)
            )
            Image(
                painter = painterResource(R.drawable.petpoint),
                contentDescription = null,
                modifier = Modifier
                    .size(130.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(
                modifier = Modifier.height(20.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp)
                    .background(
                        color = VividAzure,
                        shape = RoundedCornerShape(25.dp)
                    )
                    .padding(20.dp)
            ) {

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sign Up",
                            fontSize = 28.sp,
                            color = White,
                            fontWeight = FontWeight.Bold
                        )

                        Icon(
                            painter = painterResource(R.drawable.paw),
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(15.dp)) }
                item {
                    Text("Email", color = White, fontSize = 14.sp)
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Enter your email") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = GreyOrange,
                            unfocusedContainerColor = GreyOrange,
                            focusedIndicatorColor = Blue,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(25.dp)
                    )
                }
                item {
                    Text("Name", color = White, fontSize = 14.sp)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Enter your name") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = GreyOrange,
                            unfocusedContainerColor = GreyOrange,
                            focusedIndicatorColor = Blue,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(25.dp)
                    )
                }

                item {
                    Text("Address", color = White, fontSize = 14.sp)
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        placeholder = { Text("Enter your Address") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = GreyOrange,
                            unfocusedContainerColor = GreyOrange,
                            focusedIndicatorColor = Blue,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(25.dp)
                    )
                }

                item {
                    Text("Phone", color = White, fontSize = 14.sp)
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        placeholder = { Text("Enter your Phone Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = GreyOrange,
                            unfocusedContainerColor = GreyOrange,
                            focusedIndicatorColor = Blue,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(25.dp)
                    )
                }

                item {
                    Text("Password", color = White, fontSize = 14.sp)
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Enter your password") },
                        visualTransformation =
                            if (visibility) VisualTransformation.None else PasswordVisualTransformation(),
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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = GreyOrange,
                            unfocusedContainerColor = GreyOrange,
                            focusedIndicatorColor = Blue,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(25.dp)
                    )
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }

                item {
                    Button(
                        onClick = {
                            userViewModel.register(email,password){
                                success,message,userId ->
                                if(success){
                                    var  model = UserModel(
                                        userId = userId,
                                        email=email,
                                        name = name,
                                        address = address,
                                        phonenumber = phone
                                    )
                                    userViewModel.addUserToDatabase(userId,model){
                                        success, message ->
                                        if (success){
                                            activity?.finish()
                                            Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
                                        }else{
                                            Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }else{
                                    Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VividOrange)
                    ) {
                        Text("Sign Up", fontSize = 18.sp, color = White)
                    }
                }

                item { Spacer(modifier = Modifier.height(10.dp)) }

                item {
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
                            },
                        color = White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
