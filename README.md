# DEZSYS_GK73_WAREHOUSE_MOM
Verfasser: Berkay Genc
## Aufgabenstellung
Implementieren Sie die Lager-Kommunikationsplattform mit Hilfe des Java Message Service. Verwenden Sie Apache Kafka (https://kafka.apache.org) als Message Broker Ihrer Applikation. Das Programm soll folgende Funktionen beinhalten:
- Installation von Apache Kafka in der Zentrale.
- Jeder Lagerstandort hat eine Message Queue mit einer ID am zentralen Rechner.
- Jeder Lagerstandort legt in regelmässigen Abständen die Daten des Lagers in der Message Queue ab.
- Bei einer erfolgreichen Übertragung sendet die Zentrale die Nachricht "SUCCESS" an den Lagerstandort retour.
- Der zentrale Rechner fragt in regelmässigen Abständen alle Message Queues ab.
- Der Zentralrechner fuegt alle Daten aller Lagerstandorte zusammen und stellt diese an einer REST Schnittstelle im JSON/XML Format zur Verfügung.
## Fragen
**Nennen Sie mindestens 4 Eigenschaften der Message Oriented Middleware?**
- Asynchronität
- Entkopplung von Sender und Empfänger
- Persistenz/Zuverlässigkeit
- Skalierbarkeit

**Was versteht man unter einer transienten und synchronen Kommunikation?**

Transient bedeutet, dass Nachrichten nicht gespeichert werden und verloren gehen können, wenn der Empfänger nicht verfügbar ist, während synchron bedeutet, dass Sender und Empfänger gleichzeitig aktiv sein müssen und der Sender auf die Antwort wartet.

**Beschreiben Sie die Funktionsweise einer JMS Queue?**

Eine JMS Queue arbeitet nach dem Point-to-Point-Prinzip, bei dem ein Producer Nachrichten in eine FIFO-Warteschlange sendet und genau ein Consumer diese Nachricht erhält und verarbeitet.

**JMS Overview - Beschreiben Sie die wichtigsten JMS Klassen und deren Zusammenhang?**

ConnectionFactory erstellt Verbindungen, Connection stellt die Verbindung zum Broker dar, Session erzeugt Messages, Producers und Consumers, MessageProducer und MessageConsumer senden bzw. empfangen Nachrichten und Destinations (Queue/Topic) bestimmen das Ziel.

**Beschreiben Sie die Funktionsweise eines JMS Topic?**

Ein JMS Topic arbeitet im Publish/Subscribe-Modell, bei dem ein Publisher Nachrichten veröffentlicht, die alle aktiven Subscriber gleichzeitig erhalten.

**Was versteht man unter einem lose gekoppelten verteilten System? Nennen Sie ein Beispiel dazu. Warum spricht man hier von lose?**

Ein lose gekoppeltes System besteht aus unabhängigen Komponenten, die nur über definierte Nachrichten oder Schnittstellen kommunizieren, z. B. Microservices über eine Message Queue, und man spricht von „lose“, weil Änderungen an einer Komponente die anderen kaum betreffen.

## Code Snippets
application.yml
````yaml
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
````
build.gradle
````groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.kafka:spring-kafka'

    implementation 'jakarta.xml.bind:jakarta.xml.bind-api:4.0.0'
    implementation 'org.glassfish.jaxb:jaxb-runtime:4.0.3'

    testImplementation 'org.springframework.boot:spring-boot-starter-test'

}
````
docker-compose.yml
````yaml
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    container_name: zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    container_name: kafka
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
````
Senden einer Nachricht an Kafka Topic
````java

@PostMapping("/send")
public String sendWarehouseData(@RequestBody WarehouseData data) throws JsonProcessingException {
    data.setTimestamp(Instant.now().toString());
    String json = new ObjectMapper().writeValueAsString(data);
    kafkaTemplate.send("warehouse-input", json);
    return "SUCCESS: " + json;
}
````
Empfangen einer Nachricht von Kafka Topic
````java

@KafkaListener(topics = "warehouse-input")
public void receiveMessage(String message) throws Exception {
    WarehouseData data = new ObjectMapper().readValue(message, WarehouseData.class);
    System.out.println("Zentrale empfängt: " + message);
    aggregator.add(data);
}
````
Hinzufügen der empfangenen Daten
````java

public synchronized void add(WarehouseData data) {
    aggregated.add(data);
}
````
Abrufen der Daten im JSON Format
````java

@GetMapping(value = "/stock", produces = MediaType.APPLICATION_JSON_VALUE)
public List<WarehouseData> getJsonStock() {
    return aggregator.getAll();
}
````
Abrufen der Daten im XML Format
````java

@GetMapping(value = "/stock.xml", produces = MediaType.APPLICATION_XML_VALUE)
public WarehousesXml getXmlStock() {
    return new WarehousesXml(aggregator.getAll());
}
````
## Terminal Befehle
Starten der Spring Boot Applikation
````
./gradlew --refresh dependencies bootRun
````
Docker Container starten
````
docker compose up -d
````
Überprüfen ob apache/kafka läuft
```
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092
```
Versenden von Daten an die Zentrale
```
curl -Method POST http://localhost:8080/warehouse/send -Headers @{ "Content-Type"="application/json" } -Body '{"warehouseId":"W1","quantity":50}'
```
Abrufen des Lagers in XML: http://localhost:8080/central/stock.xml

Abrufen des Lagers in JSON: http://localhost:8080/central/stock

Versenden einer Nachricht: http://localhost:8080/send?message=HalloSpencer

Die Topics in Kafka anlegen
````
docker exec -it kafka bash
[appuser@681e006f3605 ~]$ kafka-topics --create --topic warehouse-input --bootstrap-server localhost:9092
Created topic warehouse-inout
[appuser@681e006f3605 ~]$ kafka-topics --create --topic warehouse-response --bootstrap-server localhost:9092
Created topic warehouse-response.
[appuser@681e006f3605 ~]$ kafka-topics --list --bootstrap-server localhost:9092
__consumer_offsets
quickstart-events
warehouse-input
warehouse-response
````
## Quellen
- Grundlagen Message Oriented Middleware: Presentation
- Middleware: Apache Kafka
- Apache Kafka | Getting Started

https://medium.com/@abhishekranjandev/a-comprehensive-guide-to-integrating-kafka-in-a-spring-boot-application-a4b912aee62e https://spring.io/guides/gs/messaging-jms/

https://medium.com/@mailshine/activemq-getting-started-with-springboot-a0c3c960356e

http://www.academictutorials.com/jms/jms-introduction.asp

http://docs.oracle.com/javaee/1.4/tutorial/doc/JMS.html#wp84181

https://www.oracle.com/java/technologies/java-message-service.html

http://www.oracle.com/technetwork/articles/java/introjms-1577110.html

https://spring.io/guides/gs/messaging-jms

https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-messaging.html

https://dzone.com/articles/using-jms-in-spring-boot-1