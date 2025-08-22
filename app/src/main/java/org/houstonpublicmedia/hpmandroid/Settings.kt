package org.houstonpublicmedia.hpmandroid

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri

@Composable
fun SettingsScreen(data: StationData) {
	val uriHandler = LocalUriHandler.current
	val ctx = LocalContext.current
	Column(
		modifier = Modifier
			.padding(all = 0.dp)
			.verticalScroll(rememberScrollState())
	) {
		DayWeather(data)
		Column(
			modifier = Modifier.padding(horizontal = 8.dp, vertical = 0.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				"Settings",
				modifier = Modifier.padding(start = 0.dp, end = 0.dp, top = 8.dp, bottom = 0.dp),
				fontWeight = FontWeight.Bold
			)
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.border(1.dp, colorScheme.outline, RoundedCornerShape(8.dp))
					.clip(RoundedCornerShape(8.dp))
					.background(colorScheme.surfaceBright)
			) {
				Row(
					modifier = Modifier.padding(all = 8.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					Text("Featured Categories")
					Spacer(modifier = Modifier.weight(1f))
					Icon(
						painter = painterResource(R.drawable.rounded_arrow_forward_ios_24),
						contentDescription = "Featured categories",
						tint = colorScheme.primary,
						modifier = Modifier.padding(horizontal = 4.dp).width(35.dp).height(35.dp)
					)
				}
			}
			Text(
				"Contact Us",
				modifier = Modifier.padding(start = 0.dp, end = 0.dp, top = 8.dp, bottom = 0.dp),
				fontWeight = FontWeight.Bold
			)
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.border(1.dp, colorScheme.outline, RoundedCornerShape(8.dp))
					.clip(RoundedCornerShape(8.dp))
					.background(colorScheme.surfaceBright)
			) {
				Row(
					modifier = Modifier.padding(all = 8.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					Text("Call Houston Public Media")
					Spacer(modifier = Modifier.weight(1f))
					Button(
						onClick = {
							val p = "tel:7137488888".toUri()
							val i = Intent(Intent.ACTION_DIAL, p)
							ctx.startActivity(i)
							Log.d(
								"SettingsView",
								"Call Houston Public Media"
							)
						},
						colors = ButtonDefaults.buttonColors(
							containerColor = Color(0xFFC8102E),
							contentColor = Color(0xFFFFFFFF)
						)
					) {
						Text("Call")
					}
				}
			}
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.border(1.dp, colorScheme.outline, RoundedCornerShape(8.dp))
					.clip(RoundedCornerShape(8.dp))
					.background(colorScheme.surfaceBright)
			) {
				Row(
					modifier = Modifier.padding(all = 8.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					Text("Call Member Services")
					Spacer(modifier = Modifier.weight(1f))
					Button(
						onClick = {
							val p = "tel:7137438483".toUri()
							val i = Intent(Intent.ACTION_DIAL, p)
							ctx.startActivity(i)
							Log.d(
								"SettingsView",
								"Call Member Services"
							)
						},
						colors = ButtonDefaults.buttonColors(
							containerColor = Color(0xFFC8102E),
							contentColor = Color(0xFFFFFFFF)
						)
					) {
						Text("Call")
					}
				}
			}
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.border(1.dp, colorScheme.outline, RoundedCornerShape(8.dp))
					.clip(RoundedCornerShape(8.dp))
					.background(colorScheme.surfaceBright)
			) {
				Row(
					modifier = Modifier.padding(all = 8.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					Text("Email Member Services")
					Spacer(modifier = Modifier.weight(1f))
					Button(
						onClick = {
							val i = Intent(Intent.ACTION_SENDTO)
							i.setData("mailto:".toUri())
							i.putExtra(Intent.EXTRA_EMAIL, "membership@houstonpublicmedia.org")
							i.putExtra(Intent.EXTRA_SUBJECT, "Question for Member Services")
							ctx.startActivity(i)
							Log.d(
								"SettingsView",
								"Email Houston Public Media"
							)
						},
						colors = ButtonDefaults.buttonColors(
							containerColor = Color(0xFFC8102E),
							contentColor = Color(0xFFFFFFFF)
						)
					) {
						Text("Email")
					}
				}
			}
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.border(1.dp, colorScheme.outline, RoundedCornerShape(8.dp))
					.clip(RoundedCornerShape(8.dp))
					.background(colorScheme.surfaceBright)
			) {
				Row(
					modifier = Modifier.padding(all = 8.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					Column {
						Text("Mailing Address", fontWeight = FontWeight.Bold)
						Text("4343 Elgin Street")
						Text("Houston, TX 77204")
					}
					Spacer(modifier = Modifier.weight(1f))
					Button(
						onClick = {
							uriHandler.openUri("https://maps.app.goo.gl/8uvYGsZvxuiGXugNA")
							Log.d(
								"SettingsView",
								"Open Map"
							)
						},
						colors = ButtonDefaults.buttonColors(
							containerColor = Color(0xFFC8102E),
							contentColor = Color(0xFFFFFFFF)
						)
					) {
						Text("Open Map")
					}
				}
			}
			Spacer(modifier = Modifier.height(50.dp))
			Text(
				"Made with ❤️ by Houston Public Media",
				fontSize = 10.sp,
				lineHeight = 12.sp,
				modifier = Modifier
					.align(Alignment.CenterHorizontally)
					.padding(all = 0.dp)
			)
			Text(
				"Version 0.1",
				fontSize = 10.sp,
				lineHeight = 12.sp,
				modifier = Modifier
					.align(Alignment.CenterHorizontally)
					.padding(all = 0.dp)
			)
		}
	}
}