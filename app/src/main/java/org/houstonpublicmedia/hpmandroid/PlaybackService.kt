package org.houstonpublicmedia.hpmandroid

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import android.content.Context
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import androidx.annotation.OptIn
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.AudioAttributes
import androidx.media3.common.DeviceInfo
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.common.text.CueGroup
import android.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

@UnstableApi
class PlaybackService : MediaSessionService() {
    private var _mediaSession: MediaSession? = null
    private val mediaSession get() = _mediaSession!!

    companion object {
        private const val NOTIFICATION_ID = 123
        private const val CHANNEL_ID = "session_notification_channel_id"
        private const val immutableFlag = PendingIntent.FLAG_IMMUTABLE
    }


    /**
     * This method is called when the service is being created.
     * It initializes the ExoPlayer and MediaSession instances and sets the MediaSessionServiceListener.
     */
    override fun onCreate() {
        super.onCreate() // Call the superclass method
        // Create an ExoPlayer instance
        val player = ExoPlayer.Builder(this).build()

        // Create a MediaSession instance
        _mediaSession = MediaSession.Builder(this, player)
            .also { builder ->
                // Set the session activity to the PendingIntent returned by getSingleTopActivity() if it's not null
                getSingleTopActivity()?.let { builder.setSessionActivity(it) }
            }
            .build() // Build the MediaSession instance

        // Set the listener for the MediaSessionService
        setListener(MediaSessionServiceListener())
    }


    /**
     * This method is called when the system determines that the service is no longer used and is being removed.
     * It checks the player's state and if the player is not ready to play or there are no items in the media queue, it stops the service.
     *
     * @param rootIntent The original root Intent that was used to launch the task that is being removed.
     */
    override fun onTaskRemoved(rootIntent: Intent?) {
        // Get the player from the media session
        val player = mediaSession.player

        // Check if the player is not ready to play or there are no items in the media queue
        if (!player.playWhenReady || player.mediaItemCount == 0) {
            // Stop the service
            stopSelf()
        }
    }

