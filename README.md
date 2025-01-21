# ls-tech-challenge

# PostgreSQL Database Setup with Docker Compose

This project includes a `docker-compose.yml` file to easily spin up a PostgreSQL database instance locally.

## Prerequisites

Before you begin, ensure that the following are installed on your system:

- [Docker](https://www.docker.com/get-started)
- [Docker Compose](https://docs.docker.com/compose/install/)

---

## How to Run the Database

Follow these steps to spin up the PostgreSQL database:

### 1. Clone the Repository
Clone this repository to your local machine:
```bash
git clone <repository-url>
cd <repository-folder>
```

### 2. Modify the `docker-compose.yml` (Optional)
The default settings in the `docker-compose.yml` file are:

- **PostgreSQL Username**: `app_user`
- **PostgreSQL Password**: `Beyond80.Bucks`
- **Database Name**: `app_db`
- **Port**: `5432`

If needed, update the values in the `environment` section of the `docker-compose.yml` file. For example:
```yaml
    environment:
      POSTGRES_USER: <your-preferred-username>
      POSTGRES_PASSWORD: <your-preferred-password>
      POSTGRES_DB: <your-preferred-database>
```

### 3. Run Docker Compose
Run the following command to start the PostgreSQL container in detached mode:
```bash
docker-compose up -d
```

This will:
- Pull the necessary `postgres` image (if not already present) from Docker Hub.
- Spin up a container named `postgres_db`.
- Expose the PostgreSQL instance on your local machine at `localhost:5432`.

### 4. Verify the Database
You can verify the database is running by connecting to it:

#### Using the `psql` Command Line:
```bash
psql -h localhost -U app_user -d app_db
```
Enter the password (`mypassword` by default) when prompted.

#### Using a GUI Tool:
Use a database client such as [pgAdmin](https://www.pgadmin.org/), [DBeaver](https://dbeaver.io/), or DataGrip, and connect with the following parameters:
- **Host**: `localhost`
- **Port**: `5432`
- **Username**: `app_user`
- **Password**: `Beyond80.Bucks`
- **Database**: `app_db`

---

## Stop and Remove the Database

To stop the running container:
```bash
docker-compose down
```

If you also want to delete the associated volume (and reset all database data):
```bash
docker-compose down -v
```

---

## Resetting the Database

If you want to reset the database completely:
1. Stop the container with:
   ```bash
   docker-compose down -v
   ```
2. Restart the services to recreate the volume and start fresh:
   ```bash
   docker-compose up -d
   ```

---

## Check Container Logs

To check logs for the PostgreSQL container:
```bash
docker-compose logs -f postgres
```

---

## Troubleshooting

1. **Port Conflicts**:
   If port `5432` is already in use, change the port mapping in `docker-compose.yml`:
   ```yaml
   ports:
     - "5433:5432"
   ```
   Then connect to PostgreSQL on `localhost:5433`.

2. **Permission Errors**:
   Ensure Docker is installed correctly and you have the necessary permissions to run Docker on your system.

---

This setup ensures you have a fast and easy-to-use local PostgreSQL instance while persisting data across container restarts using Docker volumes. Let me know if you encounter any issues!