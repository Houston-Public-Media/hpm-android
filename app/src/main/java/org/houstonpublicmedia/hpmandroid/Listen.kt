package org.houstonpublicmedia.hpmandroid

import android.graphics.ColorFilter
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.houstonpublicmedia.hpmandroid.ui.theme.HPM_Background_Light
import org.houstonpublicmedia.hpmandroid.ui.theme.HPM_Blue_Secondary
import org.houstonpublicmedia.hpmandroid.ui.theme.HPM_Gray
import org.houstonpublicmedia.hpmandroid.ui.theme.HPM_White

@Composable
fun ListenScreen(data: StationData) {
    Column(
        modifier = Modifier
            .padding(all = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Live Streams",
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
            fontWeight = FontWeight.Bold
        )
        data.streams?.audio?.forEach { station ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { Log.d("Listen Screen", "Play " + station.name + " Stream") }
                    .padding(start = 0.dp, end = 0.dp, top = 0.dp, bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .border(1.dp, HPM_Gray, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .background(HPM_White),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = station.artwork,
                        contentDescription = station.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(75.dp)
                            .padding(all = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = station.name,
                            color = HPM_Blue_Secondary,
                            fontSize = 16.sp,
                            fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                            modifier = Modifier
                                .padding(start = 0.dp, end = 8.dp, top = 8.dp, bottom = 2.dp)
                        )
                        Text(
                            text = nowPlayingCleanup(data.nowPlaying?.radio[station.id]),
                            color = HPM_Blue_Secondary,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .padding(start = 0.dp, end = 8.dp, top = 2.dp, bottom = 8.dp)
                        )
                    }
                    Icon(
                        painter = painterResource(id = R.drawable.play_arrow_24px),
                        contentDescription = "Play " + station.name + " Stream",
                        tint = HPM_Blue_Secondary,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
        Text(
            "Podcasts",
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
            fontWeight = FontWeight.Bold
        )
        data.podcasts?.list?.forEach { podcast ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { Log.d("Listen Screen", "Go to " + podcast.name + " episode list") }
                    .padding(start = 0.dp, end = 0.dp, top = 0.dp, bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .border(1.dp, HPM_Gray, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .background(HPM_White),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = podcast.image.thumbnail.url,
                        contentDescription = podcast.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(75.dp)
                            .padding(all = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Text(
                        text = podcast.name,
                        color = HPM_Blue_Secondary,
                        fontSize = 16.sp,
                        fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                        modifier = Modifier
                            .padding(start = 0.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
                            .weight(1f)
                    )
                    Icon(
                        painter = painterResource(R.drawable.chevron_right_24px),
                        contentDescription = "Go to " + podcast.name + " episode list",
                        tint = HPM_Blue_Secondary,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}

fun nowPlayingCleanup(nowPlaying: NowPlayingStation?): String {
    var output = ""
    if (nowPlaying?.artist?.contains("Houston Public Media") ?: false) {
        output += nowPlaying.title
    } else {
        output += nowPlaying?.artist + " - " + nowPlaying?.title
    }
    return output
}