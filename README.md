# Core Concept Questions - Day 1
**1. What are the differences between Mono and Flux, and where did you use each?**
Mono is like a delivery package that contains just one item or nothing at all. For example, if we search for a user by their ID, we'll either get one user or no user, so we return a Mono<User>.
Flux is like a box that can contain many items, like a list of products in our shopping cart. If we're listing all items in a user's cart, we'd use Flux<CartItem>.
In our app, we used Mono for operations like getting or updating a single cart, and Flux when returning multiple cart items.

**2. How does R2DBC differ from traditional JDBC?**
JDBC is like a person who waits for a pizza delivery before doing anything else; it’s blocking.
R2DBC is like someone who places the pizza order and continues cleaning the house; it’s non-blocking.
In our app, we used R2DBC so the server can handle many users at once without getting stuck waiting for database responses.

**3. How does Spring WebFlux routing differ from @RestController?**
@RestController is like giving each API its method and URL in one place.
WebFlux functional routing is like building routes using a map, more flexible but a little more code.
In our app, we used functional routing in RouterFunction and HandlerFunction to define APIs for carts in a cleaner, modular way.

**4. What are the advantages of using @Transactional with R2DBC (or why not)?**
In regular (blocking) apps, @Transactional ensures that a group of DB operations succeed or fail together.
In reactive apps, using @Transactional isn’t that simple because everything is non-blocking. For R2DBC, Spring supports it, but with limitations.
We avoided it in complex operations and preferred chaining operations with flatMap and .then() for full control in a reactive way.

**5. What is the difference between optimistic vs pessimistic locking? Where would you use either?**
Optimistic Locking: Like editing a shared doc where each user has their version. If two people edit, the system checks versions before saving.
Good for apps with rare conflicts.
Pessimistic Locking: Like borrowing a book from a library — no one else can access it until you return it.
Good for apps with frequent data conflicts.
In our cart system, optimistic locking could be used to avoid overwriting cart updates made by two devices at the same time.

**6. How does session management work using Redis in reactive apps?**
Think of Redis like a quick-access notebook. When a user logs in, their session info is stored in Redis.
In reactive apps, the session data is accessed non-blockingly, so even with many users, the app stays fast.
In our app, Redis helps remember who the user is across multiple API calls without blocking the flow.

**7. How did you expose and verify SSE updates?**
SSE (Server-Sent Events) is like a radio channel where the server keeps pushing updates.
We used Spring’s SSEEmitter style with Flux to stream updates.
To test it, we connected a client to the /cart/stream endpoint and made changes to the cart. We verified the updates were received in real time.

**8. What is the role of Swagger/OpenAPI in CI/CD and API consumer tooling?**
Swagger/OpenAPI is like a live instruction manual for your APIs.
It helps front-end developers, testers, and tools understand what each API does, without requiring them to dig into the code.
In CI/CD, it can validate APIs haven’t broken after changes, and help generate client code automatically.

**9. How does WebTestClient differ from MockMvc in testing?**
MockMvc is used for blocking (MVC-style) Spring apps.
WebTestClient is for reactive (WebFlux) apps; it works well with Mono and Flux.
In our app, we used WebTestClient to write end-to-end tests that call the actual APIs and verify the results.

**10. Compare WebSocket, SSE, and RSocket for real-time data needs.**
When building real-time features like notifications, live updates, or chat, you can choose between WebSocket, SSE (Server-Sent Events), or RSocket, depending on what your app needs. WebSocket is a two-way communication protocol, which means both the server and client can send messages to each other anytime. This makes it great for chat apps, multiplayer games, or collaborative tools. SSE, on the other hand, is simpler and one-way — the server can continuously send updates to the browser, but the browser can't send data back over the same connection. It works over regular HTTP, so it's easy to use and works well for things like stock price updates, real-time notifications, or cart change alerts in an e-commerce app. RSocket is a newer, more advanced protocol that supports both one-way and two-way messaging, with features like backpressure and resumable connections. It’s powerful for microservice-to-microservice communication where performance, reliability, and flexibility matter most. In our app, we used SSE because it's lightweight, easy to set up, and perfect for streaming cart updates to the frontend when data changes in real-time.


