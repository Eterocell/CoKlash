## ADDED Requirements

### Requirement: LogcatScreen composable
Compose screen replacing LogcatDesign with two modes (streaming/file).

#### Scenario: Streaming mode shows close action
- **WHEN** LogcatScreen is rendered with streaming=true
- **THEN** the TopAppBar shows only the close action

#### Scenario: File mode shows delete and export actions
- **WHEN** LogcatScreen is rendered with streaming=false
- **THEN** the TopAppBar shows delete and export actions

#### Scenario: Auto-scroll to bottom in streaming mode
- **WHEN** new messages arrive AND user is at bottom of list
- **THEN** the list auto-scrolls to the latest message
- **WHEN** new messages arrive AND user is scrolled up
- **THEN** the list does NOT auto-scroll, preserving user position

#### Scenario: Long-press to copy
- **WHEN** user long-presses a log message
- **THEN** onCopyMessage is called with that LogMessage

#### Scenario: Empty state
- **WHEN** messages list is empty
- **THEN** centered empty message is shown
