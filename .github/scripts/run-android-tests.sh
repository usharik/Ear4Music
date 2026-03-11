#!/usr/bin/env sh

adb wait-for-device

echo "ADB devices before boot wait:"
adb devices -l || true

while true; do
  BOOTED="$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')"
  if [ "$BOOTED" = "1" ]; then
    break
  fi
  sleep 5
done

while ! adb shell service check package 2>/dev/null | grep -q "found"; do
  sleep 5
done

while ! adb shell cmd package list packages >/dev/null 2>&1; do
  sleep 5
done

echo "Device info:"
adb shell getprop ro.build.version.release
adb shell getprop ro.build.version.sdk
adb shell getprop ro.product.cpu.abi

./gradlew --no-daemon :app:connectedDebugAndroidTest --stacktrace --info
