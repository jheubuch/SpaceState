package de.hbch.spacestate.ui.composables

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.hbch.spacestate.R
import de.hbch.spacestate.shared.NetworkClient
import de.hbch.spacestate.shared.prepareRequest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.Request
import ru.gildor.coroutines.okhttp.await

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Search(
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState()
    val spaceApiDirectory = remember { mutableStateMapOf<String, String>() }
    val filtered by remember {
        derivedStateOf {
            spaceApiDirectory
                .filter { kvp -> kvp.key.contains(textFieldState.text, true) }
                .toSortedMap()
        }
    }
    //val filtered = spaceApiDirectory.filter { kvp -> kvp.key.contains(textFieldState.text, true) }.toSortedMap()
    var initialized by remember { mutableStateOf(false) }
    var directoryLoading by remember { mutableStateOf(true) }
    var directoryError by remember { mutableStateOf(false) }

    val searchBarInputField =
        @Composable {
            SearchBarDefaults.InputField(
                modifier = modifier,
                searchBarState = searchBarState,
                textFieldState = textFieldState,
                onSearch = { coroutineScope.launch { searchBarState.animateToCollapsed() } },
                placeholder = {
                    Text(
                        text = "Search...",
                        textAlign = TextAlign.Center
                    )
                },
                trailingIcon = {
                    if (directoryLoading) {
                        ContainedLoadingIndicator()
                    }
                },
                leadingIcon = {
                    if (searchBarState.targetValue == SearchBarValue.Expanded) {
                        IconButton(
                            onClick = { coroutineScope.launch { searchBarState.animateToCollapsed() } }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_back),
                                contentDescription = null
                            )
                        }
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()

    LaunchedEffect(spaceApiDirectory) {
        if (!initialized) {
            initialized = true
            directoryLoading = true
            spaceApiDirectory.clear()
            val request = Request.Builder()
                .prepareRequest("https://directory.spaceapi.io")
                .build()
            try {
                val response = NetworkClient.newCall(request).await()
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: ""
                    val dataMap: Map<String, String> = Json.decodeFromString(body)
                    spaceApiDirectory += dataMap
                    Log.d("Directory", spaceApiDirectory.toString())
                } else {
                    directoryError = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
                directoryError = true
            }
            directoryLoading = false
        }
    }

    TopSearchBar(
        state = searchBarState,
        inputField = searchBarInputField,
        scrollBehavior = scrollBehavior
    )
    ExpandedFullScreenSearchBar(
        state = searchBarState,
        inputField = searchBarInputField
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            filtered.forEach { name, url ->
                item(key = name) {
                    SpaceListItem(name, url, modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}
