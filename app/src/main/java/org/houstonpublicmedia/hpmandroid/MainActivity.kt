package org.houstonpublicmedia.hpmandroid

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.houstonpublicmedia.hpmandroid.ui.theme.*

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel.loadStationData()
        setContent {
            HPMAndroidTheme {
                MainScaffold(viewModel.stationData, viewModel.audioManager)
            }
        }
    }
}

class MainViewModel : ViewModel() {
    var stationData by mutableStateOf(StationData())
        private set // Make the setter private to control updates from within ViewModel

    var audioManager by mutableStateOf(AudioManager())
        private set

    fun loadStationData() {
        viewModelScope.launch { // Use viewModelScope for automatic cancellation
            stationData.priorityData = StationRepository.updatePriorityData()
            stationData.promos = StationRepository.updatePromos()
            stationData.streams = StationRepository.updateStreams()
            stationData.podcasts = StationRepository.updatePodcasts()
            stationData.categories = StationRepository.updateCategories(stationData.categoryList)
            while (true) {
                stationData.nowPlaying = StationRepository.updateNowPlaying()
                Log.d("MainViewModel", "Updated Now Playing")
                delay(60000L)
            }
        }
    }
}

@Serializable
object Today
@Serializable
object AudioList
@Serializable
object Listen
@Serializable
data class PodcastDetail(val index: Int)
@Serializable
object Watch
@Serializable
object Settings

data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: Int)

val topLevelRoutes = listOf(
    TopLevelRoute("Today", Today, R.drawable.news),
    TopLevelRoute("Listen", AudioList, R.drawable.headphones),
    TopLevelRoute("Watch", Watch, R.drawable.live_tv),
    TopLevelRoute("Settings", Settings, R.drawable.settings)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(data: StationData, audioManager: AudioManager) {
    val stationData = remember { data }
    val uriHandler = LocalUriHandler.current
    val navController = rememberNavController()
    val mediaController by rememberManagedMediaController()
    val playback = remember { audioManager }
    playback.player = mediaController
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
        snackbarHost = {
            if (playback.state != AudioManager.StateType.stopped) {
                BottomAppBar(
                    actions = {
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
                                    contentDescription = "Pause " + data.streams?.audio[playback.currentStation
                                        ?: 0]?.name + " Stream",
                                    tint = HPM_Blue_Secondary,
                                    modifier = Modifier.padding(horizontal = 4.dp).width(35.dp)
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.play_arrow),
                                    contentDescription = "Play " + data.streams?.audio[playback.currentStation ?: 0]?.name + " Stream",
                                    tint = HPM_Blue_Secondary,
                                    modifier = Modifier.padding(horizontal = 4.dp).width(35.dp)
                                )
                            }
                        }
                        Row(
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                data.streams?.audio[playback.currentStation ?: 0]?.name?.let {
                                    Text(
                                        text = it,
                                        color = HPM_Blue_Secondary,
                                        fontSize = 16.sp,
                                        fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                        modifier = Modifier
                                            .padding(start = 4.dp, end = 4.dp, top = 0.dp, bottom = 0.dp)
                                    )
                                }
                                Text(
                                    text = nowPlayingCleanup(data.nowPlaying?.radio[playback.currentStation ?: 0]),
                                    color = HPM_Blue_Secondary,
                                    fontSize = 12.sp,
                                    modifier = Modifier
                                        .padding(start = 4.dp, end = 4.dp, top = 0.dp, bottom = 0.dp)
                                )
                            }
                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { /* do something */ },
                            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.keyboard_arrow_up),
                                "Localized description"
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                topLevelRoutes.forEach { topLevelRoute ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = topLevelRoute.icon),
                                contentDescription = topLevelRoute.name,
                                modifier = Modifier.width(35.dp).height(35.dp),
                                tint = HPM_Blue_Secondary
                            )
                       },
                        label = { Text(topLevelRoute.name) },
                        selected = currentDestination?.hierarchy?.any { it.hasRoute(topLevelRoute.route::class) } == true,
                        onClick = {
                            navController.navigate(topLevelRoute.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        containerColor = HPM_Background_Light
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = Today, Modifier.padding(innerPadding)) {
            composable<Today> { TodayScreen(stationData) }
            navigation<AudioList>(startDestination = Listen) {
                composable<Listen> {
                    ListenScreen(
                        stationData,
                        playback,
                        navController = navController
                    )
                }
                composable<PodcastDetail> {
                    PodcastScreen(
                        stationData,
                        playback,
                        navController = navController,
                        index = it.arguments?.getInt("index") ?: 0
                    )
                }
            }
            composable<Watch> { WatchScreen(stationData) }
            composable<Settings> { SettingsScreen(stationData) }
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
        HPMAndroidTheme {
            MainScaffold(StationData(), AudioManager())
        }
    }
}