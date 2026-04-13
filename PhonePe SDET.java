# 🚀 PhonePe SDET Interview Experience — Questions & Answers

> **Role:** SDET (Software Development Engineer in Test)  
> **Application Channel:** LinkedIn  
> **Process Timeline:** Started within a couple of weeks of applying

---

## 🔴 Round 1: Coding + Problem Solving

---

### Q1. Group transactions by user and return the top N highest spenders

**Problem Statement:**  
Given a list of transactions (each with a userId and amount), group them by user, calculate the total spending per user, and return the top N highest spenders.

**Approach:**
- Use a `HashMap` to accumulate totals per user.
- Use a `PriorityQueue` (max-heap) or sort to extract top N.
- Handle edge cases: empty list, N > number of users, negative amounts.

```java
import java.util.*;

public class TopSpenders {

    public static List<String> getTopNSpenders(List<int[]> transactions, int n) {
        // Edge case: empty input
        if (transactions == null || transactions.isEmpty() || n <= 0) {
            return Collections.emptyList();
        }

        // Step 1: Group by userId → sum amounts
        Map<Integer, Long> spendingMap = new HashMap<>();
        for (int[] txn : transactions) {
            int userId = txn[0];
            int amount = txn[1];
            spendingMap.merge(userId, (long) amount, Long::sum);
        }

        // Step 2: Sort by total spending descending
        List<Map.Entry<Integer, Long>> entries = new ArrayList<>(spendingMap.entrySet());
        entries.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

        // Step 3: Return top N
        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(n, entries.size()); i++) {
            result.add("User " + entries.get(i).getKey() + ": ₹" + entries.get(i).getValue());
        }
        return result;
    }

    public static void main(String[] args) {
        List<int[]> transactions = Arrays.asList(
            new int[]{1, 500}, new int[]{2, 300}, new int[]{1, 700},
            new int[]{3, 1000}, new int[]{2, 200}
        );
        System.out.println(getTopNSpenders(transactions, 2));
        // Output: [User 3: ₹1000, User 1: ₹1200]
    }
}
```

**Edge Cases Handled:**
- Null or empty transaction list
- N larger than number of unique users
- Duplicate transactions for same user (accumulated correctly)
- Negative/zero amounts (depends on business rules — clarify in interview)

**Time Complexity:** O(T + U log U) — T = transactions, U = unique users  
**Space Complexity:** O(U)

---

### Q2. Merge Overlapping Intervals

**Problem Statement:**  
Given a list of intervals `[start, end]`, merge all overlapping intervals and return the result.

**Approach:**
- Sort intervals by start time.
- Iterate and merge when current interval overlaps with previous.
- Edge cases: empty input, single interval, all overlapping, no overlapping.

```java
import java.util.*;

public class MergeIntervals {

    public static int[][] merge(int[][] intervals) {
        // Edge case
        if (intervals == null || intervals.length == 0) return new int[0][0];

        // Step 1: Sort by start time
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

        List<int[]> merged = new ArrayList<>();
        int[] current = intervals[0];

        for (int i = 1; i < intervals.length; i++) {
            int[] next = intervals[i];

            if (next[0] <= current[1]) {
                // Overlapping — extend the end if needed
                current[1] = Math.max(current[1], next[1]);
            } else {
                // No overlap — push current and move to next
                merged.add(current);
                current = next;
            }
        }
        merged.add(current); // Add the last interval

        return merged.toArray(new int[merged.size()][]);
    }

    public static void main(String[] args) {
        int[][] intervals = {{1,3},{2,6},{8,10},{15,18}};
        int[][] result = merge(intervals);
        for (int[] r : result) {
            System.out.println(Arrays.toString(r)); // [1,6], [8,10], [15,18]
        }
    }
}
```

**Edge Cases:**
| Case | Input | Expected Output |
|------|-------|-----------------|
| Empty | `[]` | `[]` |
| Single interval | `[[1,5]]` | `[[1,5]]` |
| All overlapping | `[[1,4],[2,5],[3,6]]` | `[[1,6]]` |
| No overlap | `[[1,2],[3,4]]` | `[[1,2],[3,4]]` |
| Touching intervals | `[[1,3],[3,5]]` | `[[1,5]]` |
| Contained interval | `[[1,10],[2,5]]` | `[[1,10]]` |

