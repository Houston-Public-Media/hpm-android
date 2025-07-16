package org.houstonpublicmedia.hpmandroid

import kotlinx.serialization.json.Json
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object StationRepository {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // Helpful if the JSON has more fields than your data class
                prettyPrint = true
                isLenient = true
            })
        }
    }

//    // Define a suspend function to fetch and parse the data
//    suspend fun fetchStationData(url: String): StationData? {
//        return try {
//            client.get(url).body<StationData>()
//        } catch (e: Exception) {
//            // Handle exceptions (network error, parsing error, etc.)
//            e.printStackTrace()
//            null
//        }
//    }
    suspend fun updateStreams(): Streams? {
        return try {
            client.get("https://cdn.houstonpublicmedia.org/assets/streams.json").body<Streams>()
        } catch (e: Exception) {
            // Handle exceptions (network error, parsing error, etc.)
            e.printStackTrace()
            null
        }
    }
    suspend fun updatePodcasts(): PodcastList? {
        return try {
            client.get("https://www.houstonpublicmedia.org/wp-json/hpm-podcast/v1/list").body<PodcastApiCall>().data
        } catch (e: Exception) {
            // Handle exceptions (network error, parsing error, etc.)
            e.printStackTrace()
            null
        }
    }
    suspend fun updatePriorityData(): PriorityArticleData? {
        return try {
            client.get("https://www.houstonpublicmedia.org/wp-json/hpm-priority/v1/list").body<PriorityApiCall>().data
        } catch (e: Exception) {
            // Handle exceptions (network error, parsing error, etc.)
            e.printStackTrace()
            null
        }
    }
    suspend fun updatePromos(): PromoData? {
        return try {
            client.get("https://www.houstonpublicmedia.org/wp-json/hpm-promos/v1/list").body<PromosApiCall>().data
        } catch (e: Exception) {
            // Handle exceptions (network error, parsing error, etc.)
            e.printStackTrace()
            null
        }
    }
    suspend fun updateNowPlaying(): NowPlaying? {
        return try {
            client.get("https://cdn.houstonpublicmedia.org/assets/nowplay/all.json").body<NowPlaying>()
        } catch (e: Exception) {
            // Handle exceptions (network error, parsing error, etc.)
            e.printStackTrace()
            null
        }
    }
    suspend fun updateCategories(list: List<WpCategory>?): HpmCategories {
        val categories = mutableMapOf<Int, List<ArticleData>>()
        try {
            if (list != null) {
                for (category in list) {
                    val articles = client.get("https://www.houstonpublicmedia.org/wp-json/wp/v2/posts/?categories=" + category.id + "&per_page=5").body<List<ArticleData>>()
                    categories.put(category.id, articles)
                }
            }
        } catch (e: Exception) {
            // Handle exceptions (network error, parsing error, etc.)
            e.printStackTrace()
            null
        }
        return HpmCategories(categories)
    }
    suspend fun pullPodcastEpisodes(podcast: Podcast): Podcast? {
        try {
            var podTemp = podcast
            podTemp.episodelist = client.get(podcast.feed_json).body<PodcastDetailApiCall>().data.feed.items
            return podTemp
        } catch (e: Exception) {
            // Handle exceptions (network error, parsing error, etc.)
            e.printStackTrace()
            null
        }
        return null
    }


}

@Serializable
data class Streams(
    val audio: List<Station>
)

@Serializable
data class Station(
    val id: Int,
    val name: String,
    val type: String,
    val artwork: String,
    val aacSource: String,
    val hlsSource: String,
    val mp3Source: String
)

@Serializable
data class PodcastApiCall(
    val code: String,
    val message: String,
    val data: PodcastList
)

@Serializable
data class PodcastList (
    var list: List<Podcast>
)

@Serializable
data class Podcast (
    val id: Int,
    val image: PodcastImageCrops,
    val feed: String,
    val archive: String,
    val slug: String,
    val name: String,
    val description: String,
    val feed_json: String,
    var episodelist: List<PodcastEpisode>? = null,
    val external_links: PodcastExternalLinks
)

@Serializable
data class PodcastExternalLinks (
    val itunes: String,
    val npr: String,
    val youtube: String,
    val spotify: String,
    val pcast: String,
    val overcast: String,
    val amazon: String,
    val tunein: String,
    val pandora: String,
    val iheart: String
)

@Serializable
data class PodcastEpisode (
    val id: Int,
    val title: String,
    val permalink: String,
    val content_html: String,
    val excerpt: String,
    val date: String,
    val date_gmt: String,
    val author: String,
    val thumbnail: String,
    val season: String,
    val episode: String,
    val episodeType: String,
    val attachments: PodcastEnclosure
)

