# Warehouse - Almacenadora

---

### Control de Roles y Usuarios

El proyecto se centra en la gestión de roles y usuarios para controlar el acceso a las diferentes funciones. Los roles están predefinidos y determinan las acciones permitidas. Se destaca que el CRUD para roles está disponible principalmente para futuras actualizaciones.

### Usuarios

El acceso a la aplicación requiere un usuario registrado. Para la creación y actualización de usuarios, se necesitan el nombre y el correo electrónico únicos. La contraseña es configurable por el usuario. Los administradores tienen la capacidad de gestionar todos los usuarios, incluyendo actualizaciones, desactivación y restricción de acceso.

### Roles

Los roles determinan las acciones permitidas. `CLIENT` tiene acceso limitado, `EMPLOYEE` gestiona sucursales, bodegas y alquileres, mientras que `ADMIN` tiene control total, incluyendo la gestión de usuarios y roles.

### Servicios

Los empleados pueden crear, gestionar y desactivar servicios. La creación y actualización de servicios requieren nombres únicos. La desactivación se condiciona a la ausencia de dependencias en bodegas o alquileres.

### Sucursales

La administración de sucursales es responsabilidad de los administradores. La creación y actualización de sucursales requieren nombres y códigos únicos. La visualización está disponible para todos los usuarios.

### Bodegas

La creación, actualización y desactivación de bodegas pueden ser realizadas por empleados o administradores. Se imponen restricciones en la unicidad del código de bodega y la validez de la sucursal asociada. La visualización está abierta a todos los usuarios.

### Alquiler

Esta función permite a los usuarios alquilar bodegas. Se requieren el usuario habilitado y una bodega disponible. Los empleados pueden agregar servicios adicionales al proceso de alquiler. La desactivación de alquileres es posible cuando un usuario ya no desea alquilar una bodega.

## Tecnologías utilizadas:

- Spring Bot con Java versión 17.
- Spring security.
- JWT
- PostgreSQL.
- Liquibase.

## Requisitos de instalación:

1. **Java 17:**
    - Descargar e instalar Java 17.

   [Download the Latest Java LTS Free](https://www.oracle.com/java/technologies/downloads/)

2. **PostgreSQL:**
    - Descargar e instalar PostgreSQL.

   [PostgreSQL: Downloads](https://www.postgresql.org/download/)

    - Asegúrate de tener credenciales de acceso (usuario y contraseña) para la base de datos.

## Próximos avances:

Tengo interés en hacer un mejor uso del token JWT para la manipulación del propio usuario, donde me agradaría agregar nuevas funciones según los roles y según el usuario dueño del token, en algunos de los lugares básicos donde he notado que sería buena idea agregar estas funciones del token son:

- Mis bodegas
- Mis favoritos
- Solicitar reservas
- Dejar comentarios y reseñas
- Mejor manejo en general de mi propio perfil

Así también deseo encontrar puntos de mi código en el cual pueda mejorar y optimizar la lógica del proyecto o facilitar la experiencia del usuario con las funciones que ya existen, pues me parece que aún tengo puntos por mejorar para ser más intuitivo.

## Motivación de la creación del proyecto:

1. **Aplicación Práctica de Conocimientos Recientes:**
    - Iniciar un proyecto desde cero fue una oportunidad para aplicar los conocimientos recientemente adquiridos durante mis prácticas.
2. **Demostración de Habilidades:**
    - Crear proyectos funcionales es una manera efectiva de demostrar mis habilidades y competencias en el desarrollo de software.
3. **Exhibición del Proceso de Trabajo:**
    - El proyecto sirve como una muestra clara de cómo abordo y resuelvo problemas, ofreciendo una visión práctica de mi enfoque en el desarrollo de software.
4. **Optimización del Tiempo:**
    - La realización del proyecto también se orienta a optimizar mi tiempo, proporcionando una oportunidad para la práctica continua y el perfeccionamiento de mis habilidades.
5. **Adopción de Nueva Tecnología:**
    - La elección de Spring Boot y Java versión 17 como tecnologías principales responde a mi interés en adoptar y demostrar habilidades con tecnologías modernas.
6. **Preparación para la Búsqueda de Empleo:**
    - El proyecto tiene un propósito estratégico al ser parte de mi cartera durante la búsqueda de empleo, permitiéndome destacar mis habilidades y capacidades en entrevistas y revisiones de portafolio.
7. **Experiencia Integral en Desarrollo:**
    - La decisión de construir el proyecto desde cero, en lugar de basarme en una estructura preexistente, busca proporcionarme una experiencia integral en todas las fases del desarrollo de software.