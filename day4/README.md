# day4 – Resilience4j with Spring Boot Microservice (Payment Service)

This project demonstrates how to implement fault tolerance in a Spring Boot microservice using Resilience4j. The goal is to simulate a real-world payment API that can face issues like timeouts, slow responses, or high traffic and recover from them using resilience patterns like:

- Retry
- TimeLimiter
- Bulkhead
- Fallbacks
- Actuator Metrics


## Core Concept Questions Answered

### 1. **What problem does each of the following solve: Retry, TimeLimiter, Bulkhead?**

- **@Retry** handles temporary failures by giving the system another chance. It’s useful when a remote service fails briefly, like due to a network glitch or a momentary backend hiccup.
- **@TimeLimiter** ensures your users aren’t waiting forever. It stops any call that takes too long and helps avoid long response times or thread blocking.
- **@Bulkhead** limits how many concurrent calls can hit a service at once. It protects your system from being overwhelmed if lots of requests arrive suddenly.


### 2. **What happens when @Retry and @TimeLimiter are combined?**

When used together, **Retry** will attempt the call multiple times, but each attempt is still bounded by the TimeLimiter’s timeout. So if a call fails or takes too long, it retries – but won’t ever exceed the time limit per attempt. This ensures resilience *without letting retries eat up all your time*.


### 3. **How does @Bulkhead protect your system under heavy load?**

Bulkhead acts like a barrier. It only allows a set number of threads (say, 5) to access the protected method at the same time. Others are rejected or queued. This prevents the entire system from crashing if one service is hit with heavy load, much like separate cabins on a ship, damage in one won’t sink the whole boat.


### 4. **Why is @TimeLimiter only applicable on async return types?**

TimeLimiter works by wrapping the method in a *future* (like CompletableFuture). That way, it can control how long it waits before giving up. If the method isn’t asynchronous, the thread is already blocked and can’t be interrupted cleanly, so TimeLimiter wouldn’t be able to stop it halfway.


### 5. **How can actuator metrics help visualise failures and retries in production?**

Actuator exposes live metrics like:
- How many retries happened?
- How many calls were blocked by Bulkhead?
- How often did TimeLimiter trigger?

These metrics help you spot patterns, like if a service keeps timing out, retrying too often, or being overwhelmed. This visibility is essential in production to understand what’s going wrong and how to fix it.


### 6. **How do you test the fallback path effectively?**

To test fallbacks:
- Simulate a failure (throw an exception or delay the response).
- Assert that the fallback method was triggered.
- Check if the fallback response and logs appear as expected.

You can also use mocking (e.g., with Mockito) to force an exception and verify that the fallback executed.

---

### 7. **What would you adjust if the service is retrying too aggressively?**

We could:
- Reduce the `max-attempts`
- Increase the `wait-duration`
- Use `exponential-backoff` to add gaps between retries
- Limit the exceptions it should retry on (e.g., avoid retrying on business logic failures)

Too aggressive retries can waste resources or hammer a service that's already down.


### 8. **How would you simulate latency or failure in a unit test?**

We can:
- Add a `Thread.sleep()` in your method to simulate delay
- Throw a `RuntimeException` to mimic a failure
- Use tools like **WireMock** to mock delayed or failed HTTP responses from external services

These allow us to test TimeLimiter, Retry, and Fallback behaviour safely.


### 9. **What’s the risk of retrying on exceptions like IllegalArgumentException?**

That’s usually a bad idea. Exceptions like IllegalArgumentException often indicate a *bug or bad input*, not a temporary failure. Retrying them wastes time and resources, and doesn’t fix the root cause. Always specify the exception types you want to retry on.


### 10. **How can you visualise open/closed states using actuator endpoints?**

Spring Boot with Resilience4j exposes circuit states via:
Although we didn’t use `@CircuitBreaker` here, if we had, this endpoint would show:
- `CLOSED`: normal behaviour
- `OPEN`: calls are blocked due to failures
- `HALF_OPEN`: testing if service is back

We can plug this into dashboards like Prometheus/Grafana to see it in real-time.