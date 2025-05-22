package de.hbch.spacestate.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.hbch.spacestate.R
import de.hbch.spacestate.ui.composables.Search
import de.hbch.spacestate.ui.composables.SpaceListItem
import de.hbch.spacestate.ui.theme.SpaceStateTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpaceStateTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        Search()
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_add),
                                contentDescription = null
                            )
                        }
                    }
                ) { innerPadding ->
                    val spaces = remember { mutableStateListOf<Pair<String, String>>() }
                    LaunchedEffect(true) {
                        if (spaces.isEmpty()) {
                            spaces.add(Pair("muCCC", "https://api.muc.ccc.de/spaceapi.json"))
                            spaces.add(Pair("Entropia", "https://club.entropia.de/spaceapi"))
                            spaces.add(Pair("Umeå Hackerspace", "https://umeahackerspace.se/spaceapi.json"))
                        }
                    }
                    LazyColumn(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = spaces,
                            key = { it.first }
                        ) { space ->
                            SpaceListItem(
                                space.first,
                                space.second,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            Box(
                                modifier = Modifier.height(64.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
