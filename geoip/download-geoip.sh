#!/bin/sh
set -eu

TARGET_DIR="${GEOIP_DATA_DIR:-/data}"
TARGET_FILE="$TARGET_DIR/dbip-city-lite.mmdb"
TMP_FILE="$TARGET_DIR/.dbip-city-lite.mmdb.tmp"
TMP_GZ="$TARGET_DIR/.dbip-city-lite.mmdb.gz.tmp"

try_download() {
    year_month="$1"
    url="https://download.db-ip.com/free/dbip-city-lite-${year_month}.mmdb.gz"
    echo "Trying: $url"
    curl -fsSL "$url" -o "$TMP_GZ"
}

CURRENT_YM=$(date -u +'%Y-%m')
PREVIOUS_YM=$(date -u -d "$(date +%Y-%m-01) -1 month" +'%Y-%m' 2>/dev/null || date -u -v-1m +'%Y-%m')

if ! try_download "$CURRENT_YM"; then
    echo "Current month not available yet, falling back to previous month"
    try_download "$PREVIOUS_YM"
fi

gunzip -c "$TMP_GZ" > "$TMP_FILE"
rm -f "$TMP_GZ"

mv "$TMP_FILE" "$TARGET_FILE"

echo "GeoIP database updated: $TARGET_FILE ($(date -u))"