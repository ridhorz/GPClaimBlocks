# GPClaimBlocks

A lightweight Spigot plugin that automatically assigns bonus claim blocks to players based on their permission groups.

## Features

- Automatic bonus claim blocks based on LuckPerms groups
- Real-time sync when player rank changes
- Configurable ranks via `config.yml`
- Accumulative mode option
- PlaceholderAPI support
- Admin commands for management

  
## Commands

| Command | Description |
|---------|-------------|
| `/gpcb reload` | Reload configuration |
| `/gpcb check <player>` | Check player's rank and blocks |
| `/gpcb list` | List all configured ranks |
| `/gpcb update [player]` | Force update claim blocks |

**Permission:** `gpclaimblocks.admin`

Requires PlaceholderAPI.

| Placeholder | Description |
|-------------|-------------|
| `%gpcb_bonus%` | Bonus claim blocks |
| `%gpcb_accrued%` | Accrued claim blocks |
| `%gpcb_total%` | Total claim blocks |
| `%gpcb_remaining%` | Remaining usable blocks |
| `%gpcb_rank%` | Current rank name |
