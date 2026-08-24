# Patient Service API 명세서

## 1. 연동 정보

이 문서는 다른 서비스와 프론트엔드가 Patient Service를 호출할 때 사용하는 현재 REST 계약을 설명한다.

| 항목 | 값 |
| --- | --- |
| 서비스명 | `patient-service` |
| 로컬 주소 | `http://localhost:8080` |
| 배포 주소 | 환경별 Patient Service 주소 사용 |
| API Prefix | `/api/patient` |
| Content-Type | `application/json` |
| 인증 | 현재 소스에 별도 인증 설정 없음 |
| 명세 기준 | `PatientController`, 요청·응답 DTO, `PatientServiceImpl`, `GlobalExceptionHandler` |

> `patientId`는 모든 API에서 숫자가 아닌 UUID 문자열이다. 예: `550e8400-e29b-41d4-a716-446655440000`

## 2. 공통 계약

### 2.1 성공 응답

HTTP 상태는 `200 OK`이며 본문은 다음 형식이다.

```json
{
  "code": 200,
  "message": "SUCCESS",
  "data": {}
}
```

### 2.2 오류 응답

```json
{
  "code": 404,
  "message": "환자 정보를 찾을 수 없습니다.",
  "data": null
}
```

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `code` | integer | HTTP 상태 코드와 같은 값 |
| `message` | string | 처리 결과 또는 오류 메시지 |
| `data` | object, array, boolean, null | 성공 데이터. 오류 시 `null` |

### 2.3 공통 데이터 형식

| 항목 | 형식/값 | 설명 |
| --- | --- | --- |
| UUID | `xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx` | 환자 식별자 |
| 날짜 | `yyyy-MM-dd` | 예: `2000-08-13` |
| 일시 | ISO-8601 LocalDateTime | 예: `2026-08-14T10:30:00` (시간대 오프셋 없음) |
| `statusCd` | `ACTIVE`, `INACTIVE` | 활성, 비활성 |
| `genderCd` | `01`, `02`, `03`, `04` | 남성, 여성, 미상, 기타 |
| Y/N 값 | `Y`, `N` | 여부 코드 |

일시 값에는 `Z`나 `+09:00` 같은 시간대 정보가 포함되지 않는다. 애플리케이션과 DB의 기준 시간대는 `Asia/Seoul`이다.

### 2.4 환자 상세 데이터

상세 조회 및 수정 API는 다음 구조를 `data`로 반환한다.

```json
{
  "patientId": "550e8400-e29b-41d4-a716-446655440000",
  "patientName": "홍길동",
  "residentRegNo": "000813-3******",
  "birthDate": "2000-08-13",
  "genderCd": "01",
  "statusCd": "ACTIVE",
  "tempPatientYn": "N",
  "deathYn": "N",
  "deathDtm": null,
  "createdAt": "2026-08-14T10:30:00",
  "updatedAt": "2026-08-14T10:30:00"
}
```

| 필드 | 타입 | Nullable | 설명 |
| --- | --- | --- | --- |
| `patientId` | string(UUID) | N | 환자 ID |
| `patientName` | string | N | 환자명 |
| `residentRegNo` | string | N | `YYMMDD-G******` 형식으로 마스킹된 주민등록번호 |
| `birthDate` | string(date) | N | 생년월일 |
| `genderCd` | string | N | 성별 코드 |
| `statusCd` | string | N | 환자 상태 코드 |
| `tempPatientYn` | string | N | 임시환자 여부 |
| `deathYn` | string | N | 사망 여부 |
| `deathDtm` | string(datetime) | Y | 사망일시. 사망 정보가 없으면 `null` |
| `createdAt` | string(datetime) | N | 등록일시 |
| `updatedAt` | string(datetime) | N | 최종 수정일시 |

## 3. API 요약

