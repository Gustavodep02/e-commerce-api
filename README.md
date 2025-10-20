# 🛒 Ecommerce API

Java Spring Boot API implementing an e-commerce backend.  
Includes **JWT authentication**, **Stripe payment integration**, and **unit testing**.  
Based on the challenge: [Ecommerce API Project - roadmap.sh](https://roadmap.sh/projects/ecommerce-api)

---

## 🚀 Tech Stack
- **Java 17+**
- **Spring Boot**
- **Maven**
- **Spring Security (JWT)**
- **Stripe Java SDK**
- **PostgreSQL** (production) / **H2** (tests)
- **JUnit 5**, **Mockito**

---

## ⚙️ Prerequisites
Make sure you have the following installed:
- Java 17 or newer  
- Maven  
- PostgreSQL  
- A Stripe account (with test keys)

---

## 🔧 Configuration

Edit `src/main/resources/application.properties` and configure your **database**, **JWT** credentials.

Example:
```properties
spring.application.name=e-commerce-api

spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

api.security.token.secret=${JWT_SECRET:my-secret-key}
```
⚠️ **Stripe credentials:**

To modify your Stripe API key, update them directly in the PaymentController class.

---

## 💻 Run Locally

Clone the repository:
```bash
git clone https://github.com/Gustavodep02/e-commerce-api.git
cd e-commerce-api
```

Build the project:
```bash
mvn clean install -DskipTests=false
```

Run from your IDE (e.g. IntelliJ) by launching the `@SpringBootApplication` main class,  
or run directly via Maven:
```bash
mvn spring-boot:run
```

You can also run the generated JAR:
```bash
java -jar target/*.jar
```

---

## 🧪 Tests

Run all unit tests:
```bash
mvn test
```

Testing stack:
- **JUnit 5**
- **Mockito**

---

## 🧰 Additional Info
- Database schema updates automatically (`spring.jpa.hibernate.ddl-auto=update`)
- H2 is used in-memory for testing environments  
- Stripe API keys should be added in your environment variables for secure integration
