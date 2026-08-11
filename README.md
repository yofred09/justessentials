# Just Essentials

Just Essentials is a modular, server-side administration and utility mod for Minecraft 1.21.1 on NeoForge. Players do not need to install it. Just Core 0.2.0 or newer is required on the server.

Install both the Just Core and Just Essentials JAR files in the server's `mods` folder. Install Just Vanish separately when vanish integration is wanted.

Complete installation, commands, permissions, configuration, integrations, and troubleshooting are maintained in the [Just Essentials Wiki](https://github.com/yofred09/justessentials/wiki).

## Implemented in 0.1.0

- `/staffchat <message>` and `/sc <message>`
- `/stp <player>` — silent staff teleport
- `/history <player>` — persistent audit history
- `/jkick <player> <reason>`
- `/jban <player> <reason>`
- `/heal [player]`
- `/feed [player]`
- `/fly [player]`
- `/god [player]`
- `/spawn`
- `/back`
- `/invsee <player>` - live inventory, armor, and offhand inspection
- `/endersee <player>` - live Ender Chest inspection
- `/invseeoffline <player>` - read-only saved inventory inspection
- `/enderseeoffline <player>` - read-only saved Ender Chest inspection
- `/curiossee <player>` - live Curios slots (also supports Accessories through its Curios compatibility layer)
- `/accessoriessee <player>` - direct Accessories API inspection without the compatibility layer
- `/mute <player>` and `/unmute <player>`
- `/freeze <player>` and `/unfreeze <player>`
- `/staffmode` - toggles flight, god mode, and staff-chat mode while preserving previous states
- `/staffchattoggle` - routes normal chat messages to the private staff channel
- `/discordtest` - validates and queues a Discord webhook test
- `/tempmute <player> <duration> <reason>`
- `/tempfreeze <player> <duration> <reason>`
- `/tempban <player> <duration> <reason>`
- `/punishments <player>` and `/punishmentinfo <player>`
- `/junban <player>` - audited unban
- `/sethome <name>`, `/home <name>`, and `/delhome <name>`
- `/setwarp <name>`, `/warp <name>`, and `/delwarp <name>`
- `/tpa <player>`, `/tpaccept`, and `/tpdeny`
- Native NeoForge permission nodes with operator-level fallbacks
- Individually configurable feature modules
- Optional asynchronous Discord audit embeds for moderation and staff actions

## Discord audit logs

Start the server once, then edit `config/justessentials-server.toml`:

```toml
[discord]
enabled = true
webhookUrl = "https://discord.com/api/webhooks/..."
username = "Just Essentials"
logStaffChat = false

[discord.channels]
activityWebhookUrl = "" # shared join/leave channel
joinWebhookUrl = ""     # optional join-only override
leaveWebhookUrl = ""    # optional leave-only override
moderationWebhookUrl = ""
inspectionWebhookUrl = ""
staffWebhookUrl = ""

[discord.events]
joins = true
leaves = true
moderation = true
inspections = true
staffActions = true

[discord.branding]
serverName = "My Server"
thumbnailUrl = "https://example.com/server-icon.png"
imageUrl = ""
footerText = "My Server Staff Logs"
usePlayerAvatars = true
```

Every category-specific URL is optional and falls back to `webhookUrl`. Join and leave URLs first fall back to `activityWebhookUrl`, allowing either one activity channel or two separate channels. Restart the server and run `/discordtest`. The webhook is kept only in the server configuration; never commit that file or share its URL. Staff-chat logging is disabled by default for privacy.

## Planned

- Offline inventory inspection
- Native Accessories API adapter without its compatibility layer
- Full message localization/customization and multiplayer regression testing

## Staff tools

Staff mode safely stores the staff member's complete inventory in persistent player data and supplies a temporary tool hotbar:

- Compass: teleport to a random online player
- Chest: right-click a player to inspect their inventory
- Blaze rod: right-click a player to freeze or unfreeze them
- Barrier: exit staff mode and restore the original inventory

If the server restarts while staff mode is active, the saved inventory remains available and is restored when staff mode is disabled.

When Just Vanish 1.2.0+ is installed, staff mode uses its public API automatically: it remembers the staff member's previous vanish state, enables vanish on entry, and restores the previous state on exit. This integration is implemented entirely in Just Essentials and is configurable with `integrations.justVanish.staffModeVanish`.

## Message and menu customization

The server config contains `[messages]`, `[menus]`, and `[staffTools]` sections. Text supports classic `&` color/style codes and placeholders such as `{player}`, `{reason}`, `{type}`, and `{expires}`. You can customize the global prefix, moderation notices, TPA messages, staff-mode messages, menu titles, tool names, or disable the staff hotbar entirely. NeoForge watches the config file; `/jereload` confirms that the current values are active.

The optional server-side custom TAB list is configured under `[tabList]`. Its header and footer support multiple lines, `&` color/style codes, and `{player}`, `{online}`, `{max}`, `{visible}`, and `{vanished}`. When Just Vanish is installed, visible counts are calculated separately for every viewer and never reveal hidden staff.

TAB text also supports RGB colors such as `&#AA54F4`, animations through `{animation:info}` and `{animation:bar}`, plus `{world}`, `{ping}`, `{time}`, `{date}`, and `{uptime}`. Player-list names and an optional companion boss bar are configurable without requiring a client mod. See [TAB configuration](docs/TAB-CONFIG.md).

Players can control the presentation with `/tab on|off|toggle|status` and `/tab bossbar on|off|toggle`. Templates additionally support `{health}`, `{food}`, `{x}`, `{y}`, `{z}`, `{tps}`, and `{mspt}`, and administrators can disable the presentation in selected dimensions.

## Join and leave messages

Enable `modules.customJoinLeaveMessages` to replace the vanilla join and leave announcements. `activityMessages.joinMessage` and `activityMessages.leaveMessage` support `&`/HEX colors plus `{player}`, `{online}`, `{max}`, and `{world}`. Each announcement can be disabled independently, and a blank template hides it completely. When Just Vanish is installed, unauthorized viewers never receive announcements for vanished staff.

With `activityMessages.staffAlwaysSeesMessages = true`, players holding `justessentials.staff.activity` still receive hidden announcements, including activity from vanished players. The permission falls back to operator level 2 by default.

Offline inspection uses a protected snapshot menu: clicks on saved slots and all shift-click transfers are rejected server-side. It never writes to player data.

## Administration and safe travel

Travel commands support safe-destination checks, configurable cooldowns, and movement-cancelled warmups. Administration discovery includes `/je help`, `/homes`, `/warps`, `/seen`, `/whois`, `/warn`, `/warnings`, and `/activitytest`.

TAB profiles may use different Nether and End headers/footers, configurable permission-level group prefixes, and `/tab preview` for immediate inspection.

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.x
- Java 21

## Official distribution

Official compiled releases are distributed through CurseForge and Modrinth. GitHub is used for documentation, issue reporting, security review, and compatibility assessment.

## License

Copyright © 2026 Yo_Fred. All rights reserved. Official compiled releases may be used on servers. Source reuse, modification, redistribution, derivative works, and commercial exploitation are prohibited without prior written permission. See [LICENSE](LICENSE).
