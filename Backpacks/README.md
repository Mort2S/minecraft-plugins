# PlayerBackpacks 🎒

A lightweight Minecraft Spigot plugin that provides persistent personal and team-based backpacks (inventories) for players – with YAML-based saving and full integration into the Minecraft scoreboard team system.

---

## 🔧 Features

- Personal backpacks per player (`/backpack`)
- Team backpacks based on Minecraft scoreboard teams (`/teambackpack`)
- Admins can access all backpacks
- Auto-saving on inventory close or server shutdown
- Configurable size for personal backpacks
- YAML-based storage (easy to read and edit)

---

## 📦 Installation

1. Place the `.jar` file into your server’s `plugins` folder.
2. Restart your server.

---

## 📚 Commands

| Command | Description | Permission |
|--------|-------------|------------|
| `/backpack` | Opens your personal backpack | Players |
| `/backpack <player>` | Opens another player’s backpack | Operator |
| `/setbackpacksize <rows>` | Sets your personal backpack size (1-6) | Operator |
| `/teambackpack` | Opens your team’s backpack | Players |
| `/teambackpack <teamName>` | Opens the backpack of a specified team | Operator |

---

## 📁 Data Files

- `backpacks.yml`: Stores personal inventories by UUID
- `team_backpacks.yml`: Stores team inventories by team name
- `sizes.yml`: Stores personal backpack size by UUID

---

## 🧠 Notes

- Team association is based on Minecraft's scoreboard teams (e.g., `/team join <teamName>`).
- Team memberships are also tracked in `teams.yml` for offline access.
- Operators always have full access to all backpacks and commands.

---

## 💬 Example Usage

```mcfunction
/team add Team1
/team join Team1 _morty1s
/teambackpack Team1
