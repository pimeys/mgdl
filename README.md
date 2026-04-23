# mg/dL

A simple Android blood glucose converter.

Licensed under the GNU Affero General Public License v3.0. See [LICENSE](LICENSE).

<img width="320" height="714" alt="Screenshot_20260423-151657" src="https://github.com/user-attachments/assets/f9dee311-b4a6-4711-85b5-a4d7f833c2e9" />

The app has two inputs:

- `mg/dL`
- `mmol/L`

Entering a value in either field automatically converts it to the other unit.

## Development environment

This repository includes a Nix flake and direnv setup.

```bash
direnv allow
```

Or enter the shell manually:

```bash
nix develop
```

## Build debug APK

```bash
gradle assembleDebug
```

The APK is written to:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Install debug APK to a connected phone

Enable USB debugging on the phone, then run:

```bash
adb devices
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Build signed release APK

Set `KEYSTORE` to the path of your signing keystore:

```bash
KEYSTORE=/path/to/keystore.jks ./build-signed-apk.sh
```

The script asks for the key alias, keystore password, and key password.

The signed APK is written to:

```text
app/build/outputs/apk/release/app-release.apk
```

## Install signed release APK

```bash
adb install -r app/build/outputs/apk/release/app-release.apk
```
