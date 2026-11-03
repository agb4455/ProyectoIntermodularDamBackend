# Guía de contribución

Gracias por colaborar en el backend de Viking Clan Wars.

Este repositorio contiene la capa de persistencia y analítica del juego: PostgreSQL, MongoDB, autenticación interna y endpoints para sincronizar los datos del proyecto.

## 1. Objetivo del repositorio

La lógica principal de este servicio es:

- persistir usuarios, personajes, partidas y participantes
- aplicar migraciones con Flyway
- mantener el handshake con el middle server
- ofrecer endpoints internos para la capa de juego
- guardar eventos y snapshots analíticos en MongoDB

## 2. Requisitos

- Java 25
- Maven Wrapper (`./mvnw`)
- Docker para Testcontainers y pruebas de integración
- PostgreSQL y MongoDB accesibles en el entorno local o mediante Docker

## 3. Inicio rápido

```bash
./mvnw clean test
./mvnw spring-boot:run
```

Para Windows:

```bash
mvnw.cmd clean test
mvnw.cmd spring-boot:run
```

## 4. Reglas de desarrollo

- No cambies contratos de API sin revisar el uso del middle server y del frontend.
- No introduzcas secretos reales en `.env`, `application.yml` ni commits.
- Mantén las migraciones de base de datos versionadas y compatibles con el esquema actual.
- Si cambias un DTO, revisa los controladores y tests que dependen de él.
- Si añades una entidad nueva, revisa repositorios, servicios y pruebas asociadas.

## 5. Estructura de carpetas

- `src/main/java/com/tfm/db_back/api`: controladores y DTOs
- `src/main/java/com/tfm/db_back/domain`: lógica de dominio y repositorios
- `src/main/java/com/tfm/db_back/infrastructure`: integraciones con MongoDB
- `src/main/resources/db/migration`: migraciones Flyway
- `src/test`: pruebas unitarias e integración

## 6. Convenciones de cambio

Usa ramas descriptivas:

- `feature/nuevo-endpoint`
- `fix/validacion-handshake`
- `refactor/limpieza-servicios`
- `docs/mejora-readme`

## 7. Convenciones de commits

Usa mensajes claros y específicos, por ejemplo:

- `feat: add analytics snapshot endpoint`
- `fix: validate handshake token before game access`
- `docs: improve backend setup guide`
- `test: cover game controller integration`

## 8. Pull requests

Antes de abrir un PR:

- ejecuta `./mvnw test`
- confirma que tu cambio no rompe contactos con el middle server
- explica qué problema resuelve y cómo comprobarlo
- indica si el cambio afecta a infraestructura, migraciones o contratos de API

## 9. Seguridad

- no compartas secretos ni `.env` reales
- usa la configuración del entorno y variables de entorno
- revisa tokens, `DB_HANDSHAKE_SECRET` y endpoints internos antes de hacer un PR

## 10. Ayuda y coordinación

Si tu cambio afecta al motor del juego o al frontend, revisa también los otros repos del proyecto:

- [Orquestador](https://github.com/agb4455/tfm)
- [Middle Server](https://github.com/agb4455/ProyectoIntermodularDamServidorIntermedio)
- [Frontend](https://github.com/agb4455/ProyectoIntermodularDamFrontend)

Gracias por ayudar a mantener Viking Clan Wars estable y consistente.
