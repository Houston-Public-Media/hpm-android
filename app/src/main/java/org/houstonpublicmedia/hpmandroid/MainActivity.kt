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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
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

@Serializable
object Today
@Serializable
object Listen
@Serializable
object Watch
@Serializable
object Settings

data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: Int)

val topLevelRoutes = listOf(
    TopLevelRoute("Today", Today, R.drawable.home_24px),
    TopLevelRoute("Listen", Listen, R.drawable.headphones_24px),
    TopLevelRoute("Watch", Watch, R.drawable.live_tv_24px),
    TopLevelRoute("Settings", Settings, R.drawable.settings_24px)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(data: StationData) {
    val stationData = remember { data }
    val uriHandler = LocalUriHandler.current
    val navController = rememberNavController()
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
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                topLevelRoutes.forEach { topLevelRoute ->
                    NavigationBarItem(
                        icon = { Icon(painter = painterResource(id = topLevelRoute.icon), contentDescription = topLevelRoute.name) },
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
            composable<Listen> { ListenScreen(stationData) }
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
        TodayScreen(data = StationData())
    }
}