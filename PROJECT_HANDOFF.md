# Project Handoff: Herd (Livestock Management) — Android App

**Date:** 2026-07-12
**Status:** Day 1 — design complete, Android app scaffolded and building successfully. See Changelog below.

This document marks the start of the build phase. It hands off from the design pass (see `design_handoff_livestock_app/`) to Android engineering.

## What exists today
- `design_handoff_livestock_app/README.md` — full design spec: screens, copy (Marathi), colors, type, spacing, icons, navigation/state behavior.
- `design_handoff_livestock_app/Livestock App - Screens Gallery.dc.html` — static visual exploration, final direction = turn 2 "2a" + turn 3 "3a".
- `design_handoff_livestock_app/Livestock App - Interactive Prototype.dc.html` — clickable nav/state reference (primary behavior source).
- `app/` — full native Android app (Kotlin + Jetpack Compose), builds clean (`./gradlew assembleDebug`). See Changelog.
- `supabase/migrations/0001_init.sql` — checked-in copy of the live Supabase schema (project ref `thitdaiznbuxkejvitex`, region ap-northeast-1).

## Product summary
Android app for cattle farmers to sign in, view their herd, add cows via ear-tag scan, and manage 5 record types per cow: cow info, breeding, milk, health, daily logs. All UI copy in Marathi. Built for outdoor/field use — high contrast, large touch targets.

## Tech stack decision
**Native Android: Kotlin + Jetpack Compose + Material 3.**
Rationale: design tokens already map cleanly to Material 3 components; native gives best camera/tag-scan integration and offline-first behavior for field use. Revisit if there's a reason to share code with iOS later.

