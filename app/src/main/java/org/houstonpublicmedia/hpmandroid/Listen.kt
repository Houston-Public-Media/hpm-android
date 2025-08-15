package org.houstonpublicmedia.hpmandroid

import android.media.session.PlaybackState
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
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
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage

@Composable
fun ListenScreen(data: StationData, audioManager: AudioManager, navController: NavHostController) {
    Column(
        modifier = Modifier
            .padding(all = 0.dp)
            .verticalScroll(rememberScrollState())
    ) {
        DayWeather(data)
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 0.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Live Streams",
                modifier = Modifier.padding(start = 0.dp, end = 0.dp, top = 8.dp, bottom = 0.dp),
                fontWeight = FontWeight.Bold
            )
            data.streams?.audio?.forEach { station ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (audioManager.playerState?.isPlaying == true && audioManager.currentStation == station.id) {
                                audioManager.pause()
                            } else {
                                audioManager.startAudio(
                                    audioType = AudioManager.AudioType.stream,
                                    station = station,
                                    nowPlaying = data.nowPlaying?.radio[station.id],
                                    episode = null
                                )
                                audioManager.currentStation = station.id
                                audioManager.audioType = AudioManager.AudioType.stream
                                audioManager.currentEpisode = null
                            }
                        }
                        .border(1.dp, colorScheme.outline, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorScheme.surfaceBright),
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
                            color = colorScheme.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 18.sp,
                            modifier = Modifier
                                .padding(start = 0.dp, end = 8.dp, top = 8.dp, bottom = 2.dp)
                        )
                        Text(
                            text = nowPlayingCleanup(data.nowPlaying?.radio[station.id]),
                            color = colorScheme.onSurface,
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            modifier = Modifier
                                .padding(start = 0.dp, end = 8.dp, top = 2.dp, bottom = 8.dp)
                        )
                    }
                    if (audioManager.playerState?.isPlaying == true && audioManager.currentStation == station.id) {
                        Icon(
                            painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_pause),
                            contentDescription = "Pause " + station.name + " Stream",
                            tint = colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 4.dp).width(35.dp)
                                .height(35.dp)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_play),
                            contentDescription = "Play " + station.name + " Stream",
                            tint = colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 4.dp).width(35.dp)
                                .height(35.dp)
                        )
                    }
                }
            }
        }
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 0.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Podcasts",
                modifier = Modifier.padding(start = 0.dp, end = 0.dp, top = 16.dp, bottom = 4.dp),
                fontWeight = FontWeight.Bold
            )
            data.podcasts?.list?.forEachIndexed { index, podcast ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Log.d(
                                "Listen Screen",
                                "Go to " + podcast.name + " episode list, index: " + index.toString()
                            )
                            navController.navigate(
                                route = PodcastDetail(index)
                            )
                        }
                        .border(1.dp, colorScheme.outline, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorScheme.surfaceBright),
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
                        color = colorScheme.onSurface,
                        fontSize = 16.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 0.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
                            .weight(1f)
                    )
                    Icon(
                        painter = painterResource(R.drawable.chevron_right),
                        contentDescription = "Go to " + podcast.name + " episode list",
                        tint = colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 4.dp).width(35.dp).height(35.dp)
                    )
                }
            }
        }
    }
}

fun nowPlayingCleanup(nowPlaying: NowPlayingStation?): String {
    var output = ""
    output += if (nowPlaying?.artist?.contains("Houston Public Media") ?: false) {
        nowPlaying.title
    } else {
        nowPlaying?.artist + " - " + nowPlaying?.title
    }
    return output
}