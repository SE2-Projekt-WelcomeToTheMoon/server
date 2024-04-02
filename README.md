# Server für Kommunikation zwischen Backend und Clients

---
## Spring Framework
**Realisiert die Implementierung der Netzwerkkommunikation zwischen Clients und Backend.**
* [Spring.io](https://spring.io/projects/spring-framework)
* [Spring Boot](https://spring.io/projects/spring-boot)

---
## File Struktur
* `src/main/java`
    * `WebSocketServerApplication.java`
    * `game`
      * `GameLogic.java`
    * `messaging/dtos`
        * `OutputMessage.java`
        * `StompMessage.java`
    * `websocket`
        * `broker`
            * `WebSocketBrokerConfig.java`
            * `WebSocketBrokerController.java`
        * `handler`
            * `WebSocketHandlerConfig.java`
            * `WebSocketHandlerImpl.java`
* `src/test/java`
    
    * `WebSocketHandleIntegrationTest.java`
    * `websocket`
        * `StompFramgeHandlerClientImpl.java`
        * `WebSocketHandlerClientImpl.java`

---
## Spiel Logik

Logik bitte in `/game` directory.

---
## Broker

tbd

---
## Handler

tbd

--- 
## Work in progress....
