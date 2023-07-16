package com.godq.cms.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godq.cms.R


/**
 * @author  GodQ
 * @date  2023/5/16 2:36 下午
 */

/**
 * 主题
 */

object NoIndication : Indication {
    private object NoIndicationInstance: IndicationInstance {
        override fun ContentDrawScope.drawIndication() {
            drawContent()
        }
    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        return NoIndicationInstance
    }

}

@Composable
fun MainTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        CompositionLocalProvider(LocalIndication provides NoIndication) {
            Box(modifier = Modifier) {
                content()
            }
        }
    }
}


/**
 * 侧滑退出rootView
 * */
@Composable
fun SwipeBackBox(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    callback: () -> Unit,
    content: @Composable () -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val anchorPosition = screenWidth * 0.5f
    var isDragging by remember { mutableStateOf(false) }
    var isFinishDrag by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }

    modifier.pointerInput(Unit) {
        detectDragGestures(onDragStart = {
            isDragging = true
        }, onDrag = { change, dragAmount ->
            if (dragPosition.x >= 0f) {
                dragPosition = dragAmount + dragPosition
                if (dragPosition.x >= anchorPosition.toPx()) {
                    isFinishDrag = true
                    change.consume()
                }
            }
        }, onDragEnd = {
            if (isFinishDrag) {
                callback()
            }
            dragPosition = Offset.Zero
            isDragging = false
            isFinishDrag = false
        })
    }.then(
        if (isDragging) {
            modifier.offset { IntOffset(dragPosition.x.toInt(), 0) }
        } else {
            modifier.offset { IntOffset.Zero }
        }
    ).run {
        Box(modifier = this, contentAlignment = contentAlignment) {
            Image(
                painter = painterResource(R.drawable.skin_common_main_bg),
                contentScale = ContentScale.FillBounds,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
            content()
        }
    }
}

typealias ClickCallback = () -> Unit

@Composable
fun TitleBar(
    modifier: Modifier = Modifier,
    title: String = "",
    backIconResId: Int = 0,
    backTextResId: Int = 0,
    menuIconResId: Int = 0,
    menuTextResId: Int = 0,
    ignoreStatusBarPadding: Boolean = false,
    onBackClick: ClickCallback = {},
    onMenuClick: ClickCallback = {},
) {
    val paddingModifier = if (ignoreStatusBarPadding) modifier else modifier.statusBarsPadding()

    Box(modifier = paddingModifier
        .fillMaxWidth()
        .wrapContentHeight()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
        ) {
            if (backTextResId > 0) {
                Text(
                    text = stringResource(id = backTextResId),
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .padding(15.dp, 0.dp)
                        .align(Alignment.CenterStart)
                        .clickable(onClick = onBackClick),
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontFamily = FontFamily(Font(R.font.iconfont))
                )
            } else if (backIconResId > 0) {
                IconButton(modifier = Modifier.align(Alignment.CenterStart), onClick = { onBackClick() }) {
                    Icon(painterResource(id = backIconResId), contentDescription = null)
                }
            } else {
                //
            }

            if (menuTextResId > 0) {
                Text(
                    text = stringResource(id = menuTextResId),
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .padding(15.dp, 0.dp)
                        .align(Alignment.CenterEnd)
                        .clickable(onClick = onMenuClick),
                    color = Color.Black,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.iconfont))
                )
            } else if (menuIconResId > 0) {
                IconButton(modifier = Modifier.align(Alignment.CenterEnd), onClick = { onMenuClick() }) {
                    Icon(painterResource(id = menuIconResId), contentDescription = null)
                }
            }


            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.Center),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun Loading(
    isLoading: Boolean,
    content: @Composable () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        content()
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {}
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ImageView(url: String, size: Size) {
}
