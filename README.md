# Persian Learning App — Team Baseline

A Java 8, no-Maven Eclipse **skeleton** for the four assigned use cases. It contains class names, method signatures, shared contracts, domain data classes, schema, and TODO placeholders. The BO rules and all four MySQL DAO implementations are intentionally left for the assigned members. It is not a finished application.

## Team ownership

| Member | Use case | Presentation | Business | DAO / database | Tests |
|---|---|---|---|---|---|
| Laiba | UC-01 Manage chapters | `ChapterManagementPanel` | `ChapterBO` | `ChapterDAO`, `MySQLChapterDAO` | `ChapterBOTest` |
| Zohaib | UC-02 Configure chapter structure | `StructureManagementPanel` | `StructureBO` | `ComponentDAO`, `MySQLComponentDAO` | `StructureBOTest` |
| Faseeh | UC-03 Manage instructional content | `ContentManagementPanel` | `ContentBO` | `ComponentContentDAO`, `MySQLComponentContentDAO` | `ContentBOTest` |
| Rabia | UC-04 Manage glossary | `GlossaryManagementPanel` | `GlossaryBO` | `GlossaryDAO`, `MySQLGlossaryDAO` | `GlossaryBOTest` |

Each member completes all three layers for their use case: presentation, business, and DAO/data access. Shared domain, factory, facade, observer, and database schema changes should be discussed and reviewed by the group before merging.

## Project layout and patterns

- `domain`: chapter, component, content, glossary, and event model classes.
- `presentation`: Swing screens. Screens call business objects and contain no SQL.
- `business`: business rules and validation.
- `dao`: DAO contracts, factory contracts, and `IDALFacade`.
- `dao/mysql`: JDBC implementations, `DALFacade`, and connection setup.
- `observer`: Observer pattern contracts/event source.
- `src/test/java`: JUnit 5 business-layer tests and shared hand-written `StubDALFacade`.
- `design`: Mermaid domain model and layer map.
- `database/schema.sql`: schema for the project's local database.

Patterns represented: DAO, Business Object (BO), Facade, Abstract Factory/Factory, and Observer.

## Implementation status

The four BO classes and four MySQL DAO classes contain TODO placeholders only; they do not perform use-case logic or SQL yet. The four Swing panels are presentation shells and need controls/actions. Tests are placeholders and need assertions. Shared domain classes, DAO contracts, DAL facade/factory wiring, database connection setup, and Observer contracts are provided as team scaffolding. Running the app opens only a placeholder window until the team connects the screens and services.

The earlier team baseline had some BO/DAO logic prefilled. This Pure Skeleton intentionally removes that use-case implementation so each member can implement all assigned layers.

## Requirements and open decisions

The group should confirm these with the instructor before implementing behavior that depends on them:

1. Can a chapter structure include the same component type more than once? What ordering rules apply?
2. Which administrative chapter details are required beyond the title?
3. Must every instructional component contain both Persian and Urdu text?
4. FR-7 mentions exercises and cascade deletion, but the provided use-case list does not define an exercise use case or fields. Confirm the exercise scope before adding a table.
5. FR-10 distinguishes study from exam display for the glossary. Confirm whether learner study/exam screens are in this iteration.
6. Confirm which Observer listener(s) the instructor expects; the shared Observer contracts are provided, and the team should agree which listeners/events to connect.

The schema creates `chapters`, `chapter_components`, `component_contents`, and `glossary_entries`. Foreign keys cascade chapter deletion to related chapter data and component deletion to its content. Exercises are not modeled until requirements are confirmed.

## Setup in Eclipse 2026 (no Maven)

### 1. Extract and import

