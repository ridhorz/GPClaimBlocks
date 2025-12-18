# GPClaimBlocks

A lightweight Spigot plugin that automatically assigns bonus claim blocks to players based on their permission groups.

## Features

- Automatic bonus claim blocks based on permission groups
- Real-time sync when player rank changes (LuckPerms)
- Vault support for group checking
- Configurable ranks via `config.yml`
- Accumulative mode option
- PlaceholderAPI support
- bStats metrics
- Update checker

## Requirements

- Spigot/Paper 1.13+
- Java 17+
- GriefPrevention
- LuckPerms (optional, for real-time sync)
- Vault (optional, for group checking)
- PlaceholderAPI (optional)

## Installation

1. Download the latest release from [SpigotMC](https://www.spigotmc.org/resources/gpclaimblocks-griefprevention-addon.130676/)
2. Put the jar file in your `plugins` folder
3. Restart the server
4. Edit `plugins/GPClaimBlocks/config.yml`
5. Use `/gpcb reload` to apply changes

## Configuration

```yaml
default-bonus-blocks: 400

# Sum all matching ranks instead of using highest only
accumulative: false

# Check Vault groups directly
use-vault-groups: false

# Ranks - checked top to bottom, highest first
ranks:
  "group.warrior": 150000
  "group.diamond": 100000
  "group.platinum": 80000
  "group.vip": 15000
```

## Commands

| Command | Description |
|---------|-------------|
| `/gpcb reload` | Reload configuration |
| `/gpcb check <player>` | Check player's rank and blocks |
| `/gpcb list` | List all configured ranks |
| `/gpcb update [player]` | Force update claim blocks |

**Permission:** `gpclaimblocks.admin`

## Placeholders

Requires PlaceholderAPI.

| Placeholder | Description |
|-------------|-------------|
| `%gpcb_bonus%` | Bonus claim blocks |
| `%gpcb_accrued%` | Accrued claim blocks |
| `%gpcb_total%` | Total claim blocks |
| `%gpcb_remaining%` | Remaining usable blocks |
| `%gpcb_rank%` | Current rank name |

## Links

- [SpigotMC](https://www.spigotmc.org/resources/gpclaimblocks-griefprevention-addon.130676/)
- [bStats](https://bstats.org/plugin/bukkit/GPClaimBlocks/24412)

## License

MIT
