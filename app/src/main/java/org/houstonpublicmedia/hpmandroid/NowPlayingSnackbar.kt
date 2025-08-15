package org.houstonpublicmedia.hpmandroid

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingSnackbar(data: StationData, audioManager: AudioManager) {
	val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
	val scope = rememberCoroutineScope()
	var showBottomSheet by remember { mutableStateOf(false) }
	val iconSize = 60.dp
	val iconPadding = 4.dp
	if (audioManager.playerState?.playbackState == 2 || audioManager.playerState?.playbackState == 3) {
		Column {
			HorizontalDivider (
				color = colorScheme.outline,
				modifier = Modifier
					.height(1.dp)
					.fillMaxWidth()
			)
			BottomAppBar(
				actions = {
					if (audioManager.audioType == AudioManager.AudioType.episode) {
						AsyncImage(
							model = audioManager.currentEpisode!!.image,
							contentDescription = audioManager.currentEpisode!!.podcastName,
							contentScale = ContentScale.Crop,
							modifier = Modifier
								.width(iconSize)
								.height(iconSize)
								.padding(all = iconPadding)
								.clip(RoundedCornerShape(8.dp))
						)
						Row(
							verticalAlignment = Alignment.CenterVertically,
							horizontalArrangement = Arrangement.spacedBy(8.dp)
						) {
							Spacer(modifier = Modifier.weight(1f))
							IconButton(
								onClick = {
									audioManager.skipBackwards()
								},
								modifier = Modifier
									.width(iconSize)
									.height(iconSize)
									.padding(all = iconPadding)
							) {
								Icon(
									painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_skip_back_15),
									contentDescription = "Play audio",
									tint = colorScheme.primary,
									modifier = Modifier
										.width(iconSize)
										.height(iconSize)
										.padding(all = iconPadding)
								)
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
									.width(iconSize)
									.height(iconSize)
									.padding(all = iconPadding)
							) {
								if (audioManager.playerState?.isPlaying == true) {
									Icon(
										painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_pause),
										contentDescription = "Pause audio",
										tint = colorScheme.primary,
										modifier = Modifier
											.width(iconSize)
											.height(iconSize)
											.padding(all = iconPadding)
									)
								} else {
									Icon(
										painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_play),
										contentDescription = "Play audio",
										tint = colorScheme.primary,
										modifier = Modifier
											.width(iconSize)
											.height(iconSize)
											.padding(all = iconPadding)
									)
								}
							}
							IconButton(
								onClick = {
									audioManager.skipForward()
								},
								modifier = Modifier
									.width(iconSize)
									.height(iconSize)
									.padding(all = iconPadding)
							) {
								Icon(
									painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_skip_forward_15),
									contentDescription = "Play audio",
									tint = colorScheme.primary,
									modifier = Modifier
										.width(iconSize)
										.height(iconSize)
										.padding(all = iconPadding)
								)
							}
							Spacer(modifier = Modifier.weight(1f))
						}
					} else {
						AsyncImage(
							model = data.streams?.audio[audioManager.currentStation ?: 0]?.artwork,
							contentDescription = data.streams?.audio[audioManager.currentStation ?: 0]?.name,
							contentScale = ContentScale.Crop,
							modifier = Modifier
								.width(iconSize)
								.height(iconSize)
								.padding(all = iconPadding)
								.clip(RoundedCornerShape(8.dp))
						)
						IconButton(
							onClick = {
								if (audioManager.playerState?.isPlaying == true) {
									audioManager.pause()
								} else {
									audioManager.play()
								}
							},
							modifier = Modifier
								.width(iconSize)
								.height(iconSize)
								.padding(all = iconPadding)
						) {
							if (audioManager.playerState?.isPlaying == true) {
								Icon(
									painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_pause),
									contentDescription = "Pause audio",
									tint = colorScheme.primary,
									modifier = Modifier
										.width(iconSize)
										.height(iconSize)
										.padding(all = iconPadding)
								)
							} else {
								Icon(
									painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_play),
									contentDescription = "Play audio",
									tint = colorScheme.primary,
									modifier = Modifier
										.width(iconSize)
										.height(iconSize)
										.padding(all = iconPadding)
								)
							}
						}
						Row(
							verticalAlignment = Alignment.CenterVertically
						) {
							Column(
								modifier = Modifier.weight(1f)
							) {
								data.streams?.audio[audioManager.currentStation ?: 0]?.name?.let {
									Text(
										text = it,
										color = colorScheme.onSurface,
										fontSize = 14.sp,
										lineHeight = 16.sp,
										fontWeight = FontWeight.Bold,
										modifier = Modifier
											.padding(
												start = 4.dp,
												end = 4.dp,
												top = 0.dp,
												bottom = 0.dp
											)
									)
								}
								data.nowPlaying?.radio[audioManager.currentStation ?: 0]?.let {
									Text(
										text = nowPlayingCleanup(it),
										color = colorScheme.onSurface,
										fontSize = 12.sp,
										lineHeight = 14.sp,
										modifier = Modifier
											.padding(
												start = 4.dp,
												end = 4.dp,
												top = 0.dp,
												bottom = 0.dp
											)
									)
								}
							}
						}
					}

					if (showBottomSheet) {
						ModalBottomSheet(
							onDismissRequest = {
								showBottomSheet = false
							},
							sheetState = sheetState
						) {
							Column(
								horizontalAlignment = Alignment.CenterHorizontally,
								verticalArrangement = Arrangement.spacedBy(8.dp),
								modifier = Modifier.padding(all = 8.dp)
							) {
								ModalPlayerPopup(audioManager, data, iconPadding)
								Spacer(modifier = Modifier.height(50.dp))
								Button(onClick = {
									scope.launch { sheetState.hide() }.invokeOnCompletion {
										if (!sheetState.isVisible) {
											showBottomSheet = false
										}
									}
								}) {
									Text("Dismiss")
								}
							}
						}
					}
				},
				floatingActionButton = {
					FloatingActionButton(
						onClick = {
							showBottomSheet = true
							Log.d("NowPlayingSnackbar", "showBottomSheet")
						},
						containerColor = colorScheme.primaryContainer,
						elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
					) {
						Icon(
							painter = painterResource(R.drawable.keyboard_arrow_up),
							"Localized description"
						)
					}
				},
				windowInsets = WindowInsets(0, 0, 0, 0)
			)
		}
	}
}
