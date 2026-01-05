package com.project.petpoint.view

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.project.petpoint.model.ProductModel
import com.project.petpoint.repository.ProductRepo
import com.project.petpoint.repository.ProductRepoImpl
import com.project.petpoint.utils.ImageUtils
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.Orange
import com.project.petpoint.view.ui.theme.PetPointTheme
import com.project.petpoint.view.ui.theme.White
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
            AddProduct(
                selectedImageUri = selectedImageUri,
                onPickImage = { imageUtils.launchImagePicker() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProduct(
    selectedImageUri: Uri?,
    onPickImage: () -> Unit) {
    var productViewModel = remember { ProductViewModel(ProductRepoImpl()) }
    var productName by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var productStock by remember { mutableStateOf("") }

    var productCategory by remember { mutableStateOf("Select Category") }
    var expanded by remember { mutableStateOf(false) }


    val context = LocalContext.current
    val activity = context as Activity


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Azure)

    ) {

        item {

            Spacer(modifier = Modifier.height(50.dp))
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {

                Text("Product Name", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    placeholder = { Text("Enter the product name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Price", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = productPrice,
                    onValueChange = { productPrice = it },
                    placeholder = { Text("Enter the price") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))


                Text("Description", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = productDescription,
                    onValueChange = { productDescription = it },
                    placeholder = { Text("Enter the description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Category", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = productCategory,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf(
                            "Dogs",
                            "Cats",
                            "Birds",
                            "Reptiles",
                            "Other"

                        ).forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    productCategory = category
                                    expanded = false

                                    productCategory = category
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                Text("Stock Quantity", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = productStock,
                    onValueChange = { productStock = it },
                    placeholder = { Text("Enter the stock quantity") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clickable { onPickImage() },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text("Upload Image")
                    }
                }
            }
        }

        item {
            Button(
                onClick = {

                    when {
                        productName.isBlank() -> {
                            Toast.makeText(
                                context,
                                "Please enter product name",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        productPrice.isBlank() -> {
                            Toast.makeText(context, "Please enter price", Toast.LENGTH_SHORT)
                                .show()
                        }

                        productPrice.toDoubleOrNull() == null -> {
                            Toast.makeText(
                                context,
                                "Price must be a number",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        productDescription.isBlank() -> {
                            Toast.makeText(
                                context,
                                "Please enter description",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        productStock.isBlank() -> {
                            Toast.makeText(
                                context,
                                "Please enter stock quantity",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        productStock.toIntOrNull() == null -> {
                            Toast.makeText(
                                context,
                                "Stock quantity must be a number",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        selectedImageUri == null -> {
                            Toast.makeText(
                                context,
                                "Please select an image",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            productViewModel.uploadImage(
                                context,
                                selectedImageUri
                            ) { imageUrl ->
                                if (imageUrl != null) {
                                    val productModel = ProductModel(
                                        productId = "",
                                        name = productName,
                                        price = productPrice.toDouble(),
                                        description = productDescription,
                                        imageUrl = imageUrl,
                                        stock = productStock.toInt()

                                    )
                                    productViewModel.addProduct(productModel) { success, msg ->
                                        if (success) {
                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT)
                                                .show()
                                            activity.finish()
                                        } else {
                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT)
                                                .show()
                                        }

                                    }
                                }
                            }

                        }
                    }


                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                Text("Add", color = White)
            }
        }


    }


}
@Preview
@Composable
fun AddProductPreview(){
    AddProduct(null,{})
}