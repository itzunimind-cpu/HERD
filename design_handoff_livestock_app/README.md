# Handoff: Herd (Livestock Management) App — Android Mobile

## Overview
An Android mobile app for livestock (cattle) farmers to sign in, view their herd, add cows by scanning an ear tag, and manage five categories of per-cow records: cow info, breeding, milk records, health, and daily logs. All UI copy is in Marathi. Built for outdoor/field use — high contrast, large touch targets.

## About the Design Files
The files in this bundle are **design references created in HTML** (interactive prototypes built with a proprietary component runtime, `<script>`-based). They show the intended look, content, and navigation flow — they are **not production code to copy directly**. The task is to **recreate these designs in the target codebase's environment** (e.g. native Android/Kotlin + Jetpack Compose, or React Native/Flutter — whichever this project uses, or the best-fit choice if none exists yet), using that environment's own component and state-management patterns.

Two files are included:
- `Livestock App - Screens Gallery.dc.html` — a static side-by-side gallery of visual-direction explorations (open in a browser; pan/zoom canvas). The final direction is the one labeled **2a** (turn 2) plus the 5 detail pages in turn 3 (label **3a**).
- `Livestock App - Interactive Prototype.dc.html` — a clickable prototype of the final direction with real navigation and state (open in a browser and click through it). **Use this one as the primary behavior reference.**

## Fidelity
**High-fidelity.** Colors, typography, spacing, icons, and copy are final. Recreate the UI pixel-close using the codebase's existing component library and native platform conventions (e.g. Material 3 components on Android), matching the measurements and tokens below.

## Design Tokens

### Colors
- Cream (background/surface): `#F7F1DE` — rgb(247, 241, 222)
- Sage (secondary/neutral accent): `#B0BA99` — rgb(176, 186, 153)
- Terracotta (primary/interactive accent): `#9D6638` — rgb(157, 102, 56)
- Dark brown (text, headers, borders): `#4E220F` — rgb(78, 34, 15)
- Supporting text (muted brown, solid not translucent, for contrast): `#6b4a35`
- Placeholder/input text: `#7a5240`
- White: `#ffffff` (card surfaces)
- Camera/scan screen background: near-black `#141210`

Contrast approach: borders are solid `#4E220F` at 2–2.5px (not low-opacity), supporting text uses solid muted-brown rather than translucent black, for outdoor legibility.

### Typography
- Font family: **Noto Sans Devanagari** (Marathi + Latin, weights 400/500/600/700/800), loaded from Google Fonts. Fallback: Roboto, system-ui, sans-serif.
- App name / large headers: 22–26px, weight 800
- Section headers: 19px, weight 800
- Card titles / values: 15–17px, weight 700–800
- Supporting / label text: 12–13px, weight 600
- Buttons: 16–17px, weight 800

### Spacing & Shape
- Screen padding: 16–20px
- Card border radius: 18px; small badges/inputs: 14–16px; large circular avatars/FAB: 50%
- Card border: 2–2.5px solid `#4E220F`
- Card shadow: `0 3px 8px rgba(78,34,15,.15)`
- Primary buttons: 52–56px tall, 16px radius, terracotta fill, 2.5px dark-brown border, cream text
- FAB (scan): 64px circle, dark-brown fill, cream 3px border, bottom-right, 22px inset

### Icons
Simple geometric/line icons (not photographic), stroke or fill in cream (`#F7F1DE`) on terracotta (`#9D6638`) square badges (14px radius) at 48–52px. No icon font — inline vector shapes.

## Screens / Views

