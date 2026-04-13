# may-backend

MAY platformu backend servisi.

## Teknolojiler

- **Java 21**
- **Spring Boot 3.4.3**
- **Spring Security + JWT**
- **Spring Data JPA**
- **PostgreSQL 17**
- **Maven**

## Schema

- **Schema:** `MAY`
- **App User:** `MAY_APP`

## API

- **Auth:** `/api/v1/auth` — Login, refresh token
- **Users:** `/api/v1/users` — Kullanici CRUD
- **Roles:** `/api/v1/roles` — Rol CRUD
- **Permissions:** `/api/v1/permissions` — Yetki CRUD
- **User Roles:** `/api/v1/user-roles` — Kullanici-Rol iliskileri
- **Role Permissions:** `/api/v1/role-permissions` — Rol-Yetki iliskileri
- **Health:** `/api/v1/health` — Health check

## Kullanim

```bash
# Build
mvn clean package

# Run
mvn spring-boot:run
```
