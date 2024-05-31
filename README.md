# Enterprise Resource Planning


## The aim of the project is to implement ERP system modules using a microservices architecture.


### Key Features

- **Inventory Management**: Real-time inventory updates and dynamic pricing with customizable margins.
  
- **Event Tracking**: Listens for and reacts to changes in item availability, pricing, and new item additions from the Goods module.
  
- **Order Processing**: Creates Preliminary Invoices upon order confirmation, handles item reservations, and processes payments. Additionally, automates overdue Preliminary Invoices management and generates Invoices upon payment confirmation.



### Technologies

- Java programming language
- Spring Boot framework 
- JPA (Java Persistence API) and Hibernate ORM (Object-Relational Mapping)
- RabbitMQ as the message broker for inter-service communication
- Docker for containerization, ensuring portability and scalability



