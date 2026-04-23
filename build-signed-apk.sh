#!/usr/bin/env bash
set -euo pipefail

properties="signing.properties"

if [[ -z "${KEYSTORE:-}" ]]; then
  printf 'KEYSTORE must be set\n' >&2
  exit 1
fi

if [[ ! -f "$KEYSTORE" ]]; then
  printf 'Keystore file does not exist\n' >&2
  exit 1
fi

read -rp "Key alias: " alias_name
read -rsp "Keystore password: " store_password
printf '\n'
read -rsp "Key password: " key_password
printf '\n'

cat > "$properties" <<EOF
storeFile=$KEYSTORE
storePassword=$store_password
keyAlias=$alias_name
keyPassword=$key_password
EOF

gradle assembleRelease
rm -f "$properties"
printf 'Signed APK: app/build/outputs/apk/release/app-release.apk\n'
