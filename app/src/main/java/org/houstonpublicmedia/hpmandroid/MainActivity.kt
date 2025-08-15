package org.houstonpublicmedia.hpmandroid

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.core.view.WindowCompat
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
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
        }
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
            stationData.streams = StationRepository.updateStreams()
            stationData.podcasts = StationRepository.updatePodcasts()
            stationData.categories = StationRepository.updateCategories(stationData.categoryList)
            while (true) {
                stationData.nowPlaying = StationRepository.updateNowPlaying()
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
fun MainScaffold(data: StationData) {
    val stationData = remember { data }
    val uriHandler = LocalUriHandler.current
    val navController = rememberNavController()
    val mediaController by rememberManagedMediaController()
    val playback = AudioManager(controller = mediaController)
    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = Color(0xFF222054),
                    titleContentColor = Color(0xFFFFFFFF),
                ),
                actions = {
                    Button(
                        onClick = {
                            uriHandler.openUri("https://www.houstonpublicmedia.org/donate")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFC8102E),
                            contentColor = Color(0xFFFFFFFF)
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
            NowPlayingSnackbar(stationData, playback)
        },
        bottomBar = {
            Column {
                HorizontalDivider(
                    color = colorScheme.outline,
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                )
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
                                    tint = colorScheme.primary
                                )
                            },
                            label = { Text(topLevelRoute.name) },
                            selected = currentDestination?.hierarchy?.any {
                                it.hasRoute(
                                    topLevelRoute.route::class
                                )
                            } == true,
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
            }
        },
        containerColor = colorScheme.surfaceDim
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
            composable<Watch> { WatchScreen() }
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
       MainScaffold(StationData())
    }
}