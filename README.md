# FactoryOps360

**Enterprise Manufacturing Execution & Supply Chain Platform — Starter Kit**

> This repository is a **project skeleton only**. It contains no business
> logic, entities, controllers, repositories, services, database schemas,
> Docker/Kubernetes manifests, or CI/CD pipelines. It exists purely to give
> engineering teams a clean, consistent, enterprise-grade foundation to build
> upon.

---

## 1. Project Overview

FactoryOps360 is architected as a distributed, cloud-native platform intended
to support core manufacturing execution and supply chain workflows —
supplier management, inventory, production, quality control, and analytics —
behind a unified API gateway with centralized authentication and service
discovery.

This starter kit provides the **structural foundation** for that platform:
independently deployable Spring Boot microservices, a React/TypeScript
frontend, and the configuration wiring needed to run them together locally.

---

## 2. Architecture

```
                         ┌───────────────────────┐
                         │   React UI (Vite)     │
                         │   factoryops-ui        │
                         │   :5173                 │
                         └───────────┬────────────┘
                                     │
                                     ▼
                         ┌───────────────────────┐
                         │   API Gateway           │
                         │   (Spring Cloud Gateway)│
                         │   :8080                 │
                         └───────────┬────────────┘
                                     │
              ┌──────────────┬──────┴───────┬───────────────┬───────────────┐
              ▼              ▼              ▼               ▼               ▼
        ┌───────────┐ ┌────────────┐ ┌─────────────┐ ┌─────────────┐ ┌──────────────┐
        │   Auth     │ │  Supplier  │ │  Inventory   │ │  Production  │ │   Quality     │
        │  Service   │ │  Service   │ │  Service     │ │  Service     │ │   Service     │
        │  :8081     │ │  :8082     │ │  :8083       │ │  :8084       │ │   :8085       │
        └───────────┘ └────────────┘ └─────────────┘ └─────────────┘ └──────────────┘
                                                                              │
                                                                              ▼
                                                                     ┌──────────────┐
                                                                     │  Analytics    │
                                                                     │  Service      │
                                                                     │  :8086        │
                                                                     └──────────────┘

        All services register with, and discover each other through:

                         ┌───────────────────────┐
                         │   Eureka Server         │
                         │   :8761                 │
                         └───────────────────────┘
```

Each backend service is an independent Spring Boot / Maven module with its
own `pom.xml`, configuration, and package structure — there is no shared
parent POM, so every service can be built, versioned, and deployed on its own
timeline.

---

## 3. Folder Structure

```
FactoryOps360/
├── backend/
│   ├── eureka-server/
│   ├── api-gateway/
│   ├── auth-service/
│   ├── supplier-service/
│   ├── inventory-service/
│   ├── production-service/
│   ├── quality-service/
│   ├── analytics-service/
│   └── common-library/
│
├── frontend/
│   └── factoryops-ui/
│
├── docs/
├── database/
├── README.md
├── .gitignore
└── LICENSE
```

Each backend service follows the same internal layout:

```
<service>/
├── mvnw / mvnw.cmd
├── .mvn/wrapper/maven-wrapper.properties
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/factoryops/<service>/
    │   │   ├── config/
    │   │   ├── controller/
    │   │   ├── dto/
    │   │   │   ├── request/
    │   │   │   └── response/
    │   │   ├── entity/
    │   │   ├── repository/
    │   │   ├── service/
    │   │   │   └── impl/
    │   │   ├── exception/
    │   │   ├── security/
    │   │   ├── util/
    │   │   ├── mapper/
    │   │   ├── constant/
    │   │   └── <Service>Application.java
    │   └── resources/
    │       └── application.properties
    └── test/
        ├── java/com/factoryops/<service>/
        └── resources/application-test.properties
```

> Note: `eureka-server` and `api-gateway` are infrastructure services and
> therefore only scaffold the `config/` (and `security/` for the gateway)
> packages plus the shared `mapper/` and `constant/` packages — they do not
> carry domain packages like `entity` or `repository`.

`common-library` (`backend/common-library`) is a plain shared Maven module —
not a runnable Spring Boot application — consumed by the other services as a
dependency. It holds cross-cutting classes only:

```
common-library/
├── mvnw / mvnw.cmd
├── .mvn/wrapper/maven-wrapper.properties
├── pom.xml
└── src/
    ├── main/java/com/factoryops/common/
    │   ├── response/    → ApiResponse<T>
    │   ├── constant/     → AppConstants, SecurityConstants
    │   ├── exception/    → BaseException, ResourceNotFoundException,
    │   │                    BusinessException, UnauthorizedException
    │   ├── util/          → DateUtils, StringUtils
    │   └── enums/          → Status, ApiResponseStatus
    └── test/java/com/factoryops/common/
```

To use it from another service, add it as a dependency in that service's
`pom.xml`:

```xml
<dependency>
    <groupId>com.factoryops</groupId>
    <artifactId>common-library</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

The frontend follows a standard feature-oriented Vite/React layout:

```
factoryops-ui/
├── index.html
├── package.json
├── tsconfig.json
├── vite.config.ts
└── src/
    ├── api/
    ├── assets/
    ├── components/
    ├── contexts/
    ├── hooks/
    ├── layouts/
    ├── pages/
    ├── routes/
    ├── services/
    ├── styles/
    ├── theme/
    ├── types/
    ├── utils/
    ├── App.tsx
    └── main.tsx