**Time Complexity:** O(n log n) for sorting  
**Space Complexity:** O(n)

---

## 🔴 Round 2: SDET Fundamentals + Automation

---

### Q3. What tests should run on PR vs Nightly vs Release?

**Answer:**

| Pipeline Stage | Test Types | Rationale |
|---------------|------------|-----------|
| **PR (Pull Request)** | Unit tests, fast integration tests, lint/static analysis | Must be fast (<10 min). Block bad code early. |
| **Nightly** | Full regression suite, slow integration tests, DB tests, API contract tests | Runs on full codebase overnight without blocking devs. |
| **Release** | End-to-end tests, performance tests, security scans, smoke tests in staging | Full confidence before production. |

**Detailed Breakdown:**

**PR Stage (Fast Feedback — under 10 minutes):**
- Unit tests (all modules)
- Component/integration tests for changed modules
- Code coverage checks
- Static analysis (SpotBugs, SonarQube)
- Code style/lint checks

**Nightly Stage (Thorough — run at night):**
- Full regression suite
- Cross-service integration tests
- Database migration tests
- API contract/compatibility tests (Pact)
- Data consistency checks

**Release Stage (Release Gate):**
- End-to-end flows (happy path + critical failures)
- Performance & load tests
- Security vulnerability scans (OWASP)
- Smoke tests in production-like staging
- Rollback verification

---

### Q4. How do you handle flaky tests?

**Answer:**

Flaky tests are tests that pass and fail intermittently without code changes. Here's a structured approach:

**Step 1 — Detect**
- Track pass/fail rate per test over time using CI dashboards.
- Flag tests that fail >5% of runs without a code change.

**Step 2 — Triage**
- Is it a test issue (bad assertion, timing, shared state) or a real product bug (race condition, network instability)?

**Step 3 — Quarantine**
- Move flaky tests to a separate "quarantine" suite so they don't block CI.
- Still run them but don't fail the build.

**Step 4 — Fix Root Cause**
Common causes and fixes:
| Root Cause | Fix |
|------------|-----|
| Timing/async issues | Explicit waits instead of `Thread.sleep()` |
| Shared test data | Isolated test data per test run |
- | Test order dependency | Make tests independent (setup/teardown) |
| External service instability | Mock/stub external dependencies |
| Race conditions | Synchronization or retry with backoff |

**Step 5 — Retry as a Band-Aid**
- Allow 1-2 retries for truly non-deterministic scenarios (network calls).
- Never use retries to hide real bugs — track retry rates.

---

### Q5. How do you reduce pipeline execution time without losing coverage?

**Answer:**

1. **Test Parallelization** — Run tests in parallel using TestNG parallel groups or JUnit's parallel execution. Split test suites across multiple agents.

2. **Test Prioritization** — Run high-risk, recently changed, and historically failing tests first. Skip or defer low-risk stable tests.

