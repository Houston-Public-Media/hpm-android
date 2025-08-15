package org.houstonpublicmedia.hpmandroid

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.session.R
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlin.collections.get
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalPlayerPopup(audioManager: AudioManager, data: StationData, iconPadding: Dp) {
	val contentDuration = audioManager.player?.getDuration()?.toFloat() ?: 1f
	var currentPosition by remember { mutableFloatStateOf(0f) }
	val iconSizePopup = 100.dp
	var name = ""
	var image = ""
	var title = ""
	if (audioManager.audioType == AudioManager.AudioType.episode) {
		name = audioManager.currentEpisode!!.podcastName
		image = audioManager.currentEpisode!!.image
		title = audioManager.currentEpisode!!.episodeTitle
	} else {
		name = data.streams?.audio[audioManager.currentStation ?: 0]?.name ?: ""
		image = data.streams?.audio[audioManager.currentStation ?: 0]?.artwork ?: ""
		title = nowPlayingCleanup(data.nowPlaying?.radio[audioManager.currentStation ?: 0])
	}
	AsyncImage(
		model = image,
		contentDescription = name,
		contentScale = ContentScale.Crop,
		modifier = Modifier
			.width(300.dp)
			.height(300.dp)
			.padding(all = iconPadding)
			.clip(RoundedCornerShape(8.dp))
	)
	Text(
		text = name,
		color = colorScheme.onSurface,
		fontSize = 16.sp,
		lineHeight = 18.sp,
		fontWeight = FontWeight.Bold,
		textAlign = TextAlign.Center
	)
	Text(
		text = title,
		color = colorScheme.onSurface,
		fontSize = 18.sp,
		lineHeight = 20.sp,
		textAlign = TextAlign.Center
	)
	if (audioManager.audioType == AudioManager.AudioType.episode) {
		Slider(
			value = currentPosition,
			onValueChange = { Log.d("NowPlayingSnackbar", "onValueChange") },
			valueRange = 0f..contentDuration,
			modifier = Modifier.padding(horizontal = 8.dp)
		)
		Row(modifier = Modifier.padding(horizontal = 8.dp)) {
			Text(
				text = convertFloatToTime(currentPosition),
				color = colorScheme.onSurface,
				fontSize = 10.sp,
				lineHeight = 12.sp
			)
			Spacer(modifier = Modifier.weight(1f))
			Text(
				text = convertFloatToTime(contentDuration),
				color = colorScheme.onSurface,
				fontSize = 10.sp,
				lineHeight = 12.sp
			)
		}
	} else {
		Slider(
			enabled = false,
			value = 0.5f,
			valueRange = 0f..1f,
			onValueChange = { },
			thumb = { Text("LIVE", color = colorScheme.onSurface) },
			colors = SliderDefaults.colors(
				thumbColor = colorScheme.outlineVariant,
				activeTrackColor = colorScheme.outlineVariant,
				inactiveTrackColor = colorScheme.outlineVariant
			),
			modifier = Modifier.padding(horizontal = 8.dp)
		)
	}
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Spacer(modifier = Modifier.weight(1f))
		if (audioManager.audioType == AudioManager.AudioType.episode) {
			IconButton(
				onClick = {
					audioManager.skipBackwards()
				},
				modifier = Modifier
					.width(iconSizePopup)
					.height(iconSizePopup)
					.padding(all = iconPadding)
			) {
				Icon(
					painter = painterResource(id = R.drawable.media3_icon_skip_back_15),
					contentDescription = "Play audio",
					tint = colorScheme.primary,
					modifier = Modifier
						.width(iconSizePopup)
						.height(iconSizePopup)
						.padding(all = iconPadding)
				)
			}
		}
		IconButton(
			onClick = {
				if (audioManager.playerState?.isPlaying == true) {
					audioManager.pause()
				} else {
					audioManager.play()
				}
			},
			modifier = Modifier
				.width(iconSizePopup)
				.height(iconSizePopup)
				.padding(all = iconPadding)
		) {
			if (audioManager.playerState?.isPlaying == true) {
				Icon(
					painter = painterResource(id = R.drawable.media3_icon_pause),
					contentDescription = "Pause audio",
					tint = colorScheme.primary,
					modifier = Modifier
						.width(iconSizePopup)
						.height(iconSizePopup)
						.padding(all = iconPadding)
				)
			} else {
				Icon(
					painter = painterResource(id = R.drawable.media3_icon_play),
					contentDescription = "Play audio",
					tint = colorScheme.primary,
					modifier = Modifier
						.width(iconSizePopup)
						.height(iconSizePopup)
						.padding(all = iconPadding)
				)
			}
		}
		if (audioManager.audioType == AudioManager.AudioType.episode) {
			IconButton(
				onClick = {
					audioManager.skipForward()
				},
				modifier = Modifier
					.width(iconSizePopup)
					.height(iconSizePopup)
					.padding(all = iconPadding)
			) {
				Icon(
					painter = painterResource(id = R.drawable.media3_icon_skip_forward_15),
					contentDescription = "Play audio",
					tint = colorScheme.primary,
					modifier = Modifier
						.width(iconSizePopup)
						.height(iconSizePopup)
						.padding(all = iconPadding)
				)
			}
		}
		Spacer(modifier = Modifier.weight(1f))
	}
	if (audioManager.audioType == AudioManager.AudioType.episode) {
		if (audioManager.playerState?.isPlaying == true) {
			LaunchedEffect(Unit) {
				while (true) {
					currentPosition = audioManager.player?.currentPosition?.toFloat() ?: 0f
					delay(1.seconds / 2)
				}
			}
		}
	}
}
@SuppressLint("DefaultLocale")
fun convertFloatToTime(time: Float): String {
	val floatToLong = time.toLong()
	val longToDuration = floatToLong.toDuration(DurationUnit.MILLISECONDS)
	longToDuration.toComponents { hours, minutes, seconds, _ ->
		return if (hours > 0) {
			String.format("%02d:%02d:%02d", hours, minutes, seconds)
		} else {
			String.format("%02d:%02d", minutes, seconds)
		}
	}
}