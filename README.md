# ♕ Abe's Chess

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

## Sequence Diagram
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoARiqfJCIK-P8gK0eh8KVEB-rgeWKkwes+h-DsXzQo8wHiVQSIwAgQnihignCQSRJgKSb6GLuNL7gyTJTspXI3r5d5LsKMBihKboynKZbvEqmAqsGGpukYEBqGgADkzBWjaIW8mFNl2T2fbbt5Nn+tIGWqBgUZolGMZxoUoGYcgqYwOmmajGMOaqHm8zQUWJb1EF6WZTlMB5TkDb0QVC4CkmvrOl2G7uluXkgSt9QUeYhByCgTSmRpwwYjF2hrOpOyksl6owAAkmgVAmkg65XQC5VbRJpaPc9yAcEdWw7NpbUlGAaYZgRfUDQWYzDdA9Tin9r0wO9ux0U2-jGpeMrZBgwbrhAABm8Uep9PmFfy9SHgdz7xOeOPaHOoUOuFO1rgGjPyPN+5fbZLrYy+G0re2emli54oZKoAGmGLfP6YRhmDcZYwUVR9ZXK1S3wO12EwLh+E9UFNFkarl7q8hJGobNTaMd4fj+F4KDoDEcSJE7Lsub4WCiYKoH1A00gRvxEbtBG3Q9HJqgKcMauIegIMflZCtx9RVtaXLxUCw59je85Qne25ageeTVK3lTMCMmAdMM-B8doMzlOLpUy6ReK64UbF8qpwnt0atISBE0TlDmPFPNFctnbdr2-abdr-sCUJLSnvDUCJ8muvg51kM9dD+ZDcWCPwOwk05zA3sNljIDxCgIAapAMDHHWMDSyiMC6llAp6oYUBIAkzBvEbAxYcrMs6rU7sLTsfM7K7QwPtScbIIHyE8iLCmC16gcBQNwY8l5a5XiZuPVmLcIrSCwUyQwSDgCEMXGA+oZAfD7nJhvMGOE8JQ35DDaCLlGSE1PC-BhI4iYz0sC4G2DFPD2wCCidc-hsDig1PxNEMAADiSoNC+0qqWBoyjQ4R3sEqWO5t66J10snMCZs65p1Ig2TOk9+arWQDkZyaJVE5iLiSUuMhy70krkyGuPcG7UMWsQ+oUUO5c2ALKbuRikJJVVBqX6L1CawBmNkaODhBaUWMSAmhdiSozyYXY-0Lll5gFXuvSookIbdWzBw-ehZD6ljgCfZgZ8L4Y2Ad4xatDMmvlQT02BDADpTkoaSTxOTRz2RcWojEjcFr3gimEysCBK5qKCdAgWriUA8gaJ0QpDxYTFOmW4-8CBAJmPluY-ROYUINHGNclA91pAm2+A8gAcpBdOlxOia2qBhbWVT9ZsJ6g81QUEGguEhT8oBdtmL+A4AAdjcE4FATgYgRmCHALiAA2eAE5DBbJgEUTefs-laNaB0ToPQHmGMsS7LM7ylS-O+oKMWLwLFZOoqpZYjKjJoVsR2ex1N8VbIxFs9xJc55lxZj4qu-iYnoDmbzNmbcJSUKiZki2cSUoPSekk8+KS0nikcAE9Z89towFKrPfpRTxZLxXo0ipOsWHbxqb1Opg0GkjWPtlVpQlz6njEaYCZGzwERP2V5Hal49ogGGYgiJKCoFoL8jAGm6JRW8uCiGlVcB8XGhgG8lAmwVGJWzXkzZSoyZSoOV+eouajwoHFac85hz4SaPZQ8p5XwQSZosk6wFBsCKduedysYvbIQdNhQ7SwWCHLFtiH-RIM6+wQGLQAKQgOKEtcwYjJFAGqYlYNSXfQDk0ZkMlqUGOmAqxSoxsAIGADOqAcAIAOSgGsYdzLEymNbeygJ3b1gPqfS+t9AGx1Ki7dYyyrbj1CpgAAK03WgUVG7xRNsJMXRN9jBQTP8tXXBASlVhRCaq8JQt5AatNX3XVyNkmPyNRk012aelWojWS10169TQCQAALxQBwRqKBYwKX7ZvCGABGdhuZ6lw0aaNTj8RuN8fRjCs1PS6Z9KTdKpukyq4Zog9IIjRChShPbtu7Z2gNXDu1XdQlaAIDMCJr4JszHy2rVY3Pdt5m3kOZFM5gT0YhPNVEy69MhtanSc9bJ71DzLUOZgE5nwnAg1qbc-ULZmnsPmqnqmm+d8QPQAUM9U4uhTi6ksGKytF1UZAcoAVqAN14kwAlfx1Nr7oDvwFCaM0NZwwtRtYK-09WwxIUE8J+MWtmEdS6lJ-qMnV7U3a7AbrBNevUUneMrpo1sBaHTUqSrcxrxlpM5KHbB1zMyh1HqS1RaauPtoK5wVdlmRnfROuDLkCstJ1-TAVDyGlRSxlpnLz9yDOw1Nve+7z6ltgb3srP7rWHkNkm5UsTQLwvgbmJBlWkPgMw9HXDvYCP1yxelvuIRfZLApbmhIuFXhH2u0XWAen8pEDBmW9ge98CFJEo0bagOQcQ5hwjsYUwWsf21tTdwPAszMACsjVL9nsvq24cVzLozzcTukOwRQvsa0eRrA+-INYjxObkaoY9hX9DGGeYl3COt0uoCA7OXLi52WajssJ4sHkegDBq+kcjsloMOqDt3h6vYPv9CGBAI71rZPBHCJS0AA
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922

```sh

```
