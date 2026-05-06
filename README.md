# CineReserve

## 1. Project Title & Description
**CineReserve** is a full-stack, scalable web application designed to streamline the movie ticket booking process. It provides a premium, interactive user experience where customers can browse currently showing movies, select showtimes, pick individual seats, and generate a QR code for their ticket after a simulated payment process.

The platform addresses the need for a simple, fast, and secure reservation flow. By utilizing role-based access control (RBAC), it provides distinct experiences for end-users (booking and viewing tickets) and administrators (managing movies, showtimes, and users).

## 2. Demo Admin Access
Use this demo admin account to test booking, movie CRUD, showtime management, and the admin dashboard:

- **Email:** `admin@gmail.com`
- **Password:** `12345`

These credentials grant Admin access, so you can explore both the normal ticket booking flow and administrative CRUD operations.

## 3. Features
- **User Authentication & Authorization**: Secure login and registration using JWT (JSON Web Tokens) with distinct User and Admin roles.
- **Movie & Showtime Browsing**: View available movies, their descriptions, and upcoming showtimes.
- **Interactive Seat Selection**: Real-time visual seat map allowing users to select available seats and see immediate pricing updates.
- **Simulated Payment Gateway**: Secure mockup of a checkout flow for booking finalization.
- **Digital Tickets**: Auto-generation of QR Codes for successful bookings, viewable in the "My Bookings" dashboard.
- **Admin Dashboard**: Manage movies, schedule new showtimes, and oversee user activity.
- **Responsive Premium UI**: Glassmorphism aesthetic with modern micro-animations for an engaging experience on both desktop and mobile.

## 4. Tech Stack
**Frontend:**
- **React.js (Vite)**: Fast, modern UI development.
- **React Router**: Client-side routing.
- **Axios**: API communication.
- **Lucide React**: Modern iconography.
- **QR Code React**: Ticket generation.
- **Vanilla CSS**: Custom premium styling with glassmorphism effects.

**Backend:**
- **Java 17 & Spring Boot 3**: Robust REST API framework.
- **Spring Security & JWT**: Authentication and Role-Based Access Control.
- **Spring Data MongoDB**: Document persistence for movies, showtimes, seats, and reservations.
- **MongoDB Atlas**: Managed cloud database for local and deployed environments.

## 5. How to Use
1. **Login/Register:** Open the app and log in using the demo credentials above (or create a new user account).
2. **Browse Movies:** On the home page, scroll through the available movies and click "Book Now" on your desired choice.
3. **Select Showtime & Seats:** Choose a convenient showtime, then click on the interactive grid to select your seats. Available seats are highlighted, while booked seats are disabled.
4. **Checkout:** Proceed to payment. Enter simulated payment details to finalize your reservation.
5. **View Tickets:** Navigate to the "My Bookings" section to view your ticket and its unique QR code.
6. **Admin Management:** If logged in as an Admin, click the "Admin Dashboard" link in the navigation bar to add new movies or schedule showtimes.

## 6. Installation & Setup
Follow these steps to run the project locally on your machine.

### Prerequisites
- Java 17+
- Node.js 18+
- Maven

### Backend Setup
1. Open a terminal and navigate to the project root directory.
2. Run the Spring Boot application using Maven:
   ```bash
   mvn spring-boot:run
   ```
3. The backend will start on `http://localhost:8080`. It connects to MongoDB Atlas using `SPRING_DATA_MONGODB_URI`, with the current default configured in [application.yml](/D:/movie-reservation-system/src/main/resources/application.yml:1).

### Frontend Setup
1. Open a new terminal and navigate to the `frontend` folder:
   ```bash
   cd frontend
   ```
2. Install the dependencies:
   ```bash
   npm install
   ```
3. Start the Vite development server:
   ```bash
   npm run dev
   ```
4. Open your browser and navigate to the URL provided by Vite (usually `http://localhost:5173`).

## 7. Project Structure
```text
movie-reservation-system/
├── frontend/                # React.js application
│   ├── src/
│   │   ├── components/      # Reusable UI components
│   │   ├── context/         # React Context (Auth)
│   │   ├── pages/           # Page layouts (Home, Booking, Admin)
│   │   └── index.css        # Global styles
├── src/main/java/.../movie/ # Spring Boot application
│   ├── config/              # Security and DB Initializers
│   ├── controller/          # REST API endpoints
│   ├── domain/              # MongoDB documents (User, Movie, Seat, etc.)
│   ├── dto/                 # Data Transfer Objects
│   ├── repository/          # Spring Data MongoDB repositories
│   └── service/             # Business logic layer
└── pom.xml                  # Maven dependencies
```

## 8. Future Enhancements
- **Email Notifications**: Send booking confirmations and tickets directly to the user's email.
- **Real Payment Gateway Integration**: Integrate Stripe or PayPal for actual transaction processing.
- **Reservation Expiration Policies**: Add richer cleanup and recovery rules for abandoned seat holds.
- **Movie Reviews & Ratings**: Allow users to leave feedback and rate movies they have watched.

## 9. Author Information
- **Name**: Nikhil Enjirapu
- **Email**: enjirapunikhil@gmail.com
- **GitHub**: [NikhilEnjirapu](https://github.com/NikhilEnjirapu)
