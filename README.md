# Persian Learning App
### فارسی سیکھنے کی ایپ

A Java desktop app for managing Persian lessons and Persian–Urdu vocabulary.

This is our group project for the **Software Construction & Development** course. The app uses **Java 8, Swing, JDBC and a local database through XAMPP**. We use Eclipse without Maven.

## Team

Each member works on the UI, business logic and database code for their use case.

| Member | Use case |
|--------|----------|
| Laiba | UC1 — Create, view, rename and delete chapters |
| Zohaib | UC2 — Select and arrange chapter components |
| Faseeh | UC3 — Add and update Persian and Urdu content |
| Rabia | UC4 — Manage Persian words and their Urdu meanings |

## Features

- Manage chapters with title validation and delete confirmation.
- Choose chapter components and set their order.
- Save Persian and Urdu text for each component.
- Add, edit and delete glossary entries.
- Display Urdu and Persian text from right to left.

Exercises are part of the chapter components. Learner exams and automatic marking are not part of this iteration.

## Code Structure

The project has three layers:

| Layer | Purpose |
|-------|---------|
| Presentation | Swing screens and user input |
| Business | Validation and use-case rules |
| Data access | SQL queries and database operations |

The project also uses shared domain classes, DAO factories, a DAL facade and Observer notifications.

Source code is in `src/main/java/edu/persianlearningapp`.

## Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/laiba644/PersianLearningApp.git
   ```

2. In Eclipse, select **File → Import → General → Existing Projects into Workspace**. Choose the folder containing `.project`. Leave **Copy projects into workspace** unchecked.

3. Set the project compiler and application runtime to **Java 8**. Set the project text encoding to **UTF-8**.

4. Put the JDBC driver here:

   ```text
   lib/mysql-connector-j-8.4.0.jar
   ```

   Add it to the project's build path if Eclipse reports a missing library.

5. Start **MySQL** in XAMPP. Start **Apache** to use phpMyAdmin, then import:

   ```text
   database/schema.sql
   ```

6. The default database is `persian_learning_app_team`, with username `root` and an empty password. If your settings differ, use these environment variables in your Eclipse run configuration:

   - `PERSIAN_DB_URL`
   - `PERSIAN_DB_USER`
   - `PERSIAN_DB_PASSWORD`

7. If Eclipse reports a missing JUnit library, select **Build Path → Add Libraries → JUnit → JUnit 5**.

8. Run `App.java` using **Run As → Java Application**.

## Database

| Table | Stores |
|-------|--------|
| `chapters` | Titles, IDs and timestamps |
| `chapter_components` | Components and their order |
| `component_contents` | Persian and Urdu content |
| `glossary_entries` | Persian words and Urdu meanings |

Deleting a chapter also deletes its related components, content and glossary entries. Chapter IDs are not renumbered after deletion.

## Team Workflow

We work on separate feature branches and open pull requests into `main`. Another member reviews each change before merging.

Coordinate changes to shared files such as `App.java`, `MainFrame.java`, DAO interfaces and the database schema.

Commit only the files related to your change. Do not commit passwords or local build files.

## Progress

The use cases have been developed on the team branches. Integration and manual checks are ongoing. Formal unit testing and control-flow coverage are planned for the next iteration.

## Learning Material

Our reference book is [Farsi Ki Pehli Kitab by Professor Khan Sahab Qazi Fazle Haq](https://archive.org/details/FarsiKiPehliKitabByProfessorKhanSahabQaziFazleHaq).

Chapter text and vocabulary are entered by the admin. Importing the database schema does not add the book's content.
