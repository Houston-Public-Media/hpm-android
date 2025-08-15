package org.houstonpublicmedia.hpmandroid

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayWeather(data: StationData) {
    val stationData = remember { data }
    Row(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
    ) {
        Text(
            text = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date()),
            color = colorScheme.onSurface,
            fontSize = 12.sp,
            lineHeight = 15.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Row {
            Text(
                text = AnnotatedString.fromHtml(stationData.priorityData?.weather?.temperature ?: "" ),
                color = colorScheme.onSurface,
                fontSize = 12.sp,
                lineHeight = 15.sp
            )
            AsyncImage(
                model = stationData.priorityData?.weather?.icon,
                contentDescription = stationData.priorityData?.weather?.description,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(15.dp)
                    .height(15.dp)
            )
        }
    }
}