### 1. Sign In
- **Purpose**: Authenticate before any app access. This is the app's first screen.
- **Layout**: Full-screen cream background, centered column. App mark (circular terracotta badge with a simple cow-head glyph, dark-brown border) + app name "Herd" (26px/800) + tagline "व्यवस्थापन अ‍ॅप" (14px/700, terracotta) centered vertically. Below: two labeled input fields (वापरकर्तानाव / username, पासवर्ड / password), each 52px tall, white fill, 2px dark-brown border, 14px radius. Right-aligned "पासवर्ड विसरलात?" (forgot password) link in terracotta. Full-width primary button pinned to bottom: "साइन इन करा" (Sign In), 56px, terracotta fill, dark-brown border, cream text.
- **Behavior**: Tapping "साइन इन करा" navigates to Home. No validation shown in the prototype — add real validation in implementation (required fields, error states).

### 2. Home — Cattle List
- **Purpose**: Landing screen after sign-in; shows all cows on the farm, entry point to add/scan/view.
- **Layout**: Header bar (cream, 2px bottom border): title "माझी गुरे" (My Cattle) left, circular "+" add-icon button right (dark-brown fill, cream plus icon). Body switches between two states:
  - **Empty state**: Centered, large dashed-border (2.5px, terracotta, 60% opacity) rounded card containing a "+" icon and "नवीन गाय जोडा" (Add new cow) label, both faint/muted (opacity 0.6) to read as a placeholder action. Below it, small centered muted text: "अजून कोणतीही गाय जोडलेली नाही" (No cow added yet).
  - **Populated state**: Scrollable vertical list of cow cards, each: circular sage avatar showing the tag number, tag label "टॅग क्रमांक {tag}" (Tag No. {tag}) bold + "तपशील पाहण्यासाठी टॅप करा" (Tap to view details) muted subtext, chevron-right icon. White card, 18px radius, 2px dark-brown border.
  - A circular FAB (camera icon) is fixed bottom-right on both states — opens the tag scanner.
- **Behavior**: Tapping the header "+" or the empty-state card adds a cow to the list (in the real app this should launch an "add cow" form — not yet designed, flag this as an open screen to design/build). Tapping any cow card navigates to that cow's Detail screen. Tapping the FAB opens Scan.

