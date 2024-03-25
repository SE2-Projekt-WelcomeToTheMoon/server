# Server für Kommunikation zwischen Backend und Clients

---
## Spring Framework
**Realisiert die Implementierung der Netzwerkkommunikation zwischen Clients und Backend.**
* [Spring.io](https://spring.io/projects/spring-framework)
* [Spring Boot](https://spring.io/projects/spring-boot)

---
## File Struktur
* `src/main/java`
  * `ServerApplication.class` 
  * `messaging`
    * `OutputMessage.class`
    * `StompMessage.class`
  * `websocket`
    * `broker`
      * `WebSocketBrokerConfig.class`
      * `WebSocketBrokerController.class`
    * `handler`
      * `WebSocketHandlerConfig.class`
      * `WebSocketHandlerImpl.class`
* `src/test/java`
    * `ServerApplicationTest.class`
    * `WebSocketBrokerIntegrationTest.class`
    * `WebSocketHandleIntegrationTest.class`
  * `client`
    * `ClientImpl.class`
  * `handler`
    * `HandlerImplTest.class`
  * `websocket`
    * `StompFramgeHandlerClientImplTest.class`
    * `WebSocketHandlerClientImplTest.class`

**Anzahl der Klassen ändert sich im Implementierungsprozess.**

---
## Broker

tbd

---
## Handler

tbd

--- 
## Work in progress...
