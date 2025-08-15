package org.houstonpublicmedia.hpmandroid

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreakingNewsView(data: StationData) {
    val stationData = remember { data }
}