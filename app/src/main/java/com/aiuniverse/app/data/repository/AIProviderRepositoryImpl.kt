package com.aiuniverse.app.data.repository

import com.aiuniverse.app.data.local.dao.AIProviderDao
import com.aiuniverse.app.data.local.entity.AIProviderEntity
import com.aiuniverse.app.domain.model.AIModel
import com.aiuniverse.app.domain.model.AIProvider
import com.aiuniverse.app.domain.repository.AIProviderRepository
import com.aiuniverse.app.security.SecureKeyStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIProviderRepositoryImpl @Inject constructor(
    private val providerDao: AIProviderDao,
    private val keyStorage: SecureKeyStorage
) : AIProviderRepository {

    private val staticProviders = listOf(
        AIProvider(
            id = "huggingface",
            name = "Hugging Face",
            description = "Open source hub with tens of thousands of free & specialized AI models.",
            baseUrl = "https://api-inference.huggingface.co",
            requiresApiKey = true,
            defaultModel = "meta-llama/Llama-3.1-8B-Instruct",
            websiteUrl = "https://huggingface.co/settings/tokens"
        ),
        AIProvider(
            id = "openrouter",
            name = "OpenRouter",
            description = "Unified gateway providing access to DeepSeek, Llama, Claude, and dozens of free models.",
            baseUrl = "https://openrouter.ai/api/v1",
            requiresApiKey = true,
            defaultModel = "meta-llama/llama-3.1-8b-instruct:free",
            websiteUrl = "https://openrouter.ai/keys"
        ),
        AIProvider(
            id = "groq",
            name = "Groq LPU",
            description = "Ultra-fast hardware LPUs offering lightning inference speeds (>500 tokens/sec).",
            baseUrl = "https://api.groq.com/openai/v1",
            requiresApiKey = true,
            defaultModel = "llama-3.3-70b-versatile",
            websiteUrl = "https://console.groq.com/keys"
        ),
        AIProvider(
            id = "together",
            name = "Together AI",
            description = "Decentralized cloud platform for open-source AI models.",
            baseUrl = "https://api.together.xyz/v1",
            requiresApiKey = true,
            defaultModel = "meta-llama/Meta-Llama-3.1-70B-Instruct-Turbo",
            websiteUrl = "https://api.together.ai/settings/api-keys"
        ),
        AIProvider(
            id = "ollama",
            name = "Ollama Local",
            description = "Run private, offline local LLMs directly on your network/device.",
            baseUrl = "http://10.0.2.2:11434",
            requiresApiKey = false,
            defaultModel = "llama3.1",
            websiteUrl = "https://ollama.com"
        ),
        AIProvider(
            id = "openai",
            name = "OpenAI",
            description = "Industry standard models including GPT-4o, GPT-4o-mini, and DALL-E 3.",
            baseUrl = "https://api.openai.com/v1",
            requiresApiKey = true,
            defaultModel = "gpt-4o-mini",
            websiteUrl = "https://platform.openai.com/api-keys"
        ),
        AIProvider(
            id = "anthropic",
            name = "Anthropic",
            description = "Claude 3.5 Sonnet and Haiku via OpenRouter or direct endpoint.",
            baseUrl = "https://openrouter.ai/api/v1",
            requiresApiKey = true,
            defaultModel = "anthropic/claude-3.5-sonnet",
            websiteUrl = "https://console.anthropic.com"
        ),
        AIProvider(
            id = "gemini",
            name = "Google Gemini",
            description = "Direct Google AI Studio API. Includes limited free-tier Gemini models; quotas and data terms apply.",
            baseUrl = "https://generativelanguage.googleapis.com/v1beta/openai",
            requiresApiKey = true,
            defaultModel = "gemini-3.8-flash",
            websiteUrl = "https://aistudio.google.com/apikey"
        ),
        AIProvider(
            id = "nvidia",
            name = "NVIDIA NIM",
            description = "NVIDIA hosted model catalog. Only use endpoints explicitly marked free in the NVIDIA catalog; other models may require paid access.",
            baseUrl = "https://integrate.api.nvidia.com/v1",
            requiresApiKey = true,
            defaultModel = "openai/gpt-oss-20b",
            websiteUrl = "https://build.nvidia.com/"
        ),
        AIProvider(
            id = "sambanova",
            name = "SambaNova Cloud",
            description = "OpenAI-compatible inference API. Free access and daily/model quotas depend on current account eligibility.",
            baseUrl = "https://api.sambanova.ai/v1",
            requiresApiKey = true,
            defaultModel = "DeepSeek-R1",
            websiteUrl = "https://cloud.sambanova.ai/"
        ),
        AIProvider(
            id = "cohere",
            name = "Cohere (Trial)",
            description = "Evaluation/trial API access only; not a guaranteed ongoing free tier. Check model pricing and trial limits before use.",
            baseUrl = "https://api.cohere.ai/compatibility/v1",
            requiresApiKey = true,
            defaultModel = "command-r7b-12-2024",
            websiteUrl = "https://dashboard.cohere.com/api-keys"
        ),
        AIProvider(
            id = "cloudflare",
            name = "Cloudflare Workers AI",
            description = "Free Workers plan includes 10,000 Neurons/day. Requires Account ID and API token entered as ACCOUNT_ID|API_TOKEN.",
            baseUrl = "https://api.cloudflare.com/client/v4/accounts/{account_id}/ai/v1",
            requiresApiKey = true,
            defaultModel = "@cf/zai-org/glm-4.7-flash",
            websiteUrl = "https://dash.cloudflare.com/"
        ),
        AIProvider(
            id = "zai",
            name = "Z.AI",
            description = "OpenAI-compatible API with free GLM Flash models. Usage limits are shown in the provider console and can change.",
            baseUrl = "https://api.z.ai/api/paas/v4",
            requiresApiKey = true,
            defaultModel = "glm-4.7-flash",
            websiteUrl = "https://z.ai/manage-apikey/apikey-list"
        )
    )

    private val staticModels = listOf(
        // Hugging Face
        AIModel(
            id = "meta-llama/Llama-3.1-8B-Instruct",
            name = "Llama 3.1 8B Instruct",
            providerId = "huggingface",
            description = "Meta's highly optimized, state-of-the-art open model with 128k context.",
            contextWindow = 131072,
            isFree = true,
            category = "General"
        ),
        AIModel(
            id = "deepseek-ai/DeepSeek-R1",
            name = "DeepSeek R1 Reasoning",
            providerId = "huggingface",
            description = "Breakthrough open reasoning model competing with OpenAI o1.",
            contextWindow = 65536,
            isFree = true,
            category = "Reasoning & Math"
        ),
        AIModel(
            id = "mistralai/Mistral-7B-Instruct-v0.3",
            name = "Mistral 7B Instruct",
            providerId = "huggingface",
            description = "Fast, versatile European language model with function calling.",
            contextWindow = 32768,
            isFree = true,
            category = "General"
        ),
        AIModel(
            id = "Qwen/Qwen2.5-7B-Instruct",
            name = "Qwen 2.5 7B",
            providerId = "huggingface",
            description = "Alibaba's benchmark leader in math, coding, and multilingual tasks.",
            contextWindow = 32768,
            isFree = true,
            category = "Multilingual"
        ),

        // OpenRouter Free Models
        AIModel(
            id = "meta-llama/llama-3.1-8b-instruct:free",
            name = "Llama 3.1 8B (Free)",
            providerId = "openrouter",
            description = "Community free tier with zero API cost on OpenRouter.",
            contextWindow = 131072,
            isFree = true,
            category = "Free Tier"
        ),
        AIModel(
            id = "deepseek/deepseek-r1:free",
            name = "DeepSeek R1 (Free)",
            providerId = "openrouter",
            description = "Full reasoning traces and deep analytical breakdown without tokens cost.",
            contextWindow = 65536,
            isFree = true,
            category = "Free Reasoning"
        ),
        AIModel(
            id = "qwen/qwen-2.5-coder-32b-instruct",
            name = "Qwen 2.5 Coder 32B",
            providerId = "openrouter",
            description = "Elite coding model rivaling proprietary code generators.",
            contextWindow = 32768,
            isFree = false,
            category = "Code & Tech"
        ),

        // Groq
        AIModel(
            id = "llama-3.3-70b-versatile",
            name = "Llama 3.3 70B (Groq LPUs)",
            providerId = "groq",
            description = "Extreme inference performance at over 300 words per second.",
            contextWindow = 128000,
            isFree = true,
            category = "Ultra Fast"
        ),
        AIModel(
            id = "deepseek-r1-distill-llama-70b",
            name = "DeepSeek R1 Distill 70B",
            providerId = "groq",
            description = "Reasoning model running with sub-second time-to-first-token.",
            contextWindow = 128000,
            isFree = true,
            category = "Reasoning"
        ),
        AIModel(
            id = "gemma2-9b-it",
            name = "Gemma 2 9B (Groq)",
            providerId = "groq",
            description = "Google's lightweight, punchy conversational architecture.",
            contextWindow = 8192,
            isFree = true,
            category = "General"
        ),

        // Ollama Local
        AIModel(
            id = "llama3.1",
            name = "Llama 3.1 Local",
            providerId = "ollama",
            description = "Self-hosted local model running completely private on your LAN.",
            contextWindow = 8192,
            isFree = true,
            category = "Offline Private"
        ),
        AIModel(
            id = "deepseek-coder",
            name = "DeepSeek Coder Local",
            providerId = "ollama",
            description = "Private offline code assistant running through local Ollama instance.",
            contextWindow = 16384,
            isFree = true,
            category = "Offline Code"
        ),
        AIModel(
            id = "phi4",
            name = "Microsoft Phi-4 Local",
            providerId = "ollama",
            description = "Microsoft's 14B synthetic-data powerhouse running locally.",
            contextWindow = 16384,
            isFree = true,
            category = "Math & Reasoning"
        ),

        // OpenAI
        AIModel(
            id = "gpt-4o-mini",
            name = "GPT-4o Mini",
            providerId = "openai",
            description = "Fast, cost-efficient multimodal intelligence from OpenAI.",
            contextWindow = 128000,
            isFree = false,
            category = "Multimodal"
        ),
        AIModel(
            id = "gpt-4o",
            name = "GPT-4o",
            providerId = "openai",
            description = "Flagship high-intelligence multimodal model.",
            contextWindow = 128000,
            isFree = false,
            category = "Flagship"
        ),
        // Direct Google AI Studio free-tier models. Free quotas are limited and can change.
        AIModel(
            id = "gemini-3.8-flash",
            name = "Gemini 3.8 Flash",
            providerId = "gemini",
            description = "Google AI Studio free-tier model; limited quotas apply and free-tier data terms differ from paid usage.",
            contextWindow = 1000000,
            isFree = true,
            category = "Free Tier",
            isMultimodal = true
        ),
        AIModel(
            id = "gemini-3.1-flash-lite",
            name = "Gemini 3.1 Flash-Lite",
            providerId = "gemini",
            description = "Lightweight Gemini model listed with free input and output on the current free tier; quota applies.",
            contextWindow = 1000000,
            isFree = true,
            category = "Free Tier",
            isMultimodal = true
        ),
        // NVIDIA Build hosted free endpoint (check endpoint status in NVIDIA catalog).
        AIModel(
            id = "openai/gpt-oss-20b",
            name = "GPT-OSS 20B (NVIDIA)",
            providerId = "nvidia",
            description = "Open-weight model available for download; hosted API access and pricing depend on the selected NVIDIA endpoint.",
            contextWindow = 131072,
            isFree = false,
            category = "Hosted pricing varies"
        ),
        // SambaNova free access is account/quota dependent; availability is not guaranteed.
        AIModel(
            id = "DeepSeek-R1",
            name = "DeepSeek R1 (SambaNova)",
            providerId = "sambanova",
            description = "SambaNova hosted reasoning model. Check current dashboard quota and model availability.",
            contextWindow = 65536,
            isFree = true,
            category = "Limited Free Tier"
        ),
        // Cohere trial keys are evaluation-only, not a perpetual free API.
        AIModel(
            id = "command-r7b-12-2024",
            name = "Command R7B (Trial)",
            providerId = "cohere",
            description = "Cohere evaluation model. Trial access is limited; check pricing before production use.",
            contextWindow = 128000,
            isFree = false,
            category = "Trial"
        ),
        AIModel(
            id = "@cf/zai-org/glm-4.7-flash",
            name = "GLM 4.7 Flash (Cloudflare)",
            providerId = "cloudflare",
            description = "Listed by Cloudflare as available on the Workers Free plan. 10,000 Neurons/day shared across the account; model limits apply.",
            contextWindow = 128000,
            isFree = true,
            category = "Free Tier"
        ),
        AIModel(
            id = "@cf/google/gemma-4-26b-a4b-it",
            name = "Gemma 4 26B (Cloudflare)",
            providerId = "cloudflare",
            description = "Cloudflare Workers AI free-plan model; shared daily Neurons quota applies.",
            contextWindow = 128000,
            isFree = true,
            category = "Free Tier"
        ),
        AIModel(
            id = "@cf/nvidia/nemotron-3-120b-a12b",
            name = "Nemotron 3 120B (Cloudflare)",
            providerId = "cloudflare",
            description = "Cloudflare Workers AI free-plan model; shared daily Neurons quota applies.",
            contextWindow = 128000,
            isFree = true,
            category = "Free Tier"
        ),
        AIModel(
            id = "glm-4.7-flash",
            name = "GLM 4.7 Flash (Z.AI)",
            providerId = "zai",
            description = "Z.AI lists GLM-4.7-Flash as free for input and output; rate limits and allowance are shown in the account console.",
            contextWindow = 200000,
            isFree = true,
            category = "Free Tier"
        ),
        AIModel(
            id = "glm-4.5-flash",
            name = "GLM 4.5 Flash (Z.AI)",
            providerId = "zai",
            description = "Z.AI free Flash model; check current account quota and model lifecycle before use.",
            contextWindow = 128000,
            isFree = true,
            category = "Free Tier"
        )
    )

    override fun getProviders(): Flow<List<AIProvider>> {
        return providerDao.getAllProviders().map { entities ->
            val storedById = entities.associateBy { it.id }.toMutableMap()
            val merged = staticProviders.map { provider ->
                storedById.remove(provider.id)
                provider.copy(
                    isConfigured = !provider.requiresApiKey || keyStorage.hasApiKey(provider.id)
                )
            }
            // Preserve custom providers already stored in Room while always exposing new built-ins.
            merged + storedById.values.map { entity ->
                entity.toDomain().copy(
                    isConfigured = !entity.requiresApiKey || keyStorage.hasApiKey(entity.id)
                )
            }
        }
    }

    override fun getProvider(id: String): Flow<AIProvider?> {
        return providerDao.getProviderFlow(id).map { entity ->
            val p = staticProviders.find { it.id == id } ?: entity?.toDomain()
            p?.copy(isConfigured = !p.requiresApiKey || keyStorage.hasApiKey(p.id))
        }
    }

    override suspend fun getAvailableModels(providerId: String): List<AIModel> {
        return staticModels.filter { it.providerId == providerId }
    }

    override suspend fun getAllAvailableModels(): List<AIModel> {
        return staticModels
    }

    override fun getApiKey(providerId: String): String {
        return keyStorage.getApiKey(providerId)
    }

    override suspend fun saveApiKey(providerId: String, apiKey: String) {
        keyStorage.saveApiKey(providerId, apiKey)
        providerDao.updateConfigurationStatus(providerId, apiKey.isNotBlank())
    }

    override suspend fun removeApiKey(providerId: String) {
        keyStorage.removeApiKey(providerId)
        providerDao.updateConfigurationStatus(providerId, false)
    }

    override suspend fun testConnection(providerId: String): Boolean {
        // Returns true if provider either doesn't need key (like Ollama) or has key configured
        return if (providerId == "ollama") {
            true
        } else {
            keyStorage.hasApiKey(providerId)
        }
    }
}
