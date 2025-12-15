package com.project.petpoint.view

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.project.petpoint.R
import com.project.petpoint.ui.theme.BlueWhite
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.PetPointTheme
import com.project.petpoint.view.ui.theme.VividAzure

class AdminDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdminDashBody()

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashBody(){

    val context = LocalContext.current
    val activity = context as Activity

    data class NavItem(val image: Int, val label: String)

    val listItems  = listOf(
        NavItem(image = R.drawable.home, label="Home"),
        NavItem(image = R.drawable.productsmanage, label="Products"),
        NavItem(image = R.drawable.vetmanagement, label="Vets"),
        NavItem(image = R.drawable.lostandfound, label="Lost and Found"),
    )

    var selectedIndex by remember{ mutableStateOf(0)}

    Scaffold(
        topBar  = {
            CenterAlignedTopAppBar(
                colors  = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = BlueWhite,
                    actionIconContentColor = BlueWhite,
                    navigationIconContentColor = BlueWhite,
                    containerColor = VividAzure
                ),
                navigationIcon = {
                    IconButton(onClick= {}){
                        Image(
                            painter = painterResource(R.drawable.dashboardlogo),
                            contentDescription = null,
                        )
                    }
                },
                title ={ IconButton(onClick = {

                }) {


                }},
                actions = {
                    IconButton(onClick = {}) {

                        Icon(
                            painter  =  painterResource(R.drawable.userprofile),
                            contentDescription = null
                        )


                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                listItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(item.image),
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(item.label)
                        },
                        onClick = {
                            selectedIndex = index
                        },
                        selected = selectedIndex == index
                    )
                }
            }
        }
    ) {
            padding ->
        Column(
            modifier=Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Azure)
        ) {
            when(selectedIndex){
                0-> HomeScreen()
                1-> ProductManagement()
                2-> VetManagement()
                3-> LostandFoundManagement()
                else -> HomeScreen()
            }
        }
    }



}