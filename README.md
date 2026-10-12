# TCG Price Tracker

Backend application designed for mass data ingestion, historical price tracking, and anomaly detection in the Pokémon Trading Card Game market. Built with a strong emphasis on data integrity, automated reconciliation (Data QA), and resilient system architecture.

## 🚀 Technical Stack
* **Language:** Java 21 (Records, Streams, Pattern Matching)
* **Framework:** Spring Boot 3
* **Database:** PostgreSQL 16 (Time-series data modeling)
* **Infrastructure:** Docker, Flyway (Schema versioning)
* **Testing & QA:** JUnit 5, Mockito, Automated Data Reconciliation
* **HTTP Client:** Spring `RestClient` with Retry/Backoff resilience

## ⚙️ Architecture & Features
* **Resilient Data Ingestion:** Automated HTTP clients configured to handle external API rate limits (HTTP 502/429) using strategic backoff policies without data loss.
* **Idempotent Operations:** Transactional boundaries (`@Transactional`) and batch processing ensure database consistency during historical catalog cold starts and daily syncs.
* **Data Quality Assurance (QA):** Integration tests dynamically query the database (via JPA projections) to mathematically reconcile local stored entities against the external API source of truth, ensuring 0% data drift.
* **Hexagonal-Inspired Design:** Clean separation of concerns between domain logic (`DealDetectionService`), infrastructure adapters (`PokemonTcgApiClient`), and scheduled triggers.

## 🛠️ Local Development Setup
1. Start the PostgreSQL container via Docker Desktop.
2. Ensure Java 21 is installed and configured in the IDE.
3. Run the application; Flyway will automatically migrate the database schema (`V1__init_schema.sql`).
4. Execute `CardIngestionServiceTest` to validate local data integrity.