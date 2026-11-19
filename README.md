<h1 align="center">⚡ JFlux</h1>

<h3 align="center">A Lightweight & Modular Java Reverse Proxy & Backend Traffic Management System</h3>
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

---

## 🚀 Overview

**JFlux** is a lightweight **Reverse Proxy** server application built from scratch in **Java**. It receives incoming HTTP requests and distributes them across multiple backend servers.

The project brings together core backend infrastructure mechanisms in a modular architecture, including load balancing, response caching, rate limiting, backend health checks, automatic failover, and live performance metrics.

JFlux demonstrates in practice how modern backend systems manage incoming traffic, tolerate server outages, and maintain reliable communication between clients and upstream services.

## ✨ Key Features

| Feature | Description |
| :--- | :--- |
| 🔀 **Reverse Proxy** | Accepts incoming HTTP requests and forwards them to backend servers. |
| ⚖️ **Load Balancer** | Distributes incoming requests across active backends using a Round-Robin algorithm. |
| ⚡ **Response Cache** | Caches successful `GET` responses in memory for a configurable TTL duration. |
| 🛡️ **Rate Limiter** | Throttles request frequency per client IP address (`429`). |
| 💓 **Health Checks** | Periodically probes backend server availability. |
| 🔄 **Automatic Failover** | Routes requests to another healthy backend when a node becomes unavailable. |
| 🔁 **Retry Mechanism** | Provides configurable retry attempts for failed upstream requests. |
| 📊 **Metrics Collector** | Exposes request statistics and per-backend success counts via `/_jflux/metrics`. |
| 🧵 **Virtual Threads** | Handles concurrent client connections lightly using Java Virtual Threads. |

## 🏗️ System Architecture

JFlux follows a modular architecture where each component is responsible for a specific stage of request processing.

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
