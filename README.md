# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
## Chess Server Design URL
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=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