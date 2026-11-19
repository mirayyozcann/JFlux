⚡ JFlux

A Lightweight Java Reverse Proxy | Hafif ve Modüler Java Tabanlı Ters Proxy

<p align="center">

English | Türkçe

</p>

⸻

🇬🇧 English

About

JFlux is a lightweight reverse proxy server developed using Java. It receives incoming HTTP requests and distributes them across multiple backend servers.

The project combines essential backend infrastructure concepts such as load balancing, response caching, rate limiting, health checks, automatic failover, and metrics collection.

✨ Features

* Reverse Proxy: Forwards incoming HTTP requests to backend servers.
* Load Balancing: Distributes requests using the Round-Robin algorithm.
* Response Caching: Temporarily stores successful GET responses.
* Rate Limiting: Controls the number of requests per client IP.
* Health Checks: Monitors backend server availability.
* Automatic Failover: Attempts another backend when a server becomes unavailable.
* Retry Mechanism: Supports configurable retry attempts.
* Metrics Collection: Tracks request statistics and backend performance.
* Concurrent Processing: Uses Java Virtual Threads.

🛠️ Technologies

Technology	Purpose
Java 25	Core programming language
Maven	Build management
Java Sockets	Client connections
Java HttpClient	Backend communication
Virtual Threads	Concurrent processing
ConcurrentHashMap	Thread-safe data management
AtomicInteger / AtomicLong	Atomic operations

🏗️ Architecture

             Client
                |
                v
         +-------------+
         | JFlux Proxy |
         +-------------+
                |
       +--------+--------+
       |        |        |
       v        v        v
   Rate      Cache    Metrics
  Limiter             Collector
       |
       v
  Load Balancer
       |
   +---+---+---+
   |   |   |   |
   v   v   v   v
 Backend Servers

🚀 Getting Started

Prerequisites

* Java JDK 25
* Maven
* IntelliJ IDEA (recommended)

Clone the Repository

git clone https://github.com/mirayyozcann/JFlux.git

Build

mvn clean compile

Run

1. Start MockBackendRunner.
2. Run com.jflux.Main.
3. Access the proxy at:

http://localhost:8080

Metrics

http://localhost:8080/_jflux/metrics

🎯 Project Goals

* Understand reverse proxy architecture.
* Explore load balancing strategies.
* Practice concurrent programming in Java.
* Implement caching and rate limiting.
* Handle backend failures.
* Monitor application performance.

📌 Project Status

JFlux is an educational backend infrastructure project. It is open to further improvements and enhancements.

⸻

🇹🇷 Türkçe

Proje Hakkında

JFlux, Java kullanılarak geliştirilmiş hafif bir Reverse Proxy (Ters Proxy) sunucu uygulamasıdır.

Gelen HTTP isteklerini karşılayarak birden fazla backend sunucusuna yönlendirir. İstek dağıtımı, önbellekleme, trafik kontrolü ve sunucu sağlık takibi gibi temel backend mekanizmalarını tek bir uygulamada bir araya getirir.

Projenin temel amacı, modern backend sistemlerinde kullanılan sunucu yönetimi ve trafik dağıtımı mekanizmalarını uygulamalı olarak geliştirmektir.

✨ Özellikler

* Reverse Proxy: İstemcilerden gelen HTTP isteklerini backend sunucularına iletir.
* Load Balancer: İstekleri Round-Robin algoritmasıyla sunucular arasında dağıtır.
* Response Cache: Başarılı GET isteklerinin yanıtlarını geçici olarak önbellekte saklar.
* Rate Limiter: İstemci IP adresine göre istek sayısını sınırlandırır.
* Health Check: Backend sunucularının erişilebilirliğini kontrol eder.
* Automatic Failover: Bir sunucu erişilemez olduğunda başka bir backend sunucusunu denemeyi sağlar.
* Retry Mechanism: Başarısız istekler için yeniden deneme mekanizması sunar.
* Metrics Collector: İstek sayılarını, önbellek kullanımını ve sunucu başarılarını takip eder.
* Concurrent Processing: Java Virtual Threads kullanarak eşzamanlı istekleri yönetir.

🛠️ Kullanılan Teknolojiler

Teknoloji	Kullanım Amacı
Java 25	Ana programlama dili
Maven	Proje ve bağımlılık yönetimi
Java Sockets	İstemci bağlantılarının yönetimi
Java HttpClient	Backend sunucularıyla iletişim
Virtual Threads	Eşzamanlı işlem yönetimi
ConcurrentHashMap	Thread-safe veri yönetimi
AtomicInteger / AtomicLong	Atomik sayaç işlemleri

🏗️ Sistem Mimarisi

JFlux, istemciden gelen istekleri önce Proxy Server üzerinden karşılar. Ardından Rate Limiter, Cache ve Load Balancer bileşenleri aracılığıyla uygun backend sunucusuna yönlendirir.

Backend sunucularının durumları Health Check mekanizmasıyla takip edilir. Sistem performansı ise Metrics Collector tarafından izlenir.

🚀 Kurulum ve Çalıştırma

Gereksinimler

* Java JDK 25
* Maven
* IntelliJ IDEA (önerilir)

Projeyi Klonlama

git clone https://github.com/mirayyozcann/JFlux.git

Projeyi Derleme

mvn clean compile

Projeyi Çalıştırma

1. Öncelikle MockBackendRunner sınıfını çalıştırın.
2. Ardından com.jflux.Main sınıfını çalıştırın.
3. Proxy sunucusuna aşağıdaki adres üzerinden erişebilirsiniz:

http://localhost:8080

Metrics Endpoint

Sistem metriklerine aşağıdaki adres üzerinden erişilebilir:

http://localhost:8080/_jflux/metrics

🎯 Projenin Amacı

* Reverse Proxy mimarisini anlamak ve uygulamak.
* Load Balancing algoritmalarını deneyimlemek.
* Java ile eşzamanlı programlama becerilerini geliştirmek.
* Cache ve Rate Limiting mekanizmalarını uygulamak.
* Backend sunucu hatalarını yönetmek.
* Sistem performansını takip etmek.

📌 Proje Durumu

JFlux, backend altyapı sistemlerini öğrenmek ve uygulamak amacıyla geliştirilmiş bir projedir. Geliştirilmeye ve yeni özellikler eklenmesine açıktır.

⸻

👩‍💻 Author / Geliştirici

Miray Özcan

Computer Engineering Student | Bilgisayar Mühendisliği Öğrencisi

GitHub: @mirayyozcann

⸻

<p align="center">

JFlux — Lightweight Proxy. Smarter Traffic Management.

JFlux — Hafif Proxy. Akıllı Trafik Yönetimi.

</p>
