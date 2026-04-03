# MentorMatch (buiphuongnam_test_t3)

MentorMatch la ung dung ket noi mentee/mentor voi backend Spring Boot va frontend React (Vite).

## 1. Kien truc du an

- Frontend: React + Vite tai thu muc `frontend`
- Backend: Spring Boot (Java 17) tai thu muc `backend`
- Database: MySQL
- Message broker: RabbitMQ

## 2. Yeu cau moi truong

- Java 17
- Maven 3.9+
- Node.js 20+
- Docker Desktop (co Docker Compose)

## 3. Cai dat va chay local

### 3.1 Cai dat dependencies

```powershell
Set-Location frontend
npm install

Set-Location ../backend
mvn -DskipTests compile
```

### 3.2 Chay MySQL va RabbitMQ bang Docker Compose

Tu thu muc goc du an:

```powershell
Copy-Item .env.docker.example .env.docker
docker compose --env-file .env.docker up -d
docker compose ps
```

Mac dinh se mo cac cong:

- MySQL: `3306`
- RabbitMQ AMQP: `5672`
- RabbitMQ Management UI: `15672` (mac dinh user/pass: `guest/guest`)

### 3.3 Cau hinh backend local

Backend da duoc externalize env trong [backend/src/main/resources/application.yml](backend/src/main/resources/application.yml).

Ban co the:

- Dung gia tri mac dinh de chay local nhanh
- Hoac dat env theo mau [backend/.env.example](backend/.env.example)

Luu y: Backend da duoc cau hinh de tu dong nap file `.env` khi chay local.

### 3.3.1 Cau hinh Google OAuth2

1. Tao OAuth Client tren Google Cloud Console:

- Application type: Web application
- Authorized JavaScript origins:
  - `http://localhost:5173`
- Authorized redirect URIs:
  - `http://localhost:8080/login/oauth2/code/google`

2. Them cac bien vao `backend/.env`:

```env
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
OAUTH2_AUTHORIZED_REDIRECT_URI=http://localhost:5173/auth/google/callback
OAUTH2_FAILURE_REDIRECT_URI=http://localhost:5173/auth
```

3. Dam bao frontend dang tro ve backend API:

```env
VITE_API_URL=http://localhost:8080/api
```

### 3.4 Chay backend

```powershell
Set-Location backend
mvn spring-boot:run
```

Backend se chay tai: `http://localhost:8080`

### 3.5 Chay frontend

```powershell
Set-Location frontend
Copy-Item .env.example .env
```

Cap nhat `VITE_API_URL` trong file `.env` cho dung backend local (thuong la `http://localhost:8080/api`).

```powershell
npm run dev
```

Frontend se chay tai: `http://localhost:5173`

## 4. Deploy len Render (backend) + Vercel (frontend)

### 4.1 Deploy backend len Render

1. Tao Web Service moi tren Render tu repo nay.
2. Dat Root Directory la `backend`.
3. Build Command:

```bash
mvn clean package -DskipTests
```

4. Start Command:

```bash
java -jar target/mentormatch-0.0.1-SNAPSHOT.jar
```

5. Khai bao bien moi truong tren Render (tham khao [backend/.env.example](backend/.env.example)).

Toi thieu nen co:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `CORS_ALLOWED_ORIGINS` (domain Vercel cua frontend)
- `FRONTEND_PAYMENT_RESULT_URL` (vi du: `https://your-frontend.vercel.app/payment-result`)
- `VNPAY_RETURN_URL` (vi du: `https://your-backend.onrender.com/api/payment/vnpay-return`)

Neu ban su dung notification/mail/upload thanh toan, cau hinh them:

- `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`
- `MAIL_*`
- `CLOUDINARY_*`
- `VNPAY_*`
- `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`
- `OAUTH2_AUTHORIZED_REDIRECT_URI`, `OAUTH2_FAILURE_REDIRECT_URI`

### 4.2 Deploy frontend len Vercel

1. Import repo vao Vercel.
2. Chon Root Directory la `frontend`.
3. Build settings:

- Build Command: `npm run build`
- Output Directory: `dist`

4. Dat env:

- `VITE_API_URL=https://your-backend.onrender.com/api`

Tham khao mau tai [frontend/.env.example](frontend/.env.example).

### 4.3 Dong bo CORS giua Vercel va Render

Tai Render, dat:

- `CORS_ALLOWED_ORIGINS=https://your-frontend.vercel.app`

Neu co nhieu domain, tach boi dau phay:

- `CORS_ALLOWED_ORIGINS=https://your-frontend.vercel.app,https://www.your-frontend.vercel.app`

## 5. Docker Compose dung de lam gi?

File [docker-compose.yml](docker-compose.yml) chi dung de chay ha tang local (MySQL + RabbitMQ), khong deploy ca backend/frontend bang compose.

## 6. Kiem tra nhanh sau deploy

- Frontend goi duoc API backend (`VITE_API_URL` dung)
- Backend khong loi CORS
- Payment redirect ve dung trang `payment-result`
- RabbitMQ/MySQL thong ket noi thanh cong trong log backend