| Method | Endpoint | 용도 |
| --- | --- | --- |
| `POST` | `/api/patient/register` | 환자 등록 |
| `GET` | `/api/patient/list` | 환자 검색 및 목록 조회 |
| `POST` | `/api/patient/duplicate-check` | 주민등록번호 중복 확인 |
| `GET` | `/api/patient/{patientId}` | 환자 상세 조회 |
| `PATCH` | `/api/patient/{patientId}` | 환자명 수정 |
| `PATCH` | `/api/patient/{patientId}/death-status` | 사망 정보 수정 |
| `PATCH` | `/api/patient/{patientId}/deactivate` | 환자 비활성화 |
| `GET` | `/api/patient/{patientId}/validation` | 활성 환자 유효성 확인 |

## 4. 환자 등록

### `POST /api/patient/register`

### Request Body

| 필드 | 타입 | 필수 | 제약 조건 |
| --- | --- | --- | --- |
| `patientName` | string | Y | 공백 제외 2~100자 |
| `birthDate` | string(date) | Y | 오늘 또는 과거 날짜 |
| `residentRegNo` | string | Y | 하이픈 없는 숫자 13자리 |
| `genderCd` | string | Y | `01`, `02`, `03`, `04` |
| `statusCd` | string | Y | `ACTIVE`, `INACTIVE` |
| `tempPatientYn` | string | N | `Y`, `N`; 생략 시 `N` |

```json
{
  "patientName": "홍길동",
  "birthDate": "2000-08-13",
  "residentRegNo": "0008133123456",
  "genderCd": "01",
  "statusCd": "ACTIVE",
  "tempPatientYn": "N"
}
```

주민등록번호에서 계산한 생년월일과 `birthDate`가 일치해야 한다. 주민등록번호 일곱 번째 숫자가 `1`, `2`, `5`, `6`이면 1900년대, `3`, `4`, `7`, `8`이면 2000년대로 판정한다.

### Response — `200 OK`

```json
{
  "code": 200,
  "message": "SUCCESS",
  "data": {
    "patientId": "550e8400-e29b-41d4-a716-446655440000",
    "patientName": "홍길동",
    "birthDate": "2000-08-13",
    "genderCd": "01",
    "statusCd": "ACTIVE",
    "tempPatientYn": "N",
    "createdAt": "2026-08-14T10:30:00"
  }
}
```

### 주요 오류

| HTTP | 조건 | 메시지 |
| --- | --- | --- |
| `400` | DTO 필드 검증 실패 | 해당 필드의 검증 메시지 |
| `400` | 주민등록번호 형식·날짜·구분 숫자 오류 | `올바른 주민등록번호 형식이 아닙니다.` |
| `400` | 주민등록번호와 생년월일 불일치 | `주민등록번호와 생년월일이 일치하지 않습니다.` |
| `400` | JSON 형식 또는 Enum 값 오류 | `요청 데이터 형식이 올바르지 않습니다.` |
| `409` | 주민등록번호 중복 | `이미 등록된 주민등록번호입니다.` |

## 5. 환자 검색 및 목록 조회

### `GET /api/patient/list`

모든 검색 조건은 선택 사항이며, 여러 조건을 전달하면 AND 조건으로 검색한다. 결과는 `createdAt` 내림차순이다. 현재 페이징은 지원하지 않는다.

### Query Parameters

| 이름 | 타입 | 필수 | 검색 방식 |
| --- | --- | --- | --- |
| `patientName` | string | N | 앞뒤 공백 제거 후 부분 일치(`LIKE %값%`); 빈 문자열은 미지정 처리 |
| `birthDate` | string(date) | N | 완전 일치 |
| `statusCd` | string | N | `ACTIVE`, `INACTIVE` 완전 일치 |

호출 예:

```http
GET /api/patient/list?patientName=홍&birthDate=2000-08-13&statusCd=ACTIVE
```

### Response — `200 OK`

```json
{
  "code": 200,
  "message": "SUCCESS",
  "data": [
    {
      "patientId": "550e8400-e29b-41d4-a716-446655440000",
      "patientName": "홍길동",
      "residentRegNo": "000813-3******",
      "birthDate": "2000-08-13",
      "genderCd": "01",
      "statusCd": "ACTIVE",
      "tempPatientYn": "N",
      "deathYn": "N",
      "createdAt": "2026-08-14T10:30:00",
      "updatedAt": "2026-08-14T10:30:00"
    }
  ]
}
```

