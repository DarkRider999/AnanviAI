# AnanviAI - Multi-Model Android Assistant

AnanviAI is a production-grade, enterprise-ready Android AI assistant application built with modern Android development standards (Kotlin 2.0, Jetpack Compose, Material 3, Clean Architecture, Hilt DI, Room Database, and Retrofit with SSE streaming).

---

## 🌟 Key Features

1. **Multi-Model Intelligence Hub**
   - Direct integration with **Hugging Face Inference API**, **OpenRouter**, **Groq LPU**, **Ollama Local Server**, **Together AI**, and **OpenAI**.
   - Zero-cost inference support for free open-source models:
     - `meta-llama/Llama-3.1-8B-Instruct`
     - `meta-llama/Llama-3.3-70B-Instruct`
     - `deepseek-ai/DeepSeek-R1`
     - `Qwen/Qwen2.5-7B-Instruct`
     - `mistralai/Mistral-7B-Instruct-v0.3`
     - `google/gemma-2-9b-it`
     - `microsoft/phi-4`

2. **Real-Time Reactive Streaming**
   - High-throughput Server-Sent Events (SSE) parsing via OkHttp and Kotlin Coroutines `Flow<String>`.
   - Markdown rendering with inline formatting, headers, lists, and code blocks.
   - Syntax-highlighted code blocks with automatic language detection and one-tap copy button.

3. **Side-by-Side Model Battle**
   - Concurrently dispatch user prompts to two distinct AI models.
   - Compare response depth, formatting, and latency in milliseconds.

4. **Productivity AI Suite**
   - Summarization, text rewriter/polisher, code generation, code explanation, translation, and social media draft creator.

5. **AI Image Studio**
   - Latent diffusion generation supporting FLUX.1 Schnell, Stable Diffusion XL, and DALL-E 3 with custom aspect ratio selection (1:1, 16:9, 9:16).

6. **Voice Assistant**
   - Integrated Speech-to-Text (`SpeechRecognizer`) with real-time audio wave animation.

7. **Hardware-Backed Security**
   - Encrypted API key storage via Android Keystore (`EncryptedSharedPreferences` AES-256 GCM).

---

## 🏗️ Architecture

```
app/src/main/java/com/aiuniverse/app/
├── common/                  # Resource, DispatcherProvider, Constants
├── security/                # SecureKeyStorage (Keystore AES-256 GCM)
├── di/                      # Hilt Modules (App, Database, Network, Repository)
├── data/
│   ├── local/               # Room Entities, DAOs, Converters, DataStore
│   ├── remote/              # Retrofit APIs, DTOs, SSE Parser, Auth Interceptor
│   └── repository/          # Repository Implementations
├── domain/
│   ├── model/               # Domain Models
│   ├── repository/          # Clean Architecture Repository Interfaces
│   └── usecase/             # Business Logic & Orchestration Use Cases
└── presentation/
    ├── theme/               # Cosmic Colors, Glassmorphism, Typography, Shapes
    ├── components/          # GlassCard, MarkdownText, CodeBlockView, AudioWave
    ├── navigation/          # Screen Routes & Animated NavGraph
    ├── splash/              # Animated Cosmic Splash Screen
    ├── onboarding/          # Multi-page Feature Onboarding
    ├── home/                # Main Hub & Quick Capabilities
    ├── chat/                # Real-Time Streaming Chat
    ├── history/             # Conversation History & Search
    ├── models/              # Provider & Model Catalog
    ├── compare/             # Side-by-Side Model Comparison
    ├── imagegen/            # Latent Diffusion Art Generation
    ├── tools/               # Productivity AI Tools
    ├── settings/            # Theme, Haptics, Sampling Sliders
    ├── profile/             # Custom Persona System Prompt & Telemetry
    ├── apimanagement/       # Secure API Key Management
    └── voice/               # Voice Modal Sheet with Visualizer
```

---