Stack as implemented (see Changelog for exact library versions):
- UI: Jetpack Compose, Material 3
- Navigation: Compose Navigation (single-stack, matches prototype's simple push/pop model)
- Local data: Room (per-cow records, offline-first — farms may have poor connectivity)
- Remote data: Supabase (Postgres + Auth), via the official `supabase-kt` client
- DI: Hilt
- Background sync: WorkManager
- Camera/tag scan: CameraX; OCR/RFID decode method still undecided (see open questions) — Scan screen is viewfinder-only

## Data model (draft, from design spec)
```
Cow: tag (id), name, breed, birthDate, gender, weight, lineage (parent tag), photoUri
Breeding: cowTag, pregnancyStatus, lastHeatDate, inseminationDate, expectedCalvingDate, calvingHistory[]
MilkRecord: cowTag, date, morningYield, eveningYield, fatPct, snfPct
Health: cowTag, currentStatus, vaccinations[], illnessLog[]
DailyLog: cowTag, date, feed, water, temperature, activityNotes
```
This needs review with whoever owns the backend/data decisions before Room schema is finalized.

## Screens to build (9, see design README for full detail)
1. Sign In
2. Home — Cattle List (empty + populated states)
3. Scan Tag (camera viewfinder only in design — no decode logic yet)
4. Cow Detail — Section Menu
5. Cow Information
6. Breeding Info
7. Milk Record
8. Health Update
9. Daily Information

Navigation: single-stack. Back from the 5 section screens goes to Cow Detail (not Home) — preserve this.

## Open questions / not yet designed
- **Add Cow form** — tapping "+" or the empty-state tile needs a real form; not designed in the design pass.
- **Tag scan mechanics** — RFID vs barcode vs visual/OCR is undecided; Scan screen is currently a static mock.
- **Auth backend** — Sign In has no validation/error states specified; need to decide auth provider (Firebase Auth? custom backend?).
- **Data sync/backend** — is this offline-only, or does it sync to a server? Determines whether Room is local-only cache or needs a sync layer.
- **Loading/error states** — none specified per screen; need to define per platform convention.
- Real cow photos to replace the placeholder avatar.

## Immediate next steps
1. ~~Decide backend/auth/sync approach~~ — done, see Changelog: Supabase (Postgres + Auth), Room as local cache, simple last-write-wins sync.
2. ~~Scaffold Android project~~ — done.
3. ~~Implement design tokens as a Compose theme~~ — done.
4. ~~Build Home + Cow Detail + the 5 section screens~~ — done, wired to real Room/Supabase data (not mock).
5. ~~Design and build the Add Cow form~~ — done, basic version (see open questions: no photo capture yet).
6. Decide and implement tag-scan mechanism — still open, Scan screen is a real camera preview but has no decode logic.
7. Build and test a sign-up flow (currently only one seed account exists, created directly in Supabase).
8. Real device/farmer testing — nothing has been tested outside the Android emulator yet.

## Changelog

**2026-07-12/13 — Engineering Day 1: full scaffold, Supabase backend, first successful build.**

- **Supabase backend provisioned and live**: project `HERD` (ref `thitdaiznbuxkejvitex`, ap-northeast-1). Schema: `cows`, `breeding_info`, `calving_history`, `milk_records`, `health_status`, `vaccinations`, `illness_log`, `daily_logs` — each with `owner_id uuid references auth.users(id)`, Row Level Security (`owner_id = (select auth.uid())` on all operations), and indexes on `owner_id`/`cow_id`. `cows` uses a surrogate `uuid` primary key (tags are only unique per-farmer, not globally); child tables key off that uuid via `cow_id`. Applied via migration, checked into `supabase/migrations/0001_init.sql`. `get_advisors` (security + performance) clean after a follow-up fix (indexes + RLS policies rewritten to use `(select auth.uid())` for query-plan performance).
- **One seed login exists** for testing: created directly via SQL against `auth.users`/`auth.identities` (no sign-up screen built yet).
- **Android app scaffolded**: Kotlin + Jetpack Compose + Material 3, package `com.motisoft.herd`, minSdk 26 / compileSdk & targetSdk 35. Key library versions: AGP 8.7.2, Kotlin 2.0.21, Compose BOM 2024.12.01, Room 2.6.1, Hilt 2.52, CameraX 1.4.0, WorkManager 2.10.0, supabase-kt BOM 3.0.3 (Postgrest + Auth + Storage modules) over Ktor 3.0.1.
- **Local-first data layer**: Room is the only thing the UI reads from. Writes land in Room immediately (`dirty=true`), then a best-effort push to Supabase happens right away; anything that fails (offline, etc.) stays `dirty` and is retried by a WorkManager job every ~20 min. Deliberately simple v1 sync: last-write-wins by timestamp, no conflict resolution — acceptable since each farm account is single-user.
- **All 9 designed screens built** (Sign In, Home, Scan Tag, Cow Detail, Cow Information, Breeding Info, Milk Record, Health Update, Daily Information) plus a new **Add Cow** form (wasn't designed in the design pass — built with the Cow entity's fields, photo capture deferred to a placeholder). Sign In now does a real Supabase Auth call with basic validation (design's prototype navigated through unconditionally). The 4 record-detail screens use a bottom sheet for "add new entry" rather than a new screen, to keep the navigation graph at the original 9 designed screens.
- **Design tokens implemented exactly** as specified (colors, Noto Sans Devanagari typography — using the platform's own Devanagari font substitution rather than a Google Fonts network download, so the app has no font dependency on first launch; card/button/FAB shapes and sizing).
- **Fixed after first Gradle sync in Android Studio** (31 compile errors on first real build): `verticalScroll`/`rememberScrollState` were imported from the wrong package (`androidx.compose.foundation`, not `androidx.compose.foundation.layout`) across 6 screen files; `Modifier.height` was missing its import in `StatCard.kt`; `HerdTopBar` needed `@OptIn(ExperimentalMaterial3Api::class)` for `CenterAlignedTopAppBar`; and all 5 repositories called Postgrest's `upsert(dto, onConflict = "...")` with `onConflict` as a function argument, but the installed supabase-kt version only exposes `onConflict` as a property to set *inside* the trailing request-builder lambda (`upsert(dto) { onConflict = "..."; select() }`). After these fixes, `./gradlew assembleDebug` succeeds.
- **Not yet done**: sign-up flow, tag OCR/RFID decode, real photo capture/upload, physical-device testing, any farmer-facing testing at all.
