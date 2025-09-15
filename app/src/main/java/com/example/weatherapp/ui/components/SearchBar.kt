package com.example.weatherapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.weatherapp.ui.theme.WeatherAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchText by remember { mutableStateOf("") }
    
    OutlinedTextField(
        value = searchText,
        onValueChange = { searchText = it },
        label = { Text("Search city") },
        trailingIcon = {
            IconButton(
                onClick = { 
                    if (searchText.isNotBlank()) {
                        onSearch(searchText.trim())
                    }
                }
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 100)
@Composable
fun SearchBarPreview() {
    WeatherAppTheme {
        SearchBar(onSearch = { })
    }
}