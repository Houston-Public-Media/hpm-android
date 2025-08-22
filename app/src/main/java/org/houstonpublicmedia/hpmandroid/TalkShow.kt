package org.houstonpublicmedia.hpmandroid

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TalkShowView(data: StationData, audioManager: AudioManager) {
	//val alert = data.priorityData?.talkshow
	val alert = "hello-houston"
	val showName: String = if (alert == "hello-houston") "Hello Houston" else "Houston Matters"
	val accentColor: Color = if (alert == "hello-houston") Color(0xFFC8102E) else Color(0xFFFF7E27)
	val textColor: Color = if (alert == "hello-houston") Color(0xFF000000) else Color(0xFFFFFFFF)
	val buttonText: Color = if (alert == "hello-houston") Color(0xFFFFFFFF) else Color(0xFF000000)
	val backgroundColor: Color = if (alert == "hello-houston") Color(0xFFFF7E27) else Color(0xFF234A93)
	val email: String = if (alert == "hello-houston") "talk@hellohouston.org" else "talk@houstonmatters.org"
	var expanded by remember { mutableStateOf(false) }
	val uriHandler = LocalUriHandler.current
	val ctx = LocalContext.current
	Row(
		modifier = Modifier
			.background(backgroundColor)
	) {
		Row(
			verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
			modifier = Modifier.padding(all = 8.dp)
		) {
			Text(
				"$showName is on the air!",
				color = textColor,
				fontWeight = FontWeight.Bold,
			)
			Spacer(modifier = Modifier.weight(1f))
			Box {
				Button(
					onClick = { expanded = !expanded },
					colors = ButtonDefaults.buttonColors(
						containerColor = accentColor,
						contentColor = buttonText
					)
				) {
					Text("Interact")
				}
				DropdownMenu(
					expanded = expanded,
					onDismissRequest = { expanded = false }
				) {
					DropdownMenuItem(
						text = { Text("Call") },
						onClick = {
							val p = "tel:7134408870".toUri()
							val i = Intent(Intent.ACTION_DIAL, p)
							ctx.startActivity(i)
							Log.d(
								"TalkShowView",
								"Call Houston Public Media"
							)
						},
						trailingIcon = {
							Icon(
								painter = painterResource(id = R.drawable.rounded_call_24),
								contentDescription = null
							)
						}
					)
					DropdownMenuItem(
						text = { Text("Text") },
						onClick = {
							val i = Intent(Intent.ACTION_SENDTO)
							i.data = "smsto:7134408870".toUri()
							ctx.startActivity(i)
							Log.d(
								"TalkShowView",
								"Text Houston Public Media"
							)
						},
						trailingIcon = {
							Icon(
								painter = painterResource(id = R.drawable.rounded_chat_24),
								contentDescription = null
							)
						}
					)
					DropdownMenuItem(
						text = { Text("Email") },
						onClick = {
							val i = Intent(Intent.ACTION_SENDTO)
							i.setData("mailto:".toUri())
							i.putExtra(Intent.EXTRA_EMAIL, email)
							i.putExtra(Intent.EXTRA_SUBJECT, "Re: Today's $showName")
							ctx.startActivity(i)
							Log.d(
								"TalkShowView",
								"Email Houston Public Media"
							)
						},
						trailingIcon = {
							Icon(
								painter = painterResource(id = R.drawable.rounded_mail_24),
								contentDescription = null
							)
						}
					)
					DropdownMenuItem(
						text = { Text("Listen") },
						onClick = {
							if (audioManager.playerState?.isPlaying == true && audioManager.currentStation == 0) {
								audioManager.pause()
							} else {
								audioManager.startAudio(
									audioType = AudioManager.AudioType.Stream,
									station = data.streams?.audio[0],
									nowPlaying = data.nowPlaying?.radio[0],
									episode = null
								)
								audioManager.currentStation = 0
								audioManager.audioType = AudioManager.AudioType.Stream
								audioManager.currentEpisode = null
							}
						},
						trailingIcon = {
							Icon(
								painter = painterResource(id = R.drawable.rounded_headphones_24),
								contentDescription = null
							)
						}
					)
					DropdownMenuItem(
						text = { Text("Watch") },
						onClick = {
							uriHandler.openUri("https://www.youtube.com/@HoustonPublicMedia/streams")
						},
						trailingIcon = {
							Icon(
								painter = painterResource(id = R.drawable.rounded_live_tv_24),
								contentDescription = null
							)
						}
					)
				}
			}
		}
	}
}