# ANANVI AI — Unified Platform Implementation Plan

## Product architecture

AnanviAI is the Android operator console. JARVIS-X is the orchestration/control-plane service. NousResearch Hermes Agent is a separately authenticated specialist-agent runtime. Existing creator and AI tools products remain independent services connected through explicit adapters.

## Existing foundations verified

- Android: Kotlin, Jetpack Compose, MVVM, Room, DataStore, Hilt, Retrofit/SSE and Android Keystore-backed encrypted provider-key storage.
- Command Center: social setup links, GitHub repository shortcuts, persisted JARVIS/Hermes endpoint configuration and HTTPS reachability checks.
- JARVIS-X: FastAPI and local Ollama model integration.
- ANANVIAICLIPPER: Next.js creator app with server-side AssemblyAI transcription and OpenAI analysis.

## Implementation status by selected capability

| Capability | Current status | Next integration |
|---|---|---|
| Advanced JARVIS Command Center | Android screen and endpoint setup | Authenticated live task/status API client |
| Hermes multi-agent workforce | Hermes endpoint setup and architecture | Authenticated adapter, agent inventory and bounded task dispatch |
| GitHub + Vercel portfolio | Repository shortcuts | OAuth/GitHub App and Vercel project/deployment sync |
| YouTube/social automation | Provider setup catalogue | OAuth callbacks, account scopes, content queue, approval and publishing |
| Visual workflow builder | Planned | Versioned workflow graph, scheduler, typed node runner |
| Obsidian/long-term memory | JARVIS namespaced memory API foundation | Obsidian vault connector, sync, retention and user controls |
| AI model/API hub | Existing multi-provider Android catalogue and encrypted keys | Shared model registry, health checks, quotas and cost tracking |
| AI website/SaaS builder | Planned | Sandboxed code generation, tests, preview, review and deployment |
| Business OS/financial intelligence | Planned | Business workspaces, read-only data connectors, ledger and reports |
| Voice/desktop control | Existing Android speech recognition | Authenticated voice sessions and separately permissioned desktop agent |
| Security vault/permissions/audit | Android encrypted provider keys; JARVIS control-plane security foundation in companion PR | Server-side secret manager, RBAC, OIDC, rotation and full audit coverage |
| Infrastructure/GPU monitoring | Planned | Host metrics exporter, GPU metrics and alerting |
| Revenue/affiliate automation | Planned | Provider integrations, reconciliation and approval-gated transactions |
| Kill switch/disaster recovery | JARVIS persisted kill-switch foundation in companion PR | Enforce in every executor, encrypted backups and restore drills |

## Companion backend implementation

The JARVIS-X control-plane implementation is proposed in [New-JARVIS-X PR #2](https://github.com/DarkRider999/New-JARVIS-X/pull/2). It adds bearer-protected task proposals, approval/rejection, durable namespaced notes, audit events and a persistent global kill-switch state. It deliberately does not execute tasks.

## Security invariants

1. Preserve existing Android provider IDs and encrypted API key records. Do not reset or migrate credentials without a tested migration.
2. Provider API keys, OAuth secrets, refresh tokens and machine credentials stay server-side or in Android Keystore as appropriate; never put secrets in source code, APK resources, browser local storage or public environment variables.
3. Every external mutation (publish, send, delete, spend, repository write, deploy, desktop control) requires a preview and explicit user approval.
4. Hermes tools must be allowlisted, scoped, sandboxed, time-bounded and audited.
5. A global kill switch must be checked by every executor immediately before dispatch and between long-running steps.
6. Android-to-backend access uses authenticated TLS or private VPN. Ollama remains private.
7. Social integrations must obey provider app review, scopes, rate limits, quotas and platform policies.

## Release gates

- Backend unit and integration tests pass.
- Android Gradle build and tests pass.
- Provider credentials are supplied through secure configuration; no secrets are committed.
- OAuth callback and token refresh tests pass for each provider.
- Hermes adapter tests use a non-production instance and verify denied tools remain blocked.
- Kill switch is verified to block every executor.
- Backup restore is tested before production memory or business records are stored.
- Pull requests remain unmerged until reviewed and required checks pass.

This plan describes a staged implementation. A listed capability is not considered live merely because a setup card, endpoint, documentation entry or placeholder exists.
