<h1 align="center">⚡ JFlux</h1>

<h3 align="center">Hafif ve Modüler Java Tabanlı Reverse Proxy & Backend Trafik Yönetim Sistemi</h3>
<p align="center">
  <strong>Akıllı Trafik Dağıtımı. Güvenilir Backend İletişimi.</strong>
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

## 🚀 Genel Bakış

**JFlux**, Java ile geliştirilmiş hafif bir **Reverse Proxy (Ters Proxy)** sunucu uygulamasıdır. Gelen HTTP isteklerini karşılayarak birden fazla backend sunucusuna dağıtır.

Proje; yük dengeleme (load balancing), yanıt önbellekleme (response caching), hız sınırlama (rate limiting), TCP tabanlı sunucu sağlık kontrolleri, bağlantı hatalarında failover ve performans metrikleri gibi temel backend altyapı kavramlarını modüler bir mimaride birleştirir.

JFlux, backend sistemlerinde gelen trafiğin nasıl yönlendirildiğini, sunucu bağlantı hatalarının nasıl ele alındığını ve istemciler ile servisler arasındaki iletişimin nasıl yönetildiğini uygulamalı olarak gösterir.

## ✨ Öne Çıkan Özellikler

| Özellik | Açıklama |
| :--- | :--- |
| 🔀 **Reverse Proxy** | İstemcilerden gelen HTTP isteklerini karşılar ve backend sunucularına iletir. |
| ⚖️ **Load Balancer** | Gelen istekleri Round-Robin algoritmasıyla sağlıklı backend sunucuları arasında dağıtır. |
| ⚡ **Response Cache** | Başarılı `GET` yanıtlarını yapılandırılabilir TTL süresi boyunca bellekte saklar. |
| 🛡️ **Rate Limiter** | İstemci IP adresine göre dakikalık istek sayısını sınırlandırır. Limit aşıldığında HTTP `429 Too Many Requests` yanıtı döndürür. |
| 💓 **Health Checks** | Backend sunucularının TCP bağlantı erişilebilirliğini düzenli aralıklarla kontrol eder. |
| 🔄 **Failover** | Backend bağlantısı başarısız olduğunda sunucuyu erişilemez olarak işaretler ve başka bir sağlıklı sunucuyu dener. |
| 🔁 **Retry Mechanism** | Başarısız backend bağlantı denemeleri için yapılandırılabilir yeniden deneme sayısı sunar. |
| 📊 **Metrics Collector** | İstek, cache, rate limit, hata ve backend başarı sayaçlarını `/_jflux/metrics` endpoint'i üzerinden JSON olarak sunar. |
| 🧵 **Virtual Threads** | İstemci bağlantılarını Java Virtual Threads kullanarak eşzamanlı şekilde yönetir. |

## 🏗️ Sistem Mimarisi

JFlux, her bileşenin istek işleme sürecinin belirli bir aşamasından sorumlu olduğu modüler bir mimari izler.

```mermaid
flowchart TD
    A[İstemci] --> B[JFlux Proxy Server]
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
