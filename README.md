<div align="center">

# Persian Learning App
### فارسی سیکھنے کی ایپ

A desktop application for managing Persian learning chapters,
instructional content, and Persian–Urdu vocabulary.

**Java 8 · Swing · JDBC · MySQL-compatible database · Eclipse**

Software Construction & Development — Group Project

</div>

---

## Overview

Persian Learning App provides an administrative workspace for preparing
chapter-based learning material.

The project is organised around four use cases: chapter management,
chapter structure, instructional content, and glossary management.
Each team member owns the presentation, business, and data-access
implementation of one use case.

The application uses Persian and Urdu text, with right-to-left
presentation in its Swing interface.

## Current Development Status

Use-case implementations have been developed on the members' feature
branches. Integration and review are ongoing.

| Area | Current position |
|---|---|
| UC-01: Chapter management | Implemented; validation and usability improvements ongoing |
| UC-02: Chapter structure | Implementation developed on Zohaib's branch |
| UC-03: Instructional content | Implementation developed on Faseeh's branch |
| UC-04: Chapter glossary | Implementation developed on Rabia's branch; integration review ongoing |
| Combined application | Integration of all four use cases is in progress |
| Formal unit testing | Scheduled for the next iteration |

> The screens available when running the app depend on the checked-out
> branch. A feature branch may not yet contain another member's latest
> changes. Fetching branches does not automatically merge their code.

## Team and Responsibilities

| Member | Use case | Presentation class | Business class | JDBC DAO |
|---|---|---|---|---|
| Laiba | UC-01: Manage Chapters | `ChapterManagementPanel` | `ChapterBO` | `MySQLChapterDAO` |
| Zohaib | UC-02: Configure Chapter Structure | `StructureManagementPanel` | `StructureBO` | `MySQLComponentDAO` |
| Faseeh | UC-03: Manage Instructional Content | `ContentManagementPanel` | `ContentBO` | `MySQLComponentContentDAO` |
| Rabia | UC-04: Manage Chapter Glossary | `GlossaryManagementPanel` | `GlossaryBO` | `MySQLGlossaryDAO` |

Shared files, including `App.java`, `MainFrame.java`, DAO contracts,
domain classes, and the database schema, require coordination before
merging changes.

## Use Cases

### UC-01 — Manage Chapters

- Create a chapter with a system-generated identifier.
- List and view existing chapters.
- Rename a chapter.
- Delete a chapter after confirmation.
- Validate required titles, title length, and permitted script.
- Require at least one letter in a chapter title.
- Publish chapter-created, chapter-updated, and chapter-deleted events.

Chapter titles are limited to 255 Unicode code points. Script validation
checks permitted characters; it does not identify the natural language
of the text.

### UC-02 — Configure Chapter Structure

- Select components for a chapter.
- Define their display order.
- Maintain and save the chapter's structure.

The component catalogue includes:

| Component |
|---|
| Learning objectives |
| Persian dialogue |
| Persian reading passage |
| Urdu translation or explanation |
| Grammar or language note |
| Examples |
| Exercises |
| Glossary |

`EXERCISES` is a component type. It does not represent a separate
implemented exam or automatic grading system.

### UC-03 — Manage Instructional Content

- Load a chapter's configured components.
- Select a component for content entry.
- Enter and maintain Persian and Urdu text.
- Save, retrieve, and remove component content.

A configured component can have one associated content record.

### UC-04 — Manage Chapter Glossary

- List glossary entries for a chapter.
- Add Persian words and their Urdu meanings.
- Edit existing entries.
- Delete entries after confirmation.

Learner study and exam interfaces are outside the current
implementation scope. A `LearningMode` contract exists for future
glossary visibility rules.

## Architecture

The application follows a three-layer structure.

| Layer | Responsibility | Package |
|---|---|---|
| Presentation | Swing screens, user input, and displayed feedback | `presentation` |
| Business | Validation and use-case rules | `business` |
| Data access | DAO contracts, SQL operations, and persistence | `dao`, `dao.mysql` |

Presentation classes call business objects. Business objects access
persistence through `IDALFacade`. The concrete `DALFacade` delegates
operations to the relevant DAO.

SQL belongs in the data-access layer.

### Design Patterns

