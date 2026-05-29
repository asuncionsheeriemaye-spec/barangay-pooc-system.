# Barangay Pooc Resident Information System

Ang system na ito ay binuo para sa pamamahala ng mga impormasyon ng mga residente sa Barangay Pooc.

## Mga Tech Stack
- **Language:** Java
- **Framework:** JavaFX, Maven
- **Database:** PostgreSQL (via Supabase)

## Installation
1. I-clone ang repository na ito:
   `git clone https://github.com/asuncionsheeriemaye-spec/barangay-pooc-system..git`
2. Gumawa ng `.env` file sa root directory at ilagay ang database credentials:
   ```env
   DB_URL=jdbc:postgresql://your-supabase-host:6543/postgres?sslmode=require
   DB_USER=your-user
   DB_PASSWORD=your-password
