# Changelog

## [0.3.1] - 2026-08-11

### Changed

- Just Core 0.2.0 or newer is now required
- Replaced the direct Just Vanish reflection bridge with the shared Just Core player-state API
- Registered Just Essentials in the central Just Core module registry
- Published staff actions through the central Just Core audit service and event bus

### Integration

- Just Vanish audit events can be delivered to Discord by Just Essentials without either feature mod depending directly on the other

## [0.3.0] - 2026-08-11

### Added

- `/je help`, `/homes`, `/warps`, `/seen`, `/whois`, `/warn`, `/warnings`, and `/activitytest`
- Persistent first/last join, last leave, and playtime records
- Persistent staff warnings with audit and Discord delivery
- Safe home and warp destinations with nearby-position search
- Configurable teleport cooldown, warmup, movement tolerance, and cancellation
- Configurable warmup, cancellation, and unsafe-destination messages
- Per-world Nether and End TAB profiles
- Configurable Owner, Admin, Moderator, Helper, and default TAB prefixes
- `/tab preview`
- Public Just Essentials audit API for companion mods

### Integration

- Just Vanish state changes can be written to the Essentials audit log and Discord channels

## [0.2.0] - 2026-08-11

### Added

- Configurable server-side TAB header and footer with multiple lines
- RGB/HEX colors using the `&#RRGGBB` format
- Configurable `{animation:info}` and `{animation:bar}` animations
- Visibility-aware player counts integrated with Just Vanish
- Placeholders for world, ping, health, food, coordinates, TPS, MSPT, clock, date, and uptime
- Configurable player-list names with staff prefixes and vanish markers
- Optional configurable companion boss bar
- Persistent `/tab on|off|toggle|status` controls
- `/tab bossbar on|off|toggle`, with persistent or session-only preferences
- Configurable dimensions where the header, footer, and boss bar are disabled
- Optional custom join and leave messages with colors, HEX, placeholders, and independent visibility switches
- Join and leave messages can be hidden completely without affecting other system messages
- Staff activity monitoring through the `justessentials.staff.activity` permission

### Fixed

- Visual modules now refresh immediately after login, dimension changes, and `/jereload`
- Disabled TAB presentations now clear their header, footer, and boss bar correctly
- Bossbar commands no longer claim success when the module is disabled server-side
- Session-only bossbar choices now work when persistent choices are disabled
- Build scripts no longer use deprecated Gradle repository syntax
- Custom activity messages respect Just Vanish visibility separately for every viewer
- Authorized staff can optionally receive hidden or vanished-player activity messages

## [0.1.0] - 2026-08-09

### Added

- Modular server configuration
- Native NeoForge permission nodes
- Staff chat
- Silent teleport
- Persistent audit history
- Audited kick and ban wrappers
- Heal, feed, flight, and god-mode utilities
- Spawn and back teleport commands
- Live online-player inventory, armor, offhand, and Ender Chest inspection
- Auditing and dedicated permission nodes for inventory inspection
- Optional live Curios inspection, including Accessories installations using the Curios compatibility layer
- Persistent mute and freeze moderation controls
- Frozen-player movement, interaction, attack, and block-breaking enforcement
- Staff mode with reversible flight/god state and private chat routing
- Optional asynchronous Discord webhook audit embeds
- Secure official-webhook URL validation and `/discordtest`
- Staff-chat Discord logging disabled by default
- Separate or shared join/leave, moderation, inspection, and staff webhook channels
- Configurable event modules, server branding, images, footer, player avatars, and embed colors
- Join online counts and leave-session duration embeds
- Persistent temporary mute, freeze, and ban with automatic expiry
- Duration parsing for seconds, minutes, hours, days, and weeks
- Punishment history/status commands and audited unban
- Staff hierarchy and self-punishment protection
- Persistent named homes and server warps across dimensions
- TPA requests with configurable timeout, accept, and deny commands
- Strictly read-only offline inventory and Ender Chest inspection
- Direct optional Accessories API inspection alongside Curios support
- Persistent staff-mode inventory backup and interactive staff tool hotbar
- Configurable prefix, moderation/TPA/staff messages, menu titles, and staff-tool names
- Ampersand color/style codes and template placeholders
- `/jereload` configuration confirmation command
- Hardened offline snapshot menus that reject slot clicks and quick transfers server-side
- Optional Just Vanish 1.2 API integration controlled entirely by Just Essentials
- Staff mode remembers, enables, and restores the previous vanish state
- Internal staff-tool identifiers prevent ordinary items from triggering admin actions
- Offline-capable temporary bans
- Atomic JSON writes with corrupt-file preservation
- JUnit coverage for durations, placeholders, and atomic persistence
- Optional dependency metadata for Just Vanish, Curios, and Accessories
