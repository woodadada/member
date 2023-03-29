# 🙋‍♂️ 회원 가입 API 

## 🌈 구현 목표
 - Spring Security + JWT를 사용하여 회원 가입, 비밀번호 재설정 API 개발.
 - 회원가입, 비밀번호 재설정은 SMS 인증이 필요.(인증 유효시간 3분)
 - 인증 유효시간 3분 안에 인증, 회원가입, 비밀번호 재설정을 완료하는 플로우.
 - 3분 안에 인증만 완료 후 10분 뒤 회원가입, 비밀번호 재설정은 가능하지 않다.
 - SMS는 [Naver Cloud Simple & Easy Notification](https://api.ncloud-docs.com/docs/ko/ai-application-service-sens-smsv2) 사용.
 - 인증 코드는 6자리 난수.
 - 테스트 코드 작성

## 개발 환경

### 기본 환경

  > IDE : IntelliJ IDEA Ultimate\
  > OS : Max OS

### Server
  > 언어 : Java\
  > 프레임워크 : SpringBoot\
  > Build : Gradle\
  > Test : JUnit5\
  > DB : H2\
  > ORM : JPA\
  > 접속 Base URI : `http://localhost:8080`

## 🧏‍♂️ 설계와 개선점

- 설계
  - Spring Security와 JWT를 사용하여 구현하였습니다.
  - 인증번호 발송에 사용하는 RestTemplate Connection 세팅하여 재요청 시 리소스 사용을 고려하였습니다.
  

- 레이어드 아키텍처
  - Controller, Service, Repository 계층 구조로 설계하였습니다.
  - Controller는 Client에게 받은 Parameter를 검사 후 Service 레이어로 요청합니다.
  - 비즈니스 로직은 Service 레이어에서 실행되며 Repository를 통해 CRUD를 진행합니다.
  - Client에 전달할 데이터는 DTO 클래스로 변환하여 전달됩니다.


- 개선점
  - 인증번호를 발송하는 부분을 Kafka 또는 RabbitMQ와 같은 메세지 큐를 사용하여 분리
  - Redis, MongoDB를 사용한다면 만료 시간 설정으로 자동 삭제 처리

## DB 설계
<img width="587" alt="image" src="https://user-images.githubusercontent.com/60130985/228501408-b4a2754d-a721-4b70-bbaa-b1ca7b0cec57.png">

## 🧑‍💻 API 기능 명세

### 1. 회원가입 SMS 발송 API
- POST `/api/sms/join`
  - body
```
{
    "phoneNumber" : "010-7320-3333"
}
```
- Request
```
http://localhost:8080/api/sms/join
```
- Response
```
{
    "statusCode": "202",
    "statusName": "success",
    "requestId": "VORSSA-1680087131264-3216-57483993-IDKqpXWH",
    "requestTime": "2023-03-29T19:52:11.264"
}
```


### 2. 회원가입 SMS 번호 인증 API
- POST `/api/sms/authentication/join`
  - body
```
{
    "phoneNumber" : "010-7570-3950",
    "numberCode" : "897029"
}
```
- Request
```
http://localhost:8080/api/sms/authentication/join
```
- Response
```
{
    "id": 1,
    "phoneNumber": "010-7570-3950",
    "email": null,
    "numberCode": "897029",
    "authenticationYn": "Y",
    "expiredTime": 1680087311245
}
```


### 3. 회원가입 API
- POST `/api/members/join`
  - body
```
{
    "name" : "강감찬",
    "password" : "kangkamchan12!",
    "nickName" : "낑깡",
    "email" : "chachacha12@gmail.com",
    "phoneNumber" : "010-7320-3333"
}
```
- Request
```
http://localhost:8080/api/members/join
```
- Response
```
{
    "memberDto": {
        "id": 1,
        "name": "강감찬",
        "nickName": "낑깡",
        "email": "chachacha12@gmail.com",
        "phoneNumber": "010-7320-3333"
    }
}
```

### 4. 로그인 - JWT return
- POST `/api/members/join`
  - body
```
{
    "email" : "chachacha12@gmail.com",
    "password" : "kangkamchan12!"
}
```
- Request
```
http://localhost:8080/api/members/login
```
- Response
```
eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6Indvb2RhZGFAZ21haWwuY29tIiwiaWF0IjoxNjgwMDg2OTEwLCJleHAiOjE2ODAxMjI5MTB9.rsIEJkHyJrp5-9BASudyuMeS1d7492k7pQXLpSA_2Y4
```

### 5. 회원정보 보기
- GET `/api/members/info`
  - parameter
```
{
    - 없습니다.
}
```
- Request
```
http://localhost:8080/api/members/info
```
- Response
```
{
    "memberDto": {
        "id": 1,
        "name": "강감찬",
        "nickName": "낑깡",
        "email": "chachacha12@gmail.com",
        "phoneNumber": "010-7320-3333"
    }
}
```

### 6. 비밀번호 변경 SMS 발송 API
- POST `/api/sms/password`
  - body
```
{
    "phoneNumber" : "010-7320-3333"
}
```
- Request
```
http://localhost:8080/api/sms/password
```
- Response
```
{
    "statusCode": "202",
    "statusName": "success",
    "requestId": "VORSSA-1680087131264-3216-57483993-IDKqpXWH",
    "requestTime": "2023-03-29T19:52:11.264"
}
```


### 7. 비밀번호 변경 SMS 번호 인증 API
- POST `/api/sms/authentication/password`
  - body
```
{
    "phoneNumber" : "010-7320-3333",
    "numberCode" : "836829"
}
```
- Request
```
http://localhost:8080/api/sms/authentication/password
```
- Response
```
{
    "id": 1,
    "phoneNumber": "010-7320-3333",
    "email": null,
    "numberCode": "836829",
    "authenticationYn": "Y",
    "expiredTime": 1680087102818
}
```

### 8. 비밀번호 변경 API
- POST `/api/sms/members/update-password`
  - body
```
{
    "email" : "chachacha12@gmail.com",
    "password" : "testtest11!"
}
```
- Request
```
http://localhost:8080/api/members/update-password
```
- Response
```
{
    "memberDto": {
        "id": 1,
        "name": "강감찬",
        "nickName": "낑깡",
        "email": "chachacha12@gmail.com",
        "phoneNumber": "010-7320-3333"
    }
}
```

