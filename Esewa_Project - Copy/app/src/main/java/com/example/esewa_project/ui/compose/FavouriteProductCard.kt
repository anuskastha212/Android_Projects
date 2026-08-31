package com.example.esewa_project.ui.compose

import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.example.esewa_project.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.esewa_project.data.local.entity.ProductEntity
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class SwipeState {
    CLOSED,
    OPEN
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavouriteProductCard(
    product: ProductEntity,
    isSelected: Boolean,
    onCheckoutClick: () -> Unit,
    onProductClick: () -> Unit,
    onDeleteSwipe: () -> Unit
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val deleteWidth = 120.dp
    val deleteWidthPx = with(density) { deleteWidth.toPx() }

    val anchors = DraggableAnchors<SwipeState> {
        SwipeState.CLOSED at 0f
        SwipeState.OPEN at -deleteWidthPx
    }

    val state = remember {
        AnchoredDraggableState(
            initialValue = SwipeState.CLOSED,
            anchors = anchors,
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            snapAnimationSpec = tween(),
            decayAnimationSpec = exponentialDecay()
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color(0xFFF1F1F5)),
            contentAlignment = Alignment.CenterEnd
        ) {
            Box(
                modifier = Modifier
                    .width(deleteWidth)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        onDeleteSwipe()
                        scope.launch {
                            state.animateTo(SwipeState.CLOSED)
                        }
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color(0xFFFF7F6A), shape = CircleShape)
                ) {
                    Icon(
                        painter = painterResource(id= R.drawable.delete),
                        contentDescription = "Delete Item",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(x = state.requireOffset().roundToInt(), y = 0) }
                .anchoredDraggable(state, Orientation.Horizontal)
        ) {
            FavouriteProductContent(
                product = product,
                isSelected = isSelected,
                onCheckoutClick = onCheckoutClick,
                onProductClick = onProductClick
            )
        }
    }
}