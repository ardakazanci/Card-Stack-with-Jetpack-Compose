package com.ardakazanci.cardstack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.ardakazanci.cardstack.ui.theme.CardStackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CardStackTheme {
                CardStack()
            }
        }
    }
}

@Composable
fun CardStack() {
    var stackedState by remember { mutableStateOf(false) }
    val cardRotation by animateFloatAsState(targetValue = if (stackedState) 10f else 0f, label = "")
    val cardOffsetY by animateDpAsState(targetValue = if (stackedState) 20.dp else 0.dp, label = "")
    val cardScale by animateFloatAsState(targetValue = if (stackedState) 0.95f else 1f, label = "")

    val density = LocalDensity.current
    val numberOfCards = 4
    val cardColors =
        listOf(Color(0XFFFFBE0B), Color(0XFFFB5607), Color(0XFFFF006E), Color(0XFF8338EC))
    var selectedCard by remember { mutableIntStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0XFFbb92d4))
            .padding(top = 120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { stackedState = !stackedState }) {
            Text("Stack Cards")
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0XFFbb92d4))
        ) {
            for (i in numberOfCards - 1 downTo 0) {
                val colors = CardDefaults.cardColors(containerColor = cardColors[i])


                val offsetY = animateDpAsState(
                    targetValue = if (i == selectedCard) (-75).dp else (i * 16).dp,
                    animationSpec = spring()
                )


                val interactionSource = remember { MutableInteractionSource() }
                val noRippleClickable = Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { selectedCard = i }
                )


                RotatingCard(
                    modifier = Modifier
                        .offset(y = offsetY.value)
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 32.dp, vertical = 64.dp / (i + 1))
                        .graphicsLayer {

                            rotationX = cardRotation

                            translationY = with(density) { cardOffsetY.toPx() * i }
                            scaleX = cardScale
                            scaleY = cardScale
                        }
                        .offset(y = offsetY.value)
                        .then(noRippleClickable),
                ) {
                    Card(
                        colors = colors,
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = "Card Stack ${i + 1}",
                            modifier = Modifier.padding(16.dp),
                            color = Color.White
                        )
                    }
                }
            }
        }

    }


}

@Composable
fun RotatingCard(modifier: Modifier = Modifier, card: @Composable () -> Unit) {
    var rotation by remember { mutableStateOf(0f) }

    Layout(
        content = card,
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress { change, dragAmount ->
                    rotation += dragAmount.x * 0.1f
                    change.consume()
                }
            }
    ) { measurables, constraints ->
        val placeable = measurables.first().measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)
        }
    }
}