| Pattern | Application |
|---|---|
| DAO | Separates persistence operations from business rules |
| Business Object | Organises validation and operations for each use case |
| Facade | Provides a unified data-access contract through `IDALFacade` |
| Abstract Factory / Factory | Creates DAO implementations and the DAL facade |
| Observer | Supports chapter-event notifications and chapter-list refresh |

Dependencies are passed into business objects through constructors.

The current chapter flow uses `LearningEventSource` and the project's
custom `Observable` and `Observer` types.

## Project Structure

| Location | Contents |
|---|---|
| `src/main/java/edu/persianlearningapp/App.java` | Application entry point |
| `src/main/java/edu/persianlearningapp/domain/` | Domain objects and enums |
| `src/main/java/edu/persianlearningapp/presentation/` | Swing windows and panels |
| `src/main/java/edu/persianlearningapp/business/` | Business objects and validation |
| `src/main/java/edu/persianlearningapp/dao/` | DAO and factory contracts |
| `src/main/java/edu/persianlearningapp/dao/mysql/` | JDBC implementations and connection setup |
| `src/main/java/edu/persianlearningapp/observer/` | Event notification infrastructure |
| `src/test/java/` | Test sources and shared test support |
| `database/schema.sql` | Database creation script |
| `design/` | Design documentation |
| `lib/` | Local dependency JARs |

## Technology

| Technology | Purpose |
|---|---|
| Java 8 / JDK 1.8 | Application language and runtime target |
| Swing | Desktop interface |
| JDBC | Database connectivity |
| XAMPP database service | Local database server |
| MySQL Connector/J 8.4.0 | JDBC driver used by the project |
| Eclipse IDE | Development and execution |
| Git and GitHub | Version control and collaboration |
| JUnit 5 | Unit-testing support for the next iteration |

The project does not use Maven.

XAMPP distributions commonly provide MariaDB under the control panel's
“MySQL” service label. Use the team's agreed server and JDBC-driver
configuration.

## Local Setup

### 1. Clone the repository

```bash
git clone https://github.com/laiba644/PersianLearningApp.git
cd PersianLearningApp
```

For a particular feature branch, fetch the latest remote references
and check out the relevant branch before running the application.

### 2. Import into Eclipse

1. Select **File → Import → General → Existing Projects into Workspace**.
2. Choose the cloned project folder containing `.project`.
3. Leave **Copy projects into workspace** unchecked.
4. Finish the import.

Keeping the project in the cloned folder ensures Eclipse and Git Bash
operate on the same files.

### 3. Configure Java and encoding

- Add a **JDK 8** installation under
  **Window → Preferences → Java → Installed JREs**.
- Set the project's Java compiler compliance level to **1.8**.
- Select Java 8 for the application's run configuration.
- Set **Project → Properties → Resource → Text file encoding** to **UTF-8**.

Eclipse itself may run on a newer Java version; the project's compiler
and application runtime must still match the Java 8 target.

### 4. Add the JDBC driver

Place the actual MySQL Connector/J 8.4.0 JAR at:

```text
lib/mysql-connector-j-8.4.0.jar
```

Do not rename a different driver version to make it appear to be 8.4.0.

Refresh the project. If the dependency remains unresolved, use:

**Project → Properties → Java Build Path → Libraries → Add JARs**

Select the JAR from the project's `lib` folder. Avoid committing
machine-specific absolute library paths.

### 5. Create the database

1. Open the XAMPP Control Panel.
2. Start the database service.
3. Start Apache if using XAMPP's phpMyAdmin interface.
4. Open phpMyAdmin.
5. Import `database/schema.sql`.

The supplied schema uses:

```text
persian_learning_app_team
```

The database name in the JDBC URL must match the database actually
created on your machine. If you use a different local database name,
configure the connection accordingly.

### 6. Configure the connection

The project supports these environment variables:

| Variable | Purpose | Default |
|---|---|---|
| `PERSIAN_DB_URL` | JDBC connection URL | Local `persian_learning_app_team` database |
| `PERSIAN_DB_USER` | Database username | `root` |
| `PERSIAN_DB_PASSWORD` | Database password | Empty string |

In Eclipse, local values can be supplied through:

**Run → Run Configurations → Java Application → Environment**

Do not commit database passwords or private connection settings.