# Core Concept Questions – Day 2
**1. What is the purpose of bootstrap.yml in a config client?**
The bootstrap.yml file is like the first file Spring Boot reads when our application starts. It’s especially important in applications that use Spring Cloud Config.
Imagine we're building a hotel chain management system with many services—each hotel (service) wants to pick its room settings (configuration) from a central settings book (Git repo). The bootstrap.yml tells our service where that central book is kept and what name to use when reading it.
So, it’s used to:
  -> Set the application name (e.g., product-service)
  -> Define the config server URL
  -> Set the environment (like dev, test, prod)
Without it, the service wouldn’t know where to fetch its actual config settings from.

**2. How does @RefreshScope work and when should it be used?**
@RefreshScope is used when you want your beans (like services or configuration classes) to reload their values if the configuration changes. Let’s say you have a shipping service that charges ₹50 per delivery. This value is fetched from config. If tomorrow the price changes to ₹70 in Git, you don’t want to restart the app.
With @RefreshScope, you just call /actuator/refresh, and the bean will pick up the new value automatically.
Use it when your bean needs to respond to config changes dynamically, like pricing, limits, or toggles.

**3. What’s the difference between static and dynamic route configuration in Gateway?**
Static routes are hardcoded in your gateway’s application.yml. If you want to change them, you have to redeploy the gateway.
Example:
routes:
  - id: product-service
    uri: http://localhost:8081
    predicates:
      - Path=/products/**
Dynamic routes, on the other hand, are stored in a Git repo (via config server). This means you can update them without touching the code or restarting the gateway.
Static routes are okay for small projects. Dynamic ones are better for big systems where routes change often or need to be managed centrally.

**4. How does rate limiting in Spring Cloud Gateway work internally?**
Rate limiting ensures that no one abuses your services by hitting them too many times in a short period. Think of it like letting only 100 customers into a store every minute.
Internally, Spring Cloud Gateway uses token buckets or Redis to track how many requests a user or IP has made. If the user has used up all tokens (or limit), further requests are rejected with an HTTP 429 (Too Many Requests).
Example: You may allow 20 requests/minute per user. If a user sends 25, 5 of them will be denied.

**5. How do you test a configuration change without restarting services?**
After updating the value in your Git repo (like maxLoginAttempts), you can:
1. Send a POST request to /actuator/refresh endpoint of the service.
2. If the bean is annotated with @RefreshScope, the new value is picked up instantly.
You can test by hitting the endpoint or checking logs to see the change reflected.
It's like refreshing your browser without closing it—changes show up instantly.

**6. What’s the difference between global and per-route filters in Gateway?**
  -> Global filters apply to every request that goes through the gateway.
      Example: Logging every request’s URL or checking for a token in the header.
  -> Per-route filters apply only to specific routes.
      Example: You might want a custom header added only when calling /orders/** and not /products/**.
Think of global filters like entrance security checks at a mall, and per-route filters like checks at a specific shop (like verifying age before entering a liquor store).

**7. How does Spring Cloud Bus enhance dynamic config refresh?**
Spring Cloud Bus acts like a message courier between your services. If one service refreshes its config, the bus sends a message to all others saying, “Hey, config has changed, refresh yourself!”
So instead of calling /actuator/refresh on each microservice, you call it on one, and all others listen and refresh themselves automatically.
It's like updating a policy and your team members automatically getting notified instead of calling them one by one.

**8. How is JWT verified at the Gateway level?**
The gateway intercepts every request and checks the JWT token (usually found in the Authorization header). It validates the token’s:
-> Signature (to ensure it’s not tampered)
->Expiry time
-> User roles or claims
If all checks pass, the request goes through. Otherwise, it’s rejected.
It’s like showing your ID at a building entrance. If it’s expired or fake, you’re not allowed in.

**9. What’s the role of service discovery when routing via Gateway?**
Service discovery (like Eureka) helps the gateway know the exact location of your services.
You don’t have to hardcode URLs like http://localhost:8081. Instead, you register each service (product, order, etc.) with Eureka. Then the gateway just says:
uri: lb://product-service
lb means “load balanced”, and Eureka will tell the gateway where product-service is running.
It’s like using a contact name instead of remembering a phone number.

**10. Why prefer centralized config and not embedded .yml?**
With centralized config (like Spring Cloud Config Server), all your microservices get their settings from a common Git repo. This has benefits:
  -> One place to update everything
  -> Easier environment switching (dev, test, prod)
  -> Avoids mistakes like different configs across services
  -> No need to rebuild apps for minor config changes
It’s like managing employee records from one HR portal instead of each department keeping their own Excel sheet.


