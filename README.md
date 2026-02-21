FRONTEND: https://github.com/kuokdavinci/movie-ticket-app-frontend

1. Overview
RESTful backend for movie ticket booking (users, movies, showtimes, seats, bookings).
Built with Spring Boot 3, Spring Security (JWT), Spring Data JPA, PostgreSQL.
Stateless JWT authentication with role-based authorization (USER / ADMIN).
Booking logic executed within service-layer transaction (@Transactional).

2. Architecture
Layered architecture: Controller → Service → Repository.
Stateless service design (no server-side session).
@Transactional applied at service layer (BookingService.bookTicket).
Layer Responsibilities
Controller: handle HTTP requests/responses.
Service: business logic, transaction boundary.
Repository: data access via JPA.

3. Booking Workflow
Client sends POST /api/movies/{movieId}/bookings with JWT.
Service loads Showtime and Seat.
Transaction starts.
Validate seat availability.
Create and persist Booking.
Commit transaction.

4. Concurrency & Consistency
Double booking prevention relies on transactional boundary + existence check.
PostgreSQL default isolation level: READ COMMITTED.
No explicit row-level locking or unique constraint on (showtime_id, seat_id).
Race condition is still possible under concurrent requests.
Stronger guarantee would require DB-level unique constraint and/or pessimistic locking.

5. Authentication & Security
Stateless JWT (Authorization: Bearer <token>).
Custom JwtFilter in Spring Security filter chain.
Role-based authorization via @PreAuthorize.
Public endpoints: /api/register, /api/login.

6. Database Design
Main entities:
User
Movie
Showtime
Seat
Booking
Relationships:
Movie 1–n Showtime
Showtime 1–n Seat
User 1–n Booking
Booking n–1 User / Showtime / Seat
Unique constraint: users.username
No unique constraint for seat per showtime.

7. API (Core Endpoints)
POST /api/register
POST /api/login
GET /api/movies
POST /api/movies (ADMIN)
POST /api/movies/{movieId}/bookings
GET /api/my-bookings
DELETE /api/my-bookings/{bookingId}

8. Testing
JUnit 5 + Mockito.
Service, controller, and security layer tests.

Covers success and failure scenarios.

No published coverage report.