조회 결과가 없으면 `data`는 `[]`이다. 목록 응답에는 `deathDtm`이 포함되지 않으므로 필요하면 상세 조회 API를 사용한다.

| HTTP | 조건 | 메시지 |
| --- | --- | --- |
| `400` | 날짜 또는 상태 코드 변환 실패 | `요청값이 올바르지 않습니다. 파라미터: {name}, 입력값: {value}` |

## 6. 주민등록번호 중복 확인

### `POST /api/patient/duplicate-check`

### Request Body

```json
{
  "residentRegNo": "0008133123456"
}
```

`residentRegNo`는 하이픈 없는 숫자 13자리여야 하며 유효한 생년월일과 주민등록번호 구분 숫자를 포함해야 한다.

### Response — `200 OK`

```json
{
  "code": 200,
  "message": "SUCCESS",
  "data": true
}
```

| `data` | 의미 |
| --- | --- |
| `true` | 동일 주민등록번호가 이미 등록됨 |
| `false` | 동일 주민등록번호가 등록되지 않음 |

이 API의 `true`는 사용 가능하다는 뜻이 아니라 **중복됨**을 뜻한다.

## 7. 환자 상세 조회

### `GET /api/patient/{patientId}`

| Path Variable | 타입 | 설명 |
| --- | --- | --- |
| `patientId` | string(UUID) | 조회할 환자 ID |

### Response — `200 OK`

