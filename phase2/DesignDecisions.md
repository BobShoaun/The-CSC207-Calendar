# Calendar App Design Decisions - Group 0248
* major design decisions of the Calendar program

## Façade Design Pattern
* implementation: Calendar.java
Calendar acts as a façade for Alerts, Memos, Tags, and Events, containing instance variables of managers for each 
object. The Calendar class wraps up the overall functionality of the program and delegates the operations to the 
managers, creating a higher-level interface that encapsulates the complexity of the whole Calendar program. Calendar 
façade provides access to the Calendar program but hides the complexity and implementations from the users.

============================================================

## Observer Design Pattern
* implementation: Event.java (Observable), AlertCollection.java (Observer)
To maintain the consistency between Event and Alert, Event is designed to be an Observable object with Observer 
AlertCollection. When the name or time of an Event is changed, the notifyObservers() call in the Event methods updates 
the observer and the AlertCollection acts accordingly. The Observable-Observer implementation handles the dependency 
between Event and AlertCollection by providing a less coupled design that keeps the two Objects from knowing irrelevant 
information of each other.

============================================================

## Iterator Design Pattern
* implementation: EventCollection.java
EventCollectionIterator allows iteration over the Events in an EventCollection. The iterating behaviour is extracted 
into an Iterator object in the EventCollection class that encapsulates all the iterating details. This gives the client
access to the Events in a sequential manner without revealing the underlying representation. Implementing an Iterator 
cleans up the EventCollection class an decouples the iteration algorithms from it.

============================================================

## Factory Design Pattern
* implementation: SeriesFactory.java
When the EventManager creates a series, the SeriesFactory decides whether a regular Series or a FiniteSeries is 
instantiated based on the input. SeriesFactory serves as an interface for creating series but the creation details are 
delegated to the subclasses. This design encapsulates the creation process and decouples the client from the 
implementation details, which allows introduction of new series types without changing the existing client code 
(Open/Closed principle). 

============================================================

## Strategy Design Pattern - Manager
* implementation: IDManager.java, EventManager.java, MemoManager.java, TagManager.java, UserManager.java
The Manager classes captures the interactions between Calendar class and the objects. All actions done to the object 
by the Calendar has to go through the Manager of that object. Manager class encapsulates the algorithm details of its 
object and creates abstraction between the client and the object. 

============================================================

## Strategy Design Pattern - DataSaver
* implementation: DataSaver.java
DataSaver is responsible for serializing data into the file system, and deserializing data to be loaded into memory. 
Manager class needs to save its data every time something is changed for that object, and this is true for all Managers.
The DataSaver class allows them to use the saving algorithm in different contexts. It also decouples the implementation 
of the Managers from the implementation of these algorithms, so they are independent and unaware of each other. 
DataSaver also allows introduction of new methods without having to change the existing client code (Open/Closed 
Principle).

============================================================

## Dependency Injection
* implementation: Calendar.java
We break the dependencies between the Calendar class and the objects in the Calendar by introducing the Manager classes.
Calendar interacts with multiple Managers that are responsible for the calendar objects. The Managers decouple the 
implementation of the objects from the Calendar, making it a lot easier to extend the program. 
