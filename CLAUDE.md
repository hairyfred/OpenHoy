# CLAUDE.md

Guidance for working on OpenHoy. See `README.md` for user-facing info and
`RELEASING.md` for the release process.

## What this is

OpenHoy is a native Android "caller" for the card-bingo game **Hoy**. It
replaces a person holding up physical cards: it shows one shuffled card at a
time — large and unmistakable — and speaks it aloud. It is an **accessibility-first**
app; the primary users are adults with learning disabilities.

## Tech stack

- Kotlin + Jetpack Compose (Material 3)
- minSdk 26, target/compile SDK 34, JVM target 17
- Gradle 8.5 (wrapper committed), AGP 8.2.2, Kotlin 1.9.22, Compose compiler
  1.5.10, Compose BOM 2024.02.02
- AndroidX only, no DI framework. State via `ViewModel` + `StateFlow`.
  Persistence via DataStore Preferences.
- Application id / package: `uk.hairyfred.openhoy`

## Build / run

- Open in Android Studio and Run, or `./gradlew assembleDebug` /
  `./gradlew assembleRelease`. Output APKs under `app/build/outputs/apk/`.
- A debug build needs no secrets. Release signing is optional via
  `OPENHOY_KEYSTORE*` env vars; without them the release falls back to the debug
  key (see `RELEASING.md`).

## Architecture

- `model/` — `Card`, `Rank`, `Suit`, `DeckState` (immutable: shuffled list + a
  drawn-count pointer; `current`, `history`, `remaining`, `drawNext`).
- `viewmodel/CallerViewModel` — deck state, auto-advance coroutine, settings
  flow, triggers TTS on draw.
- `speech/CardSpeaker` — wraps Android `TextToSpeech` (Locale.UK).
- `settings/` — `Settings` data class + DataStore repository.
- `ui/caller/` — `CallerScreen` (chooses portrait/landscape layout), `CardView`
  (`BigCardView`/`MiniCardView` + `AutoSizeText`), `SuitIcons`, `FaceCardIcons`,
  `HistoryStrip` (portrait), `HistoryGrid` (landscape, animated).
- `ui/settings/SettingsScreen`, `ui/theme/` (felt-green Material theme).

## Accessibility design rules — DO NOT REGRESS

These are the entire point of the app:

1. **4-colour deck** by default: clubs = green, spades = black, hearts = red,
   diamonds = blue. Toggleable, but on by default.
2. **Suit name spelled out** in large letters (CLUBS / SPADES / HEARTS /
   DIAMONDS) alongside the icon — text removes the ambiguity that symbols alone
   leave.
3. **Custom-drawn suit icons** (Compose `Path`, not Unicode glyphs) so clubs
   (round trefoil) and spades (sharp point) are exaggeratedly distinct.
   Clubs-vs-spades confusion is the #1 problem this app exists to solve.
4. **Face cards (J/Q/K)** lead with the spelled-out rank word
   (KING / QUEEN / JACK) plus a small icon accent. Symbol-only face cards proved
   unreliable for users.
5. **Never reuse a suit shape inside a face-card icon.** A heart on the Queen
   collides with the hearts suit and confuses users. Current icons: Queen =
   pearl-tipped tiara, King = pointed crown with jewels, Jack = jester hat.
6. **Card text auto-sizes to a consistent height** across cards. A longer word
   (e.g. DIAMONDS) is *condensed horizontally* (graphicsLayer scaleX), not shrunk,
   so every suit/rank name looks the same size. See `AutoSizeText` in
   `CardView.kt` (`sizingText` = the longest word in the group).
7. Large tap targets; TTS on by default; auto-advance off by default (the caller
   usually controls pacing).

## Layout

- **Portrait:** card on top, full-width NEXT CARD button, horizontal history
  strip; normal-height top bar.
- **Landscape:** card on the left (kept at real card proportions), normal-size
  NEXT CARD button on the right, animated history **grid** below it; slim top
  bar. Orientation is detected via `Configuration.orientation`.
- The big centre suit icon is sized to the largest square that fits **both**
  dimensions (`BoxWithConstraints`) so it never clips on the short landscape card.

## Releases

Push a `v*` tag → GitHub Actions (`.github/workflows/release.yml`) builds and
publishes an APK to the GitHub Release. Version is derived from the tag. See
`RELEASING.md`.
