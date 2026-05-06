## ☕ Coffee Order Project

커피 메뉴 조회, 포인트 충전, 주문 및 결제 시스템을 위한 RESTful API 서버입니다.  
다중 서버 인스턴스 환경에서의 **동시성 제어**와 **데이터 정합성** 보장을 최우선으로 설계되었습니다.

---

### 1. 설계 내용

#### 📊 ERD (Entity Relationship Diagram)
*   **User/Point 분리:** 포인트 수정 시 사용자 정보 잠금을 최소화하기 위한 분리 설계.
*   **Point History:** 모든 포인트 변동 내역을 기록하여 데이터 추적성 확보.

<p align="center">
  <img src="docs/images/coffee-order-erd.png" width="80%">
</p>

#### 📑 API 명세서
| 기능 | Method | Endpoint | 설명 |
| :--- | :--- | :--- | :--- |
| 메뉴 조회 | `GET` | `/api/menus` | 전체 커피 메뉴 목록 조회 |
| 포인트 충전 | `PATCH` | `/api/points/charge` | 사용자 포인트 충전 |
| 주문/결제 | `POST` | `/api/orders` | 커피 주문 및 포인트 결제 |
| 인기 메뉴 | `GET` | `/api/menus/popular` | 최근 7일간 인기 메뉴 TOP 3 조회 |

---

#### 1. 커피 메뉴 목록 조회 API
전체 메뉴의 정보를 조회합니다.

*   **Endpoint:** `GET /api/menus`
*   **Success Response (200 OK)**
    ```json
    [
      {
        "menuId": 1,
        "name": "아메리카노",
        "price": 4500
      },
      {
        "menuId": 2,
        "name": "카페라떼",
        "price": 5000
      }
    ]
    ```

---

#### 2. 포인트 충전하기 API
사용자의 포인트를 충전합니다.

*   **Endpoint:** `PATCH /api/points/charge`
*   **Request Body**
    ```json
    {
      "userId": 1,
      "amount": 10000
    }
    ```
*   **Success Response (200 OK)**
    ```json
    {
      "userId": 1,
      "currentBalance": 15000
    }
    ```
*   **Error Cases**
    *   `400 BAD_REQUEST`: 충전 금액이 0원 이하인 경우 (`INVALID_AMOUNT`)
    *   `404 NOT_FOUND`: 존재하지 않는 사용자인 경우 (`USER_NOT_FOUND`)

---

#### 3. 커피 주문 및 결제 API
사용자의 포인트를 차감하여 주문을 완료하고, 외부 데이터 플랫폼으로 전송합니다.

*   **Endpoint:** `POST /api/orders`
*   **Request Body**
    ```json
    {
      "userId": 1,
      "menuId": 1,
      "quantity": 3
    }
    ```
*   **Success Response (201 Created)**
    ```json
    {
      "orderId": 500,
      "userId": 1,
      "menuId": 1,
      "totalPrice": 4500,
      "createdAt": "2026-05-03T20:50:00"
    }
    ```
*   **Error Cases**
    *   `400 BAD_REQUEST`: 잔액이 부족한 경우 (`INSUFFICIENT_BALANCE`)
    *   `404 NOT_FOUND`: 메뉴가 존재하지 않거나 사용자가 없는 경우 (`MENU_NOT_FOUND`, `USER_NOT_FOUND`)
    *   `409 CONFLICT`: 동시 요청으로 인해 결제 처리가 실패한 경우 (`CONCURRENCY_ERROR`)

---

#### 4. 인기 메뉴 목록 조회 API
최근 7일간 주문 횟수가 가장 많은 상위 3개 메뉴를 조회합니다.

*   **Endpoint:** `GET /api/menus/popular`
*   **Success Response (200 OK)**
    
```json
    [
      {
        "menuId": 1,
        "name": "아메리카노",
        "orderCount": 150
      },
      {
        "menuId": 3,
        "name": "돌체라떼",
        "orderCount": 120
      },
      {
        "menuId": 2,
        "name": "카페라떼",
        "orderCount": 95
      }
    ]
```

---

#### ⚠️ 예외 코드 정의 (Error Codes)