3. **Smart Test Selection** — Use code coverage tools to run only tests impacted by changed files (e.g., Diffblue, Bazel's affected targets).

4. **Caching** — Cache compiled artifacts, dependencies, and Docker layers between runs.

5. **Fail Fast** — Run fastest tests (unit) first. Abort early if unit tests fail.

6. **Test Sharding** — Split large test classes across multiple nodes.

7. **Mock External Dependencies** — Replace slow real API calls/DB writes with mocks in unit/integration tests.

8. **Separate Suites** — Don't run performance/load tests on every PR — move them to nightly.

---

### Q6. How do you identify a test issue vs a product bug?

**Answer:**

| Signal | Test Issue | Product Bug |
|--------|-----------|-------------|
| Reproducibility | Intermittent | Consistently reproducible |
| Other tests | Only this test fails | Multiple related tests fail |
| Manual check | Works manually | Fails manually too |
| Recent changes | Test code changed recently | Application code changed recently |
| Environment | Fails only in CI | Fails everywhere |

**Diagnostic Steps:**
1. Run the test in isolation — does it still fail?
2. Run it manually against the same environment — does the feature work?
3. Check test setup/teardown for data pollution.
4. Review recent commits — was the test or application code modified?
5. Check logs and error messages — is it an assertion error (test issue) or an unexpected exception (product bug)?

---

### Q7. What if the pipeline is frequently failing?

**Answer:**

Treat a broken pipeline as a P1 incident. Structured response:

1. **Immediate:** Identify if it's one test or many. Revert recent changes if a bad merge caused it.
2. **Quarantine:** Isolate flaky/broken tests so the pipeline is unblocked for the rest of the team.
3. **Root Cause Analysis:**
   - Environment issue? (infra, credentials, network)
   - Code change causing regression?
   - Test data issue?
   - Dependency version conflict?
4. **Fix and Monitor:** Fix the root cause, monitor for 3-5 runs before declaring it resolved.
5. **Prevention:** Add alerts for pipeline health trends, review test quality in code reviews.

---

### Q8. How do you manage test data in CI?

**Answer:**

**Strategies:**

1. **Test Data Factory** — Generate fresh, isolated test data per test run programmatically (factories/builders). Never rely on pre-existing DB state.

2. **In-memory Databases** — Use H2 or embedded DBs for unit/integration tests. Fast and isolated.

3. **Data Teardown** — Always clean up created data in `@AfterEach` / `@AfterTest`. Use transactions that roll back.

4. **Environment-Specific Configs** — Maintain separate test data configs per environment (dev/staging/prod). Never point CI at production data.

5. **Test Containers** — Use Docker-based test containers (Testcontainers library) to spin up real DB instances per test session.

6. **Data Masking** — If using production data snapshots, mask PII (user names, phone numbers, UPI IDs).

7. **Idempotent Data** — Design tests so re-running them doesn't cause duplicate/inconsistent data.

---

## 🔴 Round 3: System Design + Test Strategy (Payment System)

---

### Q9. How do you test a payment system (UPI/wallet) end to end?

**Answer:**

**Test Layers:**

```
UI Layer          →  User initiates payment on app
API Layer         →  Payment API called, request validated
Service Layer     →  Payment orchestration, routing
DB Layer          →  Transaction recorded atomically
Queue Layer       →  Events published to Kafka/SQS
Notification      →  SMS/Push sent to user
Reconciliation    →  Ledger updated, settlements correct
```

**Critical Test Scenarios:**

| Category | Scenarios |
|----------|-----------|
| **Happy Path** | Successful debit + credit, correct balance update, receipt generated |
| **Failures** | Network timeout, bank down, insufficient funds, invalid UPI ID |
| **Retries** | Auto-retry on timeout, idempotency (no double charge) |
| **Partial Failure** | Debit succeeds but credit fails → auto-refund flow |
| **Concurrency** | Two payments initiated simultaneously by same user |
| **Limits** | Transaction limits (per day/per transaction), wallet balance edge |
| **Security** | Auth token expiry, replay attack prevention |

---

### Q10. What if payment fails after deduction from user's account?

**Answer:**

This is the most critical failure scenario in payment systems.

**What Should Happen (System Design):**
1. Debit is recorded in DB with status `DEBIT_SUCCESS_CREDIT_PENDING`.
2. Credit to payee fails (network/bank issue).
3. System detects the inconsistency.
4. Auto-refund is triggered — debit is reversed.
5. Transaction is marked `FAILED` or `REFUNDED`.
6. User is notified of refund within SLA (typically 3-5 business days).

**How to Test This:**
```
Test: Payment debit succeeds, credit fails → refund triggered

Steps:
1. Mock the credit API to return a failure (500 error or timeout)
2. Initiate a payment
3. Verify: debit is made (check DB / wallet balance)
4. Verify: credit failure is logged
5. Verify: refund is initiated automatically (check refund service)
6. Verify: user's balance is restored
7. Verify: transaction state = REFUNDED
8. Verify: user notification is sent
```

**Tools:** Mock servers (WireMock), DB state assertions, queue message validation.

---

### Q11. How do you test retry mechanisms?

**Answer:**

**What to Test:**

1. **Retry is triggered** — Service retries on transient failures (5xx, network timeout). Does NOT retry on permanent failures (4xx like invalid UPI ID).

2. **Retry count** — Does it retry exactly N times (e.g., 3)?

3. **Backoff** — Is there exponential backoff between retries to avoid thundering herd?

4. **Idempotency** — Does retrying the same payment multiple times result in only ONE charge?

5. **Final failure** — After max retries, is the transaction correctly marked FAILED and user notified?

**Test Implementation:**

```java
@Test
public void testPaymentRetryOnTimeout() {
    // Arrange: mock payment gateway to fail 2 times, succeed on 3rd
    wireMockServer.stubFor(post("/api/payment")
        .inScenario("Retry Scenario")
        .whenScenarioStateIs(STARTED)
        .willReturn(aResponse().withStatus(503))
        .willSetStateTo("FIRST_RETRY"));

    wireMockServer.stubFor(post("/api/payment")
        .inScenario("Retry Scenario")
        .whenScenarioStateIs("FIRST_RETRY")
        .willReturn(aResponse().withStatus(503))
        .willSetStateTo("SECOND_RETRY"));

    wireMockServer.stubFor(post("/api/payment")
        .inScenario("Retry Scenario")
        .whenScenarioStateIs("SECOND_RETRY")
        .willReturn(aResponse().withStatus(200).withBody("{\"status\":\"SUCCESS\"}")));

    // Act
    PaymentResponse response = paymentService.initiatePayment(paymentRequest);

    // Assert
    assertEquals("SUCCESS", response.getStatus());
    wireMockServer.verify(3, postRequestedFor(urlEqualTo("/api/payment")));
    // Verify only one DB record (idempotency)
    assertEquals(1, transactionRepo.countByReferenceId(paymentRequest.getReferenceId()));
}
```

---

### Q12. What layers would you automate — API, DB, queues, UI?

**Answer:**

Follow the **Test Pyramid** principle for a payment system:

```
        [UI]           ← Least (5-10%)
       [API E2E]       ← Medium (20-30%)
    [Integration]      ← More (30-40%)
   [Unit + Contract]   ← Most (40-50%)
```

**Layer-by-Layer Strategy:**

| Layer | What to Automate | Tools |
|-------|-----------------|-------|
| **Unit** | Business logic, calculation, validation rules | JUnit, Mockito |
| **API** | All endpoints, request/response contracts, error codes, auth | RestAssured, Postman/Newman |
| **DB** | Data integrity, constraints, correct state after transactions | JDBC assertions, TestContainers |
| **Queue/Kafka** | Messages published on payment events, correct schema, ordering | Kafka test utils, embedded Kafka |
| **Contract** | Provider-consumer API contracts don't break | Pact |
| **UI** | Critical user journeys only: initiate payment, check status, refund | Selenium, Appium |

**For Payment Systems specifically — prioritize:**
1. **API layer** — highest ROI; catches most bugs
2. **DB layer** — critical for financial correctness
3. **Queue layer** — ensures event-driven flows work
4. **UI** — only for the most critical flows (payment initiation, transaction history)

---

### Q13. How do you test idempotency and duplicate prevention?

**Answer:**

**Idempotency in Payments:** Making the same payment request multiple times (due to retries or network issues) should result in only ONE transaction.

**How to Implement:**
- Every payment request has a unique `idempotency_key` (UUID).
- If the same key is sent again, return the original response without processing again.

**Test Cases:**

```
1. First request → payment processed → 200 OK
2. Same idempotency_key sent again → same 200 OK response, NO new transaction in DB
3. Different idempotency_key, same amount → new transaction created
4. Idempotency_key after partial failure → system should complete or return original state
```

**Test Code (RestAssured):**
```java
@Test
public void testIdempotentPayment() {
    String idempotencyKey = UUID.randomUUID().toString();
    PaymentRequest request = buildRequest(idempotencyKey, 500);

    // First call
    Response first = paymentApi.initiate(request);
    assertEquals(200, first.statusCode());
    String txnId = first.jsonPath().getString("transactionId");

    // Duplicate call with same key
    Response second = paymentApi.initiate(request);
    assertEquals(200, second.statusCode());
    assertEquals(txnId, second.jsonPath().getString("transactionId")); // Same txn returned

    // DB check — only one record
    assertEquals(1, db.countTransactionsByIdempotencyKey(idempotencyKey));
}
```

---

## 💡 Key Takeaways

| Area | What PhonePe Looks For |
|------|------------------------|
| **Coding** | Clean code, edge case handling, time/space optimization |
| **CI/CD** | Deep understanding of pipeline stages, flaky test handling |
| **Test Strategy** | Risk-based prioritization, pyramid thinking |
| **Payment Domain** | Idempotency, retries, partial failures, financial consistency |
| **Debugging** | Systematic approach to test vs product bug identification |
| **Communication** | Real-world scenarios over theoretical answers |

---

*Good luck with your PhonePe SDET interview! 🎯*
