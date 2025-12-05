package com.project.petpoint.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.petpoint.R
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.GreyOrange
import com.project.petpoint.view.ui.theme.VividAzure
import com.project.petpoint.view.ui.theme.VividOrange

class VerificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VerificationBody()
        }
    }
}

@Composable
fun VerificationBody() {
    var otp = remember { mutableStateListOf("", "", "", "") }
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
                "Verify Your Email",
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .background(
                        color = VividAzure,
                        shape = RoundedCornerShape(25.dp)
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Account\nVerification",
                        fontSize = 35.sp,
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
                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    "Please enter the 4 digit code sent\nto Your Email",
                    style = TextStyle(
                        color = White,
                        fontSize = 16.sp
                    )
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    otp.forEachIndexed { index, value ->
                        OTPBox(
                            value = value,
                            onChange = { new ->
                                if (new.length <= 1) {
                                    otp[index] = new
                                }
                            }
                        )
                    }
                }
                Spacer(
                    modifier = Modifier.height(20.dp)
                )
                Text(
                    "Resend Code",
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        color = White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(
                    modifier = Modifier.height(20.dp)
                )
                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VividOrange
                    )
                ) {
                    Text("Verify", fontSize = 18.sp, color = White)
                }
            }
        }

    }
}

@Composable
fun OTPBox(value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.size(60.dp),
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            color = Color.Black
        ),
        shape = RoundedCornerShape(10.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = GreyOrange,
            unfocusedContainerColor = GreyOrange,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}


@Preview
@Composable
fun VerificationPrev(){
    VerificationBody()
}
