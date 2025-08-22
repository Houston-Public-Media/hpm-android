package org.houstonpublicmedia.hpmandroid

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import coil3.compose.AsyncImage
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(data: StationData, audioManager: AudioManager) {
    val stationData = remember { data }
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(all = 0.dp)
            .verticalScroll(rememberScrollState())
    ) {
        DayWeather(stationData)
        if (stationData.priorityData?.breaking?.id != 0) {
            BreakingNewsView(stationData)
        }
        //if (stationData.priorityData?.talkshow == "") {
            TalkShowView(stationData, audioManager)
        //}
        Text(
            text = "Top Stories",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 0.dp)
        )
        LazyRow(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .padding(start = 0.dp, end = 0.dp, top = 8.dp, bottom = 8.dp)
                .width(2700.dp)
                .height(325.dp)
        ) {
            items(stationData.priorityData?.articles ?: emptyList()) { article ->
                ArticleCard(article)
            }
        }
        stationData.categoryList?.forEach { category ->
            Text(
                category.name,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 16.dp, bottom = 0.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(all = 8.dp)
            ) {
                if (stationData.categories?.articles[category.id] !== null) {
                    stationData.categories?.articles[category.id]?.forEach { article ->
                        ArticleRow(article)
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleCard(article: PriorityArticle?) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(all = 8.dp)
            .width(300.dp)
            .height(325.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, colorScheme.outline, RoundedCornerShape(8.dp))
            .background(colorScheme.primaryContainer)
            .clickable { context.launchCustomTabs(url = article?.permalink) }
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
                color = colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 4.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)) {
            article?.date_gmt?.let {
                Text(
                    text = wpDateFormatter(it),
                    color = colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .weight(1f)
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.rounded_arrow_outward_24),
                contentDescription = "Open link to article",
                tint = colorScheme.primary,
                modifier = Modifier
                    .width(35.dp)
                    .height(35.dp)
            )
        }
    }
}

@Composable
fun ArticleRow(article: ArticleData?) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { context.launchCustomTabs(url = article?.link) }
            .border(1.dp, colorScheme.outline, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(colorScheme.surfaceBright),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (article?.featured_media_url != "") {
            AsyncImage(
                model = article?.featured_media_url,
                contentDescription = article?.excerpt?.rendered,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(75.dp)
                    .padding(all = 8.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            article?.title?.rendered.let {
                if (it != null) {
                    Text(
                        text = AnnotatedString.Companion.fromHtml(it),
                        color = colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier
                            .padding(start = 0.dp, end = 8.dp, top = 8.dp, bottom = 4.dp)
                    )
                }
            }
            article?.date_gmt?.let {
                Text(
                    text = wpDateFormatter(it),
                    color = colorScheme.outline,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(start = 0.dp, end = 8.dp, top = 4.dp, bottom = 8.dp)
                        .align(Alignment.End)
                )
            }
        }
        Icon(
            painter = painterResource(id = R.drawable.rounded_arrow_outward_24),
            contentDescription = "Open link to article",
            tint = colorScheme.primary,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .width(35.dp)
                .height(35.dp)
        )
    }
}

fun wpDateFormatter(date: String?): String {
    val zonedDateTime = ZonedDateTime.parse("$date+00:00").withZoneSameInstant(ZoneId.systemDefault())
    val formattedString = zonedDateTime.format(DateTimeFormatter.ofPattern("EEE, MMM d, yyyy @ hh:mm a"))
    return formattedString
}

fun Context.launchCustomTabs(url: String?) {
    CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse(url))
}