This project is an enhanced and secured version of the Orb Game originally developed in Assignment 1.
The application demonstrates enterprise-level Jakarta EE concepts, including authentication, authorization, REST security, JSF security, internationalization, testing, and frontend integration.

The project uses a 3-tier architecture:

Presentation Layer (JSF, React)

Business Layer (EJBs / Facades)

Persistence Layer (JPA / Database)

It is designed to reflect real-world secure web application development practices.

🧱 Architecture
🔹 Technologies Used

Jakarta EE

JSF (Jakarta Faces)

JPA (EclipseLink)

EJB (Stateless Session Beans)

CDI

Security

Basic Authentication

Database-backed Identity Store

Password hashing (PBKDF2)

Frontend

JSF (Admin & CRUD pages)

React (Sprite visualization & animation)

Testing

JUnit (Entity tests)

Selenium (JSF UI tests)

Database

MySQL / MariaDB

Server

GlassFish

Version Control

Git + GitHub (feature branches & PR workflow)

🔐 Security Implementation
Authentication

Basic Authentication is applied to:

JSF pages

RESTful API

Authentication is backed by a database user table, not file realm.

Authorization Groups
Group Name	Access
APIGroup	REST API only
JSFGroup	JSF CRUD pages
AdmGroup	JSF + REST + Admin area
Others	Index page only

Security is enforced using:

@DatabaseIdentityStoreDefinition

@BasicAuthenticationMechanismDefinition

web.xml security constraints

👤 User Administration

An Admin-only JSF area is provided to manage application users.

User Features

Create users

Assign roles/groups

Securely hash passwords

Update and delete users

Password Handling

Passwords are hashed and salted using PBKDF2

Clear-text passwords are never stored

Password fields display empty on edit

Leaving password blank during edit does not change the password

🌍 Internationalization (i18n)

The application supports multiple languages

A language selector is available on the index page

JSF pages use resource bundles (Bundle.properties, Bundle_fr.properties, etc.)

Locale is managed via a CDI-managed bean

🔌 RESTful API

REST endpoints expose sprite/orb data

Fully secured with role-based authorization

Tested using Postman

Unauthorized access returns proper HTTP status codes

⚛️ React Integration

A React frontend is included to:

Display sprite position and size data

Animate sprites using HTML5 Canvas

Modify sprite properties via API calls

React consumes the secured REST API and demonstrates frontend-backend integration.

🧪 Testing
Unit Testing (JUnit)

Entity-level tests

Meaningful logic validation (not trivial assertions)

UI Testing (Selenium)

Automated tests for JSF pages

Login success and failure scenarios

Authorization checks

((THIS PROJECT IS NOT DEPLOYED))!!!
