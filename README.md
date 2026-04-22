# 💾 Viking Clan Wars - DB Server

Capa de persistencia y gestión de datos de **Viking Clan Wars**. Actúa como puente entre el motor de juego y las bases de datos.

## 🛠️ Tecnologías

*   **Java 25**: Lenguaje principal.
*   **Spring Boot 3.x**: Framework de la aplicación.
*   **Spring Data JPA**: Abstracción para PostgreSQL.
*   **Spring Data MongoDB**: Abstracción para analíticas.
*   **Flyway**: Gestión de migraciones de base de datos.
*   **PostgreSQL**: Base de datos relacional para usuarios, clanes y estados persistentes.
*   **MongoDB**: Almacenamiento de snapshots históricos para analíticas.

## 🏗️ Responsabilidades

1.  **Persistencia**: Guardado periódico del estado de las partidas (cada 15 min).
2.  **API Interna**: Proporciona endpoints REST protegidos para el `Middle Server`.
3.  **Analíticas**: Registro de eventos de batalla y evolución de partidas en MongoDB.
4.  **Validación**: Enforce de la integridad de los datos y reglas de negocio a nivel de esquema.

## 🚀 Desarrollo

### Compilación y empaquetado
```bash
./mvnw clean install
```

### Ejecución
```bash
./mvnw spring-boot:run
```

## 📄 Licencia

Este proyecto está bajo la **Licencia MIT (Modificada para uso educativo)**. Consulta el archivo [LICENSE](./LICENSE) para más detalles.
