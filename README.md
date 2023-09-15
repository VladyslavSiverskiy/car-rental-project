# Car Rental Backend API

Welcome to the Car Rental pet project! 
This project aims to create a simple web application that allows users to rent cars online.  The backend of this application is built using Java and provides the necessary APIs for user registration, car data retrieval, reservations, and more.

## Technologies Used
<ul>
  <li>Spring Boot</li>
  <li>Spring Data JPA</li>
  <li>Spring Security (JWT token authentication)</li>
  <li>PostgreSQL</li>
  <li>Maven</li>
  <li>Swagger</li>
  <li>AWS EC2</li>
  <li>AWS RDS</li>
</ul>

## Installation
To set up the car rental backend, follow these steps:

Clone this repository:
```git clone https://github.com/yourusername/car-rental-backend.git```
Open folder:
```cd car-rental-backend```

## Configuration

Create application.properties file:
```
spring.datasource.url=jdbc:postgresql://path_to_your_db:5432/db_name
spring.datasource.username=your_db_user
spring.datasource.password=your_user_password
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

app.jwtSecret=your_jwt_secret_key

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_username
spring.mail.password=your_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

paypal.clientId=paypal_id
paypal.clientSecret=paypal_secret

server.forward-headers-strategy=framework
```
Redis functionality is available on main branch, so run docker container with redis to use it. Redis functionality is`t present on 'without-caching' branch

## Demo
To see functionality, open [https://github.com/VladyslavSiverskiy/car-rent-app-frontend/tree/main]
