# Project Context

## 1. Genel Proje Tanimi & Amac

**may-backend**, MAY platformunun backend servisini barindirir. Spring Boot tabanli REST API sunar.

- **Repo:** `may-backend`
- **Framework:** Spring Boot 3.4.3
- **Java:** 21
- **Veritabani:** PostgreSQL 17
- **Schema:** `MAY`
- **App User:** `MAY_APP`
- **Build Tool:** Maven

**Kardes projeler:**
- `may-database` — PostgreSQL veritabani (Docker Compose)
- `may-flyway` — Veritabani migration (Flyway 10)

---

## 2. Mimari Kararlar & Prensipler

- **Katmanli mimari** — Controller → Service → Repository → Entity
- **DTO pattern** — Request/Response DTO'lari ile entity ayristirmasi
- **Mapper pattern** — Entity ↔ DTO donusumleri icin mapper siniflari
- **JWT authentication** — Access token + Refresh token (HttpOnly cookie)
- **RBAC** — Role-Based Access Control (USERS → ROLE → PERMISSION)
- **BaseEntity** — Tum entity'lerde audit alanlari (createdBy, createdDate, createdIp, updatedBy, updatedDate, updatedIp)
- **ProblemDetail** — RFC 7807 hata formati
- **Flyway disabled** — Migration ayri projede (may-flyway), backend'de flyway kapali

---

## 3. Package Yapisi

```
com.may.backend/
├── config/          # SecurityConfig, JacksonConfig, JwtProperties
├── controller/      # REST controller'lar
├── dto/
│   ├── request/     # Request DTO'lari
│   └── response/    # Response DTO'lari
├── entity/
│   └── base/        # BaseEntity
├── enums/           # Status, PermissionAction
├── exception/       # BusinessException, ErrorCode, GlobalExceptionHandler
├── mapper/          # Entity ↔ DTO mapper'lar
├── repository/      # Spring Data JPA repository'ler
├── security/        # JWT filter, token provider, UserDetailsService
└── service/         # Is mantigi servisleri
```

---

## 4. API Endpointleri

| Modul | Base Path | Aciklama |
|---|---|---|
| Auth | `/api/v1/auth` | Login, refresh token |
| Users | `/api/v1/users` | Kullanici CRUD |
| Roles | `/api/v1/roles` | Rol CRUD |
| Permissions | `/api/v1/permissions` | Yetki CRUD |
| User Roles | `/api/v1/user-roles` | Kullanici-Rol iliskileri |
| Role Permissions | `/api/v1/role-permissions` | Rol-Yetki iliskileri |
| Health | `/api/v1/health` | Health check |

---

## 5. Veritabani Tablolari

```
USERS ──< USERS_ROLE >── ROLE ──< ROLE_PERMISSION >── PERMISSION
```

---

## 6. Dosya Yapisi

```
may-backend/
├── .gitignore
├── .dockerignore
├── .mvn/wrapper/
├── mvnw / mvnw.cmd
├── pom.xml
├── project-context.md
├── README.md
└── src/
    ├── main/
    │   ├── java/com/may/backend/
    │   └── resources/application.yml
    └── test/
        └── java/com/may/backend/
```
