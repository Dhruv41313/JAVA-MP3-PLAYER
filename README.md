# 🎵 JavaFX MP3 Player

A sleek, modern **MP3 Music Player** built with **JavaFX**, offering smooth playback, playlist management, and song metadata — all in a clean dark interface.

---

## 🌟 Features

- 🎶 Play, Pause, Stop songs easily  
- 🖱️ Double-click any song to play instantly  
- 📂 Import and manage playlists  
- 🏷️ Displays song metadata (Title, Artist, Album, Year)  
- 💿 Supports MP3 files (WAV/AAC optional)  
- 💬 Lyrics display (auto-loads from `.txt` files if available)  
- 🎚️ Volume slider and progress bar  
- 🖤 Dark modern interface with smooth layout  
- ⚠️ Graceful error handling for missing or broken files  

---

## 🧰 Requirements

Before running the player, please make sure you have:

1. **Java 17 or newer**  
   - Check your version by running this in Command Prompt:
     ```bash
     java -version
     ```
   - If you don’t have Java, download it here:  
     👉 [https://adoptium.net](https://adoptium.net)

2. **JavaFX SDK 23 or newer**  
   - Download from: [https://openjfx.io](https://openjfx.io)  
   - Extract it somewhere simple, like:
     ```
     D:\Java\javafx-sdk-24.0.2
     ```

---

## 💻 How to Run the Player

### Option 1 — Using the provided `runPlayer.bat` file (Recommended)
1. Download the ZIP or JAR package from the **Releases** section.  
2. Extract all files into a single folder.  
3. Double-click on the file named:
4. The player will open automatically with the dark UI.  
*(If it doesn’t open, follow the troubleshooting section below.)*

---

### Option 2 — Run Manually from Command Prompt
If you prefer running directly from terminal:

```bash
java --module-path "D:\Java\javafx-sdk-24.0.2\lib" --add-modules javafx.controls,javafx.fxml,javafx.media -jar JavaMP3Player.jar

---

🪶 Usage Guide

Click Import Songs to select one or multiple MP3 files.

Songs will appear in the playlist area.

Double-click a song to start playing.

Use the Play, Pause, Stop, Next, Previous buttons to control playback.

Adjust volume with the slider.

If lyrics are available in a .txt file with the same name as your song, they will load automatically.

---

⚠️ Troubleshooting

Problem: Nothing happens when I double-click the JAR
Cause: JavaFX modules aren’t being loaded automatically
Solution: Run the "runPlayer.bat" file instead of double-clicking the JAR

Problem: "Unsupported major.minor version" error
Cause: Your Java version is older than required
Solution: Install Java 17 or newer, then try again

Problem: No sound while playing songs
Cause: The MP3 file might be corrupted or not supported
Solution: Try a different MP3 file

Problem: Window appears plain white instead of dark mode
Cause: The CSS theme file wasn’t loaded correctly
Solution: Make sure the "style.css" file is present in the same folder as your JAR

Problem: "Lyrics available" keeps repeating after loading a playlist
Cause: The playlist file contains repeated entries or old cached data
Solution: Clear your playlist and import songs again manually

Problem: Album art not showing
Cause: The MP3 file does not contain embedded artwork
Solution: Add album art using an MP3 tag editor or keep "default_art.png" available in the same folder

Problem: Player closes immediately after opening
Cause: JavaFX path not set or wrong version used
Solution: Edit the "runPlayer.bat" file to match the correct JavaFX SDK path

---

📦 Download

Head to GitHub and download the latest version:
👉 https://github.com/Dhruv41313/JAVA-MP3-PLAYER

Then just:

Extract the ZIP

Run runPlayer.bat

Enjoy your music 🎧

---

🪪 License

This project is licensed under the MIT License.
You can freely use, modify, and share it — just keep credit to the original author.

---

💡 Summary (For Quick Reference)


| Task            | Description                |
| --------------- | -------------------------- |
| Install Java    | Version 17 or newer        |
| Install JavaFX  | SDK 23+                    |
| Run             | Use `runPlayer.bat`        |
| Supported files | MP3 (WAV/AAC optional)     |
| Optional extras | Lyrics, metadata, playlist |