### 7. Resolve the JUnit container if required

The project includes a test source folder. If Eclipse reports a missing
JUnit container, add it using:

**Build Path → Add Libraries → JUnit → JUnit 5**

Formal test development and CFG coverage analysis are planned for the
next iteration. Existing test files do not establish completed coverage.

### 8. Run the application

Open:

```text
src/main/java/edu/persianlearningapp/App.java
```

Right-click → **Run As → Java Application**.

Keep the database service running while using database-backed features.

## Database Design

The schema uses `utf8mb4` to store Persian and Urdu text.

| Table | Purpose |
|---|---|
| `chapters` | Chapter identifiers, titles, and timestamps |
| `chapter_components` | Component types and display order within a chapter |
| `component_contents` | Persian and Urdu content for a configured component |
| `glossary_entries` | Persian words and Urdu meanings belonging to a chapter |

Foreign keys provide cascading deletion:

- Deleting a chapter removes its components and glossary entries.
- Deleting a component removes its associated content.

Chapter identifiers are stable database keys. Deleting a chapter does
not renumber the remaining chapters.

## Team Workflow

### Feature branches

| Member | Branch |
|---|---|
| Laiba | `feature/laiba-uc1-chapters` |
| Zohaib | `feature/zohaib-uc2-components` |
| Faseeh | `feature/faseeh-uc3-content` |
| Rabia | `feature/rabia-uc4-glossary` |

Branch names are case-sensitive. Use `git branch -r` to confirm the
exact remote branch name before checking it out.

### Working on an existing branch

For example, after Laiba's local tracking branch has been created:

```bash
git status
git switch feature/laiba-uc1-chapters
git pull --ff-only origin feature/laiba-uc1-chapters
```

Commit or safely stash pending work before switching branches.

### Making a focused change

1. Implement one coherent improvement or fix.
2. Build and manually check the affected flow.
3. Inspect the diff.
4. Stage only the relevant files.
5. Commit with a message describing the change.
6. Push to the member's feature branch.

Example:

```bash
git diff
git add src/main/java/edu/persianlearningapp/business/ChapterBO.java
git diff --cached
git commit -m "Require at least one letter in chapter titles"
git push origin feature/laiba-uc1-chapters
```

### Review and integration

- Open a pull request into `main`.
- Have another member review the changes.
- Check shared startup and navigation files carefully.
- Preserve all required screens when resolving conflicts.
- Use merge commits when preserving individual commit history.
- Verify the integrated application before treating it as complete.

Do not force-push to shared branches or commit local build output,
workspace metadata, or credentials.

## Verification and Next Iteration

During the current iteration, changes are checked by building the
application and manually exercising the affected user flows.

The next iteration will develop formal unit tests using control-flow
graphs and test-path mappings, including:

- Node coverage
- Edge coverage
- Branch coverage
- Edge-pair coverage
- Valid, invalid, and boundary inputs

Business-layer unit tests will use test doubles rather than a live
database. Database persistence and cascading deletion require separate
integration checks.

No complete coverage percentage is claimed at this stage.

## Troubleshooting

| Problem | Check |
|---|---|
| Missing JDBC library | Confirm the JAR exists in `lib` and is on the build path |
| Unsupported class version | Align compiler and application runtime to Java 8, then clean and rebuild |
| A tab is missing | Check the current branch and its `MainFrame` wiring |
| Changes do not appear in Eclipse | Refresh with F5 and confirm the project location matches the Git repository |
| Undefined constructor after switching branches | Refresh and rebuild; check that `App` and `MainFrame` signatures match |
| Database connection fails | Check the running service, port, database name, credentials, and JDBC URL |
| Persian or Urdu text is corrupted | Check UTF-8 source encoding, database charset, and connection settings |

## Learning-Material Reference

[Farsi Ki Pehli Kitab — Professor Khan Sahab Qazi Fazle Haq](https://archive.org/details/FarsiKiPehliKitabByProfessorKhanSahabQaziFazleHaq/page/n15/mode/2up)

This book is the reference for preparing learning material.
A fresh schema import does not automatically populate the application
with the book's chapters or text.

---

Developed collaboratively by **Laiba, Zohaib, Faseeh, and Rabia**
for the Software Construction & Development course.
