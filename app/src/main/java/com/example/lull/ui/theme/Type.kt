package com.example.lull.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.lull.R


val Poppins = FontFamily(
    Font(R.font.poppins_regular),
    Font(R.font.poppins_extrabold, weight = FontWeight.ExtraBold),
    Font(R.font.poppins_light, weight = FontWeight.Light)
)
// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp
    ),
    h1 = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 23.sp,
        letterSpacing = 2.sp,
        color = DarkGray
    ),
    h2 = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 19.sp,
        letterSpacing = 1.2.sp,
        color = Gray
    )
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)