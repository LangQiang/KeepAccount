package com.godq.cms.asset.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godq.cms.R
import com.godq.cms.common.MainTheme
import com.godq.cms.common.SwipeBackBox
import com.godq.cms.common.TitleBar
import com.lazylite.mod.fragmentmgr.FragmentOperation


/**
 * @author  GodQ
 * @date  2023/5/18 4:29 下午
 */

@Preview
@Composable
fun AddAssetScreen(viewModel: AddAssetViewModel = AddAssetViewModel()) {
    MainTheme {
        SwipeBackBox(callback = { FragmentOperation.getInstance().close() }) {
            Column(modifier = Modifier
                .fillMaxSize()
                .background(Color.White)) {
                TitleBar(title = "添加", backTextResId = R.string.title_back_icon)
                Content(modifier = Modifier.fillMaxSize(), viewModel)
            }
        }
    }
}

@Composable
fun Content(modifier: Modifier = Modifier, viewModel: AddAssetViewModel) {

    val progress by viewModel.progress
    val uploadState by viewModel.uploadState
    val title by viewModel.title
    val belong by viewModel.belong
    val format by viewModel.format
    val url by viewModel.url
    val extra by viewModel.extra


    Column(modifier = modifier) {
        Column(modifier = modifier
            .weight(1f)
            .padding(15.dp, 0.dp)
            .fillMaxWidth()) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .align(Alignment.CenterVertically)
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .align(Alignment.Center)
                            .wrapContentHeight(),
                        progress = progress,
                        color = Color.Red.copy(alpha = 0.6f),
                        backgroundColor = Color.Gray,
                    )

                    Text(
                        modifier = Modifier,
                        text = when (uploadState) {
                            AddAssetViewModel.UPLOAD_STATE_FINISH_SUC -> {
                                "上传成功！"
                            }
                            AddAssetViewModel.UPLOAD_STATE_FINISH_FAIL -> {
                                "上传失败！"
                            }
                            else -> {
                                ""
                            } },
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .wrapContentSize()
                        .padding(15.dp, 0.dp)
                        .background(colorResource(id = R.color.skin_high_color), RoundedCornerShape(4.dp))
                ) {
                    Text(
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(15.dp, 6.dp)
                            .clickable { viewModel.onUploadClick() },
                        text = when(uploadState) {
                            AddAssetViewModel.UPLOAD_STATE_UPLOADING -> { "取消" }
                            else -> { "上传" } },
                        textAlign = TextAlign.Center,
                        color = Color.White,
                    )
                }
            }

            if (uploadState == AddAssetViewModel.UPLOAD_STATE_FINISH_SUC) {
                Box(modifier = Modifier.padding(0.dp, 20.dp)
                    .fillMaxWidth()){
                    OutlinedTextField(
                        value = title,
                        onValueChange = {viewModel.title.value = it},
                        label = { Text(text = "Title") }
                    )
                }

                Box(modifier = Modifier.padding(0.dp, 20.dp)
                    .fillMaxWidth()){
                    OutlinedTextField(
                        value = belong,
                        onValueChange = {viewModel.belong.value = it},
                        label = { Text(text = "Belong") }
                    )
                }

                Box(modifier = Modifier.padding(0.dp, 20.dp)
                    .fillMaxWidth()){
                    OutlinedTextField(
                        value = format,
                        onValueChange = {viewModel.format.value = it},
                        label = { Text(text = "Format(PDF/PIC)") }
                    )
                }

                Box(modifier = Modifier.padding(0.dp, 20.dp)
                    .fillMaxWidth()){
                    OutlinedTextField(
                        value = url,
                        enabled = false,
                        onValueChange = {viewModel.url.value = it},
                        label = { Text(text = "Url") }
                    )
                }

                Box(modifier = Modifier.padding(0.dp, 20.dp)
                    .fillMaxWidth()){
                    OutlinedTextField(
                        value = extra,
                        onValueChange = {viewModel.extra.value = it},
                        label = { Text(text = "Extra") }
                    )
                }
            }
        }



        Box(modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(15.dp, 20.dp)
            .clickable(enabled = uploadState == AddAssetViewModel.UPLOAD_STATE_FINISH_SUC) {
                viewModel.onCommitClick()
            }
            .background(
                when (uploadState) {
                    AddAssetViewModel.UPLOAD_STATE_FINISH_SUC -> {
                        colorResource(id = R.color.skin_high_blue_color)
                    }
                    else -> {
                        Color.Gray
                    }
                }, RoundedCornerShape(30.dp)
            )
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "提交",
                color = Color.White,
                fontSize = 24.sp,
            )
        }
    }

}