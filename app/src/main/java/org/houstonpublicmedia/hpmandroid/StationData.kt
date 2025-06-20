package org.houstonpublicmedia.hpmandroid

import java.net.URL
import java.util.Date
import kotlinx.serialization.json.Json

data class Streams(
    val audio: List<Station>
)
data class Station(
    val id: Int,
    val name: String,
    val type: String,
    val artwork: String,
    val aacSource: String,
    val hlsSource: String,
    val mp3Source: String
)
data class PodcastApiCall(
    val code: String,
    val message: String,
    val data: PodcastList
)
data class PodcastList (
    var list: List<Podcast>
)
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
data class PodcastEpisode (
    val id: Int,
    val title: String,
    val permalink: String,
    val content_html: String,
    val excerpt: String,
    val date: Date,
    val date_gmt: Date,
    val author: String,
    val thumbnail: String,
    val season: String,
    val episode: String,
    val episodeType: String,
    val attachments: PodcastEnclosure
)
data class PodcastEnclosure (
    val url: String,
    val duration_in_seconds: String
)
data class PodcastImageCrops (
    val full: ImageCrop,
    val medium: ImageCrop,
    val thumbnail: ImageCrop
)
data class PodcastDetailApiCall (
    val code: String,
    val message: String,
    val data: PodcastFeedDetail
)
data class PodcastFeedDetail (
    val feed: PodcastFeedItems
)
data class PodcastFeedItems (
    val items: List<PodcastEpisode>
)
data class PodcastEpisodePlayable (
    val id: Int,
    val image: PodcastImageCrops,
    val podcastName: String,
    val episodeTitle: String,
    val excerpt: String,
    val date_gmt: Date,
    val thumbnail: String,
    val attachments: PodcastEnclosure,
    val duration: String
)
data class ImageCrop (
    val url: String,
    val width: Int,
    val height: Int
)
data class PriorityApiCall (
    val code: String,
    val message: String,
    val data: PriorityArticleData
)
data class PriorityArticleData (
    val articles: List<PriorityArticle>,
    val breaking: BreakingNews,
    val talkshow: String,
    val weather: HpmWeather,
)
data class BreakingNews (
    val id: Int,
    val title: String,
    val type: String,
    val link: String
)
data class PriorityArticle (
    val id: Int,
    val title: String,
    val excerpt: String,
    val picture: String,
    val permalink: String,
    val date: Date,
    val date_gmt: Date,
    val primary_category: WpCategory,
)
data class HpmWeather (
    val icon: String,
    val description: String,
    val temperature: String
)
data class NowPlaying (
    val radio: List<NowPlayingStation>,
    val tv: List<NowPlayingStation>
)
data class NowPlayingStation (
    var id: Int,
    var name: String,
    var artist: String,
    var title: String,
    var album: String
)
data class PromosApiCall (
    val code: String,
    val message: String,
    val data: PromoData
)
data class PromoData(
    val promos: List<Promo>
)
data class Promo (
    var type: String,
    var location: String,
    var content: String
)
data class ArticleData (
    val id: Int,
    val status: String,
    val date: Date,
    val date_gmt: Date,
    val modified_gmt: Date,
    val link: String,
    val title: ArticleDataRendered,
    val excerpt: ArticleDataRendered,
    val featured_media_url: String
)
data class ArticleDataRendered (
    val rendered: String
)
data class Coauthor (
    val display_name: String,
    val user_nicename: String,
    val guest_author: Boolean,
    var extra: CoauthorExtra? = null
)
data class CoauthorExtra (
    var biography: String? = null,
    var metadata: CoauthorMetadata?
)
data class CoauthorMetadata (
    val title: String,
    val pronouns: String,
    val email: String,
    val facebook: String,
    val twitter: String,
    val linkedin: String,
    val phone: String
)
data class WpCategory (
    val id: Int,
    val name: String,
    val slug: String
)
data class HpmCategories (
    var articles: MutableMap<Int, ArticleData>
)

