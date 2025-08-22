package org.houstonpublicmedia.hpmandroid

import android.media.session.PlaybackState
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import androidx.compose.ui.platform.LocalConfiguration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PodcastScreen(data: StationData, audioManager: AudioManager, navController: NavHostController, index: Int) {
    val episodeListPulled = remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    data.podcasts?.list[index]?.name.let {
                        if (it != null) {
                            Text(it)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Listen) }) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_arrow_back_ios_new_24),
                            tint = colorScheme.primary,
                            contentDescription = "Go back to Listen screen"
                        )
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        containerColor = colorScheme.surfaceDim
    ) { innerPadding ->
        val padding = innerPadding
        Column(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 64.dp, bottom = 0.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = data.podcasts?.list[index]?.image?.medium?.url,
                    contentDescription = data.podcasts?.list[index]?.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(300.dp)
                        .padding(all = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                data.podcasts?.list[index]?.description.let {
                    if (it != null) {
                        Text(
                            text = AnnotatedString.Companion.fromHtml(it),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier
                                .padding(start = 0.dp, end = 0.dp, top = 8.dp, bottom = 0.dp)
                        )
                    }
                }
                Row {
                    data.podcasts?.list[index]?.external_links?.spotify.let {
                        if (it != null) {
                            IconButton(
                                onClick = {
                                    uriHandler.openUri(it)
                                },
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.spotify),
                                    contentDescription = "Open podcast in Spotify",
                                    modifier = Modifier
                                        .height(40.dp)
                                        .width(40.dp)
                                )
                            }
                        }
                    }
                    data.podcasts?.list[index]?.external_links?.npr.let {
                        if (it != null) {
                            IconButton(
                                onClick = {
                                    uriHandler.openUri(it)
                                },
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.npr),
                                    contentDescription = "Open podcast in NPR",
                                    modifier = Modifier
                                        .height(40.dp)
                                        .width(40.dp)
                                )
                            }
                        }
                    }
                    data.podcasts?.list[index]?.external_links?.pcast.let {
                        if (it != null) {
                            IconButton(
                                onClick = {
                                    uriHandler.openUri(it)
                                },
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.pocketcasts),
                                    contentDescription = "Open podcast in Pocket Casts",
                                    modifier = Modifier
                                        .height(40.dp)
                                        .width(40.dp)
                                )
                            }
                        }
                    }
                    data.podcasts?.list[index]?.external_links?.amazon.let {
                        if (it != null) {
                            IconButton(
                                onClick = {
                                    uriHandler.openUri(it)
                                },
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.amazon),
                                    contentDescription = "Open podcast in Amazon",
                                    modifier = Modifier
                                        .height(40.dp)
                                        .width(40.dp)
                                )
                            }
                        }
                    }
                }
            }
            key(episodeListPulled.value) {
                data.podcasts?.list[index]?.episodelist?.forEach { episode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val podEp = PodcastEpisodePlayable(
                                    id = episode.id,
                                    image = data.podcasts?.list[index]?.image?.medium?.url ?: "",
                                    podcastName = data.podcasts?.list[index]?.name ?: "",
                                    episodeTitle = AnnotatedString.fromHtml(episode.title).toString(),
                                    excerpt = episode.excerpt,
                                    date_gmt = episode.date_gmt,
                                    thumbnail = episode.thumbnail,
                                    attachments = episode.attachments,
                                    duration = episode.attachments.duration_in_seconds
                                )
                                if (audioManager.playerState?.isPlaying == true) {
                                    audioManager.pause()
                                    if (audioManager.currentEpisode?.id != episode.id) {
                                        audioManager.startAudio(
                                            audioType = AudioManager.AudioType.Episode,
                                            station = null,
                                            nowPlaying = null,
                                            episode = podEp
                                        )
                                        audioManager.currentStation = episode.id
                                        audioManager.audioType = AudioManager.AudioType.Episode
                                        audioManager.currentEpisode = podEp
                                    }
                                } else {
                                    if (audioManager.currentEpisode?.id == podEp.id) {
                                        audioManager.play()
                                    } else {
                                        audioManager.startAudio(
                                            audioType = AudioManager.AudioType.Episode,
                                            station = null,
                                            nowPlaying = null,
                                            episode = podEp
                                        )
                                        audioManager.currentStation = episode.id
                                        audioManager.audioType = AudioManager.AudioType.Episode
                                        audioManager.currentEpisode = podEp
                                    }
                                }
                            }
                            .border(1.dp, colorScheme.outline, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .background(colorScheme.surfaceBright),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (episode.thumbnail != "") {
                            AsyncImage(
                                model = episode.thumbnail,
                                contentDescription = episode.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(75.dp)
                                    .padding(start = 8.dp, end = 0.dp, top = 8.dp, bottom = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }
                        Column(
                            Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                        ) {
                            Text(
                                text = AnnotatedString.fromHtml(episode.title),
                                color = colorScheme.onSurface,
                                fontSize = 14.sp,
                                lineHeight = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(start = 0.dp, end = 8.dp, top = 8.dp, bottom = 2.dp)
                            )
                            Row() {
                                Text(
                                    text = wpDateFormatter(episode.date_gmt),
                                    color = colorScheme.outline,
                                    fontSize = 12.sp,
                                    lineHeight = 14.sp,
                                    modifier = Modifier
                                        .padding(start = 0.dp, end = 8.dp, top = 2.dp, bottom = 8.dp)
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = episode.attachments.duration_in_seconds,
                                    color = colorScheme.outline,
                                    fontSize = 12.sp,
                                    lineHeight = 14.sp,
                                    modifier = Modifier
                                        .padding(start = 8.dp, end = 0.dp, top = 2.dp, bottom = 8.dp)
                                )
                            }

                        }
                        if (audioManager.playerState?.isPlaying == true && audioManager.currentStation == episode.id) {
                            Icon(
                                painter = painterResource(id = R.drawable.rounded_pause_24),
                                contentDescription = "Pause " + episode.title,
                                tint = colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.rounded_play_arrow_24),
                                contentDescription = "Play " + episode.title,
                                tint = colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
    LaunchedEffect(true) {
        if (data.podcasts?.list[index]?.episodelist == null) {
            Log.d("Podcast Detail Screen", "Trying to update episode list")
            data.podcasts?.list[index]?.episodelist = StationRepository.pullPodcastEpisodes(data.podcasts?.list[index]?.feed_json
                ?: "")
            episodeListPulled.value = true
            Log.d("Podcast Detail Screen", "Updated episode list")
        }
    }
}
