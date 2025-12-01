# 🎲 Yatch Game (Yahtzee in Java)

A simple console-based Yatch (Yahtzee) game written in **pure Java**.  
This project is structured with clear object-oriented design principles and supports **game saving/loading using JSON (Gson)**.

---

## 📁 Project Structure

```

yatch/
├── src/
│    ├── App.java
│    ├── dice/
│    ├── game/
│    ├── player/
│    ├── score/
│    ├── screen/
│    └── storage/
│
├── lib/               # Gson .jar located here
├── bin/               # Auto-generated build output
├── .gitignore
└── README.md

```

---

## 🧱 Key Classes

| Component | Description |
|----------|-------------|
| `GameManager` | Controls overall game flow |
| `Dice` / `DiceSet` | Manages dice rolling logic |
| `Player` | Player information & scoreboard |
| `ScoreCategory` | Abstract base class for all scoring rules |
| `ScoreBoard` | Stores player scores |
| `Screen` | Handles console UI output |
| `FileIOManager` | Handles JSON save/load using Gson |

---

## 🛠 Requirements

- **JDK 17+**
- **VS Code (recommended)**  
  with the following extensions:
  - Extension Pack for Java
  - Language Support for Java by Red Hat

---

## 📦 Library

This project uses **Gson** for JSON serialization.

Make sure `lib/gson-2.10.1.jar` exists.

To register Gson in VS Code:

```

Cmd + Shift + P
→ Java: Configure Java Runtime
→ Referenced Libraries
→ Add → lib/gson-2.10.1.jar

````

---

## ▶️ Run the Game

### **Method 1 — VS Code Run Button**
Open `App.java`  
Click **Run ▶** on the top right.

---

### **Method 2 — Terminal**

Compile:

```bash
javac -cp "lib/gson-2.10.1.jar" -d bin src/**/*.java
````

Run:

```bash
java -cp "bin:lib/gson-2.10.1.jar" App
```

(macOS/Linux 기준. Windows는 `;` 사용)

---

## 💾 Saving / Loading

The project supports saving current game state to `save.json`.

`FileIOManager` handles:

* Saving game state (`save.json`)
* Loading game state

JSON format example:

```json
{
  "currentPlayer": 0,
  "hasRolled": true,
  "diceValues": [1, 3, 3, 5, 6],
  "players": [
    {
      "name": "Alice",
      "scoreBoard": { "ONES": 3, "FULL_HOUSE": 25 }
    }
  ]
}
```

---

## 🧪 Development Guide (For Team Members)

### 1. Clone the repository

```bash
git clone https://github.com/Chanhui03/yatch.git
```

### 2. Open folder in VS Code

```
File → Open Folder → yatch
```

### 3. Verify Source Path

```
Cmd + Shift + P → Java: Configure Java Runtime
Source Paths → src
```

### 4. Ensure Gson is loaded under "Referenced Libraries"

Should appear as:

```
Referenced Libraries
 └── gson-2.10.1.jar
```

### 5. Run `App.java`

---

## 🔧 .gitignore

Included by default:

```
bin/
out/
*.class
.vscode/
.DS_Store
*.log
```

---
