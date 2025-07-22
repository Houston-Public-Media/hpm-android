package org.houstonpublicmedia.hpmandroid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.MimeTypes
import androidx.media3.session.MediaController
import androidx.media3.common.MediaMetadata
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import org.houstonpublicmedia.hpmandroid.PlaybackService
import androidx.core.net.toUri


class AudioManager () {
    var itemTitle by mutableStateOf<String?>("")
    var state by mutableStateOf<StateType>(StateType.stopped)
    var currentStation by mutableStateOf<Int?>(0)
    var audioType by mutableStateOf<AudioType>(AudioType.stream)
    var currentEpisode by mutableStateOf<PodcastEpisodePlayable?>(PodcastEpisodePlayable(
        id = 0,
        image = PodcastImageCrops(
            full = ImageCrop(
                url = "",
                width = 0,
                height = 0
            ),
            medium = ImageCrop(
                url = "",
                width = 0,
                height = 0
            ),
            thumbnail = ImageCrop(
                url = "",
                width = 0,
                height = 0
            )
        ),
        podcastName = "",
        episodeTitle = "",
        excerpt = "",
        date_gmt = "",
        thumbnail = "",
        attachments = PodcastEnclosure(
            url = "",
            duration_in_seconds = ""
        ),
        duration = ""
    ))

    var player: MediaController? = null
    var playerState: PlayerState? = null
    enum class StateType {
        stopped,
        playing,
        paused;
    }
    enum class AudioType {
        stream,
        episode;
    }

    @OptIn(UnstableApi::class)
    fun startAudio(station: Station, nowPlaying: NowPlayingStation?) {
        val mediaItem = MediaItem.Builder()
            .setMediaId("media-1")
            .setUri(station.aacSource)
            .setMimeType(MimeTypes.APPLICATION_ICY)
            .setMediaMetadata(MediaMetadata.Builder()
                .setAlbumTitle(nowPlaying?.album)
                .setArtworkUri(station.artwork.toUri())
                .build())
            .build()
        player?.stop()
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
    }
    fun play() {
        player?.play()
        state = StateType.playing
    }

    fun pause() {
        player?.pause()
        state = StateType.paused
    }
}
