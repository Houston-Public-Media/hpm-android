package org.houstonpublicmedia.hpmandroid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(data: StationData) {
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
                "Settings",
                modifier = Modifier.padding(start = 0.dp, end = 0.dp, top = 8.dp, bottom = 0.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}