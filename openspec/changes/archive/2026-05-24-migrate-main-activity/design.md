## Decisions
1. VPN toggle via registerForActivityResult for permission request
2. Traffic polling via lifecycleScope coroutine (1s interval)
3. State events handled via BaseComposeActivity overrides (onStarted, onStopped, etc.)
4. About dialog uses ComposeView inside AlertDialog (preserves existing pattern)
