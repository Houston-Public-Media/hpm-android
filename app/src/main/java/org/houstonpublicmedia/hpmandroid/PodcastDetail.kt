package org.houstonpublicmedia.hpmandroid

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PodcastScreen(data: StationData, playback: AudioManager, navController: NavHostController, index: Int) {
    val episodeListPulled = remember { mutableStateOf(false) }
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
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = "Go back to Listen screen"
                        )
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.padding(all = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = data.podcasts?.list[index]?.image?.full?.url,
                    contentDescription = data.podcasts?.list[index]?.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(350.dp)
                        .padding(all = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                data.podcasts?.list[index]?.description.let {
                    if (it != null) {
                        Text(
                            text = AnnotatedString.Companion.fromHtml(it),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier
                                .padding(start = 0.dp, end = 8.dp, top = 8.dp, bottom = 4.dp)
                        )
                    }
                }
            }
            key(episodeListPulled.value) {
                data.podcasts?.list[index]?.episodelist?.forEach { episode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                Log.d(
                                    "Podcast Detail Screen",
                                    "Play " + data.podcasts?.list[index]?.name + " episode"
                                )
                            }
                            .padding(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                .clip(RoundedCornerShape(8.dp)),
                                //.background(HPM_White),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = episode.thumbnail,
                                contentDescription = episode.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(75.dp)
                                    .padding(all = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Text(
                                text = episode.title,
                                //color = HPM_Blue_Secondary,
                                fontSize = 16.sp,
                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                modifier = Modifier
                                    .padding(start = 0.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
                                    .weight(1f)
                            )
                            Icon(
                                painter = painterResource(R.drawable.play_arrow),
                                contentDescription = "Go to " + data.podcasts?.list[index]?.name + " episode list",
                                //tint = HPM_Blue_Secondary,
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
