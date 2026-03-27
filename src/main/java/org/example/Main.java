package org.example;

import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] strings) {
        String filePath = "src/main/resources/logs.txt";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        ObjectMapper mapper = new ObjectMapper();

        try {
            List<String> allLines = Files.readAllLines(Paths.get(filePath));

            //Part1
            System.out.println("Part 1");
            List<String> first30Lines = allLines.stream().limit(30).toList();
            for (String line : first30Lines) {
                System.out.println(line);
            }

            //Part2
            System.out.println("-------------");
            System.out.println("Part 2");
            List<Message> messages = allLines.stream()
                    .skip(1)
                    .map(line -> line.split(",", 2))
                    .filter(array -> array.length > 1)
                    .map(array -> array[1])
                    .map(msg -> mapper.readValue(msg, Message.class))
                    .toList();
            long part2Count = messages.size();
            System.out.println(part2Count);

            //Part3
            System.out.println("-------------");
            System.out.println("Part 3");
            List<ParsedMessage> parsedMessages = allLines.stream()
                    .skip(1)
                    .map(line -> line.split(",", 2))
                    .filter(array -> array.length > 1)
                    .map(array -> array[1])
                    .map(msg -> mapper.readValue(msg, Message.class))
                    .filter(msg -> msg.type() != null && msg.type().equals("relay_trx"))
                    .map(ParsedMessage::new)
                    .toList();
            long part3Count = parsedMessages.size();
            System.out.println(part3Count);

            //Part4
            System.out.println("-------------");
            System.out.println("Part 4");
            Map<String,Double> averages = allLines.stream()
                    .skip(1)
                    .map(line -> line.split(",", 2))
                    .filter(array -> array.length > 1)
                    .map(array -> new ParsedMessage(array[0], formatter, mapper.readValue(array[1], Message.class)))
                    .filter(msg -> msg.type() != null && msg.type().equals("relay_trx"))
                    .collect(Collectors.groupingBy(ParsedMessage::relayUuid, Collectors.averagingDouble(ParsedMessage::elapsed)));
            averages.entrySet()
                    .stream()
                    .sorted(Comparator.comparingDouble(Map.Entry::getValue))
                    .forEach(System.out::println);
        } catch (IOException _) {}

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            //Part5
            System.out.println("-------------");
            System.out.println("Part 5");
            Map<String, Window> relayTimestamps = new HashMap<>();
            AtomicInteger alertCounts = new AtomicInteger();
            reader.lines()
                    .skip(1)
                    .map(line -> line.split(",", 2))
                    .filter(array -> array.length > 1)
                    .map(array -> new ParsedMessage(array[0], formatter, mapper.readValue(array[1], Message.class)))
                    .filter(msg -> msg.type() != null && msg.type().equals("relay_trx"))
                    .forEach(msg -> {
                        var currentLogTime = msg.dateTime();
                        var fiveMinutesAgo = currentLogTime.minusMinutes(5);
                        String currentLogRelayUuid = msg.relayUuid();
                        Window currentWindow = relayTimestamps.computeIfAbsent(currentLogRelayUuid, k -> new Window());
                        currentWindow.addFirst(msg);

                        while (currentWindow.peekLast().dateTime().isBefore(fiveMinutesAgo)) {
                            currentWindow.removeLast();
                        }

                        double currentAverage = currentWindow.getAverageElapsed();

                        if (currentAverage > 500.0) {
                            System.out.println("ALERT: High average response time -  " + currentAverage);
                            alertCounts.addAndGet(1);
                        }
                    });
            System.out.println("Total Alerts: " + alertCounts);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
