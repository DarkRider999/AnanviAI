# ANANVI AI Command Center

## Purpose

The Android Command Center is the single control surface for:
- Social and video publishing integrations.
- The user's GitHub project portfolio.
- JARVIS-X as the central local AI service.
- Hermes Agent as a delegated specialist-agent runtime.

The current Android screen is an integration dashboard and connection tester. It does not claim that social accounts, publishing scopes, GitHub private-repository OAuth, or agent execution are connected until the required backend services and account authorizations are configured.

## System topology

```text
AnanviAI Android app
       |
       | HTTPS + authenticated session (target architecture)
       v
JARVIS-X FastAPI gateway (NVIDIA GPU workstation)
       |             |                  |
       |             |                  +-- GitHub API / repository index
       |             +-- Hermes Agent API / approvals / task status
       +-- Social OAuth broker + encrypted token store
              |-- YouTube Data API
              |-- Meta Graph API (Instagram / Facebook Pages / Threads)
              |-- TikTok Content Posting API
              |-- LinkedIn API
              +-- X API
```

JARVIS-X currently runs its API on `127.0.0.1:8000`, its Next.js dashboard on `localhost:3000`, and Ollama on `127.0.0.1:11434`. Do not expose the existing unauthenticated FastAPI service directly to the public internet. Add authentication, TLS, authorization checks, audit logs, and rate limits before allowing Android or remote clients to reach it.

Hermes Agent's web dashboard commonly uses port `9119`; its API/dashboard authentication must remain enabled. The current Android tester checks `GET /api/status` and treats 401/403 as reachable-but-authenticated, not as a successful login.

## Current Android implementation

- Command Center route and Home shortcut.
- Service URL fields for JARVIS-X and Hermes.
- Service URL values saved in Android Preferences DataStore.
- HTTPS-only reachability checks; no insecure LAN HTTP bypass.
- Repository shortcut list for the 15 repositories visible in the connected GitHub account at implementation time.
- Social connector setup links for YouTube, Instagram, Facebook Pages, TikTok, LinkedIn, X and Threads.
- No API keys, OAuth client secrets, refresh tokens or access tokens are embedded in the APK.

The GitHub list is a snapshot. Replace it with a server-side GitHub App/OAuth repository index to support automatic discovery, private repository access, and refresh.

## Social connector implementation requirements

All OAuth client secrets and long-lived provider tokens must be stored only by the JARVIS-X backend, encrypted at rest. The Android app should launch an authorization flow and receive only a short-lived, app-scoped session. Use OAuth state validation, PKCE where supported, exact redirect URI allowlists, minimum scopes, token rotation, revocation, and redacted logs.

| Platform | Initial scope |
|---|---|
| YouTube | Google OAuth; channel identity, upload, video metadata and permitted analytics |
| Instagram | Meta app review; eligible professional account publishing and insights |
| Facebook Pages | Meta login and Page permissions; publish only to authorized Pages |
| Threads | Meta Threads API permissions and app review |
| TikTok | Content Posting API; creator info, video/photo upload and post status |
| LinkedIn | LinkedIn developer app and approved member/organization posting products |
| X | X developer project and API access for post creation and account data |

### Platform-specific restrictions

- YouTube Data API has quota-based usage. Its documented default allocation includes separate daily buckets for search and video uploads plus a shared bucket for other methods; inspect the Google Cloud Console before scheduling volume.
- TikTok Direct Post requires an approved `video.publish` scope and user authorization. Unaudited clients are restricted to private viewing; TikTok's guidelines also restrict tools built solely for private/internal account management. Keep draft upload and manual completion as a fallback where approved.
- Instagram publishing is limited to eligible professional accounts and the permissions granted to the app.
- API access, review, quotas, and paid tiers vary by platform. Do not label a platform connector "free" merely because API documentation is public.

## Approval and audit model

Every publishing or external mutation starts as a draft. Require a human confirmation that shows:
- Target platform and account.
- Exact text, media and visibility.
- Scheduled or immediate publish time.
- Provider quota / rate-limit status when available.

Record action ID, actor, platform, account ID, request hash, timestamp, approval, provider response and final status. Never log OAuth tokens, API keys, media credentials or authorization headers.

Require explicit approval for publishing, deleting content, replying to comments, changing account settings, creating ad campaigns, spending money, repository writes, deployments and destructive agent tasks. Read-only status and analytics may run automatically within the granted scopes.

## JARVIS-X and Hermes integration milestones

1. **Secure gateway:** add API authentication, TLS deployment guidance, session issuance for Android, request limits and audit logging.
2. **GitHub:** use a GitHub App or OAuth authorization to list all repositories, read default branch / latest commit / CI / releases, and keep write scopes disabled until explicitly granted.
3. **Hermes:** add a server-side adapter for the authenticated Hermes API. Start with read-only status, sessions, jobs and tool inventory. Add task dispatch only after explicit approval and support approval/clarification hand-offs.
4. **Social OAuth:** implement each provider independently with provider-specific scopes and callback handling.
5. **Content queue:** draft once, create per-platform variants, preview, approve, schedule, publish and reconcile asynchronous status.
6. **Observability:** unified activity feed, retries with idempotency keys, failed-job queue and per-provider quota reporting.

## Local workstation networking

The current JARVIS-X API is bound to loopback. A phone cannot use the workstation's `127.0.0.1` as its own host. For remote access, prefer a private HTTPS endpoint through an authenticated VPN/reverse proxy (for example, a private Tailscale HTTPS service). Do not open ports 8000, 9119 or 11434 directly to the public internet.

## Reference documentation

- YouTube Data API: https://developers.google.com/youtube/data
- YouTube video upload: https://developers.google.com/youtube/data/guides/uploading_a_video
- Meta developer docs: https://developers.facebook.com/docs
- TikTok Content Posting API: https://developers.tiktok.com/docs/en/content-posting-api-get-started
- LinkedIn developer portal: https://www.linkedin.com/developers/
- X developer portal: https://developer.x.com/
- Hermes Agent: https://github.com/NousResearch/hermes-agent
- Hermes dashboard API reference: https://github.com/NousResearch/hermes-agent/blob/main/website/docs/user-guide/features/web-dashboard.md
- JARVIS-X: https://github.com/DarkRider999/New-JARVIS-X
