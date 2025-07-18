Core Concept Questions (Explained Simply)
1. What are the differences between Mono and Flux, and where did you use each?
Mono is like a delivery package that contains just one item or nothing at all. For example, if we search for a user by their ID, we'll either get one user or no user, so we return a Mono<User>.
Flux is like a box that can contain many items, like a list of products in our shopping cart. If we're listing all items in a user's cart, we'd use Flux<CartItem>.
In our app, we used Mono for operations like getting or updating a single cart, and Flux when returning multiple cart items.

2. How does R2DBC differ from traditional JDBC?
JDBC is like a person who waits for a pizza delivery before doing anything else, it’s blocking.
R2DBC is like someone who places the pizza order and continues cleaning the house, it’s non-blocking.
In our app, we used R2DBC so the server can handle many users at once without getting stuck waiting for database responses.

3. How does Spring WebFlux routing differ from @RestController?
@RestController is like giving each API its own method and URL in one place.
WebFlux functional routing is like building routes using a map, more flexible but a little more code.
In our app, we used functional routing in RouterFunction and HandlerFunction to define APIs for carts in a cleaner, modular way.

4. What are the advantages of using @Transactional with R2DBC (or why not)?
In regular (blocking) apps, @Transactional ensures that a group of DB operations succeed or fail together.
In reactive apps, using @Transactional isn’t that simple because everything is non-blocking. For R2DBC, Spring supports it, but with limitations.
We avoided it in complex operations and preferred chaining operations with flatMap and .then() for full control in a reactive way.

5. What is the difference between optimistic vs pessimistic locking? Where would you use either?
Optimistic Locking: Like editing a shared doc where each user has their own version. If two people edit, the system checks versions before saving.
Good for apps with rare conflicts.
Pessimistic Locking: Like borrowing a book from a library — no one else can access it until you return it.
Good for apps with frequent data conflicts.
In our cart system, optimistic locking could be used to avoid overwriting cart updates made by two devices at the same time.

6. How does session management work using Redis in reactive apps?
Think of Redis like a quick-access notebook. When a user logs in, their session info is stored in Redis.
In reactive apps, the session data is accessed non-blockingly, so even with many users, the app stays fast.
In our app, Redis helps remember who the user is across multiple API calls without blocking the flow.

7. How did you expose and verify SSE updates?
SSE (Server-Sent Events) is like a radio channel where the server keeps pushing updates.
We used Spring’s SseEmitter style with Flux to stream updates.
To test it, we connected a client to the /cart/stream endpoint and made changes to the cart. We verified the updates were received in real time.

8. What is the role of Swagger/OpenAPI in CI/CD and API consumer tooling?
Swagger/OpenAPI is like a live instruction manual for your APIs.
It helps front-end devs, testers, and tools understand what each API does, without digging into the code.
In CI/CD, it can validate APIs haven’t broken after changes, and help generate client code automatically.

9. How does WebTestClient differ from MockMvc in testing?
MockMvc is used for blocking (MVC-style) Spring apps.
WebTestClient is for reactive (WebFlux) apps, it works well with Mono and Flux.
In our app, we used WebTestClient to write end-to-end tests that call the actual APIs and verify the results.

10. Compare WebSocket, SSE, and RSocket for real-time data needs.
When building real-time features like notifications, live updates, or chat, you can choose between WebSocket, SSE (Server-Sent Events), or RSocket, depending on what your app needs. WebSocket is a two-way communication protocol, which means both the server and client can send messages to each other anytime. This makes it great for chat apps, multiplayer games, or collaborative tools. SSE, on the other hand, is simpler and one-way — the server can continuously send updates to the browser, but the browser can't send data back over the same connection. It works over regular HTTP, so it's easy to use and works well for things like stock price updates, real-time notifications, or cart change alerts in an e-commerce app. RSocket is a newer, more advanced protocol that supports both one-way and two-way messaging, with features like backpressure and resumable connections. It’s powerful for microservice-to-microservice communication where performance, reliability, and flexibility matter most. In our app, we used SSE because it's lightweight, easy to set up, and perfect for streaming cart updates to the frontend when data changes in real-time.


