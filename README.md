# AI Universe - Multi-Model Android Assistant

AI Universe is a production-grade, enterprise-ready Android AI assistant application built with modern Android development standards (Kotlin 2.0, Jetpack Compose, Material 3, Clean Architecture, Hilt DI, Room Database, and Retrofit with SSE streaming).

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

## 🔑 Setting Up Free Hugging Face & OpenRouter API Keys

1. Navigate to **API Keys** inside the app.
2. For Hugging Face:
   - Obtain a free User Access Token from: [https://huggingface.co/settings/tokens](https://huggingface.co/settings/tokens)
   - Paste the token into the Hugging Face section and tap **Save Key**.
3. For OpenRouter / Groq:
   - Obtain free keys from [https://openrouter.ai/keys](https://openrouter.ai/keys) or [https://console.groq.com/keys](https://console.groq.com/keys).
   - Enter and test the connection.