1. Extract this ZIP to a normal folder (not inside Eclipse's workspace `.metadata` folder).
2. In Eclipse choose **File → Import → General → Existing Projects into Workspace**.
3. Select the extracted `PersianLearningApp_PureSkeleton` folder, keep **Copy projects into workspace** unchecked if importing directly from that folder, then click **Finish**.
4. Install/select a **JDK 8** in **Window → Preferences → Java → Installed JREs**. In project **Properties → Java Compiler**, set compliance to **1.8**.

### 2. Add MySQL Connector/J

This project does not bundle the Connector/J binary. Download MySQL Connector/J 8.x, then copy the JAR into this project's `lib` folder and name it:

`lib/mysql-connector-j-8.4.0.jar`

In Eclipse, right-click the project → **Refresh**. If Eclipse does not pick it up, use **Build Path → Configure Build Path → Libraries → Add JARs**, select the JAR under the project `lib` folder, and apply. The project's `.classpath` uses this relative JAR location so teammates can use the same project layout. Do not commit a machine-specific absolute JAR path.

### 3. Create the database in XAMPP

1. Open XAMPP Control Panel and start **MySQL** (Apache is only needed if you are opening phpMyAdmin through XAMPP).
2. Open phpMyAdmin, select **Import**, and import `database/schema.sql`.
3. This project schema is named **`persian_learning_app_team`**. It is separate from `persian_learning_app` and `persian_learning_app_final`; the SQL does not delete those databases.
4. Default local connection settings are `localhost:3306`, username `root`, and an empty password. If your local settings differ, set `PERSIAN_DB_URL`, `PERSIAN_DB_USER`, and `PERSIAN_DB_PASSWORD` environment variables. Never put a real password in Git.

### 4. Add JUnit 5 and run tests

1. Right-click the project → **Build Path → Add Libraries → JUnit → JUnit 5 → Finish**. The project classpath is set to Eclipse's JUnit 5 container.
2. In Package Explorer, right-click a test class or `src/test/java` → **Run As → JUnit Test**.
3. The included business tests are empty placeholders until members add assertions; stub-based business tests should not require a running database. Add separate integration checks only when XAMPP is running and the team has agreed on test data cleanup.

### 5. Run the app

Right-click `src/main/java/edu/persianlearningapp/App.java` → **Run As → Java Application**. The current Swing screens are UI skeletons; implement the actions through the assigned BO and DAO before treating them as functional.

## GitHub team workflow

### First push: publish the shared baseline to `main`

Create an empty GitHub repository and add Zohaib, Faseeh, and Rabia as collaborators. Extract/import the folder, open Git Bash in the project root (the folder containing `.project` and `README.md`), then run:

```bash
git init
git branch -M main
git add .
git status
git commit -m "Add shared Java project baseline"
git remote add origin https://github.com/laiba644/PersianLearningApp.git
git push -u origin main
```

Before `git add`, confirm `.env`, passwords, `.metadata`, `bin`, and `*.class` are not being staged. The `.gitignore` excludes the usual local files. If Git says `remote origin already exists`, use `git remote -v` to inspect it and `git remote set-url origin https://github.com/laiba644/PersianLearningApp.git` only if it points to the wrong repository.

### Each member's work after baseline is pushed

Do not work directly on `main`. Each member creates their own branch from the latest `main`:

```bash
git switch main
git pull origin main
git switch -c feature/laiba-uc1-chapters
```

Use the matching branch names: `feature/zohaib-uc2-structure`, `feature/faseeh-uc3-content`, and `feature/rabia-uc4-glossary`. Make small commits, then publish the branch and open a Pull Request in GitHub:

```bash
git add src
git status
git commit -m "Implement UC-01 chapter management"
git push -u origin feature/laiba-uc1-chapters
```

A teammate reviews the PR, checks the change and tests, then merges it into `main`. Replace the branch and commit text with your own use case. After a PR is merged, everyone updates their local `main` before starting or continuing work:

```bash
git switch main
git pull origin main
```

Keep database passwords and local XAMPP settings out of commits. Never force-push to `main`.

## First implementation tasks

1. The group confirms the open requirements above and agrees on the schema.
2. Each member reads their BO, DAO interface, DAO implementation, Swing panel, and corresponding test.
3. Implement the tests and business rules, then the DAO SQL methods, then connect the screen actions.
4. Run tests and manually verify each flow with XAMPP before opening the Pull Request.
