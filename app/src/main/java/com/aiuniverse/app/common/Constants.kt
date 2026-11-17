package com.aiuniverse.app.common

object Constants {
    const val DATABASE_NAME = "ai_universe_db"
    const val PREFERENCES_NAME = "ai_universe_prefs"
    const val ENCRYPTED_PREFS_NAME = "ai_universe_secure_keys"

    // Endpoints
    const val HUGGING_FACE_BASE_URL = "https://api-inference.huggingface.co/"
    const val HUGGING_FACE_ROUTER_URL = "https://router.huggingface.co/hf-inference/v1/"
    const val OPENROUTER_BASE_URL = "https://openrouter.ai/api/v1/"
    const val GROQ_BASE_URL = "https://api.groq.com/openai/v1/"
    const val TOGETHER_BASE_URL = "https://api.together.xyz/v1/"
    const val OLLAMA_DEFAULT_BASE_URL = "http://10.0.2.2:11434/"
    const val OPENAI_BASE_URL = "https://api.openai.com/v1/"

    // Default Models
    const val DEFAULT_HF_MODEL = "meta-llama/Llama-3.1-8B-Instruct"
    const val DEFAULT_GROQ_MODEL = "llama-3.3-70b-versatile"
    const val DEFAULT_OPENROUTER_MODEL = "meta-llama/llama-3.1-8b-instruct:free"

    // SSE Stream Delimiters
    const val SSE_DATA_PREFIX = "data: "
    const val SSE_DONE = "[DONE]"
}
