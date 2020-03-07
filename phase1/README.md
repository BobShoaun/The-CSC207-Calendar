# Calendar App - Group 0248
* To run the program, execute the main() method inside consoleui.UserManagerUI.java.
* You can log in using the username tests and password tests.
* The UI works by giving the user a list of options. To select a menu item,
input the number of the item into the text field.

## Configuration Files
* Project files are inside the 'Calendar App' folder.
* Each user has its own directory in users/, with the username as the directory name.
* Inside the user directory:
  * credentials.txt stores the username, password, and time since last logged in.
  * events/ stores the events the user has created
    * event files are stored by the series name, with "noname.txt" being any
    unnamed series.
  * alerts/ stores the alerts the user has created
    * alert files represent AlertCollections, and are stored by eventID.

## Notes
* There is no option yet to add an infinite series, we will add that later.
