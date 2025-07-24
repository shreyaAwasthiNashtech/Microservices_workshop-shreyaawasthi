# Day 4 – Observability with Spring Boot

This project demonstrates how to build a Spring Boot microservice that is production-ready in terms of observability. It includes structured logging, distributed tracing, metrics instrumentation, and visualisation using tools like Prometheus, Grafana, Zipkin, Logstash, Elasticsearch, and Kibana.


## Core Concept Questions

### 1. What are the differences between metrics, logs, and traces?

- **Metrics** are numbers. They show how the app is performing over time, e.g., how many requests per minute, how much memory used, or the average DB response time.
- **Logs** are records of what happened. They include errors, info messages, and debugging outputs, usually in text or JSON format.
- **Traces** are like stories. They show how a request travels through various services and what happened at each step (span), often with timings.


### 2. What’s the role of Micrometer and how does it enable backend-agnostic metrics?

Micrometer is like a middleman. It collects metrics from your Spring Boot app and lets you push them to any system you like Prometheus, Datadog, New Relic, etc. You write code once, and then change the output backend if needed. It saves time and avoids vendor lock-in.


### 3. How do traceId and spanId travel across microservice boundaries?

They’re passed through HTTP headers (X-B3-TraceId, X-B3-SpanId, etc.). When Service A calls Service B, Sleuth automatically adds these headers, and the receiving service continues the trace from where it left off.


### 4. How does Sleuth propagate context in WebClient/RestTemplate?

Sleuth hooks into the HTTP client under the hood. When you use WebClient or RestTemplate, it automatically inserts the current trace and span information into the outgoing request headers, no manual work required.


### 5. What is the difference between Timer, Gauge, and Counter in Micrometer?

- **Timer**: Measures how long something takes. Use it for latency, like user.api.latency.
- **Counter**: Just counts how many times something happened (e.g., errors, logins).
- **Gauge**: Tracks values that go up and down, like memory usage or DB pool size.


### 6. What is MDC and how does it help with log correlation?

MDC (Mapped Diagnostic Context) stores values (like traceId, userId) for the current thread. When a log line is written, these values get included automatically. That way, you can trace a single user’s journey across multiple log entries easily.


### 7. What is the difference between structured vs unstructured logging?

- **Structured logs**: Machine-readable, usually JSON. Easy to search, filter, and analyse in tools like Kibana.
- **Unstructured logs**: Plain text. Good for humans, but hard for machines to parse or index.


### 8. How do you use Kibana to search for logs tied to a specific traceId?

You search using a simple query like:

``text
traceId:"c7a3efab34a1b2c5"

Kibana will return all logs with that traceId, even across multiple services. Very useful when debugging distributed systems.


### 9. How can you monitor memory, DB pool, and request error rates in Grafana?

Use JVM memory metrics from Actuator via Prometheus.

Use HikariCP pool metrics (hikaricp.connections.usage).

Use HTTP metrics like http.server.requests to track 5xx errors and latency.

Grafana dashboards can visualise these with graphs, gauges, and alerts.


### 10. How does sampling affect Zipkin trace accuracy and performance?

Sampling means only some requests are traced to reduce overhead. If you trace 100% of requests, it’s accurate but can be heavy on memory and network. Sampling 10% is lighter, but you might miss some rare issues. You can adjust sampling rate in application.yml.
