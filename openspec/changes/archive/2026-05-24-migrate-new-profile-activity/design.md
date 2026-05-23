## Context
NewProfileActivity shows a list of profile providers (File, URL, QR, External apps) and handles creating profiles, QR scanning, and launching external provider apps.

## Goals
- Migrate to Compose + M3 using BaseComposeActivity
- Render Drawable icons using toBitmap().asImageBitmap() (no external libs)
- Convert suspend startActivityForResult to registerForActivityResult pattern
- Support QR scanning via quickie library

## Decisions
1. Use registerForActivityResult for all activity results (properties, external provider, QR scan)
2. Drawable icons rendered via toBitmap().asImageBitmap() — no Accompanist/Coil needed
3. Long-press on External providers opens app details via Settings intent
