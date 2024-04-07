
# Train Time Display System

## Overview
This project is a train time display system designed to provide users with up-to-date information on train schedules. It utilizes a client-server architecture to fetch and display train times, leveraging a database for schedule storage and real-time updates.

## Features
- Real-time train schedule display in a Swing GUI
- Database-backed storage for train line and station details
- Client-Server multithreaded architecture
- Utilizes MTA's GTFS API for transit data queries from the server
- Configuration options for customization

## Getting Started

### Prerequisites
- Java JDK 8 or newer.
- Access to a SQL database.

### Installation
1. Clone the repository to your local machine.
2. Configure /resources/config.properties to reflect your setup on your machine
3. Ensure the database schema is set up as expected by the application using the /resources/sql/ folder

### Running the Application
1. Start the server:
   ```bash
   java StartServer.java
   ```
2. In a separate terminal, start the client:
   ```bash
   java StartClient.java
   ```

## Architecture
- **Server:** Handles data processing, database operations, and client requests. Multi-threaded to allow for multiple Client connections.
- **Client:** Displays the train times to the user and interacts with the server for real-time updates.
