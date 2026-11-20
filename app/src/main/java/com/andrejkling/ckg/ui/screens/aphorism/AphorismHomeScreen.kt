package com.andrejkling.ckg.ui.screens.aphorism

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andrejkling.ckg.R
import com.andrejkling.ckg.model.Aphorism
import com.andrejkling.ckg.ui.theme.BlueBottom
import com.andrejkling.ckg.ui.theme.BlueTop
import com.andrejkling.ckg.ui.theme.SavedRed
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// Start screen, like the iOS Home view: header, date, saved pill, today's aphorism, "Show me another".
@Composable
fun AphorismHomeScreen(
    todays: Aphorism?,
    onOpenTodays: () -> Unit,
    onShowAnother: () -> Unit,
    onOpenSaved: () -> Unit
) {
    val dateText = remember {
        LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.getDefault()))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(top = 30.dp, bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header: guru picture above the title
        Image(
            painter = painterResource(R.drawable.guru1),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(56.dp).clip(CircleShape)
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Aphorisms",
            color = Color.White,
            style = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 22.sp, letterSpacing = 3.3.sp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = dateText,
            color = Color.White.copy(alpha = 0.8f),
            style = TextStyle(fontSize = 15.sp)
        )
        Spacer(Modifier.height(16.dp))

        // Saved pill (opens the saved list)
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color.White)
                .clickable(onClick = onOpenSaved)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "♥", color = SavedRed, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(
                text = "My saved aphorisms",
                color = SavedRed,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }

        Spacer(Modifier.height(40.dp))

        // Today's aphorism, a preview that opens in full
        if (todays != null) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 25.dp)
                    .fillMaxWidth()
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(18.dp), ambientColor = Color(0x5973B2FF), spotColor = Color(0x5973B2FF))
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White)
                    .clickable(onClick = onOpenTodays)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "TODAY'S APHORISM",
                    color = BlueTop,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = todays.text,
                    color = Color.Black.copy(alpha = 0.85f),
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Medium,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Show me another (random aphorism)
        Column(
            modifier = Modifier
                .padding(horizontal = 25.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(15.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF051A6B), Color(0xFF0F2A85))))
                .clickable(onClick = onShowAnother)
                .padding(vertical = 17.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Show me another", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
        }
    }
}
