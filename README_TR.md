# ⚡ JFlux

Saf Java ile yazılmış, harici bağımlılığı olmayan hafif bir HTTP reverse proxy ve yük dengeleyici.

[🇬🇧 English](README.md) · 🇹🇷 Türkçe

## Özellikler

- Sağlıklı sunucular arasında **round-robin yük dengeleme**
- TCP **sağlık kontrolü** ve yeniden denemeli **otomatik failover**
- `GET` istekleri için **yanıt önbelleği** (ayarlanabilir TTL)
- İstemci IP'si başına **istek sınırlama** (`429` döner)
- `/_jflux/metrics` adresinde **metrik uç noktası**
- **Sanal thread'ler** üzerine kurulu, yalnızca Java standart kütüphanesi

## Gereksinimler

- JDK 25
- Maven

## Hızlı Başlangıç

```bash
git clone https://github.com/mirayyozcann/JFlux.git
cd JFlux
mvn clean compile
```

Bir terminalde demo sunucularını başlatın (9001–9003 portları):

```bash
java -cp target/classes com.jflux.testutil.MockBackendRunner
```

Diğer terminalde proxy'yi başlatın:

```bash
java -cp target/classes com.jflux.Main
```

Sonra deneyin:

```bash
curl localhost:8080/hello
curl localhost:8080/_jflux/metrics
```

## Yapılandırma

`src/main/resources/config.properties` dosyasını düzenleyin veya çalışma dizinine bir `config.properties` koyarak ayarları ezin.

| Özellik | Varsayılan | Açıklama |
|---|---|---|
| `proxy.port` | `8080` | Proxy portu |
| `backend.urls` | `http://localhost:9001,...9002,...9003` | Virgülle ayrılmış sunucular |
| `cache.ttl.millis` | `10000` | Önbellek ömrü (`0` kapatır) |
| `ratelimit.max.per.minute` | `30` | IP başına dakikadaki istek sayısı (`0` kapatır) |
| `healthcheck.interval.millis` | `5000` | Sağlık kontrolü aralığı |
| `proxy.max.retries` | `3` | İstek başına en fazla sunucu denemesi |

## Nasıl Çalışır?

İstek → istek sınırı → önbellek → round-robin sunucu seçimi → hata olursa yeniden deneme → yanıt.

- Önbellekten gelen yanıtlarda `X-Proxy-Cache: HIT` (yoksa `MISS`) başlığı bulunur.
- Bir sunucu hata verirse `DOWN` işaretlenir ve istek başka bir sunucuya gider.
- Tüm sunucular kapalıysa istemci `503` alır.

## Sınırlamalar

İstek başlıkları arka uca iletilmez, yalnızca keep-alive olmayan HTTP/1.1 desteklenir ve HTTPS yoktur.

