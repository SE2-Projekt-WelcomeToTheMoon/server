# Repository for the Backend of the application _Welcome to the Moon_
# Sonar Cloud Stats
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=SE-II-group-do-1_stratego-server&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=SE-II-group-do-1_stratego-server)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=SE-II-group-do-1_stratego-server&metric=bugs)](https://sonarcloud.io/summary/new_code?id=SE-II-group-do-1_stratego-server)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=SE-II-group-do-1_stratego-server&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=SE-II-group-do-1_stratego-server)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=SE-II-group-do-1_stratego-server&metric=coverage)](https://sonarcloud.io/summary/new_code?id=SE-II-group-do-1_stratego-server)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=SE-II-group-do-1_stratego-server&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=SE-II-group-do-1_stratego-server)
---
## Spring Framework
**Server Client communication is being realised with the Spring Boot Tool WebSocketHandler.**
* [Spring.io](https://spring.io/projects/spring-framework)
* [Spring Boot](https://spring.io/projects/spring-boot)

---
## File structure 
#### (only for productive Code, no tests)
* `src/main/java`
  * `websocketserver`
      * `game`
        * `enums`
            * `ChoosenCardCombination.java`
            * `EndType.java`
            * `FieldCategory.java`
            * `FieldValue.java`
            * `GameState.java`
            * `RewardCategory.java`
        * `exceptions`
            * `FinalizedException.java`
            * `FloorSequenceException.java`
            * `GameStateException.java`
        * `lobby`
            * `lobby.java`
        * `model`
            * `CardCombination.java`
            * `CardStack.java`
            * `Chamber.java`
            * `Field.java`
            * `FieldUpdateMessage.java`
            * `Floor.java`
            * `Game.java`
            * `GameBoard.java`
            * `Player.java`
            * `PlayingCard.java`
            * `Reward.java`
            * `RocketBarometer.java`
            * `SystemErrors.java`
        * `services`
            * `CardController.java`
            * `GameBoardService.java`
        * `GameLogic.java`
      * `services`
        * `json`
          * `ActionValues.java`
          * `GenerateJSONObjectService.java`
        * `user`
          * `CreateUserSerice.java`
          * `ManageUserService.java`
          * `UserListService.java`
        * `CardManager.java`
        * `GameBoardManger.java`
        * `GameService.java`
        * `LobbyService.java`
        * `SendMessageService.java`
      * `websocket`
        * `handler`
            * `WebSocketHandlerConfig.java`
            * `WebSocketHandlerImpl.java`
      * `WebSocketServerApplication.java`
---
## WebSocketHandler
Configuration file to configure the WebSocketHandler Server endpoint.
WebSocketHandlerImpl to handle the connection between server and clients. Also handles incoming messages from clients 
and to send out messages to the client connected to the server.

---
## Game logic
All game logic can be found in the `game` package.

--- 
## File is being permanently updated...
