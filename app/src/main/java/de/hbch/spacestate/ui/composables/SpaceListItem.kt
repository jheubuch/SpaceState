package de.hbch.spacestate.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.hbch.spacestate.R
import de.hbch.spacestate.shared.NetworkClient
import de.hbch.spacestate.shared.prepareRequest
import de.hbch.spacestate.ui.theme.Typography
import io.spaceapi.parseString
import io.spaceapi.types.Status
import okhttp3.Request
import ru.gildor.coroutines.okhttp.await

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SpaceListItem(
    spaceName: String,
    spaceApiEndpoint: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        var spaceState by remember { mutableStateOf<Status?>(null) }
        var isLoading by rememberSaveable { mutableStateOf(false) }
        var isError by rememberSaveable { mutableStateOf(false) }
        var error by rememberSaveable { mutableStateOf("") }

        LaunchedEffect(spaceState) {
            if (spaceState == null && !isLoading) {
                isLoading = true
                isError = false
                val request = Request.Builder()
                    .prepareRequest(spaceApiEndpoint)
                    .build()

                try {
                    val response = NetworkClient.newCall(request).await()
                    if (response.isSuccessful) {
                        val statusJson = response.body?.string() ?: ""
                        spaceState = parseString(statusJson)
                    } else {
                        isError = true
                    }
                } catch (e: Exception) {
                    isError = true
                    error = e.message ?: ""
                }
                isLoading = false
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = spaceState?.space ?: spaceName,
                style = Typography.bodyLarge
            )
            if (isLoading) {
                LoadingIndicator(
                    modifier = Modifier.size(24.dp)
                )
            } else {
                if (spaceState != null || isError) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val icon = if (isError) {
                            R.drawable.ic_error
                        } else if (spaceState?.state?.open == true) {
                            R.drawable.ic_open
                        } else {
                            R.drawable.ic_closed
                        }
                        Icon(
                            painter = painterResource(
                                id = icon
                            ),
                            contentDescription = null,
                            tint = if (isError) Color.Red else LocalContentColor.current
                        )
                        val message = spaceState?.state?.message
                        AnimatedVisibility(message != null) {
                            Text(
                                text = message!!,
                                style = Typography.labelSmall,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }
    }
}
