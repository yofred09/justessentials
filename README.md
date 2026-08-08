# Just Essentials

Just Essentials is a modular, server-side administration and utility mod for Minecraft 1.21.1 on NeoForge. Players do not need to install it.

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
- `/curiossee <player>` - live Curios slots (also supports Accessories through its Curios compatibility layer)
- `/mute <player>` and `/unmute <player>`
- `/freeze <player>` and `/unfreeze <player>`
- `/staffmode` - toggles flight, god mode, and staff-chat mode while preserving previous states
- `/staffchattoggle` - routes normal chat messages to the private staff channel
- Native NeoForge permission nodes with operator-level fallbacks
- Individually configurable feature modules

## Planned

- Offline inventory inspection
- Native Accessories API adapter without its compatibility layer
- Homes, warps, TPA, timed punishments, and richer punishment history

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.x
- Java 21

## License

Copyright © 2026 Yo_Fred. All rights reserved. Official compiled releases may be used on servers. Source reuse, modification, redistribution, derivative works, and commercial exploitation are prohibited without prior written permission. See [LICENSE](LICENSE).
