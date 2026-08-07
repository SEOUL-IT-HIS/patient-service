# Patient Service API 명세서

## 1. 기본 정보

| 항목 | 내용 |
| --- | --- |
| 서비스 | Patient Service |
| Base URL | `http://{host}:8080` |
| API Prefix | `/api/patient` |
| Content-Type | `application/json` |
| 작성 기준 | 현재 `PatientController`, DTO, Service 및 예외 처리 구현 |

## 2. 공통 응답

모든 API는 다음 공통 형식으로 응답한다.

```json
{
  "code": 200,
  "message": "SUCCESS",
  "data": {}
}
```

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `code` | number | HTTP 상태 코드 |
| `message` | string | 처리 결과 메시지 |
| `data` | object, array, boolean, null | 성공 데이터. 오류 시 `null` |

- 날짜 형식: `yyyy-MM-dd`
- 일시 형식: ISO-8601 (`yyyy-MM-dd'T'HH:mm:ss[.SSS...]`)
- 환자 상태 코드: `ACTIVE`, `INACTIVE`

## 3. API 목록

| Method | URL | 설명 |
| --- | --- | --- |
| `POST` | `/api/patient/register` | 환자 등록 |
| `GET` | `/api/patient/list` | 전체 환자 목록 조회 |
| `POST` | `/api/patient/duplicate-check` | 주민등록번호 중복 확인 |
| `GET` | `/api/patient/{patientId}` | 환자 상세 조회 |
| `GET` | `/api/patient/{patientId}/validation` | 활성 환자 유효성 확인 |

---

## 4. 환자 등록

### `POST /api/patient/register`

신규 환자를 등록한다.

### 요청 본문

| 필드 | 타입 | 필수 | 제약 조건 |
| --- | --- | --- | --- |
| `patientName` | string | Y | 2~100자, 공백만 입력 불가 |
| `birthDate` | string | Y | `yyyy-MM-dd`, 오늘 또는 과거 날짜 |
| `residentRegNo` | string | Y | 하이픈 없는 숫자 13자리 |
| `genderCd` | string | Y | `01`, `02`, `03`, `04` 중 하나 |
| `statusCd` | string | Y | `ACTIVE`, `INACTIVE` 중 하나 |

```json
{
  "patientName": "홍길동",
  "birthDate": "2000-08-13",
  "residentRegNo": "0008133123456",
  "genderCd": "01",
  "statusCd": "ACTIVE"
}
```

주민등록번호에서 계산한 생년월일과 요청의 `birthDate`가 일치해야 한다. 주민등록번호의 일곱 번째 숫자는 `1`, `2`, `5`, `6`일 때 1900년대, `3`, `4`, `7`, `8`일 때 2000년대로 판정한다.

### 성공 응답 — `200 OK`

```json
{
  "code": 200,
  "message": "SUCCESS",
  "data": {
    "patientId": 101,
    "patientName": "홍길동",
    "birthDate": "2000-08-13",
    "genderCd": "01",
    "statusCd": "ACTIVE",
    "createdAt": "2026-08-07T10:30:00"
  }
}
```

### 오류 응답

| HTTP | 발생 조건 | 메시지 |
| --- | --- | --- |
| `400` | 필수값 누락 또는 필드 검증 실패 | 첫 번째 필드 검증 메시지 |
| `400` | 주민등록번호 형식, 날짜 또는 구분 코드가 유효하지 않음 | `올바른 주민등록번호 형식이 아닙니다.` |
| `400` | 주민등록번호와 생년월일 불일치 | `주민등록번호와 생년월일이 일치하지 않습니다.` |
| `400` | JSON 또는 Enum 형식 오류 | `요청 데이터 형식이 올바르지 않습니다.` |
| `409` | 주민등록번호 중복 | `이미 등록된 주민등록번호입니다.` |

---

## 5. 전체 환자 목록 조회

### `GET /api/patient/list`

전체 환자를 `patientId` 내림차순으로 조회한다. 현재 검색 조건과 페이징은 지원하지 않는다.

### 성공 응답 — `200 OK`

```json
{
  "code": 200,
  "message": "SUCCESS",
  "data": [
    {
      "patientId": 101,
      "patientName": "홍길동",
      "residentRegNo": "000813-3******",
      "birthDate": "2000-08-13",
      "genderCd": "01",
      "statusCd": "ACTIVE",
      "createdAt": "2026-08-07T10:30:00",
      "updatedAt": "2026-08-07T10:30:00"
    }
  ]
}
```

등록된 환자가 없으면 `data`는 빈 배열(`[]`)이다.

