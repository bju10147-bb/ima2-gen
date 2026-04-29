# ima2-gen — AI Context

## What This Project Does
GPT Image 2 (gpt-image-2) 이미지 생성기 CLI + 웹 UI
- OAuth (ChatGPT 계정) 또는 API Key 인증 지원
- 텍스트→이미지, 이미지→이미지(편집) 생성
- 병렬 생성 (최대 8장)

## Tech Stack
- Runtime: Node.js >=20 (ES Module)
- Server: Express 5
- API Client: OpenAI SDK v5
- OAuth: openai-oauth (ChatGPT 세션 프록시)
- Frontend: React + Vite (`ui/src`, built to `ui/dist`)

## Project Structure
```
image_gen/
├── bin/                  # CLI entry + subcommands
├── server.js             # Express bootstrap / static UI serving
├── config.js             # Runtime config
├── routes/               # API route modules (`*.ts` source)
├── lib/                  # Server helpers (`*.ts` source + emitted/legacy `*.js`)
├── ui/src/               # React/Vite app source
├── ui/dist/              # Built frontend served by server.js
├── site/                 # Astro marketing/docs site
├── integrations/comfyui/ # ComfyUI bridge/custom node
├── structure/            # Current architecture reference docs
├── devlog/               # `_plan`, `_fin`, `_spikes`
├── tests/                # node:test contracts/regressions
└── package.json
```

## Devlog Phase Roadmap
- Current active plans live under `devlog/_plan/`.
- Completed plans live under `devlog/_fin/`.
- Legacy phase docs live under `devlog/_plan/_legacy/`.
- Use `structure/07-devlog-map.md` and `devlog/_plan/README.md` as the current roadmap references.

## Conventions
- ES Module only (import/export)
- File length < 500 lines (split if exceeded)
- Function length < 50 lines
- try/catch mandatory for all async operations
- Config values in config.js or .env, never hardcode

## Test Command
```bash
npm test
cd ui && npx tsc -b --noEmit
cd ui && npm run build
```

## Heartbeat
- 20분마다 devlog/_plan 점검 및 다음 작업 제안
- 완료된 phase는 _fin/으로 이동 (YYMMDD_ prefix)

## Android App Development Plan (Based on ima2-gen)

### 1. Project Goal
- `ima2-gen` 서버(Node.js/Express)를 백엔드로 활용하는 네이티브 안드로이드 클라이언트 앱 개발.

### 2. Core Integration Points
- **API Client**: `ima2-gen`의 REST API(이미지 생성, 히스토리 조회 등)와 통신. (Retrofit2 활용)
- **Client-side Authentication (BYOK)**: 
    - 서버의 전역 인증에 의존하지 않고, 사용자가 직접 자신의 개인 **OpenAI API Key**를 입력하여 인증하는 방식을 채택.
    - **Security**: `EncryptedSharedPreferences` 및 `Android Keystore`를 활용하여 API 키를 기기 내 보안 영역에 암호화 보관.
    - **Beginner Guide**: 초보 사용자를 위해 앱 내에서 API 키 발급 방법 가이드(스크린샷 포함) 및 발급 페이지 다이렉트 링크 제공.
    - **Smart Input**: 클립보드 자동 감지 기능을 통해 입력 편의성 극대화.
- **State Sync**: 이미지 생성 진행 상태(진척률, 완료 여부 등)를 백엔드로부터 폴링 또는 소켓으로 받아와 모바일 UI에 반영.

### 3. Key Features (MVP)
- **프롬프트 입력 및 생성**: Text-to-Image 및 Image-to-Image 생성 요청.
- **갤러리 뷰**: 로컬 서버의 SQLite에 저장된 생성 히스토리를 불러와 그리드 형태로 표시.
- **이미지 뷰어 및 다운로드**: 생성된 이미지를 확인하고 안드로이드 기기에 저장하거나 공유.
- **모바일 최적화**: Classic 모드를 우선 구현하고 Node/Canvas 모드는 추후 모바일 UX에 맞게 단계적 도입.

### 4. Policy & Security Compliance
- **Google Play Compliance**: 
    - WebView를 통한 세션 가로채기 방식(OAuth)은 정책상 금지되므로 배제하고 API Key 방식을 우선함.
    - **AI UGC Policy**: AI 생성물에 대한 신고/차단 기능 및 부적절한 콘텐츠 필터링 필수 적용.
    - **Data Privacy**: 사용자 API Key는 서버로 전송/저장되지 않으며 클라이언트 보안 영역에만 유지됨을 명시.

### 5. Next Action Items
1. `routes/` 및 `server.ts` 내의 API 엔드포인트 규격 분석.
2. Android 프로젝트 초기 세팅 (Kotlin, Jetpack Compose, Retrofit, Hilt 등).
3. 기본 인증(Auth) 설정 화면 및 API Key 보안 저장 로직 구현.
4. `ima2-gen` 서버와의 기본 API 통신 연동 테스트.
