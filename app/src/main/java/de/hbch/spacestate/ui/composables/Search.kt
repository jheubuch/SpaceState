package de.hbch.spacestate.ui.composables

import android.util.Log
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import de.hbch.spacestate.shared.getClient
import de.hbch.spacestate.shared.prepareRequest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.Request
import ru.gildor.coroutines.okhttp.await

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Search() {
    val coroutineScope = rememberCoroutineScope()
    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState()
    val spaceApiDirectory = remember { mutableStateMapOf<String, String>() }
    var initialized by remember { mutableStateOf(false) }
    var directoryLoading by remember { mutableStateOf(true) }
    var directoryError by remember { mutableStateOf(false) }

    val searchBarInputField =
        @Composable {
            SearchBarDefaults.InputField(
                modifier = Modifier,
                searchBarState = searchBarState,
                textFieldState = textFieldState,
                onSearch = { coroutineScope.launch { searchBarState.animateToCollapsed() } },
                placeholder = { Text("Search...") },
                trailingIcon = {
                    if (directoryLoading) {
                        LoadingIndicator()
                    }
                }
            )
        }
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()

    LaunchedEffect(initialized) {
        if (!initialized) {
            initialized = true
            directoryLoading = true
            spaceApiDirectory.clear()
            val client = getClient()
            val request = Request.Builder()
                .prepareRequest("https://directory.spaceapi.io")
                .build()
            try {
                val response = client.newCall(request).await()
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
    ) { }
}
