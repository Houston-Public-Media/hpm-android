package org.houstonpublicmedia.hpmandroid

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingSnackbar(data: StationData, playback: AudioManager) {
    if (playback.state != AudioManager.StateType.stopped) {
        Column {
            HorizontalDivider (
                color = colorScheme.outline,
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
            )
            BottomAppBar(
                actions = {
                    if (playback.audioType == AudioManager.AudioType.stream) {
                        AsyncImage(
                            model = data.streams?.audio[playback.currentStation ?: 0]?.artwork,
                            contentDescription = data.streams?.audio[playback.currentStation ?: 0]?.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(60.dp)
                                .height(60.dp)
                                .padding(all = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    } else {
                        AsyncImage(
                            model = playback.currentEpisode!!.thumbnail,
                            contentDescription = playback.currentEpisode!!.podcastName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(60.dp)
                                .height(60.dp)
                                .padding(all = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                    IconButton(
                        onClick = {
                            if (playback.state == AudioManager.StateType.playing) {
                                playback.player?.pause()
                                playback.state = AudioManager.StateType.paused
                            } else {
                                playback.player?.play()
                                playback.state = AudioManager.StateType.playing
                            }
                        }
                    ) {
                        if (playback.state == AudioManager.StateType.playing) {
                            Icon(
                                painter = painterResource(id = R.drawable.pause),
                                contentDescription = "Pause audio",
                                tint = colorScheme.primary,
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .width(35.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.play_arrow),
                                contentDescription = "Play audio",
                                tint = colorScheme.primary,
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .width(35.dp)
                            )
                        }
                    }
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            if (playback.audioType == AudioManager.AudioType.stream) {
                                data.streams?.audio[playback.currentStation ?: 0]?.name?.let {
                                    Text(
                                        text = it,
                                        color = colorScheme.onSurface,
                                        fontSize = 14.sp,
                                        lineHeight = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .padding(
                                                start = 4.dp,
                                                end = 4.dp,
                                                top = 0.dp,
                                                bottom = 0.dp
                                            )
                                    )
                                }
                                Text(
                                    text = nowPlayingCleanup(
                                        data.nowPlaying?.radio[playback.currentStation ?: 0]
                                    ),
                                    color = colorScheme.onSurface,
                                    fontSize = 12.sp,
                                    lineHeight = 14.sp,
                                    modifier = Modifier
                                        .padding(
                                            start = 4.dp,
                                            end = 4.dp,
                                            top = 0.dp,
                                            bottom = 0.dp
                                        )
                                )
                            } else {
                                playback.currentEpisode?.podcastName?.let {
                                    Text(
                                        text = it,
                                        color = colorScheme.onSurface,
                                        fontSize = 14.sp,
                                        lineHeight = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .padding(
                                                start = 4.dp,
                                                end = 4.dp,
                                                top = 0.dp,
                                                bottom = 0.dp
                                            )
                                    )
                                }
                                playback.currentEpisode?.episodeTitle?.let {
                                    Text(
                                        text = it,
                                        color = colorScheme.onSurface,
                                        fontSize = 12.sp,
                                        lineHeight = 14.sp,
                                        modifier = Modifier
                                            .padding(
                                                start = 4.dp,
                                                end = 4.dp,
                                                top = 0.dp,
                                                bottom = 0.dp
                                            )
                                    )
                                }
                            }
                        }
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { /* do something */ },
                        containerColor = colorScheme.primaryContainer,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.keyboard_arrow_up),
                            "Localized description"
                        )
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        }
    }
}
