package com.example.clickretina_assignment.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun AuthHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(ArcShape())
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))
                )
            )
    )
}

class ArcShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height * 0.75f)
            quadraticBezierTo(
                x1 = size.width / 2,
                y1 = size.height,
                x2 = 0f,
                y2 = size.height * 0.75f
            )
            close()
        }
        return Outline.Generic(path)
    }
}
