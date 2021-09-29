### About
A Spigot plugin for Minecraft 1.17.1. Written with Kotlin and Java 16

The plugin records damage and healing the player has received, up to 45 entries.
The user is able to view the info in a clean gui window. You can swap between your
current life's info and your last death's info.

My reason for making this was I always thought it would be cool to have a 
damage meter type plugin showing stats. Plus I wanted to practice kotlin.

### Installing
Requires no dependencies but does use the KSpigot library which your server will automatically download
#### Server
* Download the jar from releases
* Place inside your server's plugin's directory
* Use /combatreport to open the gui

#### Developer
* Clone the repo
* To build `./gradlew build`

### Roadmap
Possible updates and new content
* Persistent reports? (Not sure if I want to add)
* More details about the combat
* Support for custom damage causes or API?
* DPS and HPS reporting
