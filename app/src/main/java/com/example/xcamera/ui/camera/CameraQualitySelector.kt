package com.example.xcamera.ui.camera

import androidx.camera.video.Quality
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CameraQualitySelector(
    modifier: Modifier = Modifier,
    quality : Quality,
    qualitySelector : Boolean,
    onQualitySelectorChange: (Boolean) -> Unit,
    onQualitySelected : (Quality) -> Unit
) {
    if (!qualitySelector) {
            Card(
                modifier = modifier
                    .padding(4.dp)
                    .clickable { onQualitySelectorChange(true) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),

            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = getQualityLabel(quality) ,
                        color = Color.White
                    )
                }
            }
        } else {
            val qualityMap = mapOf(
                "SD" to Quality.SD,
                "HD" to Quality.HD,
                "FHD" to Quality.FHD,
                "UHD" to Quality.UHD
            )

        Card(
            modifier = Modifier.padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                qualityMap.forEach { (label, qualityValue) ->
                    Text(
                        text = label,
                        color = Color.LightGray,
                        modifier = Modifier
                            .clickable {
                                onQualitySelected(qualityValue)  // Send selected quality
                                onQualitySelectorChange(false)  // Close the selector after choosing
                            }
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}


fun getQualityLabel(quality: Quality) : String {
    return when(quality) {
        Quality.FHD -> "FHD"
        Quality.HD -> "HD"
        Quality.SD-> "SD"
        Quality.UHD ->"UHD"
        else -> "unknown"
    }
}


@Preview(showBackground = false)
@Composable
fun QualitySelectorPreview() {
    CameraQualitySelector(
        quality = Quality.FHD,
        qualitySelector = false,
        onQualitySelectorChange = {true},
        onQualitySelected = {

        }
    )
}