@Serializable
data class PodcastEnclosure (
    val url: String,
    val duration_in_seconds: String
)

@Serializable
data class PodcastImageCrops (
    val full: ImageCrop,
    val medium: ImageCrop,
    val thumbnail: ImageCrop
)

@Serializable
data class PodcastDetailApiCall (
    val code: String,
    val message: String,
    val data: PodcastFeedDetail
)

@Serializable
data class PodcastFeedDetail (
    val feed: PodcastFeedItems
)

@Serializable
data class PodcastFeedItems (
    val items: List<PodcastEpisode>
)

@Serializable
data class PodcastEpisodePlayable (
    val id: Int,
    val image: PodcastImageCrops,
    val podcastName: String,
    val episodeTitle: String,
    val excerpt: String,
    val date_gmt: String,
    val thumbnail: String,
    val attachments: PodcastEnclosure,
    val duration: String
)

@Serializable
data class ImageCrop (
    val url: String,
    val width: Int,
    val height: Int
)

@Serializable
data class PriorityApiCall (
    val code: String,
    val message: String,
    val data: PriorityArticleData
)

@Serializable
data class PriorityArticleData (
    val articles: List<PriorityArticle>,
    val breaking: BreakingNews,
    val talkshow: String,
    val weather: HpmWeather,
)

@Serializable
data class BreakingNews (
    val id: Int,
    val title: String,
    val type: String,
    val link: String
)

@Serializable
data class PriorityArticle (
    val id: Int,
    val title: String,
    val excerpt: String,
    val picture: String,
    val permalink: String,
    val date: String,
    val date_gmt: String,
    val primary_category: WpCategory,
)

@Serializable
data class HpmWeather (
    val icon: String,
    val description: String,
    val temperature: String
)

@Serializable
data class NowPlaying (
    val radio: List<NowPlayingStation>,
    val tv: List<NowPlayingStation>
)

@Serializable
data class NowPlayingStation (
    var id: Int,
    var name: String,
    var artist: String,
    var title: String,
    var album: String
)

@Serializable
data class PromosApiCall (
    val code: String,
    val message: String,
    val data: PromoData
)

@Serializable
data class PromoData(
    val promos: List<Promo>
)

@Serializable
data class Promo (
    var type: String,
    var location: String,
    var content: String
)

@Serializable
data class ArticleData (
    val id: Int,
    val status: String,
    val date: String,
    val date_gmt: String,
    val modified_gmt: String,
    val link: String,
    val title: ArticleDataRendered,
    val excerpt: ArticleDataRendered,
    val featured_media_url: String
)

@Serializable
data class ArticleDataRendered (
    val rendered: String
)

@Serializable
data class Coauthor (
    val display_name: String,
    val user_nicename: String,
    val guest_author: Boolean,
    var extra: CoauthorExtra? = null
)

@Serializable
data class CoauthorExtra (
    var biography: String? = null,
    var image: String,
    var metadata: CoauthorMetadata?
)

@Serializable
data class CoauthorMetadata (
    val title: String,
    val pronouns: String,
    val email: String,
    val facebook: String,
    val twitter: String,
    val linkedin: String,
    val phone: String
)

@Serializable
data class WpCategory (
    val id: Int,
    val name: String,
    val slug: String
)

@Serializable
data class HpmCategories (
    var articles: MutableMap<Int, List<ArticleData>>
)

class StationData () {
    var streams by mutableStateOf<Streams?>(Streams(audio = emptyList()))
    var podcasts by mutableStateOf<PodcastList?>(PodcastList(list = emptyList()))
    var priorityData by mutableStateOf<PriorityArticleData?>(PriorityArticleData(articles = emptyList(), breaking = BreakingNews(id = 0, title = "", type = "", link = ""), talkshow = "", weather = HpmWeather("", "", "")))
    var promos by mutableStateOf<PromoData?>(PromoData(promos = emptyList()))
    var nowPlaying by mutableStateOf<NowPlaying?>(NowPlaying(radio = emptyList(), tv = emptyList()))
    var categories by mutableStateOf<HpmCategories?>(HpmCategories(articles = mutableMapOf<Int, List<ArticleData>>()))
    var categoryList by mutableStateOf<List<WpCategory>?>(listOf(
        WpCategory(id = 29328, name = "inDepth", slug = "indepth"),
        WpCategory(id = 3340, name = "Sports", slug = "sports"),
        WpCategory(id = 2232, name = "Weather", slug = "weather")
    ))
}