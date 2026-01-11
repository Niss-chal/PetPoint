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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.petpoint.R
import com.project.petpoint.model.UserModel
import com.project.petpoint.repository.UserRepoImpl
import com.project.petpoint.ui.theme.VividAzure
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.PetPointTheme
import com.project.petpoint.viewmodel.UserViewModel


class EditProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Editprofilebody()

        }
    }
}

@Composable
fun Editprofilebody(){


    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("User", Context.MODE_PRIVATE)
    val userViewModel = remember { UserViewModel(UserRepoImpl()) }

    val userId = sharedPref.getString("userId", null)

    var name by remember { mutableStateOf(sharedPref.getString("name", "") ?: "") }
    var email by remember { mutableStateOf(sharedPref.getString("email", "") ?: "") }
    var phone by remember { mutableStateOf(sharedPref.getString("phone", "") ?: "") }
    var address by remember { mutableStateOf(sharedPref.getString("address", "") ?: "") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Azure),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .background(
                    VividAzure,
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Edit Profile", color = Color.White, fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))



        Image(
            painter = painterResource(id = R.drawable.userprofile),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .border(3.dp, Color(0xFF9EC760), CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(20.dp))


        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(Color(0xFFE7F0F3), shape = RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {


            Text(text = "Name", fontSize = 18.sp, color = Color.Black)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                shape = RoundedCornerShape(25.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )


            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Email", fontSize = 18.sp)
            OutlinedTextField(
                value = email,
                onValueChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                readOnly = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(25.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )


            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Phone", fontSize = 18.sp)
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(25.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )




            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Address", fontSize = 18.sp)
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                shape = RoundedCornerShape(25.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White)
            )
            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {

                    // Update LOCAL data
                    val editor = sharedPref.edit()
                    editor.putString("name", name)
                    editor.putString("email", email)
                    editor.putString("phone", phone)
                    editor.putString("address", address)
                    editor.apply()

                    // Update DATABASE data
                    if (userId != null) {
                        val updatedUser = UserModel(
                            userId = userId,
                            name = name,
                            email = email,
                            address = address,
                            phonenumber = phone
                        )

                        userViewModel.addUserToDatabase(userId, updatedUser) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                ,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VividAzure ,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Save",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {  val intent = Intent(context, ProfileActivity::class.java)
                    context.startActivity(intent)
                    (context as? Activity)?.finish() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VividAzure ,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Back",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }

            }

        }
    }


