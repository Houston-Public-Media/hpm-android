package org.houstonpublicmedia.hpmandroid

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.session.MediaController
import androidx.media3.common.MediaMetadata
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.core.net.toUri


class AudioManager (controller: MediaController?) {
    var currentStation by mutableStateOf<Int?>(0)
    var audioType by mutableStateOf<AudioType>(AudioType.stream)
    var currentEpisode by mutableStateOf<PodcastEpisodePlayable?>(PodcastEpisodePlayable(
        id = 0,
        image = "",
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

    var player: MediaController? by mutableStateOf(controller)
    var playerState: PlayerState? by mutableStateOf(player?.state())
    enum class AudioType {
        stream,
        episode;
    }

    @OptIn(UnstableApi::class)
    fun startAudio(audioType: AudioType, station: Station?, nowPlaying: NowPlayingStation?, episode: PodcastEpisodePlayable?): Boolean {
        var mediaItem: MediaItem? = null
        if (audioType == AudioType.episode) {
            if (episode == null) {
                return false
            }
            mediaItem = MediaItem.Builder()
                .setMediaId("media-" + episode.id)
                .setUri(episode.attachments.url)
                .setMimeType(MimeTypes.AUDIO_MPEG)
                .setMediaMetadata(MediaMetadata.Builder()
                    .setAlbumTitle(wpDateFormatter(episode.date_gmt))
                    .setTitle(episode.episodeTitle)
                    .setArtist(episode.podcastName)
                    .setArtworkUri(episode.image.toUri())
                    .build())
                .build()
        } else {
            if (station == null) {
                return false
            }
            mediaItem = MediaItem.Builder()
                .setMediaId("media-" + station.id)
                .setUri(station.hlsSource)
                .setMimeType(MimeTypes.APPLICATION_M3U8)
                .setMediaMetadata(MediaMetadata.Builder()
                    .setArtworkUri(station.artwork.toUri())
                    .build())
                .build()
        }

        player?.stop()
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
        return true
    }
    fun play() {
        player?.play()
    }

    fun pause() {
        player?.pause()
    }
    fun stop() {
        player?.stop()
    }
    fun skipForward() {
        player?.seekForward()
    }
    fun skipBackwards() {
        player?.seekBack()
    }
}
