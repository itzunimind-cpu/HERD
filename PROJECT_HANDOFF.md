# Project Handoff: Herd (Livestock Management) — Android App

**Date:** 2026-07-12
**Status:** Day 1 — design complete, engineering not yet started.

This document marks the start of the build phase. It hands off from the design pass (see `design_handoff_livestock_app/`) to Android engineering.

## What exists today
- `design_handoff_livestock_app/README.md` — full design spec: screens, copy (Marathi), colors, type, spacing, icons, navigation/state behavior.
- `design_handoff_livestock_app/Livestock App - Screens Gallery.dc.html` — static visual exploration, final direction = turn 2 "2a" + turn 3 "3a".
- `design_handoff_livestock_app/Livestock App - Interactive Prototype.dc.html` — clickable nav/state reference (primary behavior source).
- No app code yet. No repo initialized.

## Product summary
Android app for cattle farmers to sign in, view their herd, add cows via ear-tag scan, and manage 5 record types per cow: cow info, breeding, milk, health, daily logs. All UI copy in Marathi. Built for outdoor/field use — high contrast, large touch targets.

## Tech stack decision
**Native Android: Kotlin + Jetpack Compose + Material 3.**
Rationale: design tokens already map cleanly to Material 3 components; native gives best camera/tag-scan integration and offline-first behavior for field use. Revisit if there's a reason to share code with iOS later.

Suggested starting stack:
- UI: Jetpack Compose, Material 3
- Navigation: Compose Navigation (single-stack, matches prototype's simple push/pop model)
- Local data: Room (per-cow records, offline-first — farms may have poor connectivity)
- DI: Hilt
- Camera/tag scan: CameraX to start; defer OCR/RFID decode method until clarified (see open questions)

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
1. Decide backend/auth/sync approach (blocks Sign In and data model finalization).
2. Scaffold Android project (Kotlin, Compose, Material 3, package structure).
3. Implement design tokens (colors, type, spacing) as a Compose theme.
4. Build Home + Cow Detail + the 5 section screens against mock data first (matches prototype fidelity).
5. Design and build the Add Cow form.
6. Decide and implement tag-scan mechanism.
