package com.example.submanager.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submanager.ui.screens.viewModel.HomeActions

@Composable
fun SubscriptionListHeader(
    currentFilterName: String,
    actions: HomeActions
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = currentFilterName,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )

        Box {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filtra",
                    tint = Color(0xFF60A5FA)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Tutti") },
                    onClick = {
                        actions.showAll()
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("In scadenza") },
                    onClick = {
                        actions.showExpiring()
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Nome A-Z") },
                    onClick = {
                        actions.sortByNameAsc()
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Nome Z-A") },
                    onClick = {
                        actions.sortByNameDesc()
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Prezzo crescente") },
                    onClick = {
                        actions.sortByPriceAsc()
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Prezzo decrescente") },
                    onClick = {
                        actions.sortByPriceDesc()
                        expanded = false
                    }
                )
            }
        }
    }
}