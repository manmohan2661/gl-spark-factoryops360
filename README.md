# FactoryOps360 🏭

**Enterprise Manufacturing Execution & Supply Chain Platform**

![FactoryOps360 Architecture](https://img.shields.io/badge/Architecture-Microservices-blue) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-brightgreen) ![React](https://img.shields.io/badge/React-18-blue) ![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-blue)

FactoryOps360 is a fully-featured, distributed, cloud-native platform engineered to support core manufacturing execution and supply chain workflows. It handles supplier management, inventory tracking, production scheduling, quality control, and advanced analytics—all operating behind a unified API gateway with centralized JWT authentication and service discovery.

---

## 1. Project Overview

FactoryOps360 eliminates data silos in the manufacturing process by implementing a **Database-per-Service** microservices architecture. It provides a highly responsive React/TypeScript frontend (Command Center) that consumes data via an API Gateway.

**Key Features Implemented:**
- **Centralized Security:** API Gateway JWT authentication with downstream Role-Based Access Control (RBAC). Roles include `ADMIN`, `SUPPLIER_MANAGER`, `INVENTORY_MANAGER`, `PRODUCTION_MANAGER`, and `QUALITY_INSPECTOR`.
- **Decision Support Engine:** A specialized Analytics Service that aggregates data via OpenFeign to generate actionable, ranked operational recommendations and a composite Factory Health Score.
- **Supplier Performance Tracking:** Side-by-side comparison of supplier reliability footprints based on quality, delivery, and defect rates.
- **Realistic Data Seeding:** Idempotent DataSeeders in every service to instantly populate the platform with realistic factory simulation data upon startup.

---

## 2. Architecture Diagram

```
                         ┌───────────────────────┐
                         │   React UI (Vite)     │
                         │   factoryops360-ui    │
                         │   :5173                 │
                         └───────────┬────────────┘
                                     │ (JWT Token)
                                     ▼
                         ┌───────────────────────┐
                         │   API Gateway           │
                         │   (Spring Cloud Gateway)│
                         │   - JWT Validation      │
                         │   - Role Propagation    │
                         │   :8080                 │
                         └───────────┬────────────┘
                                     │
              ┌──────────────┬──────┴───────┬───────────────┬───────────────┐
              ▼              ▼              ▼               ▼               ▼
        ┌───────────┐ ┌────────────┐ ┌─────────────┐ ┌─────────────┐ ┌──────────────┐
        │   Auth     │ │  Supplier  │ │  Inventory   │ │  Production  │ │   Quality     │
        │  Service   │ │  Service   │ │  Service     │ │  Service     │ │   Service     │
        │  (JWT Gen) │ │  (Postgres)│ │  (Postgres)  │ │  (Postgres)  │ │   (Postgres)  │
        │  :8081     │ │  :8082     │ │  :8083       │ │  :8084       │ │   :8085       │
        └───────────┘ └────────────┘ └─────────────┘ └─────────────┘ └──────────────┘
                                                                              │
                                                                              ▼
                                                                     ┌──────────────┐
                                                                     │  Analytics    │
                                                                     │  Service      │
                                                                     │  (OpenFeign)  │
                                                                     │  :8086        │
                                                                     └──────────────┘

        All services register with, and discover each other through:

                         ┌───────────────────────┐
                         │   Eureka Server         │
                         │   :8761                 │
                         └───────────────────────┘
```

---

## 3. Tech Stack

### Backend
| Category            | Technology                              |
|----------------------|------------------------------------------|
| **Language**         | Java 21                                  |
| **Framework**        | Spring Boot 3.5.x                        |
| **Microservices**    | Spring Cloud 2025.0.0                    |
| **Service Discovery**| Netflix Eureka                           |
| **Edge Routing**     | Spring Cloud Gateway                     |
| **Inter-service**    | OpenFeign                                |
| **Persistence**      | Spring Data JPA + PostgreSQL             |
| **Security**         | Spring Security + JWT (jjwt)             |
| **Logging**          | SLF4J + Logback                          |

### Frontend
| Category      | Technology              |
|-----------------|---------------------------|
| **Library**       | React 18                  |
| **Language**      | TypeScript                |
| **Build Tool**    | Vite                      |
| **UI Components** | Tailwind CSS + shadcn/ui  |
| **Data Fetching** | React Query + Axios       |
| **Visualization** | Recharts                  |
| **Routing**       | React Router DOM          |

---

## 4. Microservices Breakdown

| Service              | Responsibility                                     | Port   |
|------------------------|----------------------------------------------------|--------|
| `eureka-server`         | Service registry and discovery server              | `8761` |
| `api-gateway`           | Single entry point, JWT validation, role routing   | `8080` |
| `auth-service`          | Authentication, User Management, JWT generation    | `8081` |
| `supplier-service`      | Supplier CRUDS, performance evaluations            | `8082` |
| `inventory-service`     | Inventory, warehouse tracking, low-stock alerts    | `8083` |
| `production-service`    | Production orders, machine states, line tracking   | `8084` |
| `quality-service`       | Defect tracking, batch inspections, QA logs        | `8085` |
| `analytics-service`     | Cross-domain dashboard data, Recommendation Engine | `8086` |
| `factoryops-ui`         | Enterprise web application (Frontend)              | `5173` |

*(Note: The `common-library` is a shared Maven module containing cross-cutting exceptions, API response structures, and utilities consumed by all backend services).*

---

## 5. How to Run Locally

### 5.1 Prerequisites

- **Java 21**
- **Maven 3.9+** (or use the bundled `mvnw` wrapper)
- **Node.js 18+** and **npm**
- **PostgreSQL 14+** running locally at `localhost:5432` (Username: `postgres`, Password: `password`).
- You must create the following empty databases in PostgreSQL before starting:
  - `factoryops_auth`
  - `factoryops_supplier`
  - `factoryops_inventory`
  - `factoryops_production`
  - `factoryops_quality`

### 5.2 Starting the Backend

Because services rely on Eureka for discovery and the API Gateway for routing, they must be started in a specific order.

From the `backend/` directory, open multiple terminal windows and run:

1. **Discovery Server:** `cd eureka-server && ./mvnw spring-boot:run`
2. **API Gateway:** `cd api-gateway && ./mvnw spring-boot:run`
3. **Auth Service:** `cd auth-service && ./mvnw spring-boot:run`
4. **Domain Services (Any Order):**
   - `cd supplier-service && ./mvnw spring-boot:run`
   - `cd inventory-service && ./mvnw spring-boot:run`
   - `cd production-service && ./mvnw spring-boot:run`
   - `cd quality-service && ./mvnw spring-boot:run`
5. **Analytics Service (Start Last):** `cd analytics-service && ./mvnw spring-boot:run`

*Note: As each service starts, its internal `DataSeeder` will automatically populate the database with realistic test data.*

### 5.3 Starting the Frontend

From the `frontend/` directory:

```bash
npm install
npm run dev
```

The application will be available at `http://localhost:5173`.

### 5.4 Test Credentials

You can log in to the UI using any of the seeded accounts to test Role-Based Access Control:

- **Admin:** `admin` / `password`
- **Supplier Manager:** `sup_manager` / `password`
- **Inventory Manager:** `inv_manager` / `password`
- **Production Manager:** `prod_manager` / `password`
- **Quality Inspector:** `qa_inspector` / `password`

---

## 6. Project Status
**Active / Feature Complete (MVP)**
The core infrastructure, security perimeters, cross-service communication (Feign), data seeding, and advanced frontend analytics dashboards have been successfully implemented.
