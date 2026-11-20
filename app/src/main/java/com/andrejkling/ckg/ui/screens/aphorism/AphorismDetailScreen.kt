package com.andrejkling.ckg.ui.screens.aphorism

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.view.HapticFeedbackConstants
import com.andrejkling.ckg.model.Aphorism
import com.andrejkling.ckg.ui.theme.BlueTop
import com.andrejkling.ckg.ui.theme.SavedRed
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// One aphorism on a card, like the iOS DetailView: Back and Save, date, card with number, text, share.
// Swipe left or right to move through the list.
@Composable
fun AphorismDetailScreen(
    aphorisms: List<Aphorism>,
    startIndex: Int,
    isSaved: (Aphorism) -> Boolean,
    onToggleSave: (Aphorism) -> Unit,
    onBack: () -> Unit
) {
    var index by remember { mutableIntStateOf(startIndex) }
    var appeared by remember { mutableStateOf(false) }
    var drag by remember { mutableFloatStateOf(0f) }
    val context = LocalContext.current
    val view = LocalView.current
    val current = aphorisms[index]
    val saved = isSaved(current)

    BackHandler(onBack = onBack)
    LaunchedEffect(Unit) { appeared = true }

    val dateText = remember {
        LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.getDefault()))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(top = 16.dp, bottom = 40.dp)
    ) {
        // Back and Save
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 25.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.clickable(onClick = onBack),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                Spacer(Modifier.width(4.dp))
                Text("Back", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier.clickable {
                    onToggleSave(current)
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (saved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (saved) "Saved" else "Save",
                    tint = if (saved) SavedRed else Color.White
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = if (saved) "Saved" else "Save",
                    color = if (saved) SavedRed else Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        Text(
            text = dateText,
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 15.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(28.dp))

        // The card: centred when it fits, scrolls when the text is long
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 25.dp)
                .verticalScroll(rememberScrollState())
                .pointerInput(aphorisms.size) {
                    detectHorizontalDragGestures(
                        onDragStart = { drag = 0f },
                        onDragEnd = {
                            if (drag < -40f && index < aphorisms.size - 1) index++
                            else if (drag > 40f && index > 0) index--
                        },
                        onHorizontalDrag = { _, amount -> drag += amount }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            val entrance by animateFloatAsState(
                targetValue = if (appeared) 1f else 0f,
                animationSpec = tween(500),
                label = "entrance"
            )
            AnimatedContent(
                targetState = index,
                transitionSpec = {
                    (fadeIn(tween(250)) + scaleIn(initialScale = 0.96f, animationSpec = tween(250)))
                        .togetherWith(fadeOut(tween(150)))
                },
                label = "aphorism-card",
                modifier = Modifier.graphicsLayer {
                    alpha = entrance
                    val s = 0.94f + 0.06f * entrance
                    scaleX = s
                    scaleY = s
                }
            ) { i ->
                AphorismCard(
                    aphorism = aphorisms[i],
                    onShare = {
                        val send = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, aphorisms[i].text)
                        }
                        context.startActivity(Intent.createChooser(send, null))
                    }
                )
            }
        }
    }
}

@Composable
private fun AphorismCard(aphorism: Aphorism, onShare: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 22.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = Color(0x8C8CBFFF),
                spotColor = Color(0x8C8CBFFF)
            )
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "№ ${aphorism.number}",
            color = BlueTop,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(BlueTop.copy(alpha = 0.12f))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        )
        Text(
            text = aphorism.text,
            color = Color.Black.copy(alpha = 0.85f),
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            lineHeight = 30.sp,
            textAlign = TextAlign.Center
        )
        Box(Modifier.width(40.dp).height(2.dp).background(BlueTop.copy(alpha = 0.35f)))
        Text(
            text = "Swipe left or right for more",
            color = BlueTop.copy(alpha = 0.9f),
            fontSize = 13.sp
        )
        Text(
            text = "Share",
            color = BlueTop,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            modifier = Modifier.clickable(onClick = onShare)
        )
    }
}