    /**
     * This method is called when a MediaSession.ControllerInfo requests the MediaSession.
     * It returns the current MediaSession instance.
     *
     * @param controllerInfo The MediaSession.ControllerInfo that is requesting the MediaSession.
     * @return The current MediaSession instance.
     */
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return _mediaSession
    }


    /**
     * This method is called when the service is being destroyed.
     * It releases the player and the MediaSession instances, sets the _mediaSession to null, clears the listener, and calls the superclass method.
     */
    override fun onDestroy() {
        // If _mediaSession is not null, run the following block
        _mediaSession?.run {
            // If getBackStackedActivity() returns a non-null value, set it as the session activity
            getBackStackedActivity()?.let { setSessionActivity(it) }
            // Release the player
            player.release()
            // Release the MediaSession instance
            release()
            // Set _mediaSession to null
            _mediaSession = null
        }
        // Clear the listener
        clearListener()
        // Call the superclass method
        super.onDestroy()
    }

    /**
     * This method creates a PendingIntent that starts the MainActivity.
     * The PendingIntent is created with the "FLAG_UPDATE_CURRENT" flag, which means that if the described PendingIntent already exists,
     * then keep it but replace its extra data with what is in this new Intent.
     * The PendingIntent also has the "FLAG_IMMUTABLE" flag if the Android version is 23 or above, which means that the created PendingIntent
     * is immutable and cannot be changed after it's created.
     *
     * @return A PendingIntent that starts the MainActivity. If the PendingIntent cannot be created for any reason, it returns null.
     */
    private fun getSingleTopActivity(): PendingIntent? {
        return PendingIntent.getActivity(
            this,
            0,
            Intent(this, AudioManager::class.java),
            immutableFlag or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    /**
     * This method creates a PendingIntent that starts the MainActivity with a back stack.
     * The PendingIntent is created with the "FLAG_UPDATE_CURRENT" flag, which means that if the described PendingIntent already exists,
     * then keep it but replace its extra data with what is in this new Intent.
     * The PendingIntent also has the "FLAG_IMMUTABLE" flag if the Android version is 23 or above, which means that the created PendingIntent
     * is immutable and cannot be changed after it's created.
     * The back stack is created using TaskStackBuilder, which allows the user to navigate back to the MainActivity from the PendingIntent.
     *
     * @return A PendingIntent that starts the MainActivity with a back stack. If the PendingIntent cannot be created for any reason, it returns null.
     */
    private fun getBackStackedActivity(): PendingIntent? {
        return TaskStackBuilder.create(this).run {
            addNextIntent(Intent(this@PlaybackService, AudioManager::class.java))
            getPendingIntent(0, immutableFlag or PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    @OptIn(UnstableApi::class) // MediaSessionService.Listener
    private inner class MediaSessionServiceListener : Listener {

        /**
         * This method is only required to be implemented on Android 12 or above when an attempt is made
         * by a media controller to resume playback when the {@link MediaSessionService} is in the
         * background.
         */
        @SuppressLint("MissingPermission")
        override fun onForegroundServiceStartNotAllowedException() {
            if (
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                // Notification permission is required but not granted
                return
            }
            val notificationManagerCompat = NotificationManagerCompat.from(this@PlaybackService)
            ensureNotificationChannel(notificationManagerCompat)
            val builder =
                NotificationCompat.Builder(this@PlaybackService, CHANNEL_ID)
                    .setSmallIcon(R.drawable.hpm_sq_logo_1024)
                    .setContentTitle("Playback cannot be resumed")
                    .setStyle(
                        NotificationCompat.BigTextStyle().bigText("Press on the play button on the media notification if it\n" +
                                "    is still present, otherwise please open the app to start the playback and re-connect the session\n" +
                                "    to the controller")
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .also { builder -> getBackStackedActivity()?.let { builder.setContentIntent(it) } }
            notificationManagerCompat.notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun ensureNotificationChannel(notificationManagerCompat: NotificationManagerCompat) {
        if ( notificationManagerCompat.getNotificationChannel(CHANNEL_ID) != null ) {
            return
        }

        val channel =
            NotificationChannel(
                CHANNEL_ID,
                "Playback cannot be resumed",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        notificationManagerCompat.createNotificationChannel(channel)
    }
}

/**
 * A Composable function that provides a managed MediaController instance.
 *
 * @param lifecycle The lifecycle of the owner of this MediaController. Defaults to the lifecycle of the LocalLifecycleOwner.
 * @return A State object containing the MediaController instance. The Composable will automatically re-compose whenever the state changes.
 */
@Composable
fun rememberManagedMediaController(
    lifecycle: Lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle
): State<MediaController?> {
    // Application context is used to prevent memory leaks
    val appContext = LocalContext.current.applicationContext
    val controllerManager = remember { MediaControllerManager.getInstance(appContext) }

    // Observe the lifecycle to initialize and release the MediaController at the appropriate times.
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> controllerManager.initialize()
                Lifecycle.Event.ON_STOP -> controllerManager.release()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    return controllerManager.controller
}

/**
 * A Singleton class that manages a MediaController instance.
 *
 * This class observes the Remember lifecycle to release the MediaController when it's no longer needed.
 */
@Stable
internal class MediaControllerManager private constructor(context: Context) : RememberObserver {
    private val appContext = context.applicationContext
    private var factory: ListenableFuture<MediaController>? = null
    var controller = mutableStateOf<MediaController?>(null)
        private set

    init { initialize() }

    /**
     * Initializes the MediaController.
     *
     * If the MediaController has not been built or has been released, this method will build a new one.
     */
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    internal fun initialize() {
        if (factory == null || factory?.isDone == true) {
            factory = MediaController.Builder(
                appContext,
                SessionToken(appContext, ComponentName(appContext, PlaybackService::class.java))
            ).buildAsync()
        }
        factory?.addListener(
            { controller.value = factory?.let { if (it.isDone) it.get() else null } },
            MoreExecutors.directExecutor()
        )
    }

    /**
     * Releases the MediaController.
     *
     * This method will release the MediaController and set the controller state to null.
     */
    internal fun release() {
        factory?.let {
            MediaController.releaseFuture(it)
            controller.value = null
        }
        factory = null
    }

    // Lifecycle methods for the RememberObserver interface.
    override fun onAbandoned() { release() }
    override fun onForgotten() { release() }
    override fun onRemembered() {}

    companion object {
        @Volatile
        private var instance: MediaControllerManager? = null

        /**
         * Returns the Singleton instance of the MediaControllerManager.
         *
         * @param context The context to use when creating the MediaControllerManager.
         * @return The Singleton instance of the MediaControllerManager.
         */
        fun getInstance(context: Context): MediaControllerManager {
            return instance ?: synchronized(this) {
                instance ?: MediaControllerManager(context).also { instance = it }
            }
        }
    }
}

/**
 * Create a instance of [PlayerState] and register a [listener][Player.Listener] to the [Player] to
 * observe its states.
 *
 * NOTE: Should call [dispose][PlayerState.dispose] to unregister the listener to avoid leaking this
 * instance when it is no longer used.
 */
fun Player.state(): PlayerState {
    return PlayerStateImpl(this)
}

/**
 * A state object that can be used to observe the [player]'s states.
 */
interface PlayerState {
    val player: Player

    val timeline: Timeline

    val mediaItemIndex: Int

    val tracks: Tracks

    val currentMediaItem: MediaItem?

    val mediaMetadata: MediaMetadata

    val playlistMetadata: MediaMetadata

    val isLoading: Boolean

    val availableCommands: Player.Commands

    val trackSelectionParameters: TrackSelectionParameters

    @get:Player.State
    val playbackState: Int

    val playWhenReady: Boolean

    @get:Player.PlaybackSuppressionReason
    val playbackSuppressionReason: Int

    val isPlaying: Boolean

    @get:Player.RepeatMode
    val repeatMode: Int

    val shuffleModeEnabled: Boolean

    val playerError: PlaybackException?

    val playbackParameters: PlaybackParameters

    val seekBackIncrement: Long

    val seekForwardIncrement: Long

    val maxSeekToPreviousPosition: Long

    val audioAttributes: AudioAttributes

    val volume: Float

    val deviceInfo: DeviceInfo

    val deviceVolume: Int

    val isDeviceMuted: Boolean

    val videoSize: VideoSize

    val cues: CueGroup

    fun dispose()
}

internal class PlayerStateImpl(
    override val player: Player
) : PlayerState {
    override var timeline: Timeline by mutableStateOf(player.currentTimeline)
        private set

    override var mediaItemIndex: Int by mutableIntStateOf(player.currentMediaItemIndex)
        private set

    override var tracks: Tracks by mutableStateOf(player.currentTracks)
        private set

    override var currentMediaItem: MediaItem? by mutableStateOf(player.currentMediaItem)
        private set

    override var mediaMetadata: MediaMetadata by mutableStateOf(player.mediaMetadata)
        private set

    override var playlistMetadata: MediaMetadata by mutableStateOf(player.playlistMetadata)
        private set

    override var isLoading: Boolean by mutableStateOf(player.isLoading)
        private set

    override var availableCommands: Player.Commands by mutableStateOf(player.availableCommands)
        private set

    override var trackSelectionParameters: TrackSelectionParameters by mutableStateOf(player.trackSelectionParameters)
        private set

    @get:Player.State
    override var playbackState: Int by mutableIntStateOf(player.playbackState)
        private set

    override var playWhenReady: Boolean by mutableStateOf(player.playWhenReady)
        private set

    @get:Player.PlaybackSuppressionReason
    override var playbackSuppressionReason: Int by mutableIntStateOf(player.playbackSuppressionReason)
        private set

    override var isPlaying: Boolean by mutableStateOf(player.isPlaying)
        private set

    @get:Player.RepeatMode
    override var repeatMode: Int by mutableIntStateOf(player.repeatMode)
        private set

    override var shuffleModeEnabled: Boolean by mutableStateOf(player.shuffleModeEnabled)
        private set

    override var playerError: PlaybackException? by mutableStateOf(player.playerError)
        private set

    override var playbackParameters: PlaybackParameters by mutableStateOf(player.playbackParameters)
        private set

    override var seekBackIncrement: Long by mutableLongStateOf(player.seekBackIncrement)
        private set

    override var seekForwardIncrement: Long by mutableLongStateOf(player.seekForwardIncrement)
        private set

    override var maxSeekToPreviousPosition: Long by mutableLongStateOf(player.maxSeekToPreviousPosition)
        private set

    override var audioAttributes: AudioAttributes by mutableStateOf(player.audioAttributes)
        private set

    override var volume: Float by mutableFloatStateOf(player.volume)
        private set

    override var deviceInfo: DeviceInfo by mutableStateOf(player.deviceInfo)
        private set

    override var deviceVolume: Int by mutableIntStateOf(player.deviceVolume)
        private set

    override var isDeviceMuted: Boolean by mutableStateOf(player.isDeviceMuted)
        private set

    override var videoSize: VideoSize by mutableStateOf(player.videoSize)
        private set

    override var cues: CueGroup by mutableStateOf(player.currentCues)
        private set

    private val listener = object : Player.Listener {
        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            this@PlayerStateImpl.timeline = timeline
            this@PlayerStateImpl.mediaItemIndex = player.currentMediaItemIndex
        }

        override fun onTracksChanged(tracks: Tracks) {
            this@PlayerStateImpl.tracks = tracks
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            this@PlayerStateImpl.currentMediaItem = mediaItem
        }

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            this@PlayerStateImpl.mediaMetadata = mediaMetadata
        }

        override fun onPlaylistMetadataChanged(mediaMetadata: MediaMetadata) {
            this@PlayerStateImpl.playlistMetadata = mediaMetadata
        }

        override fun onIsLoadingChanged(isLoading: Boolean) {
            this@PlayerStateImpl.isLoading = isLoading
        }

        override fun onAvailableCommandsChanged(availableCommands: Player.Commands) {
            this@PlayerStateImpl.availableCommands = availableCommands
        }

        override fun onTrackSelectionParametersChanged(parameters: TrackSelectionParameters) {
            this@PlayerStateImpl.trackSelectionParameters = parameters
        }

        override fun onPlaybackStateChanged(@Player.State playbackState: Int) {
            this@PlayerStateImpl.playbackState = playbackState
            Log.d("hpmNowPlaying", "Playback State: " + playbackState.toString())
        }

        override fun onPlayWhenReadyChanged(
            playWhenReady: Boolean,
            @Player.PlayWhenReadyChangeReason reason: Int
        ) {
            this@PlayerStateImpl.playWhenReady = playWhenReady
        }

        override fun onPlaybackSuppressionReasonChanged(playbackSuppressionReason: Int) {
            this@PlayerStateImpl.playbackSuppressionReason = playbackSuppressionReason
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            this@PlayerStateImpl.isPlaying = isPlaying
            Log.d("hpmNowPlaying", "Is Playing? " + isPlaying.toString())
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            this@PlayerStateImpl.repeatMode = repeatMode
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            this@PlayerStateImpl.shuffleModeEnabled = shuffleModeEnabled
        }

        override fun onPlayerErrorChanged(error: PlaybackException?) {
            this@PlayerStateImpl.playerError = error
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            this@PlayerStateImpl.mediaItemIndex = player.currentMediaItemIndex
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
            this@PlayerStateImpl.playbackParameters = playbackParameters
        }

        override fun onSeekBackIncrementChanged(seekBackIncrementMs: Long) {
            this@PlayerStateImpl.seekBackIncrement = seekBackIncrementMs
        }

        override fun onSeekForwardIncrementChanged(seekForwardIncrementMs: Long) {
            this@PlayerStateImpl.seekForwardIncrement = seekForwardIncrementMs
        }

        override fun onMaxSeekToPreviousPositionChanged(maxSeekToPreviousPositionMs: Long) {
            this@PlayerStateImpl.maxSeekToPreviousPosition = maxSeekToPreviousPositionMs
        }

        override fun onAudioAttributesChanged(audioAttributes: AudioAttributes) {
            this@PlayerStateImpl.audioAttributes = audioAttributes
        }

        override fun onVolumeChanged(volume: Float) {
            this@PlayerStateImpl.volume = volume
        }

        override fun onDeviceInfoChanged(deviceInfo: DeviceInfo) {
            this@PlayerStateImpl.deviceInfo = deviceInfo
        }

        override fun onDeviceVolumeChanged(volume: Int, muted: Boolean) {
            this@PlayerStateImpl.deviceVolume = volume
            this@PlayerStateImpl.isDeviceMuted = muted
        }

        override fun onVideoSizeChanged(videoSize: VideoSize) {
            this@PlayerStateImpl.videoSize = videoSize
        }

        override fun onCues(cues: CueGroup) {
            this@PlayerStateImpl.cues = cues
        }
    }

    init {
        player.addListener(listener)
    }

    override fun dispose() {
        player.removeListener(listener)
    }
}
