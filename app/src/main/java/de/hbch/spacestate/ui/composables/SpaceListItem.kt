package de.hbch.spacestate.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import de.hbch.spacestate.R
import de.hbch.spacestate.ui.theme.Typography
import io.spaceapi.parseString
import io.spaceapi.types.Status
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.gildor.coroutines.okhttp.await

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
        var isLoading by remember { mutableStateOf(false) }
        var isError by remember { mutableStateOf(false) }

        LaunchedEffect(spaceState) {
            if (spaceState == null && !isLoading) {
                isLoading = true
                isError = false
                val request = Request.Builder()
                    .get()
                    .addHeader("Accept", "application/json")
                    .addHeader("User-Agent", "SpaceState/1.0.0")
                    .url(spaceApiEndpoint)
                    .build()
                val client = OkHttpClient.Builder()
                    .build()

                try {
                    val response = client.newCall(request).await()
                    if (response.isSuccessful) {
                        val statusJson = response.body?.string() ?: ""
                        spaceState = parseString(statusJson)
                    } else {
                        isError = true
                    }
                } catch (_: Exception) {
                    isError = true
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
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeCap = StrokeCap.Round,
                    strokeWidth = 4.dp
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (spaceState?.state?.open == true) R.drawable.ic_open else R.drawable.ic_closed
                        ),
                        contentDescription = null
                    )
                    val message = spaceState?.state?.message
                    AnimatedVisibility(message != null) {
                        Text(
                            text = message!!,
                            style = Typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}
