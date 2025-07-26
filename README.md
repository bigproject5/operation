# operation


---
- 윈도우 어플리케이션 실행
```angular2html
.\gradlew.bat bootRun
```

- 회원가입
```angular2html
POST http://localhost:8080/api/operation/admin
{
    "loginId": "admin123",
    "password": "123",
    "name": "관리자",
    "email": "admin@example.com",
    "phoneNumber": "01012345678",
    "companyNumber": "123-45-67890",
    "address": "서울특별시 강남구",
    "adminCode": "ADM001"
}
```

- 로그인
```angular2html
POST http://localhost:8080/api/operation/admin/login
{
  "loginId": "admin123",
  "password": "123"
}
```

- 토큰 테스트
```angular2html
POST http://localhost:8080/api/operation/admin/test 
Authorization:"Bearer <JWT-토큰>"
{
    "message": "데이터 요청 A"
}
```