| Error Code | Status | Description |
| :--- | :--- | :--- |
| `USER_NOT_FOUND` | 404 | 요청한 사용자를 찾을 수 없음 |
| `MENU_NOT_FOUND` | 404 | 요청한 메뉴를 찾을 수 없음 |
| `INVALID_AMOUNT` | 400 | 충전 금액이 올바르지 않음 |
| `INSUFFICIENT_BALANCE` | 400 | 결제 시 포인트 잔액이 부족함 |
| `CONCURRENCY_ERROR` | 409 | 분산 락 획득 실패 등 동시성 문제 발생 |
| `INTERNAL_SERVER_ERROR`| 500 | 서버 내부 로직 오류 |

---

### 2. 설계의 의도

*   **관심사의 분리 (Separation of Concerns):** 주문(Order), 결제(Point), 통계(Popular Menu) 로직을 서비스 레이어에서 명확히 분리하여 응집도를 높이고 유지보수성을 확보했습니다.
*   **확장성 (Scalability):** 다중 인스턴스 환경에서 데이터 정합성을 유지하기 위해 로컬 자원에 의존하지 않고 Redis 등 외부 분산 환경을 적극 활용했습니다.
*   **비결합도 (Loose Coupling):** 주문 완료 후 데이터 수집 플랫폼으로의 전송 로직을 **비동기 이벤트 기반(Spring Event + Kafka)**으로 처리하여, 외부 시스템의 지연이나 장애가 핵심 비즈니스인 주문 서비스에 영향을 주지 않도록 설계했습니다.

---

### 3. 문제 해결 전략 및 분석

#### 🛡️ 동시성 이슈 해결 (Point Charge & Use)
*   **문제 분석:** 다중 서버 환경에서 동일 사용자가 짧은 시간에 여러 건의 결제/충전을 시도할 경우, Race Condition으로 인해 포인트 정합성이 깨질 위험이 있음.
*   **해결 전략:** **Redisson 분산 락(Distributed Lock)** 채택.
    *   `userId`를 키로 사용하여 특정 사용자에 대한 포인트 작업의 원자성(Atomicity) 보장.
    *   `tryLock` 타임아웃 설정을 통해 무한 대기 현상을 방지하고 시스템 가용성 확보.

#### 📈 인기 메뉴 집계 성능 및 정확성 최적화
*   **문제 분석:** RDBMS에서 최근 7일간의 대량 주문 데이터를 직접 `GROUP BY` 할 경우, 트래픽 증가 시 심각한 조회 성능 저하 유발.
*   **해결 전략:** **Redis Sorted Set (`ZSET`)을 이용한 실시간 집계**.
    *   날짜별(Daily) 키를 생성하여 주문 발생 시마다 `ZINCRBY`로 카운팅.
    *   조회 시점에는 최근 7일치 키를 `ZUNIONSTORE`로 합산하여 상위 3개 추출.

---

### 4. 기술적 선택 이유

| 기술                   | 선택 이유 |
|:---------------------| :--- |
| **Redis (Redisson)** | 분산 락 구현체 중 `Pub/Sub` 방식을 사용하여 스핀 락보다 부하가 적고 안정적임. |
| **Redis Caching**    | 메뉴 목록처럼 변경이 적고 조회가 빈번한 데이터를 JSON 형태로 캐싱하여 DB I/O 절감. |
| **Apache Kafka**     | 높은 처리량(Throughput)을 바탕으로 주문 이벤트를 안전하게 전달하며 시스템 간 결합도를 최소화. |

---

### 5. 테스트 케이스 및 검증
*   **동시성 테스트:** `ExecutorService`와 `CountDownLatch`를 활용해 100명의 사용자가 동시에 결제 시도 시 최종 잔액 일치 여부 검증.
*   **통합 테스트:** 주문 -> 포인트 차감 -> 주문 기록 저장 -> Kafka 메시지 발행으로 이어지는 전체 파이프라인 검증.
*   **단위 테스트:** 각 도메인 엔티티(`Point`, `Menu`)의 비즈니스 메서드(충전, 사용 등) 독립 검증.

---