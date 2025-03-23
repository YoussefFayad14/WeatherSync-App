package com.example.weathersync.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.weathersync.R
import com.example.weathersync.ui.components.SearchBar
import com.example.weathersync.ui.components.SearchResultItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var searchResults by remember { mutableStateOf(listOf<String>()) }
    val coroutineScope = rememberCoroutineScope()

    Row (
        modifier = Modifier.fillMaxSize()
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(vertical = 8.dp)
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = Color.White,
                modifier = Modifier
                    .size(32.dp)
                    .padding(start = 4.dp)
            )
        }
        SearchBar(
            searchQuery = searchQuery,
            onQueryChange = { newQuery ->
                searchQuery = newQuery
                coroutineScope.launch {
                    searchResults = if (newQuery.text.isNotEmpty()) {
                        TODO("Implement search logic")
                    } else {
                        emptyList()
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(searchResults.size) { index ->
                SearchResultItem(result = searchResults[index])
            }

        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SearchScreenPreview() {
    val fakeNavController = rememberNavController()
    SearchScreen(navController = fakeNavController)
}

