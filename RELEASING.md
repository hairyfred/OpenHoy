# Releasing OpenHoy

Releases are built and published automatically by
[`.github/workflows/release.yml`](.github/workflows/release.yml) whenever a tag
matching `v*` is pushed.

## Cut a release

```bash
git tag v1.0.0
git push origin v1.0.0
```

The workflow then:

1. Builds `assembleRelease`, injecting the version from the tag
   (`v1.0.0` → versionName `1.0.0`, versionCode = the workflow run number).
2. Signs the APK (see signing below).
3. Creates a GitHub Release for the tag and attaches `OpenHoy-v1.0.0.apk`,
   with auto-generated release notes.

Tags should be GPG-signed for authenticity:

```bash
git tag -s v1.0.0 -m "OpenHoy 1.0.0"
git push origin v1.0.0
```

## Signing

The release build is signed with a proper keystore **only if** the signing
secrets below are present. Without them it falls back to the debug key, which
still produces an installable APK — but the debug key is regenerated on every CI
run, so **debug-signed releases cannot be updated in place** (users must
uninstall first). Set up a real keystore so updates install cleanly.

### One-time keystore setup

1. Generate a keystore (keep `openhoy.jks` **private** — never commit it):

   ```bash
   keytool -genkeypair -v -keystore openhoy.jks \
     -alias openhoy -keyalg RSA -keysize 2048 -validity 10000
   ```

2. Base64-encode it:

   - macOS/Linux: `base64 -w0 openhoy.jks`
   - Windows PowerShell:
     `[Convert]::ToBase64String([IO.File]::ReadAllBytes("openhoy.jks"))`

3. In the GitHub repo, go to **Settings → Secrets and variables → Actions** and
   add these repository secrets:

   | Secret              | Value                                  |
   | ------------------- | -------------------------------------- |
   | `KEYSTORE_BASE64`   | the base64 string from step 2          |
   | `KEYSTORE_PASSWORD` | the keystore password                  |
   | `KEY_ALIAS`         | `openhoy` (or your chosen alias)       |
   | `KEY_PASSWORD`      | the key password                       |

The workflow decodes the keystore to a temp file and passes it to Gradle via the
`OPENHOY_KEYSTORE*` environment variables, which `app/build.gradle.kts` reads.

## Local release build

```bash
# debug-signed (quick, installable)
./gradlew assembleRelease

# properly signed
OPENHOY_KEYSTORE=/path/to/openhoy.jks \
OPENHOY_KEYSTORE_PASSWORD=... \
OPENHOY_KEY_ALIAS=openhoy \
OPENHOY_KEY_PASSWORD=... \
./gradlew assembleRelease -PversionName=1.0.0 -PversionCode=1
```