| `data[]` 필드 | 타입 | 설명 |
| --- | --- | --- |
| `patientId` | number | 환자 ID |
| `patientName` | string | 환자명 |
| `residentRegNo` | string | 마스킹된 주민등록번호(`YYMMDD-G******`) |
| `birthDate` | string | 생년월일 |
| `genderCd` | string | 성별 코드 |
| `statusCd` | string | 환자 상태 코드 |
| `createdAt` | string | 등록 일시 |
| `updatedAt` | string | 최종 수정 일시 |

---

## 6. 주민등록번호 중복 확인

### `POST /api/patient/duplicate-check`

주민등록번호의 유효성을 검사한 뒤 등록 여부를 반환한다.

### 요청 본문

| 필드 | 타입 | 필수 | 제약 조건 |
| --- | --- | --- | --- |
| `residentRegNo` | string | Y | 하이픈 없는 숫자 13자리 |

```json
{
  "residentRegNo": "0008133123456"
}
```

### 성공 응답 — `200 OK`

```json
{
  "code": 200,
  "message": "SUCCESS",
  "data": true
}
```

| `data` | 의미 |
| --- | --- |
| `true` | 이미 등록된 주민등록번호 |
| `false` | 등록되지 않은 주민등록번호 |

### 오류 응답

| HTTP | 발생 조건 | 메시지 |
| --- | --- | --- |
| `400` | 필수값 누락 또는 숫자 13자리 제약 위반 | 해당 필드 검증 메시지 |
| `400` | 주민등록번호의 날짜 또는 구분 코드가 유효하지 않음 | `올바른 주민등록번호 형식이 아닙니다.` |
| `400` | JSON 형식 오류 | `요청 데이터 형식이 올바르지 않습니다.` |

---

## 7. 환자 상세 조회

### `GET /api/patient/{patientId}`

### 경로 변수

| 변수 | 타입 | 필수 | 제약 조건 |
| --- | --- | --- | --- |
| `patientId` | number | Y | 양의 정수 |

### 성공 응답 — `200 OK`

```json
{
  "code": 200,
  "message": "SUCCESS",
  "data": {
    "patientId": 101,
    "patientName": "홍길동",
    "residentRegNo": "000813-3******",
    "birthDate": "2000-08-13",
    "genderCd": "01",
    "statusCd": "ACTIVE",
    "createdAt": "2026-08-07T10:30:00",
    "updatedAt": "2026-08-07T10:30:00"
  }
}
```

### 오류 응답

| HTTP | 발생 조건 | 메시지 |
| --- | --- | --- |
| `400` | `patientId`가 양의 정수가 아님 | `입력값이 올바르지 않습니다.` |
| `404` | 해당 환자가 존재하지 않음 | `환자 정보를 찾을 수 없습니다.` |

---

## 8. 활성 환자 유효성 확인

### `GET /api/patient/{patientId}/validation`

해당 ID의 환자가 존재하며 상태가 `ACTIVE`인지 확인한다.

### 경로 변수

| 변수 | 타입 | 필수 | 제약 조건 |
| --- | --- | --- | --- |
| `patientId` | number | Y | 양의 정수 |

### 성공 응답 — `200 OK`

```json
{
  "code": 200,
  "message": "SUCCESS",
  "data": {
    "patientId": 101,
    "valid": true
  }
}
```

| `data` 필드 | 타입 | 설명 |
| --- | --- | --- |
| `patientId` | number | 요청한 환자 ID |
| `valid` | boolean | 환자가 존재하고 `ACTIVE`이면 `true`; 미존재 또는 `INACTIVE`이면 `false` |

존재하지 않는 환자도 `404`가 아니라 `200 OK`와 `valid: false`로 반환한다.

### 오류 응답

| HTTP | 발생 조건 | 메시지 |
| --- | --- | --- |
| `400` | `patientId`가 양의 정수가 아님 | `입력값이 올바르지 않습니다.` |

---

## 9. 공통 오류 응답

```json
{
  "code": 404,
  "message": "환자 정보를 찾을 수 없습니다.",
  "data": null
}
```

| HTTP | 설명 |
| --- | --- |
| `400 Bad Request` | 요청값 검증 또는 요청 형식 실패 |
| `404 Not Found` | 환자 상세 조회 대상 없음 |
| `409 Conflict` | 주민등록번호 중복 |
| `500 Internal Server Error` | 처리되지 않은 서버 오류 |

## 10. Swagger

애플리케이션 실행 후 다음 경로에서 확인할 수 있다.

- Swagger UI: `http://{host}:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://{host}:8080/v3/api-docs`
