package com.project.petpoint.view

import android.R.attr.onClick
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.unit.dp
import com.project.petpoint.R
import com.project.petpoint.ui.theme.BlueWhite
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.PetPointTheme
import com.project.petpoint.view.ui.theme.VividAzure
import com.project.petpoint.view.ui.theme.VividOrange

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashboardBody()

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody() {
    val context = LocalContext.current
    val activity = context as Activity

    data class NavItem(val icon: Int, val label: String)

    val listItems  = listOf(
        NavItem(icon =R.drawable.baseline_shopping_bag_24, label="Shopp"),
        NavItem(icon =R.drawable.baseline_local_hospital_24, label="Vets"),
        NavItem(icon =R.drawable.baseline_local_phone_24, label="Lost and Found"),
        NavItem(icon =R.drawable.baseline_person_24, label="Profile"),
    )

    var selectedIndex by remember{ mutableStateOf(0)}

    Scaffold(
        topBar  = {
            TopAppBar(
                colors  = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = BlueWhite,
                    actionIconContentColor = BlueWhite,
                    navigationIconContentColor = BlueWhite,
                    containerColor = VividAzure
                ),
                navigationIcon = {
                    IconButton(onClick= {}){
                        Icon(
                            painter = painterResource(R.drawable.petpoint),
                            contentDescription = null
                        )
                    }
                },
                title ={ IconButton(onClick = {}) {
                    Icon(
                        painter  =  painterResource(R.drawable.baseline_search_24),
                        contentDescription = null
                    )
                }},
                actions = {
                    IconButton(onClick = {}) {

                        Icon(
                            painter  =  painterResource(R.drawable.baseline_notifications_24),
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
                                painter = painterResource(item.icon),
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
                0-> ShopScreen()
//                1-> ShopScreen()
//                2-> ShopScreen()
//                3-> ShopScreen()
                else -> ShopScreen()
            }
        }
    }
}


@Preview
@Composable
fun DashboardPreview(){
    DashboardBody()
}
