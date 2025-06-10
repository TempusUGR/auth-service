# TempusUGR - Authentication Service (auth-service)

Este repositorio contiene el código fuente del `auth-service`, un microservicio de la arquitectura de **TempusUGR** dedicado exclusivamente a la **autenticación de usuarios** y la **gestión de JSON Web Tokens (JWT)**.

Su función es validar las credenciales de un usuario contra el `user-service` y, si son correctas, emitir un par de tokens (acceso y refresco) que serán utilizados por el `api-gateway` para autorizar las peticiones a otros servicios del sistema.

---

## ✨ Funcionalidad Principal

El flujo de autenticación es el siguiente:

1.  El cliente envía una petición `POST` al endpoint `/auth/login` con su email y contraseña.
2.  El `auth-service` recibe las credenciales y valida que el email pertenezca a la UGR.
3.  Realiza una llamada REST al `user-service` para obtener los datos del usuario, incluido su hash de contraseña y rol.
4.  Compara de forma segura la contraseña proporcionada con el hash almacenado.
5.  Si la validación es exitosa, genera un **access token** (corta duración) y un **refresh token** (larga duración).
6.  Devuelve ambos tokens al cliente, que los almacenará para futuras peticiones.

Este servicio es **stateless**, lo que significa que no almacena ninguna información de sesión, delegando la validación del estado en los tokens JWT.

![Flujo de Autenticación](https://i.imgur.com/L1n8pZl.png)

---

### Gestión de Tokens JWT

* **Access Token**:
    * **Vida útil**: 24 horas.
    * **Contenido (Payload)**: Incluye el ID del usuario (`sub`), su rol y la fecha de expiración.
    * **Uso**: Se envía en la cabecera `Authorization` de cada petición a los endpoints protegidos.

* **Refresh Token**:
    * **Vida útil**: 7 días.
    * **Uso**: Se utiliza exclusivamente contra el endpoint `/auth/refresh` para obtener un nuevo `access token` cuando el actual ha expirado, sin necesidad de que el usuario vuelva a introducir sus credenciales.

* **Algoritmo de Firma**: Todos los tokens se firman utilizando el algoritmo simétrico **HS256** y una clave secreta (`JWT_SECRET`).

---

## 🛠️ Pila Tecnológica

* **Lenguaje/Framework**: Java 21, Spring Boot 3.4.4
* **Comunicación Síncrona**: Spring `WebClient` para realizar llamadas REST reactivas al `user-service`.
* **Seguridad**: Biblioteca `io.jsonwebtoken` (JJWT) para la creación y validación de los JWT.
* **Descubrimiento de Servicios**: Cliente de **Eureka** para el registro del servicio.

---

## 🏗️ Arquitectura y Dependencias

`auth-service` es un componente fundamental para la seguridad del sistema y depende de:

* **`user-service`**: Esencial para obtener los datos del usuario y validar sus credenciales. No puede funcionar si el `user-service` no está disponible.
* **`eureka-service`**: Necesario para registrarse y poder ser localizado por el `api-gateway`.

---

## 🔌 API Endpoints

| Método | Ruta            | Descripción                                                 |
| :----- | :-------------- | :---------------------------------------------------------- |
| `POST` | `/auth/login`   | Inicia sesión con email y contraseña. Devuelve los tokens.  |
| `POST` | `/auth/refresh` | Genera un nuevo access token a partir de un refresh token.  |

---

## 🚀 Puesta en Marcha Local

### **Prerrequisitos**

* Java 21 o superior.
* Maven 3.x.
* Una instancia de `eureka-service` en ejecución.
* Una instancia de `user-service` en ejecución.

### **Configuración**

Asegúrate de configurar las siguientes variables en `src/main/resources/application.properties`:

```properties
# -- CONFIGURACIÓN DEL SERVIDOR --
server.port=8082 # O el puerto deseado

# -- CONFIGURACIÓN DE EUREKA --
eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka

# -- CONFIGURACIÓN DE JWT --
# Clave secreta para firmar los tokens. Debe ser larga y segura.
jwt.secret=unaClaveMuyLargaYSeguraParaLaFirmaDeTokensJWT
# Tiempos de vida de los tokens en milisegundos
jwt.access-token-expiration=86400000  # 24 horas
jwt.refresh-token-expiration=604800000 # 7 días
