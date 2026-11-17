package com.aiuniverse.app.presentation.commandcenter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

private data class ProjectLink(val name: String, val description: String)

private val currentProjects = listOf(
    ProjectLink("AnanviAI", "Android multi-model AI assistant"),
    ProjectLink("New-JARVIS-X", "Local-first AI brain and GPU dashboard"),
    ProjectLink("ananvi-aicollection", "AI tools discovery website"),
    ProjectLink("ANANVIAICLIPPER", "AI clipping project"),
    ProjectLink("ai-universe", "AI application project"),
    ProjectLink("ananvi-cv-developer", "CV and career tools"),
    ProjectLink("claude-code-plugin", "Claude Code extensions"),
    ProjectLink("Kinni", "Application project"),
    ProjectLink("My-Project1", "Development project"),
    ProjectLink("desktop-tutorial", "GitHub desktop tutorial"),
    ProjectLink("agents-in-sdlc", "AI agents in software delivery"),
    ProjectLink("securing-your-code", "Code security project"),
    ProjectLink("task-dashboard", "Task dashboard"),
    ProjectLink("managing-work", "Work management project"),
    ProjectLink("copilot-hack", "Copilot project")
)

private data class SocialConnector(
    val name: String,
    val detail: String,
    val setupUrl: String
)

private val socialConnectors = listOf(
    SocialConnector("YouTube", "Upload videos, manage metadata and read channel analytics", "https://console.cloud.google.com/"),
    SocialConnector("Instagram", "Publish media and Reels for eligible professional accounts", "https://developers.facebook.com/"),
    SocialConnector("Facebook Pages", "Publish Page posts and manage Page content", "https://developers.facebook.com/"),
    SocialConnector("TikTok", "Direct posting or upload drafts, subject to app approval", "https://developers.tiktok.com/"),
    SocialConnector("LinkedIn", "Publish to authorized member or organization profiles", "https://www.linkedin.com/developers/"),
    SocialConnector("X", "Publish posts and read account data according to API access", "https://developer.x.com/"),
    SocialConnector("Threads", "Publish and manage authorized Threads content", "https://developers.facebook.com/docs/threads/")
)

private val connectorHttpClient = OkHttpClient.Builder()
    .connectTimeout(5, TimeUnit.SECONDS)
    .readTimeout(5, TimeUnit.SECONDS)
    .callTimeout(8, TimeUnit.SECONDS)
    .build()

