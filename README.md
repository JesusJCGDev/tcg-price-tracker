# TCG Price Tracker

Backend application designed for mass data ingestion, historical price tracking, and anomaly detection in the Pokémon Trading Card Game market. Built to demonstrate advanced Java backend engineering, robust system architecture, and efficient database management.

## 🚀 Technical Stack
* **Language:** Java 21 (Records, Streams, Pattern Matching)
* **Framework:** Spring Boot 3
* **Database:** PostgreSQL 16
* **Infrastructure:** Docker, Flyway (Schema versioning)
* **Testing:** JUnit 5, Mockito, Testcontainers
* **HTTP Client:** Spring `RestClient` with Retry/Backoff resilience

## ⚙️ Architecture & Features
* **Resilient Data Ingestion:** Automated HTTP clients configured to handle external API rate limits (HTTP 502/429) using strategic backoff policies without data loss.
* **Idempotent Operations:** Transactional boundaries (`@Transactional`) and batch processing ensure database consistency during historical catalog cold starts and daily syncs.
* **Hexagonal-Inspired Design:** Clean separation of concerns between domain logic, infrastructure adapters, and scheduled triggers.
* **Automated Testing:** Integration tests utilizing Testcontainers for reliable and isolated database validation.
