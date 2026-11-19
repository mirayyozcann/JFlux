<h1 align="center">⚡ JFlux</h1>

<h3 align="center">A Lightweight and Modular Java-Based Reverse Proxy & Backend Traffic Management System</h3>
<p align="center">
  <strong>Smart Traffic Distribution. Reliable Backend Communication.</strong>
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

---

## 🚀 Overview

**JFlux** is a lightweight **Reverse Proxy** server application developed using Java. It receives incoming HTTP requests and distributes them across multiple backend servers.

The project brings together essential backend infrastructure concepts in a modular architecture, including load balancing, response caching, rate limiting, TCP-based health checks, failover on connection errors, and performance metrics.

JFlux demonstrates how backend systems route incoming traffic, handle backend connection failures, and manage communication between clients and services.

## ✨ Key Features

| Feature | Description |
| :--- | :--- |
| 🔀 **Reverse Proxy** | Receives incoming HTTP requests from clients and forwards them to backend servers. |
| ⚖️ **Load Balancer** | Distributes incoming requests among healthy backend servers using the Round-Robin algorithm. |
| ⚡ **Response Cache** | Stores successful `GET` responses in memory for a configurable TTL period. |
| 🛡️ **Rate Limiter** | Limits the number of requests per minute based on client IP addresses. Returns HTTP `429 Too Many Requests` when the limit is exceeded. |
| 💓 **Health Checks** | Periodically checks the TCP connectivity of backend servers. |
| 🔄 **Failover** | Marks a backend as unavailable after a connection failure and attempts to use another healthy server. |
| 🔁 **Retry Mechanism** | Provides a configurable number of retry attempts for failed backend connections. |
| 📊 **Metrics Collector** | Exposes request, cache, rate-limit, failure, and backend success counters in JSON format through `/_jflux/metrics`. |
| 🧵 **Virtual Threads** | Handles concurrent client connections using Java Virtual Threads. |

## 🏗️ System Architecture

JFlux follows a modular architecture in which each component is responsible for a specific stage of the request-processing pipeline.

```mermaid
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
```

### Core Components

- **ProxyServer:** Starts the main socket server, accepts incoming connections, and manages client requests.
- **ClientHandler:** Parses client requests and handles rate limiting, cache checks, and backend communication.
- **LoadBalancer:** Selects healthy backend servers using the Round-Robin algorithm and performs TCP-based health checks.
- **ResponseCache:** Stores successful GET responses in memory using a TTL-based caching mechanism.
- **RateLimiter:** Controls request frequency based on client IP addresses.
- **MetricsCollector:** Collects request, cache, rate-limit, failure, and backend success counters.
- **ProxyConfig:** Loads runtime settings from `config.properties`.

## 🛠️ Tech Stack

<p align="center">
  <img src="https://img.shields.io/badge/Java-25-orange?style=flat-square&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Maven-Build%20Tool-C71A36?style=flat-square&logo=apachemaven&logoColor=white" />
  <img src="https://img.shields.io/badge/IntelliJ%20IDEA-000000?style=flat-square&logo=intellijidea&logoColor=white" />
</p>

| Technology | Purpose |
| :--- | :--- |
| **Java 25** | Core application development |
| **Maven** | Project and build management |
| **Java Sockets** | Listening for and managing client connections |
| **Java HttpClient** | HTTP communication with backend servers |
| **Virtual Threads** | Managing concurrent connections |
| **ConcurrentHashMap** | Thread-safe memory and cache management |
| **AtomicInteger / AtomicLong** | Atomic counter and index operations |
| **ScheduledExecutorService** | Periodic backend health-check tasks |

## 📂 Project Structure

```text
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
├── README.md
└── README_TR.md
```

## ⚙️ Getting Started

### Prerequisites

- Java JDK 25
- Apache Maven
- IntelliJ IDEA (recommended)

### 1. Clone the Repository

```bash
git clone [https://github.com/mirayyozcann/JFlux.git](https://github.com/mirayyozcann/JFlux.git)
cd JFlux
```

### 2. Build the Project

```bash
mvn clean compile
```

### 3. Start the Mock Backend Servers

Run the following class from IntelliJ IDEA. It starts mock backend servers on ports `9001`, `9002`, and `9003`.

```text
com.jflux.testutil.MockBackendRunner
```

### 4. Start JFlux

Run the main class:

```text
com.jflux.Main
```

JFlux starts listening on port `8080` by default.

### 5. Send a Request

```bash
curl http://localhost:8080/
```

## ⚙️ Configuration

JFlux can be configured through the `config.properties` file.

| Property | Default Value | Description |
| :--- | :--- | :--- |
| `proxy.port` | `8080` | Proxy server port |
| `backend.urls` | `localhost:9001,9002,9003` | Backend server addresses |
| `cache.ttl.millis` | `10000` | Cache expiration time (ms) |
| `ratelimit.max.per.minute` | `30` | Request limit per IP per minute |
| `healthcheck.interval.millis` | `5000` | TCP health-check interval (ms) |
| `proxy.max.retries` | `3` | Maximum backend attempt count |

## 📊 Metrics Endpoint

JFlux exposes collected metrics in JSON format through the following endpoint:

```text
http://localhost:8080/_jflux/metrics
```

The report includes total requests, cache hits and misses, rate-limited requests, failed requests, and successful request counters for each backend.

## 🎯 Project Goals

- Understand and implement reverse proxy architecture.
- Practice the Round-Robin load-balancing strategy.
- Explore concurrent programming with Java Virtual Threads.
- Manage backend availability and connection failures.
- Implement caching and rate-limiting mechanisms.
- Collect and monitor application metrics.

## 🔮 Future Improvements

- Expand automated unit and integration test coverage.
- Implement advanced HTTP header forwarding rules.
- Improve cache eviction policies.
- Enhance observability and logging.
- Introduce additional load-balancing algorithms (e.g., *Least Connections*).

## 📌 Project Status

JFlux is a Java backend infrastructure project designed to demonstrate reverse proxy and backend traffic management concepts. It is open to further development and improvements.

---

<p align="center">
  <strong>⚡ JFlux — Lightweight Proxy. Smarter Traffic Management.</strong>
</p>
