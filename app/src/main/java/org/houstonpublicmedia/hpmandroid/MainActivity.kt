package org.houstonpublicmedia.hpmandroid

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.houstonpublicmedia.hpmandroid.ui.theme.*

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel.loadStationData()
        setContent {
            HPMAndroidTheme {
                MainScaffold(viewModel.stationData)
            }
        }
    }
}

class MainViewModel : ViewModel() {
    var stationData by mutableStateOf(StationData())
        private set // Make the setter private to control updates from within ViewModel

    fun loadStationData() {
        viewModelScope.launch { // Use viewModelScope for automatic cancellation
            stationData.priorityData = StationRepository.updatePriorityData()
            stationData.promos = StationRepository.updatePromos()
            stationData.nowPlaying = StationRepository.updateNowPlaying()
            stationData.streams = StationRepository.updateStreams()
            stationData.podcasts = StationRepository.updatePodcasts()
            stationData.categories = StationRepository.updateCategories(stationData.categoryList)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(data: StationData) {
    val stationData = remember { data }
    val uriHandler = LocalUriHandler.current
    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = HPM_Blue_Secondary,
                    titleContentColor = HPM_White
                ),
                actions = {
                    Button(
                        onClick = {
                            uriHandler.openUri("https://www.houstonpublicmedia.org/donate")
                        },
                        colors = ButtonDefaults.buttonColors(
                                containerColor = HPM_Red,
                                contentColor = HPM_White
                            )
                        ) {
                        Text("Donate")
                    }
                },
                title = {
                    Image(
                        painter = painterResource(R.drawable.hpm_bat_logo),
                        contentDescription = "Houston Public Media",
                        modifier = Modifier
                            .height(40.dp)
                    )
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = HPM_Background_Light,
                contentColor = HPM_Blue_Secondary,
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Bottom app bar",
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Today(stationData)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Today(data: StationData) {
    val stationData = remember { data }
    val scrollState = rememberScrollState()
    Column {
        Text("Top Stories")
        LazyRow(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .padding(all = 8.dp)
                .width(2700.dp)
                .height(350.dp)
        ) {
            items(stationData.priorityData?.articles ?: emptyList()) { article ->
                ArticleCard(article)
            }
        }
        stationData.categoryList?.forEach { category ->
            Text(category.name)
            if (stationData.categories?.articles[category.id] !== null) {
                stationData.categories?.articles[category.id]?.forEach { article ->
                    ArticleRow(article)
                }
            }
        }
    }
}

@Composable
fun ArticleCard(article: PriorityArticle?) {
    Column(
        modifier = Modifier
            .padding(all = 8.dp)
            .width(300.dp)
            .height(350.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, HPM_Blue_Secondary, RoundedCornerShape(8.dp))
    ) {
        AsyncImage(
            model = article?.picture,
            contentDescription = article?.excerpt,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 2f)
        )
        article?.title?.let {
            Text(
                text = it,
                color = HPM_Blue_Secondary,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .padding(all = 8.dp)
            )
        }
    }
}

@Composable
fun ArticleRow(article: ArticleData?) {
    Row(
        modifier = Modifier
            .padding(all = 8.dp)
            .fillMaxWidth()
    ) {
        if (article?.featured_media_url != null) {
            AsyncImage(
                model = article.featured_media_url,
                contentDescription = article.excerpt.rendered,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .aspectRatio(3f / 2f)
            )
        }
        Column {
            article?.title?.rendered.let {
                if (it != null) {
                    Text(
                        text = it,
                        color = HPM_Blue_Secondary,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}

@Preview(name = "Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun TodayPreview() {
    HPMAndroidTheme {
        Today(data = StationData())
    }
}