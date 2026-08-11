# Custom TAB configuration

Just Essentials provides a server-side TAB header, footer, player-name formatter, animations, and an optional boss bar. Vanilla clients are supported.

The settings are generated in `world/serverconfig/justessentials-server.toml`:

```toml
[modules]
customTabList = true

[tabList]
refreshTicks = 10
header = "&#AA54F4&lJUST NETWORK\n&7Welcome, &f{player}&7!\n&7World: &d{world}"
footer = "{animation:info}\n{animation:bar}\n&7Online: &a{visible}&8/&a{max} &8| &7Ping: &a{ping}ms"

[tabList.playerNames]
enabled = true
format = "{staff_prefix}{player}{vanish_suffix}"
staffPrefix = "&b[Staff] &f"
vanishSuffix = " &7[Vanished]"

[tabList.animations]
enabled = true
intervalMilliseconds = 1000
info = ["&dDiscord: &fdiscord.example.com", "&dStore: &fstore.example.com"]
bar = ["&d━&8━━━━━━━━━━━━", "&8━&d━&8━━━━━━━━━━━", "&8━━&d━&8━━━━━━━━━━"]

[tabList.bossBar]
enabled = false
text = "&#AA54F4&lJUST NETWORK &8| &f{visible}/{max} online"
color = "PURPLE"
style = "PROGRESS"
progress = 1.0
```

## Placeholders

- `{player}` - viewing player's name
- `{online}` - actual connected player count
- `{visible}` - players visible to this viewer, respecting Just Vanish
- `{vanished}` - connected players hidden from this viewer
- `{max}` - configured server capacity
- `{world}` - current dimension identifier
- `{ping}` - viewing player's latency
- `{time}` and `{date}` - server-local time and date
- `{uptime}` - JVM uptime
- `{animation:info}` and `{animation:bar}` - current configured animation frames

Classic `&` codes and RGB colors in the `&#RRGGBB` format can be mixed. Just Essentials intentionally does not alter vanilla scoreboard teams for sorting, because those teams may control Just Vanish trusted visibility and other mod behavior.
