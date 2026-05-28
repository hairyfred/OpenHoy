# OpenHoy

> **WARNING: AI Slop Coded**
>
> This software was AI slop coded. While it has been tested, do not use
> this in mission-critical situations. Use at your own risk. The code may
> contain bugs, security issues, or unexpected behavior.

An accessible Android caller for the card-bingo game **Hoy** ([how to play](https://www.hoythegame.com/pages/how-to-play-hoy)).

Built to be used with adults who have learning disabilities, where the usual problem — clubs and spades being muddled up when a caller holds a real card — is solved by showing one card at a time on screen, huge, recoloured (4-colour deck), labelled by name ("CLUBS"), and spoken aloud.

## Features

- Big, clear card display — one card fills the screen
- **4-colour deck**: clubs = green, spades = black, hearts = red, diamonds = blue
- Suit name spelled out in large capitals next to the icon
- Custom-drawn suit icons (clubs visibly round-lobed, spades visibly pointed) — no ambiguous Unicode glyphs
- **Text-to-speech**: every card is announced ("Seven of clubs")
- **History strip**: previously called cards stay visible at the bottom; tap to re-speak
- **Auto-advance**: optional, 1–20 seconds per card
- Settings: toggle TTS, 4-colour deck, suit names, and auto-advance independently

## Install (download a build)

Grab the latest `OpenHoy-vX.Y.Z.apk` from the [**Releases**](https://github.com/hairyfred/OpenHoy/releases) page and sideload it onto an Android 8.0+ (API 26) device. You'll need to allow installing from unknown sources.

> Releases are built automatically by GitHub Actions when a version tag is pushed (see [RELEASING.md](RELEASING.md)).

## Open & run from source

You need **Android Studio** (Hedgehog or newer recommended) and an Android device or emulator running **Android 8.0+** (API 26).

1. Clone the repo and **File → Open** the project folder in Android Studio.
2. Click **Trust Project** if prompted.
3. Studio will sync Gradle — first time pulls Gradle 8.5, AGP 8.2.2, and dependencies (a few minutes).
4. Plug in an Android phone with USB debugging enabled, or start an emulator from **Tools → Device Manager**.
5. Hit the green ▶ **Run 'app'** button.

Command line: `./gradlew assembleDebug` (or `assembleRelease`). The APK lands in `app/build/outputs/apk/`.

## Project structure

See `app/src/main/java/uk/hairyfred/openhoy/` — domain model, UI, TTS, settings, view model, all in one module. Package name is `uk.hairyfred.openhoy`; rename freely.

## Out of scope (for now)

- Generating Hoy sheets for players
- Multi-device sync between caller and player screens
- Play Store packaging

## Licence

MIT (see [LICENSE](LICENSE)) — but the "Hoy" name and game are © Hoy The Game; this app is an unofficial assistive tool, not affiliated with them.