### 3. Scan Tag
- **Purpose**: Use the camera to scan a cow's ear tag (RFID/barcode/visual tag — clarify capture method with the team; currently a static camera viewfinder mock, no OCR/decode logic implied).
- **Layout**: Full-screen near-black camera background. Top bar: close "X" icon left, "टॅग स्कॅन करा" (Scan Tag) title center. Centered square viewfinder (240×240) drawn as four corner brackets only (sage color, 5px), not a full frame. Below it, instruction text "गाईचा टॅग चौकटीत ठेवा" (Place the cow's tag inside the frame).
- **Behavior**: Close button returns to Home. Real implementation needs actual camera capture + tag decoding + a result/confirmation state (not scoped in this design pass).

### 4. Cow Detail — Section Menu
- **Purpose**: Hub for one cow; routes to its 5 record types.
- **Layout**: Header (dark-brown fill): back-chevron + "टॅग क्रमांक {tag}" (cream text). Body: vertical stack of 5 large tappable rows (18px radius white cards, 2.5px dark-brown border, shadow), each with a 52px terracotta icon badge (14px radius) + 17px/700 label:
  1. गाईची माहिती (Cow Information)
  2. प्रजनन माहिती (Breeding Info)
  3. दुधाची नोंद (Milk Record)
  4. आरोग्य अद्यतन (Health Update)
  5. दैनंदिन माहिती (Daily Information)
- **Behavior**: Back returns to Home. Each row navigates to its respective detail screen below.

### 5. गाईची माहिती — Cow Information
- **Layout**: Header as above. Centered 96px circular photo placeholder (sage fill, cow icon — replace with actual photo/camera-upload affordance). Below: single white card (18px radius, 2px border) listing fields as label/value rows separated by 1.5px hairlines:
  टॅग क्रमांक (Tag No.), नाव (Name), जात (Breed), जन्मतारीख/वय (Birth date/Age), लिंग (Gender), वजन (Weight), वंशावळ (Lineage — parent tag reference).
- Bottom pinned button: "माहिती संपादित करा" (Edit Information).

### 6. प्रजनन माहिती — Breeding Info
- **Layout**: Header. A highlighted status pill card (terracotta fill): "गर्भधारणा स्थिती" (Pregnancy Status) with current value badge (e.g. "गाभण" / pregnant). Field card: शेवटचे माजावर येणे (Last heat date), रेतन तारीख (Insemination date), अपेक्षित प्रसूती तारीख (Expected calving date). Sub-header "विण्याचा इतिहास" (Calving history) + a card listing past calving entries (offspring sex + date).
- Bottom button: "नवीन नोंद जोडा" (Add new entry).

### 7. दुधाची नोंद — Milk Record
- **Layout**: Header. Two side-by-side stat cards: सकाळ (Morning) and संध्याकाळ (Evening) yields in liters. A 7-day bar chart card ("गेल्या 7 दिवसांचा आलेख" — last 7 days' chart), simple CSS bars. Field card: एकूण मासिक उत्पादन (Monthly total), सरासरी उत्पादन (Average yield), फॅट % (Fat %), SNF % (quality parameters).
- Bottom button: "आजची नोंद जोडा" (Add today's entry).

### 8. आरोग्य अद्यतन — Health Update
- **Layout**: Header. Status pill (sage fill): "सद्य आरोग्य स्थिती" (Current health status) + badge (e.g. "निरोगी" / healthy). Sub-header + card: "लसीकरण वेळापत्रक" (Vaccination schedule) — vaccine name + date rows. Sub-header + card: "आजार व औषधोपचार नोंद" (Illness & treatment log) — includes vet visit records.
- Bottom button: "नवीन नोंद जोडा" (Add new entry).

### 9. दैनंदिन माहिती — Daily Information
- **Layout**: Header shows tag + today's date. Vertical list of stat rows (not a single table): चारा/खाद्य नोंद (Feed log), पाणी वापर (Water intake), तापमान तपासणी (Temperature check), क्रियाकलाप टिपणी (Activity notes) — each its own card, value in terracotta.
- Bottom button: "आजची नोंद जोडा" (Add today's entry).

## Interactions & Behavior (from the interactive prototype)
- Navigation is a simple single-screen stack, not deep-linked: Sign In → Home → {Scan | Cow Detail → one of 5 sections}.
- Back button on Scan and Cow Detail returns to Home. Back on each of the 5 section screens returns to Cow Detail (not Home) — preserve this "one level up" back behavior.
- Home starts empty by design intent (new farm/first run); adding a cow (header "+" or empty-state tile) should open a proper "Add Cow" form in production — this form was **not designed** in this pass; flag it as a follow-up.
- No animated transitions were specified; use the platform's standard push/pop navigation transition.
- No loading/error states were designed for any screen — add them per the target platform's conventions (network calls for cattle data, form submission, camera permission, etc).
- Form validation, camera permission handling, and actual tag-decode logic are unscoped — the Scan screen is a static viewfinder only.

## State Management
Minimal state needed to reproduce the flow:
- `currentScreen`: enum of the 9 screens above
- `cows`: list of `{ tag }` (extend with the full field set once real data model is defined — see Cow Information fields)
- `selectedCowTag`: which cow's detail/section screens are showing
- Per-section data (breeding, milk, health, daily) is currently static/mock per cow — production needs a real per-cow data store keyed by tag/cow ID.

## Assets
No external image assets — all icons are inline vector shapes (simple geometric icons: document, heart, droplet, cross/plus, calendar) and one circular photo placeholder. No photography included; the team should supply real cow photos for the Cow Information screen once available.

## Files
- `Livestock App - Screens Gallery.dc.html` — visual exploration + all static screens (final direction: turn 2 "2a" + turn 3 "3a")
- `Livestock App - Interactive Prototype.dc.html` — clickable reference for navigation/state, final direction only
