## How to Run the Project Locally

### Prerequisites
Before running the project, ensure you have the following tools installed:
1. **Java Development Kit (JDK 21)**  
   Download and install [Java JDK 21](https://www.oracle.com/java/technologies/javase-jdk21-downloads.html).

2. **Gradle**  
   Gradle is used to build and manage dependencies. If you do not have Gradle installed globally, you can use the wrapper script (`./gradlew` or `gradlew.bat`), which is included in the project.

3. **PostgreSQL Database**  
   This project uses a PostgreSQL database configured via Docker Compose. You'll need the following tools:
   - [Docker](https://www.docker.com/get-started)
   - [Docker Compose](https://docs.docker.com/compose/install/)

4. An **IDE**  
   An IDE such as [IntelliJ IDEA](https://www.jetbrains.com/idea/download) for seamless development and debugging.

---

### Steps to Run the Project

#### Step 1: Clone the Repository
Clone the repository using Git:
```bash
git clone https://github.com/joopabs/ls-tech-challenge.git
cd ls-tech-challenge
```

---

#### Step 2: Set Up the PostgreSQL Database
The database is set up using Docker Compose. Follow these steps to get the database running:

1. Locate the `docker-compose.yml` file in the root folder.
2. Start the database container:
   ```bash
   docker-compose up -d
   ```
3. By default, the configuration in `docker-compose.yml` is:
   - **PostgreSQL Username**: `app_user`
   - **PostgreSQL Password**: `Beyond80.Bucks`
   - **Database Name**: `app_db`
   - **Port**: `5432`

   If needed, modify these values directly in the `docker-compose.yml` file.

4. Verify that the PostgreSQL instance is running:
   - Use the `psql` command-line tool:
     ```bash
     psql -h localhost -U app_user -d app_db
     ```
   - Or connect through GUI tools like [pgAdmin](https://www.pgadmin.org/), [DBeaver](https://dbeaver.io/), or JetBrains DataGrip.

---

#### Step 3: Configure the Application
Ensure the application is correctly configured to connect to the database.

1. Open the `application.yaml` file under `src/main/resources`, and check the database connection details:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/app_db
       username: app_user
       password: Beyond80.Bucks
   ```
2. If you modified ports or credentials in the `docker-compose.yml` file, update them in this configuration file as well.

---

#### Step 4: Run the Application
You can now run the application locally using **Gradle**.

**Using IntelliJ IDEA:**
1. Open the project in IntelliJ IDEA.
2. Ensure the JDK is set to version 21 in **File > Project Structure > SDK**.
3. Navigate to the main Spring Boot application file (usually located in `src/main/java`) and click the green "Run" button.

**Using Gradle Wrapper (Command Line):**
1. Ensure you're in the project directory (where `build.gradle` is located).
2. Run the following command to start the application:
   - On **Linux/Mac**:
     ```bash
     ./gradlew bootRun
     ```
   - On **Windows**:
     ```bash
     gradlew.bat bootRun
     ```

---

#### Step 5: Run Tests
This project includes REST Assured tests to verify the functionality of the REST APIs along with a Testcontainer instance of PostgreSQL.

**Using IntelliJ IDEA:**
1. Open the project in IntelliJ IDEA.
2. Navigate to the test classes, usually located in `src/test/java`.
3. Right-click on the test file or folder containing the tests (e.g., `SpeechControllerIntegrationTest`) and select **Run 'Tests'**.

**Using Gradle Wrapper (Command Line):**
1. Run the following command to execute all tests in the project:
   - On **Linux/Mac**:
     ```bash
     ./gradlew test
     ```
   - On **Windows**:
     ```bash
     gradlew.bat test
     ```

   This will automatically execute the REST Assured tests as part of the Gradle test task.

**Viewing Test Results:**
1. **In IntelliJ IDEA:** After running the tests, check the run window for results showing which tests passed or failed.
2. **In Command Line:** Gradle will output a summary of test results in the terminal. You can also review the detailed test reports, which are typically located at:
   ```bash
   build/reports/tests/test/index.html
   ```
   Open this file in your browser for a detailed summary.

---

#### Step 6: Access the Application
Once the application starts, it will be accessible at:
- **URL**: `http://localhost:8080`

---

### Stopping the Application and Database

To stop the Spring Boot application:
- If running in IntelliJ, click the "Stop" button.
- If running with Gradle, press `Ctrl+C` in the terminal.

To stop the database container:
```bash
docker-compose down
```

To stop and remove the database container and all its data:
```bash
docker-compose down -v
```

---

### Troubleshooting

#### Database Port Conflicts
If port `5432` is already in use:
1. Edit the port mapping in the `docker-compose.yml` file:
   ```yaml
   ports:
     - "5433:5432"
   ```
2. Update the `application.yml` file with the new port:
   ```yaml
   spring.datasource.url: jdbc:postgresql://localhost:5433/app_db
   ```

#### Resetting the Database
To reset the database:
1. Stop and remove the container with its volumes:
   ```bash
   docker-compose down -v
   ```
2. Restart the database by running:
   ```bash
   docker-compose up -d
   ```

#### Logs and Debugging
- View container logs:
  ```bash
  docker-compose logs -f postgres
  ```
---

This setup allows you to run the project locally with a PostgreSQL database. If you encounter any issues or have questions, feel free to reach out.