`data`는 [환자 상세 데이터](#24-환자-상세-데이터) 구조다.

### 주요 오류

| HTTP | 조건 | 메시지 |
| --- | --- | --- |
| `400` | UUID 형식 오류 | `요청값이 올바르지 않습니다. 파라미터: patientId, 입력값: {value}` |
| `404` | 환자 미존재 | `환자 정보를 찾을 수 없습니다.` |

## 8. 환자명 수정

### `PATCH /api/patient/{patientId}`

환자명만 수정한다. 다른 환자 정보는 이 API로 변경할 수 없다.

### Request Body

```json
{
  "patientName": "홍길순"
}
```

| 필드 | 타입 | 필수 | 제약 조건 |
| --- | --- | --- | --- |
| `patientName` | string | Y | 공백 제외 2~100자; 저장 전 앞뒤 공백 제거 |

### Response — `200 OK`

수정된 [환자 상세 데이터](#24-환자-상세-데이터)를 반환한다.

### 주요 오류

| HTTP | 조건 | 메시지 |
| --- | --- | --- |
| `400` | 이름 검증 실패 또는 UUID 형식 오류 | 검증/형식 오류 메시지 |
| `404` | 환자 미존재 | `환자 정보를 찾을 수 없습니다.` |

## 9. 사망 정보 수정

### `PATCH /api/patient/{patientId}/death-status`

### Request Body

사망 처리:

```json
{
  "deathYn": "Y",
  "deathDtm": "2026-08-14T09:30:00"
}
```

사망 정보 해제:

```json
{
  "deathYn": "N",
  "deathDtm": null
}
```

| 필드 | 타입 | 필수 | 제약 조건 |
| --- | --- | --- | --- |
| `deathYn` | string | Y | `Y` 또는 `N` |
| `deathDtm` | string(datetime) | 조건부 | `deathYn=Y`이면 필수이며 현재 시각 이하여야 함 |

- `deathYn=Y`: 요청한 `deathDtm`을 저장한다.
- `deathYn=N`: 요청의 `deathDtm` 값과 관계없이 저장된 사망일시를 `null`로 초기화한다.
- 이 API는 `statusCd`를 자동으로 `INACTIVE`로 변경하지 않는다.

### Response — `200 OK`

수정된 [환자 상세 데이터](#24-환자-상세-데이터)를 반환한다.

### 주요 오류

| HTTP | 조건 | 메시지 |
| --- | --- | --- |
| `400` | `deathYn` 누락 또는 허용되지 않은 값 | DTO 검증 메시지 |
| `400` | `deathYn=Y`인데 `deathDtm` 누락 | `사망 환자는 사망일시를 입력해야 합니다.` |
| `400` | 사망일시가 현재 시각보다 미래 | `사망일시는 현재 시각보다 이후일 수 없습니다.` |
| `404` | 환자 미존재 | `환자 정보를 찾을 수 없습니다.` |

## 10. 환자 비활성화

### `PATCH /api/patient/{patientId}/deactivate`

요청 본문 없이 환자의 `statusCd`를 `INACTIVE`로 변경한다.

```http
PATCH /api/patient/550e8400-e29b-41d4-a716-446655440000/deactivate
```

이미 `INACTIVE`인 환자에게 다시 요청해도 오류 없이 현재 상세 정보를 반환하므로 멱등적으로 사용할 수 있다. 이 API는 사망 여부를 변경하지 않는다.

### Response — `200 OK`

비활성화된 [환자 상세 데이터](#24-환자-상세-데이터)를 반환한다.

### 주요 오류

| HTTP | 조건 | 메시지 |
| --- | --- | --- |
| `400` | UUID 형식 오류 | 요청값 형식 오류 메시지 |
| `404` | 환자 미존재 | `환자 정보를 찾을 수 없습니다.` |

## 11. 활성 환자 유효성 확인

### `GET /api/patient/{patientId}/validation`

다른 서비스가 참조한 환자를 업무 처리에 사용할 수 있는지 확인할 때 사용하는 API다.

### Response — `200 OK`

```json
{
  "code": 200,
  "message": "SUCCESS",
  "data": {
    "patientId": "550e8400-e29b-41d4-a716-446655440000",
    "valid": true
  }
}
```

| `valid` | 의미 |
| --- | --- |
| `true` | 해당 UUID의 환자가 존재하고 `statusCd=ACTIVE` |
| `false` | 환자가 존재하지 않거나 `statusCd=INACTIVE` |

- 환자가 없어도 `404`가 아니라 `200 OK`, `valid=false`를 반환한다.
- 현재 판정 기준은 `statusCd`뿐이다. `deathYn`, `tempPatientYn`은 판정에 사용하지 않는다.
- UUID 형식 자체가 잘못되면 `400 Bad Request`다.

## 12. 오류 코드 요약

| HTTP | 발생 상황 | 대표 메시지 |
| --- | --- | --- |
| `400 Bad Request` | DTO 검증 실패 | 필드별 검증 메시지 |
| `400 Bad Request` | UUID, 날짜, Enum 등 타입 변환 실패 | `요청값이 올바르지 않습니다. 파라미터: ..., 입력값: ...` |
| `400 Bad Request` | 잘못된 JSON | `요청 데이터 형식이 올바르지 않습니다.` |
| `400 Bad Request` | 잘못된 주민등록번호 | `올바른 주민등록번호 형식이 아닙니다.` |
| `400 Bad Request` | 생년월일 불일치 | `주민등록번호와 생년월일이 일치하지 않습니다.` |
| `400 Bad Request` | 사망일시 누락/미래 시각 | 사망일시 관련 메시지 |
| `404 Not Found` | 상세·수정 대상 환자 미존재 | `환자 정보를 찾을 수 없습니다.` |
| `404 Not Found` | 매핑되지 않은 URL | `요청한 경로를 찾을 수 없습니다.` |
| `409 Conflict` | 주민등록번호 중복 | `이미 등록된 주민등록번호입니다.` |
| `500 Internal Server Error` | 처리되지 않은 오류 | `서버 오류가 발생했습니다.` |

## 13. 다른 서비스 연동 권장 방식

환자의 존재 여부와 활성 상태만 필요하면 상세 조회 대신 다음 API를 사용한다.

```http
GET /api/patient/{patientId}/validation
```

환자명, 생년월일 등 표시 정보가 필요하면 다음 API를 사용한다.

```http
GET /api/patient/{patientId}
```

다른 서비스의 DB에 주민등록번호 원문을 복제하지 않는다. 상세·목록 응답의 주민등록번호도 이미 마스킹되어 있다.

## 14. Swagger

애플리케이션 실행 후 확인할 수 있다.

- Swagger UI: `http://{host}:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://{host}:8080/v3/api-docs`
