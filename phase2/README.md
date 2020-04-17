# Calendar App - Group 0248
* To run the program as a GUI, execute the main() method Main.java
* To run the console interface (which doesn't have phase 2 features), run consoleui.UserManagerUI.main().

## Feature List
* Team-specific feature: togglable dark theme
* Infinitely recurring event series
* GUI created using JavaFX and FXML
* Postponing (possibly indefinitely), rescheduling, and duplicating events
* [Deprecated] Console ui for part 1 functionality
## Configuration Files
* Project files are inside the 'Calendar App' folder.
* Each user has its own directory in users/, with the username as the directory name.
* Inside the user directory:
  * credentials.txt stores the username, password, and time since last logged in.
  * Each folder represents a calendar that belongs to the user.
* Inside each calendar directory:
  * events/ stores the events the user has created
    * event files are stored by event ID
    * the postponed/ directory is where postponed events are stored
  * alerts/ stores the alerts the user has created
    * alert files represent AlertCollections, and are stored by eventID.
  * memos.txt and tags.txt store the Memos and Tags respectively.
