# Security Policy

## Reportar una vulnerabilidad

Si descubres una vulnerabilidad de seguridad en este proyecto, por favor no la publiques en un issue público.

Contacta con el responsable del repositorio o usa el canal privado disponible para este fin. Si la plataforma lo permite, abre un reporte privado.

Incluye la siguiente información:

- Descripción del problema
- Pasos para reproducirlo
- Impacto esperado
- Evidencia o ejemplo mínimo
- Entorno afectado

## Recomendaciones mínimas

- No se deben incluir secretos ni `.env` reales en el repositorio
- Las credenciales de producción deben gestionarse con variables de entorno seguras y almacenamiento apropiado
- No exponer puertos internos de bases de datos ni servicios internos en Internet
- Revisar con frecuencia JWT, PostgreSQL, Redis, MongoDB y MinIO
- Mantener actualizadas las dependencias y la infraestructura

## Política de despliegue

- Usar variables de entorno para datos sensibles
- Configurar TLS y puertos adecuados en producción
- Limitar la exposición del backend y de los servicios internos
- Revisar logs y accesos de forma periódica
