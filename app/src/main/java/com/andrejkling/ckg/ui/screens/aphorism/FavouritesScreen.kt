package com.andrejkling.ckg.ui.screens.aphorism

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andrejkling.ckg.model.Aphorism
import com.andrejkling.ckg.ui.theme.BlueTop
import com.andrejkling.ckg.ui.theme.SavedRed

// Saved aphorisms, like the iOS FavouriteView: a card per aphorism with Share and Remove.
@Composable
fun FavouritesScreen(
    favorites: List<Aphorism>,
    onRemove: (Aphorism) -> Unit,
    onDone: () -> Unit
) {
    BackHandler(onBack = onDone)
    val context = LocalContext.current
    var pendingRemoval by remember { mutableStateOf<Aphorism?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Title bar: Done on the right, like the iOS navigation bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.weight(1f))
            Text(
                text = "Saved Aphorisms",
                color = Color.White,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "Done",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.clickable(onClick = onDone).padding(4.dp)
            )
        }

        if (favorites.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(40.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Filled.Favorite, contentDescription = null, tint = Color.White.copy(alpha = 0.35f), modifier = Modifier.size(60.dp))
                    Text("No Favorites Yet", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 22.sp)
                    Text(
                        "Save your favorite aphorisms to see them here",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(favorites, key = { "${it.number}-${it.text.hashCode()}" }) { aphorism ->
                    FavouriteCard(
                        aphorism = aphorism,
                        onShare = {
                            val send = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, aphorism.text)
                            }
                            context.startActivity(Intent.createChooser(send, null))
                        },
                        onRemove = { pendingRemoval = aphorism }
                    )
                }
            }
        }
    }

    pendingRemoval?.let { a ->
        AlertDialog(
            onDismissRequest = { pendingRemoval = null },
            title = { Text("Remove Favorite") },
            text = { Text("Are you sure you want to remove this aphorism from your favorites?") },
            confirmButton = {
                TextButton(onClick = {
                    onRemove(a)
                    pendingRemoval = null
                }) { Text("Remove", color = SavedRed) }
            },
            dismissButton = {
                TextButton(onClick = { pendingRemoval = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun FavouriteCard(aphorism: Aphorism, onShare: () -> Unit, onRemove: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Text("“", color = BlueTop.copy(alpha = 0.4f), fontFamily = FontFamily.Serif, fontSize = 32.sp, modifier = Modifier.fillMaxWidth())
        Text(
            text = aphorism.text,
            color = Color.Black.copy(alpha = 0.85f),
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Medium,
            fontSize = 17.sp,
            lineHeight = 26.sp,
            textAlign = TextAlign.Center
        )
        Text("”", color = BlueTop.copy(alpha = 0.4f), fontFamily = FontFamily.Serif, fontSize = 32.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)

        Spacer(Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onShare)) {
                Text("↑", color = BlueTop, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Text("Share", color = Color.Gray, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onRemove)) {
                Text("♥", color = SavedRed, fontSize = 20.sp)
                Text("Remove", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}
