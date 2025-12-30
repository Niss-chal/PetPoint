package com.project.petpoint.view

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.petpoint.R
import com.project.petpoint.model.ProductModel
import com.project.petpoint.repository.ProductRepoImpl
import com.project.petpoint.utils.ImageUtils
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.Green
import com.project.petpoint.view.ui.theme.VividAzure
import com.project.petpoint.view.ui.theme.VividOrange
import com.project.petpoint.view.ui.theme.White
import com.project.petpoint.view.ui.theme.Yellow
import com.project.petpoint.viewmodel.ProductViewModel

class AddProductActivity : ComponentActivity() {
    lateinit var imageUtils: ImageUtils
    var selectedImageUri by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        imageUtils = ImageUtils(this, this)
        imageUtils.registerLaunchers { uri ->
            selectedImageUri = uri
        }
        setContent {
            ProductManagementScreen(
                selectedImageUri = selectedImageUri,
                onPickImage = { imageUtils.launchImagePicker() }
            )
        }
    }
}

@Composable
fun ProductManagementScreen(
    selectedImageUri: Uri?,
    onPickImage: () -> Unit
) {
    val productRepo = remember { ProductRepoImpl() }
    val productViewModel = remember { ProductViewModel(productRepo) }

    val context = LocalContext.current
    val activity = context as? Activity

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Azure)
            .padding(16.dp)
    ) {

        // Title
        Text(
            text = "Product Management",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))
        Row {
            Divider(
                color = Color.Gray.copy(alpha = 0.7f),
                thickness = 1.dp,
            )
        }
        Spacer(modifier = Modifier.height(30.dp))

        // Add Product Button
        Button(
            onClick = {
                val model = ProductModel(
                    "",
                    name,
                    price.toDouble(),
                    description
                )
                productViewModel.addProduct(model){
                        success,message ->
                    if(success){
                        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
                        activity?.finish()
                    }else{
                        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = VividAzure),
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
        ) {
            Text("+ Add Product", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(30.dp))

        LazyColumn {
            item {
                ProductCard(
                    name = "Dog Collar",
                    price = 500.0,
                    stock = 12,
                    status = "In Stock",
                    statusColor = Green
                )
            }

            item {
                ProductCard(
                    name = "Cat Food Bowl",
                    price = 725.0,
                    stock = 6,
                    status = "Low Stock",
                    statusColor = Yellow
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

    }
}

@Composable
fun ProductCard(
    name: String,
    price: Double,
    stock: Int,
    status: String,
    statusColor: Color
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(name, fontWeight = FontWeight.Bold)
                Text(status, color = statusColor, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                price
                Text("Stock: $stock")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row {
                Divider(
                    color = Color.Gray.copy(alpha = 0.7f),
                    thickness = 1.dp,
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Edit
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Edit",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }

                // Delete
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        
                        text = "Delete",
                        fontSize = 14.sp,
                        color = Color.Red
                    )
                }
            }

        }
    }
}