## ANANVI AI Command Center

The Android app now includes a Command Center entry point for the social/video connector setup, GitHub project shortcuts, and JARVIS-X / Hermes endpoint checks. Service URLs are stored in DataStore; API credentials and OAuth tokens are not stored by this screen.

The initial GitHub project list is a snapshot of repositories visible to the connected GitHub account. Dynamic private-repository sync requires a GitHub App/OAuth integration in the secured JARVIS-X backend.

See [Command Center integration architecture and security plan](docs/COMMAND_CENTER_INTEGRATION.md) before enabling remote services or provider publishing.

## 🚀 Getting Started

### Prerequisites
- **Android Studio Ladybug (2024.2.1+) or newer**
- **JDK 17 or JDK 21**
- **Android SDK Platform 35**

### Opening the Project
1. Open Android Studio.
2. Select **Open** and choose the directory:
   `/home/openclaw/.gemini/antigravity/scratch/ai-universe`
3. Allow Gradle to sync dependencies.
4. Run the app on an Android Emulator (API 26+) or physical device.

---

## Free-first provider catalogue

The built-in catalogue includes existing integrations and these additional OpenAI-compatible providers:

| Provider | Access type | API setup |
|---|---|---|
| Google Gemini | Limited free tier for selected models | [Google AI Studio](https://aistudio.google.com/apikey) |
| NVIDIA NIM | Selected catalog endpoints are marked free; availability varies | [NVIDIA Build](https://build.nvidia.com/) |
| Cloudflare Workers AI | Workers Free includes 10,000 Neurons/day; model restrictions apply | [Cloudflare Dashboard](https://dash.cloudflare.com/) |
| Z.AI | GLM-4.7-Flash and selected GLM Flash models are listed as free; limits are shown in the console | [Z.AI API Keys](https://z.ai/manage-apikey/apikey-list) |
| SambaNova Cloud | Free access/quotas depend on current account eligibility | [SambaNova Cloud](https://cloud.sambanova.ai/) |
| Cohere | Trial/evaluation access only; not a perpetual free tier | [Cohere API Keys](https://dashboard.cohere.com/api-keys) |
| OpenRouter | Models ending in `:free` have a limited free quota | [OpenRouter Keys](https://openrouter.ai/keys) |
| Groq | Free plan with per-model limits | [Groq Console](https://console.groq.com/keys) |

Existing integrations remain available: Hugging Face, OpenRouter, Groq, Together AI, Ollama, OpenAI, Anthropic and Gemini. Existing provider IDs are retained so saved credentials remain associated with their original providers.

### Cloudflare key format

Cloudflare requires both an account ID and an API token. In the Cloudflare API key field, enter:

`ACCOUNT_ID|API_TOKEN`

The combined value is stored locally in the encrypted credential store. Use a token with the Workers AI permission required by Cloudflare's API.

### Free does not mean unlimited

Provider quotas, eligible models, account requirements, rate limits and data-use terms change. The app labels limited/trial access separately and does not promise unlimited or permanently free inference. Check each provider's current dashboard before relying on a model in production.

### Credential security and migration

- Existing provider IDs and saved keys are preserved; new providers use new, separate key IDs.
- Credentials remain local to the Android app and are not committed to this repository.
- API keys are stored with Android Keystore-backed EncryptedSharedPreferences.
- Existing encrypted keys are migrated to the current encrypted store. Legacy plaintext-fallback entries, if present from an older build, are copied into the encrypted store before their legacy entries are removed.
- If Android Keystore cannot be initialized, the app fails closed instead of silently saving new credentials in plaintext.
- Credential preference files are excluded from Android cloud backup and device transfer.
- Never paste API keys into GitHub issues, commits, screenshots or logs.

### Existing providers and paid services

OpenAI, Anthropic, Together AI and some models available through other providers may incur charges. They remain in the app for compatibility with your existing credentials; they are not represented as free services.