@Composable
fun CommandCenterScreen(onNavigateBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()
    var jarvisUrl by rememberSaveable { mutableStateOf("") }
    var hermesUrl by rememberSaveable { mutableStateOf("") }
    var jarvisStatus by rememberSaveable { mutableStateOf("Not configured") }
    var hermesStatus by rememberSaveable { mutableStateOf("Not configured") }
    var checking by remember { mutableStateOf(false) }
    var message by rememberSaveable { mutableStateOf("Use a private HTTPS URL for your workstation services.") }

    fun checkEndpoint(baseUrl: String, path: String, onResult: (String) -> Unit) {
        scope.launch {
            checking = true
            val result = withContext(Dispatchers.IO) {
                try {
                    val normalized = baseUrl.trim().trimEnd('/')
                    require(normalized.startsWith("https://")) {
                        "Use HTTPS. Do not expose an unauthenticated local service over plain HTTP."
                    }
                    val request = Request.Builder().url(normalized + path).get().build()
                    connectorHttpClient.newCall(request).execute().use { response ->
                        when {
                            response.isSuccessful -> "Reachable"
                            response.code == 401 || response.code == 403 -> "Reachable · authentication required"
                            else -> "HTTP ${response.code}"
                        }
                    }
                } catch (error: Exception) {
                    error.message?.take(120) ?: "Connection failed"
                }
            }
            onResult(result)
            checking = false
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090D18)),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("ANANVI COMMAND CENTER", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Your AI, projects and publishing hub", color = Color(0xFF9AA8C4), fontSize = 12.sp)
                }
                Icon(Icons.Default.Link, contentDescription = null, tint = Color(0xFF8B72FF))
            }
        }

        item {
            SectionCard(title = "AI SYSTEMS", subtitle = "Connect the Android app to your workstation services") {
                OutlinedTextField(
                    value = jarvisUrl,
                    onValueChange = { jarvisUrl = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("JARVIS-X HTTPS base URL") },
                    placeholder = { Text("https://your-private-host.example") },
                    singleLine = true
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("JARVIS-X · $jarvisStatus", color = Color(0xFFB8C7E8), modifier = Modifier.weight(1f), fontSize = 12.sp)
                    TextButton(enabled = jarvisUrl.isNotBlank() && !checking, onClick = {
                        checkEndpoint(jarvisUrl, "/api/health") { jarvisStatus = it }
                    }) { Text("Test") }
                }
                OutlinedTextField(
                    value = hermesUrl,
                    onValueChange = { hermesUrl = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Hermes dashboard HTTPS URL") },
                    placeholder = { Text("https://your-private-host.example") },
                    singleLine = true
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Hermes · $hermesStatus", color = Color(0xFFB8C7E8), modifier = Modifier.weight(1f), fontSize = 12.sp)
                    TextButton(enabled = hermesUrl.isNotBlank() && !checking, onClick = {
                        checkEndpoint(hermesUrl, "/api/status") { hermesStatus = it }
                    }) { Text("Test") }
                }
                if (checking) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                Text(message, color = Color(0xFF8D9AB4), fontSize = 11.sp)
                Text(
                    "Security: this screen only checks service reachability. It does not store passwords, API keys or OAuth tokens. Keep JARVIS-X and Hermes behind HTTPS and authenticated access.",
                    color = Color(0xFFFFC66D), fontSize = 11.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { uriHandler.openUri("https://github.com/DarkRider999/New-JARVIS-X") }) {
                        Text("JARVIS-X repo")
                        Icon(Icons.Default.OpenInNew, null, modifier = Modifier.size(14.dp))
                    }
                    TextButton(onClick = { uriHandler.openUri("https://github.com/NousResearch/hermes-agent") }) {
                        Text("Hermes project")
                        Icon(Icons.Default.OpenInNew, null, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }

        item {
            SectionCard(title = "SOCIAL & VIDEO CONNECTORS", subtitle = "OAuth app approval and server-side credentials are required") {
                socialConnectors.forEach { connector ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { uriHandler.openUri(connector.setupUrl) }.padding(vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (connector.name == "YouTube") Icons.Default.SmartDisplay else Icons.Default.Share,
                            contentDescription = null,
                            tint = Color(0xFF8B72FF),
                            modifier = Modifier.size(22.dp)
                        )
                        Column(modifier = Modifier.weight(1f).padding(horizontal = 10.dp)) {
                            Text(connector.name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(connector.detail, color = Color(0xFF9AA8C4), fontSize = 10.sp, lineHeight = 14.sp)
                        }
                        Text("Setup", color = Color(0xFF63D9E8), fontSize = 11.sp)
                    }
                }
                Text(
                    "No posts are sent from this screen. Provider OAuth credentials and refresh tokens must live in the secured JARVIS-X backend, never in the Android APK.",
                    color = Color(0xFFFFC66D), fontSize = 11.sp
                )
            }
        }

        item {
            SectionCard(title = "GITHUB PROJECT UNIVERSE", subtitle = "15 repositories currently visible to the connected GitHub account") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Code, contentDescription = null, tint = Color(0xFF63D9E8))
                    Text("DarkRider999", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = { uriHandler.openUri("https://github.com/DarkRider999?tab=repositories") }) {
                        Text("All repositories")
                    }
                }
                currentProjects.forEach { project ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable {
                            uriHandler.openUri("https://github.com/DarkRider999/${project.name}")
                        }.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Code, contentDescription = null, tint = Color(0xFF8B72FF), modifier = Modifier.size(18.dp))
                        Column(modifier = Modifier.weight(1f).padding(horizontal = 10.dp)) {
                            Text(project.name, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                            Text(project.description, color = Color(0xFF9AA8C4), fontSize = 10.sp)
                        }
                        Icon(Icons.Default.OpenInNew, contentDescription = "Open repository", tint = Color(0xFF7F8BA5), modifier = Modifier.size(15.dp))
                    }
                }
            }
        }

        item {
            SectionCard(title = "AUTOMATION SAFETY", subtitle = "Human approval stays enabled for external actions") {
                Text(
                    "Publishing, deleting, replying, spending on ads, changing repository settings and deploying production builds should require an explicit approval. Every action should be logged with its account, destination, timestamp and result.",
                    color = Color(0xFFB8C7E8), fontSize = 12.sp, lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151C2D))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, color = Color(0xFF8F7BFF), fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
            Text(subtitle, color = Color(0xFF9AA8C4), fontSize = 11.sp)
            Spacer(Modifier.height(4.dp))
            content()
        }
    }
}
