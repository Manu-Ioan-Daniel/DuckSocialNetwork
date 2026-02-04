# DuckSocialNetwork

A desktop *social network* app with a playful theme (users can be **Duck** or **Person**), built with **JavaFX** + **PostgreSQL**. The project includes authentication, user management, friendships/friend requests, chat with replies, events (including *race events* for ducks), and a notification system.

> Project: `DuckSocialNetwork` (Gradle) • UI: FXML + CSS • JavaFX 17

---

## Main features

- **Login / authentication**
  - login using username + password
  - passwords are stored **hashed + salted** in the DB (format `salt:hash`)

- **Users dashboard (user list + pagination)**
  - user list (TableView)
  - **pagination** (configured via `USERS_PER_PAGE`)
  - stats: total users / ducks / people
  - **double click** a user → opens their profile
  - add/delete user (you can’t delete yourself)

- **Create user (Duck / Person)**
  - dedicated form with fields depending on the user type
  - `Duck`: `DuckType`, `speed`, `resistance`
  - `Person`: `name`, `surname`, `occupation`, `dateOfBirth`, `empathyLevel`

- **User profile**
  - separate profile views for `Duck` and `Person` (distinct FXML)

- **Friends & Friend Requests**
  - friends list
  - “others” list (users who aren’t friends)
  - sent/received requests
  - actions: **send / cancel / accept / deny** + remove friend

- **Chat**
  - 1–1 conversations between users
  - messages grouped with headers (You / username)
  - supports **replying to a message**

- **Events + subscribers + notifications**
  - create events
  - subscribe to events
  - for `RaceEvent`: **only ducks** can register
  - start event → computes the result (via `NatatieSolver`) and sends notifications to subscribers

- **Notifications (UI alerts)**
  - notifications are persisted in the DB
  - when opening screens, notifications are read and then deleted

---

## Tech stack

- **Language:** Java
- **UI:** JavaFX 17 (`javafx.controls`, `javafx.fxml`)
- **Build tool:** Gradle
- **Database:** PostgreSQL
- **Testing:** JUnit 5 (configured in Gradle)

Main dependencies (from `build.gradle`):
- `org.openjfx.javafxplugin` (JavaFX)
- `org.postgresql:postgresql`
- `org.junit.jupiter` (tests)

---

## Architecture / structure

The project follows a classic layered structure:

- `controller/` – JavaFX controllers (UI logic + event handlers)
- `service/` – business logic (Users, Community, Friends, Chat, Events, Security, etc.)
- `repo/` – data access layer (PostgreSQL via JDBC)
- `models/` – domain entities (User, Duck, Person, Friendship, Message, Event, etc.)
- `validation/` – input/entity validators
- `utils/` – helpers (FXML loader, stage manager, password hashing, observer utilities, UI utils, etc.)
- `src/main/resources/view/` – **FXML** files
- `src/main/resources/styling/` – **CSS** files
- `src/main/resources/images/` – icons/images used by the app

### Patterns / concepts used

- **MVC-ish (JavaFX):** FXML + Controller + Service
- **Repository pattern:** `Db*Repo` + the `Repository` interface
- **Observer pattern:** services extend `Observable`, UI subscribes via `Observer` / `NotificationHandler`
- **Factory pattern:** `UserFactory`, `EventFactory`, `NotificationFactory`, etc.
- **DTOs:** e.g. `UserDTO`, `UserTableDTO`, `EventSubscriberDTO`

---

## Database (PostgreSQL)

The app uses JDBC and connects via a singleton (`utils/DbConnection`). At the moment, the connection string is hardcoded in the project, in the form:

- `jdbc:postgresql://localhost:5432/MAP2?user=postgres&password=123`

The tables used by the app are listed in `dbquery/tables.txt` (friendships, friend_requests, messages, events, notifications, etc.).

> GitHub tip: move credentials into an external config file (e.g. `application.properties`) or environment variables and exclude it from the repo.

---

## How to run

### Requirements

- JDK 17
- A local PostgreSQL instance + schema/tables created

### Build & run (Gradle)

```cmd
gradlew.bat clean build
gradlew.bat run
```

`mainClass` is set in Gradle to `main.Main`.

---

## UI (FXML)

Main windows/scenes (from `src/main/resources/view`):

- `loginWindow.fxml`
- `usersForm.fxml`
- `friendsForm.fxml`
- `chat.fxml`
- `eventsForm.fxml`
- `addUserForm.fxml`
- `addEventForm.fxml`
- `sendMessageForm.fxml`

---

## Known notes / possible improvements

- In `main/Main.java`, `openLoginWindow()` is called twice (it opens two login windows). One call is likely accidental.
- The DB connection is hardcoded (user/password). Ideally, this should be externalized.
- Adding unit/integration tests for services + repositories would be a good next step.

---

## License

Educational / demo project. Choose a license (MIT / Apache-2.0) if you want the usage terms to be explicit.
