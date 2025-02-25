package com.example.xcamera.ui.camera

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.xcamera.ui.state.PhotoState
import com.example.xcamera.ui.state.VideoState
import java.io.File

@Composable
fun BottomSheetPhotoContents(
    photos: List<PhotoState>,
    videos : List<VideoState>,
    modifier: Modifier = Modifier
) {
    var photoOrVideoState by remember { mutableStateOf(false) }
    Row (
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround

    ){
        Text(
            modifier = Modifier
                .clickable {
                    photoOrVideoState = false
                },
            text = "photos",
            color = if (!photoOrVideoState ) Color.White else Color.LightGray
        )
        Text(
            modifier = Modifier
                .clickable {
                    photoOrVideoState = true
                },
            text = "videos",
            color = if (photoOrVideoState ) Color.White else Color.LightGray
        )

    }
    if(!photoOrVideoState ) {
        if (photos.isEmpty()){
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Photo's \n" +
                            "Click Some"
                )
            }
        } else {
            PhotosStateList(photos, modifier)
            Log.d("BottomSheet", "Videos list size: ${photos.size}")
        }

    }  else {
        if (videos.isEmpty()){

            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Video's \n" +
                            "Record Some"
                )

            }
        } else {
            Log.d("BottomSheet", "Videos list size: ${videos.size}")
            VideoStateList(videos, modifier)
        }
    }
}

@Composable
fun PhotosStateList(
    photos: List<PhotoState>,
    modifier: Modifier
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 16.dp,
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
    ) {
        items(photos) { photo ->
            AsyncImage(
                model = photo.photoPath?.let { File(it) },
                contentDescription = "Photos",
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun VideoStateList(
    videos : List<VideoState>,
    modifier: Modifier
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 16.dp,
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
    ) {
        items(videos) { video ->
            AsyncImage(
                model = video.videoPath?.let { File(it) },
                contentDescription = "Videos",
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }

}