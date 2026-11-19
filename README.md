⚡ JFlux

<h3 align="center">A Lightweight Java Reverse Proxy & Backend Traffic Management System</h3>
<p align="center">
  <strong>Smarter Traffic Distribution. Reliable Backend Communication.</strong>
</p>
<p align="center">
  <a href="README.md">🇬🇧 English</a> · <a href="README_TR.md">🇹🇷 Türkçe</a>
</p>
<p align="center">
  <img src="https://img.shields.io/badge/Java-25-orange?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Maven-Build%20Tool-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" />
  <img src="https://img.shields.io/badge/Reverse%20Proxy-Architecture-blue?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Load%20Balancing-Round%20Robin-6C63FF?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Cache-Enabled-16A085?style=for-the-badge" />
</p>

⸻

🚀 Overview

JFlux is a lightweight reverse proxy server developed using Java. It receives incoming HTTP requests and distributes them across multiple backend servers.

The project brings together essential backend infrastructure concepts in a modular architecture, including load balancing, response caching, rate limiting, backend health monitoring, automatic failover, and performance metrics.

JFlux aims to demonstrate how backend systems manage incoming traffic, handle server failures, and maintain reliable communication between clients and backend services.

✨ Key Features

Feature	Description
🔀 Reverse Proxy	Receives client requests and forwards them to backend servers.
⚖️ Load Balancer	Distributes incoming requests using the Round-Robin algorithm.
⚡ Response Cache	Temporarily stores successful GET responses.
🛡️ Rate Limiter	Controls request frequency based on client IP addresses.
💓 Health Checks	Monitors backend server availability.
🔄 Automatic Failover	Attempts another backend when a server becomes unavailable.
🔁 Retry Mechanism	Supports retry attempts for failed requests.
📊 Metrics Collector	Tracks request statistics and backend performance.
🧵 Virtual Threads	Uses Java Virtual Threads for concurrent request handling.

🏗️ System Architecture

JFlux follows a modular architecture where each component is responsible for a specific part of request processing.

flowchart TD
    A[Client] --> B[JFlux Proxy Server]
    B --> C[Rate Limiter]
    C --> D[Response Cache]
    D --> E[Load Balancer]
    E --> F[Backend Server 1]
    E --> G[Backend Server 2]
    E --> H[Backend Server 3]
    I[Health Checker] --> F
    I --> G
    I --> H
    B --> J[Metrics Collector]

Core Components

* ProxyServer: Manages the main proxy server and incoming connections.
* ClientHandler: Handles client requests and backend communication.
* LoadBalancer: Selects a backend server.
* ResponseCache: Manages temporary response storage.
* RateLimiter: Controls client request frequency.
* MetricsCollector: Collects application performance statistics.
* ProxyConfig: Manages application configuration.

🛠️ Technology Stack

<p align="center">
  <img src="https://img.shields.io/badge/Java-25-orange?style=flat-square&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Maven-Build%20Tool-C71A36?style=flat-square&logo=apachemaven&logoColor=white" />
  <img src="https://img.shields.io/badge/IntelliJ%20IDEA-000000?style=flat-square&logo=intellijidea&logoColor=white" />
</p>

Technology	Usage
Java 25	Core application development
Maven	Dependency and build management
Java Sockets	Client connection handling
Java HttpClient	Backend HTTP communication
Virtual Threads	Concurrent processing
ConcurrentHashMap	Thread-safe data management
AtomicInteger / AtomicLong	Atomic counter operations
ScheduledExecutorService	Scheduled tasks

📂 Project Structure

JFlux/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/jflux/
│       │       ├── balancer/
│       │       │   └── LoadBalancer.java
│       │       ├── cache/
│       │       │   └── ResponseCache.java
│       │       ├── config/
│       │       │   └── ProxyConfig.java
│       │       ├── metrics/
│       │       │   └── MetricsCollector.java
│       │       ├── model/
│       │       │   ├── BackendServer.java
│       │       │   └── HttpResponseData.java
│       │       ├── ratelimit/
│       │       │   └── RateLimiter.java
│       │       ├── server/
│       │       │   ├── ClientHandler.java
│       │       │   └── ProxyServer.java
│       │       ├── testutil/
│       │       │   └── MockBackendRunner.java
│       │       └── Main.java
│       │
│       └── resources/
│           └── config.properties
│
├── pom.xml
└── README.md

⚙️ Getting Started

Prerequisites

* Java JDK 25
* Apache Maven
* IntelliJ IDEA (recommended)

1. Clone the Repository

git clone https://github.com/mirayyozcann/JFlux.git
cd JFlux

2. Build the Project

mvn clean compile

3. Start Mock Backend Servers

Run the following class from IntelliJ IDEA:

com.jflux.testutil.MockBackendRunner

4. Start JFlux

Run the main class:

com.jflux.Main

5. Send a Request

curl http://localhost:8080/

📊 Metrics Endpoint

http://localhost:8080/_jflux/metrics

🎯 Project Objectives

* Understand reverse proxy architecture.
* Implement request distribution strategies.
* Explore concurrent programming with Java.
* Manage backend availability and failure scenarios.
* Apply caching and rate-limiting mechanisms.
* Collect and monitor application metrics.

🔮 Future Improvements

* Expanded automated testing.
* Advanced HTTP header forwarding.
* Improved cache management.
* Enhanced monitoring and observability.
* Additional load balancing strategies.

📌 Project Status

JFlux is an educational backend infrastructure project developed to demonstrate reverse proxy and traffic management concepts.

The project is open to further development and improvements.

⸻

👩‍💻 Author

Miray Özcan

Computer Engineering Student

<p>
  <a href="https://github.com/mirayyozcann">
    <img src="https://img.shields.io/badge/GitHub-mirayyozcann-181717?style=for-the-badge&logo=github" />
  </a>
</p>

⸻

<p align="center">
  <strong>⚡ JFlux — Lightweight Proxy. Smarter Traffic Management.</strong>
</p>
