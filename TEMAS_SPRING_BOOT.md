# Índice Temático: Fundamentos de Spring Boot

Este documento compila y detalla todos los temas, conceptos y herramientas abordados en los archivos PDF presentes en este directorio, correspondientes a la serie **"Java con Spring Boot de 0 a Pro — Fundamentos de Spring Boot"**.

---

## 📑 Tabla de Contenidos

1. [Resumen General de los Documentos](#-resumen-general-de-los-documentos)
2. [1. ¿Qué es Spring vs Spring Boot?](#1-qué-es-spring-vs-spring-boot) (`1-Que-es-Spring-vs-Spring-Boot.pdf`)
3. [2. Crear tu primer proyecto con Spring Boot](#2-crear-tu-primer-proyecto-con-spring-boot) (`2-Crear-tu-primer-proyecto-con-Spring-Boot.pdf`)
4. [3. ApplicationContext y Beans](#3-applicationcontext-y-beans) (`3-ApplicationContext-y-Beans.pdf`)
5. [4. Inyección de dependencias y estereotipos](#4-inyección-de-dependencias-y-estereotipos) (`4-Inyeccion-de-dependencias-y-estereotipos.pdf`)
6. [5. Configuración en Spring Boot](#5-configuración-en-spring-boot) (`5-Configuracion-en-Spring-Boot.pdf`)
7. [6. Auto-configuración, Starters y DX](#6-auto-configuración-starters-y-dx) (`6-Auto-configuracion-Starters-y-DX.pdf`)

---

## 📊 Resumen General de los Documentos

| Archivo | Tema Central | Conceptos Clave |
| :--- | :--- | :--- |
| [1-Que-es-Spring-vs-Spring-Boot.pdf](file:///E:/Informacion/Santi/facu/3ro/Desarrollo%20de%20software/SpringBoot/1-Que-es-Spring-vs-Spring-Boot.pdf) | Introducción y Evolución del Framework | Spring Framework tradicional vs. Spring Boot, IoC/DI, AOP, Servidor embebido, Starters, Autoconfiguración, Fat JAR. |
| [2-Crear-tu-primer-proyecto-con-Spring-Boot.pdf](file:///E:/Informacion/Santi/facu/3ro/Desarrollo%20de%20software/SpringBoot/2-Crear-tu-primer-proyecto-con-Spring-Boot.pdf) | Inicialización y Estructura del Proyecto | Spring Initializr (`start.spring.io`), JDK 17+, Maven/Gradle, `@SpringBootApplication`, estructura de carpetas, ejecución y verificación de logs. |
| [3-ApplicationContext-y-Beans.pdf](file:///E:/Informacion/Santi/facu/3ro/Desarrollo%20de%20software/SpringBoot/3-ApplicationContext-y-Beans.pdf) | Contenedor IoC y Gestión de Objetos | `ApplicationContext`, ciclo de vida de un Bean, Component Scan, registro automático (`@Component`) vs. manual (`@Configuration` y `@Bean`), ventajas de reutilización. |
| [4-Inyeccion-de-dependencias-y-estereotipos.pdf](file:///E:/Informacion/Santi/facu/3ro/Desarrollo%20de%20software/SpringBoot/4-Inyeccion-de-dependencias-y-estereotipos.pdf) | Inyección de Dependencias y Capas de Software | Problema del acoplamiento con `new`, tipos de inyección (Constructor vs. Setter vs. Field), estereotipos semánticos (`@Component`, `@Service`, `@Repository`, `@Controller`). |
| [5-Configuracion-en-Spring-Boot.pdf](file:///E:/Informacion/Santi/facu/3ro/Desarrollo%20de%20software/SpringBoot/5-Configuracion-en-Spring-Boot.pdf) | Configuración Externa y Entornos | Formatos `application.properties` vs. `application.yml`, inyección mediante `@Value` y `@ConfigurationProperties`, perfiles (`dev`, `test`, `prod`) y su activación. |
| [6-Auto-configuracion-Starters-y-DX.pdf](file:///E:/Informacion/Santi/facu/3ro/Desarrollo%20de%20software/SpringBoot/6-Auto-configuracion-Starters-y-DX.pdf) | Mecanismo Interno y Experiencia de Desarrollo | Anatomía de un Starter, funcionamiento de la autoconfiguración condicional (`@ConditionalOnClass`, `@ConditionalOnMissingBean`), Developer Experience con DevTools (Hot Reload, LiveReload), configuración de Logging. |

---

## 1. ¿Qué es Spring vs Spring Boot?
**Archivo:** [1-Que-es-Spring-vs-Spring-Boot.pdf](file:///E:/Informacion/Santi/facu/3ro/Desarrollo%20de%20software/SpringBoot/1-Que-es-Spring-vs-Spring-Boot.pdf)  
**Módulo:** Fundamentos de Spring Boot — Video #1

### Temas cubiertos:
1. **Contexto histórico y motivación:**
   - La problemática de las aplicaciones Java EE tradicionales y Spring clásico.
   - El "infierno de configuración XML": cientos de líneas de configuración repetitiva y propensa a fallas.
   - Gestión manual de dependencias y conflictos entre versiones de librerías JAR.
   - Servidores de aplicaciones externos (Tomcat, WebLogic, JBoss) requeridos por separado.
   - Línea temporal: Nacimiento de Spring Framework (2004), crecimiento y sobrecarga de complejidad (2009-2012), y lanzamiento de Spring Boot 1.0 (2014).

2. **¿Qué es Spring Framework?:**
   - Contenedor de Inversión de Control (IoC).
   - Inyección de dependencias (DI) para desacoplar componentes.
   - Programación Orientada a Aspectos (AOP).
   - Gestión declarativa de transacciones e integración con múltiples tecnologías.

3. **¿Qué es Spring Boot?:**
   - Definición: *Spring Boot = Spring Framework + Configuración Automática + Convenciones Inteligentes*.
   - Principio de convención sobre configuración.
   - Objetivo: Que los desarrolladores se enfoquen en la lógica de negocio y no en la infraestructura.

4. **Pilares de Spring Boot:**
   - **Autoconfiguración:** Detección en el classpath y aplicación automática de configuraciones sensatas.
   - **Starters:** Agrupaciones preconfiguradas de dependencias para necesidades concretas (`spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-security`).
   - **Servidor embebido:** Inclusión de servidores como Tomcat o Jetty embebidos, eliminando la instalación manual de servidores externos.
   - **Fat JAR:** Empaquetado completo en un único archivo ejecutable listo para desplegar y contenerizar (Docker/Cloud).

5. **Comparativa Antes vs. Después:**
   - Reducción del tiempo de setup inicial de días a minutos.
   - Eliminación del 80% de configuraciones repetitivas.

---

## 2. Crear tu primer proyecto con Spring Boot
**Archivo:** [2-Crear-tu-primer-proyecto-con-Spring-Boot.pdf](file:///E:/Informacion/Santi/facu/3ro/Desarrollo%20de%20software/SpringBoot/2-Crear-tu-primer-proyecto-con-Spring-Boot.pdf)  
**Módulo:** Fundamentos de Spring Boot — Video #2

### Temas cubiertos:
1. **Requisitos y herramientas previas:**
   - JDK 17 o superior (estándar para Spring Boot 3.x).
   - Entornos de desarrollo integrados (IDEs): IntelliJ IDEA, Eclipse, Visual Studio Code.
   - Conexión a internet y gestores de compilación (Maven / Gradle).

2. **Spring Initializr (`start.spring.io`):**
   - Generador oficial de proyectos base de Spring Boot.
   - Configuración de metadatos:
     - **Group:** Identificador de la organización (ej. `com.miempresa.tutorial`).
     - **Artifact:** Nombre del proyecto y del artefacto final (ej. `mi-primer-spring-boot`).
     - **Packaging:** Formato de salida (`JAR` recomendado frente a `WAR`).
     - **Java Version:** Versión del compilador y runtime (17+).

3. **Dependencias iniciales recomendadas:**
   - **Spring Web:** Soporte para MVC, APIs REST y servidor Tomcat embebido.
   - **Spring Boot DevTools:** Aceleración del flujo de trabajo con recarga automática.
   - **Spring Boot Starter Test:** Herramientas para testing unitario y de integración (JUnit, Mockito, AssertJ).

4. **Estructura estándar de directorios (Maven):**
   - `src/main/java`: Clases y código fuente de la aplicación.
   - `src/main/resources`: Archivos estáticos, plantillas y `application.properties`.
   - `src/test/java`: Pruebas automatizadas.
   - `pom.xml` / `build.gradle`: Definición de dependencias, plugins y versiones.

5. **La anotación `@SpringBootApplication`:**
   - Explicación de las tres anotaciones esenciales que agrupa:
     - `@Configuration`: Marca la clase como fuente de definiciones de beans.
     - `@EnableAutoConfiguration`: Habilita el mecanismo de autoconfiguración de Spring Boot.
     - `@ComponentScan`: Activa el escaneo automático de componentes a partir del paquete actual hacia sus subpaquetes.

6. **Ejecución y Verificación:**
   - Métodos de ejecución: desde el IDE, vía terminal mediante el Maven Wrapper (`./mvnw spring-boot:run`), o compilado como JAR autónomo (`java -jar target/mi-app.jar`).
   - Interpretación de logs de arranque: banner de consola, inicialización del contexto y puerto asignado (puerto 8080 por defecto).
   - Verificación inicial en el navegador (`http://localhost:8080` mostrando página 404 por falta de rutas definidas).

---

## 3. ApplicationContext y Beans
**Archivo:** [3-ApplicationContext-y-Beans.pdf](file:///E:/Informacion/Santi/facu/3ro/Desarrollo%20de%20software/SpringBoot/3-ApplicationContext-y-Beans.pdf)  
**Módulo:** Fundamentos de Spring Boot — Video #3

### Temas cubiertos:
1. **¿Qué es el `ApplicationContext`?:**
   - El contenedor central de Inversión de Control (IoC Container).
   - Actúa como administrador principal que almacena, conecta y gestiona los objetos de la aplicación.
   - Mantiene el registro de todos los beans disponibles y coordina la inyección de dependencias.

2. **Concepto de Bean:**
   - Definición: Objeto instanciado, ensamblado y administrado completamente por Spring en lugar de usar `new`.
   - Ejemplos comunes de beans:
     - **Servicios:** Clases de lógica de negocio (ej. `UsuarioService`).
     - **Repositorios:** Clases de acceso a datos y persistencia (ej. `UsuarioRepository`).
     - **Controladores:** Clases que procesan solicitudes HTTP (ej. `UsuarioController`).
     - **Configuraciones:** Clases que definen parámetros o beans auxiliares.

3. **Mecanismo de Detección (Component Scan):**
   - El escaneo automático que realiza Spring en busca de clases anotadas con `@Component` (y derivados).
   - Alcance del escaneo: Por defecto, parte del paquete donde reside `@SpringBootApplication` hacia abajo.

4. **Ciclo de Vida de un Bean:**
   - Secuencia ordenada en 6 fases:
     1. **Detección:** Descubrimiento mediante Component Scan.
     2. **Instanciación:** Creación de la instancia llamando al constructor.
     3. **Inyección:** Suministro de dependencias requeridas por el bean.
     4. **Inicialización:** Ejecución de lógica de inicialización si existe.
     5. **Uso:** El bean queda activo y disponible en el contexto para atender peticiones.
     6. **Destrucción:** Limpieza ordenada de recursos al finalizar la aplicación.

5. **Registro de Beans: Automático vs. Manual:**
   - **Automático:** Uso de la anotación `@Component` (enfoque más simple y común para clases propias).
   - **Manual:** Definición de métodos anotados con `@Bean` dentro de clases con `@Configuration` (necesario para clases de librerías de terceros o inicializaciones complejas con parámetros dinámicos).

6. **Ventajas de la gestión mediante Beans:**
   - Reutilización de instancias (gestión de memoria optimizada mediante alcance singleton por defecto).
   - Desacoplamiento arquitectónico.
   - Alta testabilidad (facilidad para inyectar *mocks* o *stubs* en pruebas).

---

## 4. Inyección de Dependencias y Estereotipos
**Archivo:** [4-Inyeccion-de-dependencias-y-estereotipos.pdf](file:///E:/Informacion/Santi/facu/3ro/Desarrollo%20de%20software/SpringBoot/4-Inyeccion-de-dependencias-y-estereotipos.pdf)  
**Módulo:** Fundamentos de Spring Boot — Video #4

### Temas cubiertos:
1. **El Problema del Acoplamiento Fuerte:**
   - Desventajas de crear instancias de dependencias con `new`:
     - Código rígido y frágil.
     - Dificultad para sustituir clases por mocks en pruebas unitarias.
     - Cambios en cascada: modificar una clase obliga a cambiar múltiples archivos.
     - Duplicación de instancias del mismo objeto.

2. **Concepto de Inyección de Dependencias (DI):**
   - Delegación de la responsabilidad de instanciar y conectar componentes al contenedor de Spring.
   - Principio de Inversión de Control: la clase pide lo que necesita en lugar de crearlo ella misma.
   - Beneficios: Desacoplamiento, programación orientada a interfaces/abstracciones, reutilización y alta cobertura de tests.

3. **Formas de Inyección de Dependencias en Spring:**
   - **Inyección por Constructor (Práctica Recomendada):**
     - Permite declarar dependencias como variables inmutables (`final`).
     - Garantiza que el objeto no se instancie en un estado inválido o incompleto.
     - Facilita pruebas unitarias sin levantar el contexto de Spring (se pasan los mocks directo al constructor).
     - Detección temprana de fallas al iniciar el contenedor.
   - **Inyección por Setter:**
     - Dependencias asignadas tras la instanciación; adecuada solo para dependencias opcionales.
   - **Inyección por Campo (*Field Injection* con `@Autowired` directo en atributo):**
     - Desaconsejada en producción: dificulta las pruebas unitarias y oculta el acoplamiento real de la clase.

4. **Estereotipos de Spring (Especializaciones de `@Component`):**
   - **`@Component`:** Estereotipo genérico para cualquier clase gestionada que no encaje en un rol específico.
   - **`@Service`:** Marca componentes de la capa de negocio, reglas de dominio y lógica de cálculo.
   - **`@Repository`:** Marca componentes de acceso a datos y persistencia; habilita la traducción automática de excepciones de bases de datos.
   - **`@Controller` / `@RestController`:** Marca controladores de la capa web que procesan solicitudes HTTP y devuelven vistas o respuestas REST.

5. **Arquitectura Limpia en Capas:**
   - Flujo de responsabilidades:
     $$\text{Petición HTTP} \longrightarrow \mathbf{@Controller} \longrightarrow \mathbf{@Service} \longrightarrow \mathbf{@Repository} \longrightarrow \text{Base de Datos}$$

---

## 5. Configuración en Spring Boot
**Archivo:** [5-Configuracion-en-Spring-Boot.pdf](file:///E:/Informacion/Santi/facu/3ro/Desarrollo%20de%20software/SpringBoot/5-Configuracion-en-Spring-Boot.pdf)  
**Módulo:** Fundamentos de Spring Boot — Video #5

### Temas cubiertos:
1. **Fundamentos de la Configuración Externa:**
   - Problemas de valores *hardcodeados*: necesidad de recompilar y riesgos de seguridad.
   - Principio de adaptación a diferentes entornos sin alterar el código fuente.

2. **Formatos de Configuración:**
   - **`application.properties`:**
     - Sintaxis tradicional basada en pares `clave=valor`.
     - Una propiedad por línea, soporte de comentarios con `#`.
     - Amplia compatibilidad con herramientas del ecosistema Java.
   - **`application.yml` (YAML):**
     - Sintaxis estructurada basada en indentación jerárquica.
     - Mayor legibilidad para configuraciones complejas y anidadas.
     - Soporte nativo y limpio para listas y mapas.

3. **Propiedades Típicas del Servidor y Aplicación:**
   - Configuración de puerto (`server.port`).
   - Contexto base de URLs (`server.servlet.context-path`).
   - Identificación de la aplicación (`spring.application.name`).
   - Exposición de endpoints de gestión (`management.endpoints.web.exposure.include`).

4. **Inyección de Propiedades en Clases Java:**
   - **Anotación `@Value`:**
     - Inyección directa de valores atómicos en variables de instancia.
     - Soporte de valores por defecto mediante la sintaxis: `${propiedad:valorDefault}`.
   - **Anotación `@ConfigurationProperties`:**
     - Enfoque recomendado, fuertemente tipado (*type-safe*) y estructurado.
     - Agrupación bajo un prefijo común (ej. `@ConfigurationProperties(prefix = "app.database")`).
     - Soporte de clases anidadas con *getters* y *setters*, validación de datos y autocompletado en IDEs.

5. **Perfiles de Configuración (Profiles):**
   - Segmentación de configuraciones por entorno:
     - **`dev`:** BD en memoria (H2), reinicio rápido, nivel de log detallado (`DEBUG`), puerto local.
     - **`test`:** Base de datos aislada para testing, métricas mínimas.
     - **`prod`:** Base de datos productiva, logs de errores, máxima seguridad y optimización.
   - Convención de nombres de archivo: `application-{perfil}.properties` o `application-{perfil}.yml`.
   - Modos de activación de un perfil:
     - En propiedades: `spring.profiles.active=dev`.
     - Vía variable de entorno: `export SPRING_PROFILES_ACTIVE=prod`.
     - Por línea de comandos al ejecutar: `java -jar mi-app.jar --spring.profiles.active=test`.
     - Configuración de perfiles en las variables de ejecución del IDE.

---

## 6. Auto-configuración, Starters y Developer Experience (DX)
**Archivo:** [6-Auto-configuracion-Starters-y-DX.pdf](file:///E:/Informacion/Santi/facu/3ro/Desarrollo%20de%20software/SpringBoot/6-Auto-configuracion-Starters-y-DX.pdf)  
**Módulo:** Fundamentos de Spring Boot — Video #6

### Temas cubiertos:
1. **El Problema Histórico de la Configuración Manual:**
   - Configuración XML extensa y propensa a fallas de sintaxis y compatibilidad.
   - Conflictos de dependencias (*jar hell*) en builds manuales.
   - Pérdida de horas previas a iniciar la lógica de negocio.

2. **Concepto y Utilidad de los Starters:**
   - Paquetes de dependencias coherentes y probadas para resolver necesidades concretas:
     - `spring-boot-starter-web`: Tomcat embebido, Spring MVC, Jackson (JSON), validación.
     - `spring-boot-starter-data-jpa`: Hibernate ORM, Spring Data JPA, drivers JDBC y pool HikariCP.
     - `spring-boot-starter-security`: Spring Security, autenticación, autorización y filtros de seguridad.

3. **Funcionamiento Interno de la Auto-configuración:**
   - Flujo de 4 pasos ejecutado al arrancar la aplicación:
     1. **Detección de dependencias:** Escaneo del *classpath* para descubrir qué librerías están presentes.
     2. **Evaluación de condiciones:** Evaluación de anotaciones condicionales como `@ConditionalOnClass` (si la clase existe en classpath) y `@ConditionalOnMissingBean` (solo si el usuario no ha registrado un bean propio).
     3. **Instanciación automática:** Creación y configuración de beans con valores por defecto óptimos.
     4. **Capacidad de sobrescritura:** Prioridad del código del usuario si este provee una configuración explícita.

4. **Herramientas de Developer Experience (DX) — Spring Boot DevTools:**
   - Inclusión mediante la dependencia `spring-boot-devtools`.
   - **Hot Reload Automático:** Reinicio rápido del contexto ante modificaciones en el código sin necesidad de relanzar toda la JVM.
   - **LiveReload:** Notificación automática al navegador web para recargar assets estáticos (HTML/CSS/JS) en cuanto cambian.
   - **Configuraciones automáticas de desarrollo:** Desactivación de cachés de templates y niveles de logging optimizados para debugging.

5. **Sistema y Niveles de Logging:**
   - Framework integrado por defecto: SLF4J con Logback.
   - Jerarquía de niveles de log: `ERROR` $\rightarrow$ `WARN` $\rightarrow$ `INFO` $\rightarrow$ `DEBUG` (y `TRACE`).
   - Personalización básica en `application.properties`:
     - Nivel por paquete: `logging.level.com.miapp=DEBUG`, `logging.level.org.springframework=WARN`.
     - Formato de salida: `logging.pattern.console`.
     - Salida a archivo: `logging.file.name=miapp.log`.

6. **Impacto en el Desarrollo Profesional:**
   - Reducción drástica del *time-to-market*.
   - Estandarización de arquitecturas en equipos de ingeniería.
   - Menor superficie de errores de configuración humana.
