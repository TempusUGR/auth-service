# Auth Service - CalendarUgr

## Descripción
El **Auth Service** es un microservicio dentro del sistema **CalendarUgr** encargado de la autenticación de usuarios. Proporciona funcionalidades para el inicio de sesión y la renovación de tokens de autenticación.

## Características
- Autenticación mediante correo y contraseña.
- Generación de tokens JWT con información del rol y nickname.
- Endpoint para refrescar el token JWT.
- Integración con otros microservicios de CalendarUgr.

## Requisitos previos
Para ejecutar este servicio, es necesario configurar las siguientes variables de entorno:

- `DB_USERNAME`: Nombre de usuario de la base de datos.
- `DB_PASSWORD`: Contraseña de la base de datos.
- `DB_URL`: URL de conexión a la base de datos.

## Instalación y ejecución
1. Clonar el repositorio:
   ```sh
   git clone <repository-url>
   cd auth-service
   ```
2. Configurar las variables de entorno:
   ```sh
   export SECRET_KEY = <your_jwt_secret_key>
   ```
3. Construir y ejecutar el servicio:
   ```sh
   ./mvnw spring-boot:run
   ```

## API Endpoints
- `POST /login` - Iniciar sesión con correo y contraseña, devuelve un JWT con rol y nickname.
- `POST /refresh` - Renovar un token JWT expirado.