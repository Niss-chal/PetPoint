package com.project.petpoint.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.petpoint.R
import com.project.petpoint.view.ui.theme.Azure
import com.project.petpoint.view.ui.theme.BlanchedAlmond
import com.project.petpoint.view.ui.theme.Blue
import com.project.petpoint.view.ui.theme.DarkOrange


data class LostFoundItem(
    val id: Int,
    val type: String,
    val status: String,
    val title: String,
    val category: String,
    val description: String,
    val location: String,
    val date: String,
    val reportedBy: String
)



val lostFoundList = listOf(
    LostFoundItem(
        1,
        "Lost",
        "Pending",
        "Blue Backpack",
        "Bags",
        "Blue backpack with laptop inside",
        "Building A, 2nd Floor",
        "Dec 10, 2025",
        "John Smith"
    ),
    LostFoundItem(
        2,
        "Found",
        "Resolved",
        "Water Bottle",
        "Accessories",
        "Steel water bottle near cafeteria",
        "Cafeteria",
        "Dec 08, 2025",
        "Emma Brown"
    )
)

@Composable
fun LostandFoundManagement()
{

var search by remember { mutableStateOf("") }
var selectedType by remember { mutableStateOf("All") }
var selectedStatus by remember { mutableStateOf("All") }

val filteredList = lostFoundList.filter { item ->
    val matchesSearch =
        item.title.contains(search, true) ||
                item.category.contains(search, true) ||
                item.location.contains(search, true)

    val matchesType =
        selectedType == "All" || item.type == selectedType

    val matchesStatus =
        selectedStatus == "All" || item.status == selectedStatus

    matchesSearch && matchesType && matchesStatus
}

LazyColumn(
modifier = Modifier
.fillMaxSize()
.background(Azure),
contentPadding = PaddingValues(20.dp),
verticalArrangement = Arrangement.spacedBy(15.dp)
) {
    item {
        Column {
            Text("Lost & Found", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(
                "Manage lost and found item reports",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }

    item {
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Search by title, category or location")
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_search_24),
                    contentDescription = "Search"
                )
            }
        )
    }

    item {
        TextButton(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .background(Blue, RoundedCornerShape(8.dp))
        ) {
            Text("+ Report Item", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }

    item {
        FilterRow(
            options = listOf("All", "Lost", "Found"),
            selected = selectedType,
            onSelect = { selectedType = it }
        )
    }

    item {
        FilterRow(
            options = listOf("All", "Pending", "Resolved"),
            selected = selectedStatus,
            onSelect = { selectedStatus = it }
        )
    }

    items(filteredList, key = { it.id }) {
        LostFoundItemCard(it)
    }

    if (filteredList.isEmpty()) {
        item {
            Text(
                "No results found",
                modifier = Modifier.fillMaxWidth(),
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
}

@Composable
fun FilterRow(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach {
            val isSelected = it == selected
            TextButton(
                onClick = { onSelect(it) },
                modifier = Modifier.background(
                    if (isSelected) Color.Blue else Color.White,
                    RoundedCornerShape(20.dp)
                )
            ) {
                Text(
                    it,
                    color = if (isSelected) Color.White else Color.Black
                )
            }
        }
    }
}

@Composable
fun LostFoundItemCard(item: LostFoundItem) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {


        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            Badge(
                text = item.type,
                color = if (item.type == "Lost") Color(0xFFFEE2E2) else Color(0xFFDCFCE7),
                textColor = if (item.type == "Lost") Color.Red else Color(0xFF15803D)
            )

            Badge(
                text = item.status,
                color = BlanchedAlmond,
                textColor = DarkOrange
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(item.title, fontWeight = FontWeight.Bold)
        Text(item.category, color = Color.Gray, fontSize = 13.sp)
        Text(item.description, fontSize = 13.sp)

        Spacer(modifier = Modifier.height(8.dp))

        InfoRow(R.drawable.baseline_location_on_24, item.location)
        InfoRow(R.drawable.baseline_calendar_today_24, item.date)
        InfoRow(R.drawable.baseline_person_24, item.reportedBy)

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { /* Edit */ }) {
                Text("Edit")
            }
            TextButton(onClick = { /* Delete */ }) {
                Text("Delete", color = Color.Red)
            }
        }
    }
}

@Composable
fun Badge(text: String, color: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .background(color, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 12.sp, color = textColor)
    }
}

@Composable
fun InfoRow(
    icon: Int,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.DarkGray
        )
    }
}


@Preview(showBackground = true)
@Composable
fun LostFoundPreview() {
    LostAndFoundScreen()
}