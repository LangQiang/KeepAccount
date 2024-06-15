package com.godq.cms.asset

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.godq.cms.R
import com.godq.cms.common.Loading
import com.godq.cms.common.MainTheme
import com.godq.cms.common.SwipeBackBox
import com.godq.cms.common.TitleBar
import com.lazylite.mod.fragmentmgr.FragmentOperation


/**
 * @author  GodQ
 * @date  2023/5/16 3:39 下午
 */

@Preview
@Composable
fun ViewPDFAssetItem() {
    PDFAssetItem(assetEntity = AssetEntity(AssetData(title = "ssssss"))) {

    }
}

@Preview
@Composable
fun ViewPicAssetItem() {
    PicAssetItem(assetEntity = AssetEntity(AssetData(title = "ssssss"))) {

    }
}

@Preview
@Composable
fun AssetFragmentScreen(assetViewModel: AssetViewModel = viewModel()) {

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(Unit) {
        lifecycleOwner.lifecycle.addObserver(assetViewModel)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(assetViewModel)
        }
    }

    val assetList = assetViewModel.assetList

    val state by assetViewModel.state

    val showFullPicUrl by assetViewModel.showFullPicUrl

    MainTheme {
        val modifier = if (showFullPicUrl.isNotEmpty()) Modifier.blur(15.dp) else Modifier
        SwipeBackBox(modifier = modifier, callback = { FragmentOperation.getInstance().close() }) {
            Column(modifier = Modifier
                .fillMaxSize()) {

                TitleBar(
                    title = "ASSET",
                    backTextResId = R.string.title_back_icon,
                    menuTextResId = R.string.asset_menu_str,
                    onBackClick = { FragmentOperation.getInstance().close() },
                    onMenuClick = { assetViewModel.onMenuClick() },
                )

                Loading(isLoading = state) {
                    AssetList(assets = assetList) {
                        assetViewModel.onItemClick(it)
                    }
                }
            }
        }
        if (showFullPicUrl.isNotEmpty()) {
            FullPicScreen(picUrl = showFullPicUrl) {
                assetViewModel.onFullPicClick()
            }
        }
    }
}

@Composable
fun FullPicScreen(picUrl: String, onFullPicClick: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxSize()
        .clickable {
            onFullPicClick()
        }) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = picUrl,
            contentDescription = null,
            contentScale = ContentScale.Inside,
        )
    }
}

@Composable
fun AssetList(assets: List<AssetEntity>, onTodoClick: (AssetEntity) -> Unit) {
    LazyColumn {
        items(assets, key = {it.assetData.id}) { asset ->
            val rememberAsset by remember { mutableStateOf(asset) }

            when (asset.assetData.format) {
                AssetData.FORMAT_PDF, AssetData.FORMAT_EXCEL, AssetData.FORMAT_WORD -> { PDFAssetItem(assetEntity = rememberAsset) { onTodoClick(rememberAsset) } }
                AssetData.FORMAT_PIC -> { PicAssetItem(assetEntity = rememberAsset) { onTodoClick(rememberAsset) } }
                AssetData.FORMAT_TEXT -> {}
                else -> {}
            }
        }
    }
}

@Composable
fun PicAssetItem(assetEntity: AssetEntity, onClick: () -> Unit) {
    Box(modifier = Modifier
        .clickable(onClick = onClick)
        .fillMaxWidth()
        .padding(15.dp, 12.dp)
        .height(78.dp)) {
        Surface(modifier = Modifier.shadow(1.dp, RoundedCornerShape(8.dp))) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp)
            ) {
                Box(modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .width(38.dp)
                    .height(50.dp)) {
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = assetEntity.assetData.url,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                }


                Box(modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(50.dp)
                    .weight(1f)) {
                    Text(
                        text = assetEntity.assetData.title,
                        modifier = Modifier
                            .padding(8.dp, 4.dp, 15.dp, 4.dp),
                        maxLines = 2,
                        fontSize = 16.sp
                    )
                }

            }
        }
    }
}

@Composable
fun PDFAssetItem(assetEntity: AssetEntity, onClick: () -> Unit) {
    Box(modifier = Modifier
        .clickable(onClick = onClick)
        .fillMaxWidth()
        .padding(15.dp, 12.dp)
        .height(78.dp)) {
        Surface(modifier = Modifier.shadow(1.dp, RoundedCornerShape(8.dp))) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp)
            ) {
                val painterResId = when (assetEntity.assetData.format) {
                    AssetData.FORMAT_PDF -> R.drawable.pdf_icon
                    AssetData.FORMAT_WORD -> R.drawable.word_icon
                    AssetData.FORMAT_EXCEL -> R.drawable.excel_icon
                    else -> R.drawable.pdf_icon
                }
                Image(modifier = Modifier
                    .height(50.dp)
                    .width(38.dp)
                    .align(Alignment.CenterVertically),
                    painter = painterResource(id = painterResId),
                    contentDescription = "pdf icon")

                Box(modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(50.dp)
                    .weight(1f)) {
                    Text(
                        text = assetEntity.assetData.title,
                        modifier = Modifier
                            .padding(8.dp, 4.dp, 15.dp, 4.dp),
                        maxLines = 2,
                        fontSize = 16.sp
                    )
                }

                Box(modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)) {
                    Text(
                        text = assetEntity.downloadState.value,
                        modifier = Modifier
                            .wrapContentSize()
                            .align(Alignment.Center),
                        color = Color.Black.copy(.8f)
                    )
                }

            }
        }
    }

}