# 🍽 Canteen Management System

A full-stack Canteen Management System built with:
- **Frontend**: HTML5, CSS3, Vanilla JavaScript
- **Backend**: Java 17 + Spring Boot 3.2
- **Database**: MySQL (DB name: `canteen_data`)

---

## 📁 Project Structure

```
canteen-management/
├── frontend/
│   ├── index.html          # Main app (all screens)
│   ├── css/
│   │   └── style.css       # Full UI styles
│   └── js/
│       ├── api.js          # API call helpers
│       └── app.js          # App logic & interactions
│
└── backend/
    ├── pom.xml
    └── src/main/
        ├── java/com/canteen/
        │   ├── CanteenManagementApplication.java
        │   ├── config/CorsConfig.java
        │   ├── model/          (Customer, Order, OrderItem, MenuItem, Payment, CustomerLedger, User)
        │   ├── repository/     (Spring Data JPA repos)
        │   ├── service/        (Business logic)
        │   └── controller/     (REST endpoints)
        └── resources/
            ├── application.properties
            └── data.sql        (Seed data - run manually after first start)
```

---

## ⚙️ Prerequisites

| Tool        | Version   |
|-------------|-----------|
| Java JDK    | 17+       |
| Maven       | 3.8+      |
| MySQL       | 8.0+      |
| Any browser | Modern    |

---

## 🚀 Setup Instructions

### Step 1 — MySQL Setup

```sql
CREATE DATABASE IF NOT EXISTS canteen_data;
```

### Step 2 — Configure Database

Edit `backend/src/main/resources/application.properties`:

```properties
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

### Step 3 — Run Backend

```bash
cd backend
mvn spring-boot:run
```

Backend starts at: `http://localhost:8080`

### Step 4 — Seed Initial Data

After the first run (Spring auto-creates tables), run the seed SQL:

```bash
mysql -u root -p canteen_data < src/main/resources/data.sql
```

### Step 5 — Open Frontend

Open `frontend/index.html` in your browser.

> **For production**: Serve frontend via nginx or any static server.

---

## 🔑 Default Login Credentials

| Role             | Username  | Password    |
|------------------|-----------|-------------|
| Admin/Owner      | admin     | admin123    |
| Waiter           | waiter1   | waiter123   |
| Kitchen Manager  | kitchen   | kitchen123  |

---

## 🌐 REST API Endpoints

### Customers
| Method | Endpoint                          | Description             |
|--------|-----------------------------------|-------------------------|
| POST   | /api/customers/register           | Register new customer   |
| GET    | /api/customers/search?query=RA    | Auto-search customers   |
| GET    | /api/customers/mobile/{mobile}    | Find by mobile          |
| GET    | /api/customers/{customerId}       | Find by customer ID     |
| GET    | /api/customers                    | All customers           |
| GET    | /api/customers/outstanding        | Customers with balance  |

### Menu Items
| Method | Endpoint                  | Description          |
|--------|---------------------------|----------------------|
| GET    | /api/menu                 | All available items  |
| GET    | /api/menu/search?query=pa | Search menu          |
| POST   | /api/menu                 | Add menu item        |
| PUT    | /api/menu/{id}            | Update menu item     |
| PATCH  | /api/menu/{id}/toggle     | Toggle availability  |
| DELETE | /api/menu/{id}            | Delete menu item     |

### Orders
| Method | Endpoint                              | Description          |
|--------|---------------------------------------|----------------------|
| POST   | /api/orders/create                    | Create new order     |
| POST   | /api/orders/{orderId}/items           | Add item to order    |
| DELETE | /api/orders/{orderId}/items/{itemId}  | Remove item          |
| PATCH  | /api/orders/items/{itemId}/status     | Update item status   |
| GET    | /api/orders/active                    | All active orders    |
| GET    | /api/orders/{orderId}                 | Get order by ID      |
| GET    | /api/orders/customer/{customerId}     | Orders by customer   |
| PATCH  | /api/orders/{orderId}/cancel          | Cancel order         |

### Payments
| Method | Endpoint                          | Description            |
|--------|-----------------------------------|------------------------|
| POST   | /api/payments/process             | Process payment        |
| GET    | /api/payments/billing/{customerId}| Get billing info       |
| GET    | /api/payments/customer/{id}       | Payment history        |
| GET    | /api/payments/ledger/{customerId} | Customer ledger        |

### Reports
| Method | Endpoint               | Description         |
|--------|------------------------|---------------------|
| GET    | /api/reports/daily     | Full daily report   |
| GET    | /api/reports/dashboard | Live dashboard stats|

### Users
| Method | Endpoint           | Description       |
|--------|--------------------|-------------------|
| POST   | /api/users/login   | Login             |
| GET    | /api/users         | All users         |
| POST   | /api/users         | Create user       |
| PUT    | /api/users/{id}    | Update user       |
| DELETE | /api/users/{id}    | Delete user       |

---

## 📱 Screens & Roles

| Screen              | Waiter | Kitchen Manager | Admin |
|---------------------|--------|-----------------|-------|
| Dashboard           | ✅     | ✅              | ✅    |
| Customer Search     | ✅     |                 | ✅    |
| Add Customer        | ✅     |                 | ✅    |
| Order Entry         | ✅     |                 | ✅    |
| Active Orders       | ✅     | ✅              | ✅    |
| Kitchen Dashboard   |        | ✅              | ✅    |
| Billing & Payment   | ✅     |                 | ✅    |
| Reports             |        |                 | ✅    |
| Menu Admin          |        |                 | ✅    |
| User Management     |        |                 | ✅    |

---

## 📊 Database Tables

1. `customers` — Customer profiles
2. `orders` — Order records
3. `order_items` — Items per order
4. `menu_items` — Canteen menu
5. `payments` — Payment transactions
6. `customer_ledger` — Balance tracking
7. `users` — Staff accounts

---

## 🔄 Real-Time Polling

- **Kitchen dashboard** & **Active Orders** auto-refresh every **10 seconds**
- **Owner Dashboard** auto-refreshes every **30 seconds**
- All changes reflect across all connected clients on next poll

---

## ⚠️ Notes for Production

1. Replace plain-text passwords with BCrypt hashing (add Spring Security)
2. Add JWT token-based authentication
3. Use environment variables for DB credentials
4. Deploy frontend to nginx / Apache
5. Package backend as JAR: `mvn clean package` then `java -jar target/*.jar`