class StationData (
    var streams: Streams,
    var podcasts: PodcastList,
    var priorityData: PriorityArticleData,
    var promos: PromoData,
    var nowPlaying: NowPlaying,
    var categories: HpmCategories
) {
    init {
        streams = Streams(audio = arrayOf(
            Station(id = 0, name = "News 88.7", type = "audio", artwork = "https://cdn.houstonpublicmedia.org/assets/images/ListenLive_News.png.webp", aacSource = "https://stream.houstonpublicmedia.org/news-aac", mp3Source = "https://stream.houstonpublicmedia.org/news-mp3", hlsSource = "https://hls.houstonpublicmedia.org/hpmnews/playlist.m3u8"),
            Station(id = 1, name = "Classical", type = "audio", artwork = "https://cdn.houstonpublicmedia.org/assets/images/ListenLive_Classical.png.webp", aacSource = "https://stream.houstonpublicmedia.org/classical-aac", mp3Source = "https://stream.houstonpublicmedia.org/classical-mp3", hlsSource = "https://hls.houstonpublicmedia.org/classical/playlist.m3u8"),
            Station(id = 2, name = "The Vibe", type = "audio", artwork = "https://cdn.houstonpublicmedia.org/assets/images/ListenLive_TheVibe.png.webp", aacSource = "https://stream.houstonpublicmedia.org/thevibe-aac", mp3Source = "https://stream.houstonpublicmedia.org/thevibe-mp3", hlsSource = "https://hls.houstonpublicmedia.org/thevibe/playlist.m3u8")
        ).toList())
        podcasts = PodcastList(list = emptyList())
        priorityData = PriorityArticleData(articles = emptyList(), breaking = BreakingNews(id = 0, title = "", type = "", link = ""), talkshow = "", weather = HpmWeather(icon = "", description = "", temperature = ""))
        promos = PromoData(promos = emptyList())
        nowPlaying = NowPlaying(radio = arrayOf(
            NowPlayingStation(id = 0, name = "News 88.7", artist = "Houston Public Media News", title = "", album = ""),
            NowPlayingStation(id = 1, name = "Classical", artist = "Houston Public Media Classical", title = "", album = ""),
            NowPlayingStation(id = 2, name = "The Vibe from KTSU and HPM", artist = "The Vibe from KTSU and HPM", title = "", album = "")
        ).toList(), tv = emptyList())
        categories = HpmCategories(articles = mutableMapOf<Int, ArticleData>())
        println("StationData initialized")
    }
    fun updateStreams() {
        val url = URL("https://cdn.houstonpublicmedia.org/assets/streams.json")
        val json = url.openStream().use {
            it.readAllBytes().decodeToString()
        }
        streams = Json.decodeFromString<Streams>(json)
    }
    fun updatePodcasts() {
        val url = URL("https://www.houstonpublicmedia.org/wp-json/hpm-podcast/v1/list")
        val json = url.openStream().use {
            it.readAllBytes().decodeToString()
        }
        podcasts = Json.decodeFromString<PodcastApiCall>(json).data
    }
    fun updatePriorityData() {
        val url = URL("https://cdn.houstonpublicmedia.org/assets/promos-test.json")
        val json = url.openStream().use {
            it.readAllBytes().decodeToString()
        }
        priorityData = Json.decodeFromString<PriorityApiCall>(json).data
    }
    fun updatePromos() {
        val url = URL("https://www.houstonpublicmedia.org/wp-json/hpm-promos/v1/list")
        val json = url.openStream().use {
            it.readAllBytes().decodeToString()
        }
        promos = Json.decodeFromString<PromosApiCall>(json).data
    }
    fun updateNowPlaying() {
        val url = URL("https://cdn.houstonpublicmedia.org/assets/nowplay/all.json")
        val json = url.openStream().use {
            it.readAllBytes().decodeToString()
        }
        nowPlaying = Json.decodeFromString<NowPlaying>(json)
    }
    fun updateCategories(list: List<WpCategory>) {
        for (category in list) {
            val url = URL("https://www.houstonpublicmedia.org/wp-json/wp/v2/posts/?categories=" + category.id + "&per_page=5")
            val json = url.openStream().use {
                it.readAllBytes().decodeToString()
            }
            val articles = Json.decodeFromString<List<ArticleData>>(json)
            for (article in articles) {
                categories.articles[article.id] = article
            }
        }
    }
    fun categoryIds(categories: List<WpCategory>): List<Int> {
        var ids = emptyList<Int>().toMutableList()
        for (category in categories) {
            ids.add(category.id)
        }
        return ids
    }
    fun pullPodcastEpisodes(podcast: Podcast): Podcast {
        var podTemp = podcast
        val url = URL(podcast.feed_json)
        val json = url.openStream().use {
            it.readAllBytes().decodeToString()
        }
        podTemp.episodelist = Json.decodeFromString<PodcastDetailApiCall>(json).data.feed.items
        return podTemp
    }
}