```

---

## 4. Tech Stack

### Backend
| Category            | Technology                              |
|----------------------|------------------------------------------|
| Language              | Java 21                                  |
| Framework             | Spring Boot 3.5.x                        |
| Microservices         | Spring Cloud 2025.0.0                    |
| Service Discovery     | Netflix Eureka                           |
| Edge Routing          | Spring Cloud Gateway                     |
| Inter-service calls   | OpenFeign                                |
| Persistence            | Spring Data JPA + PostgreSQL             |
| Security               | Spring Security + JWT (jjwt)             |
| Validation             | Jakarta Bean Validation                  |
| Observability          | Spring Boot Actuator                     |
| API Documentation      | springdoc-openapi / Swagger UI           |
| Boilerplate reduction  | Lombok                                   |
| Logging                | SLF4J + Logback                          |
| Build Tool              | Maven (with Maven Wrapper)              |

### Frontend
| Category      | Technology              |
|-----------------|---------------------------|
| Library           | React 18                  |
| Language           | TypeScript                |
| Build Tool          | Vite                      |
| UI Components       | Material UI (MUI)         |
| HTTP Client          | Axios                     |
| Routing               | React Router DOM           |

---

## 5. Services

| Service              | Responsibility (planned)                         | Module                              |
|------------------------|----------------------------------------------------|---------------------------------------|
| `eureka-server`         | Service registry / discovery server                | `backend/eureka-server`               |
| `api-gateway`           | Single entry point, routing, cross-cutting concerns | `backend/api-gateway`                 |
| `auth-service`          | Authentication, authorization, JWT issuance         | `backend/auth-service`                |
| `supplier-service`      | Supplier & procurement management                  | `backend/supplier-service`            |
| `inventory-service`     | Inventory & warehouse tracking                      | `backend/inventory-service`           |
| `production-service`    | Production planning & manufacturing execution       | `backend/production-service`          |
| `quality-service`       | Quality control & compliance                         | `backend/quality-service`             |
| `analytics-service`     | Cross-domain analytics & reporting                   | `backend/analytics-service`           |
| `common-library`        | Shared classes (ApiResponse, constants, exceptions, utils, enums) | `backend/common-library`  |
| `factoryops-ui`         | Web frontend                                          | `frontend/factoryops-ui`              |

---

## 6. Ports

| Service               | Port   |
|--------------------------|--------|
| Eureka Server              | 8761   |
| API Gateway                 | 8080   |
| Auth Service                 | 8081   |
| Supplier Service              | 8082   |
| Inventory Service              | 8083   |
| Production Service              | 8084   |
| Quality Service                   | 8085   |
| Analytics Service                  | 8086   |
| Frontend (factoryops-ui)             | 5173   |

---

## 7. How to Run

### 7.1 Prerequisites

- **Java 21** (JDK)
- **Maven 3.9+** (or use the bundled `mvnw` / `mvnw.cmd` wrapper in each service)
- **Node.js 18+** and **npm**
- **PostgreSQL 14+** running locally (or reachable), with a database created
  per service that needs one (see `application.properties` in each service for the
  expected database name)
- An IDE such as **IntelliJ IDEA** or **VS Code** (with the Java Extension
  Pack, for backend work)

> This starter kit does not include database migration scripts, seed data,
> or Docker Compose files by design — provisioning PostgreSQL and any schema
> management strategy (e.g. Flyway/Liquibase) is left to the team building on
> top of this skeleton.

### 7.2 Backend — running an individual service

From within a given service directory:

```bash
cd backend/eureka-server
./mvnw spring-boot:run
```

(Use `mvnw.cmd` instead of `./mvnw` on Windows.)

**Recommended startup order**, since downstream services register against
Eureka and route through the gateway:

1. `eureka-server` (8761)
2. `api-gateway` (8080)
3. `auth-service` (8081)
4. `supplier-service`, `inventory-service`, `production-service`,
   `quality-service`, `analytics-service` (8082–8086, any order)

### 7.3 Frontend

```bash
cd frontend/factoryops-ui
npm install
npm run dev
```

The app will be available at `http://localhost:5173`.

### 7.4 Importing into an IDE

- **IntelliJ IDEA**: Open the `FactoryOps360/` root folder. Each service
  under `backend/` can be opened/imported as its own Maven module (there is
  intentionally no aggregator/parent POM tying them together, to keep
  services independently buildable and deployable).
- **VS Code**: Open the `FactoryOps360/` root folder as a workspace. Use the
  Java Extension Pack for backend modules and the built-in TypeScript
  tooling for `frontend/factoryops-ui`.

---

## 8. Project Status

This is a **structural starter kit**. No entities, controllers,
repositories, services, security implementations, or database schemas are
included. It is intended as the first commit of a new enterprise project,
ready for teams to begin implementing business functionality on top of a
consistent, agreed-upon foundation.
