package org.example;

import java.util.ArrayDeque;
import java.util.Deque;

public class Window {

    private Deque<ParsedMessage> messages;
    private double totalElapsed;
    private int numMessages;

    Window() {
        this.messages = new ArrayDeque<>();
        this.totalElapsed = 0.0;
        this.numMessages = 0;
    }

    Window(Deque<ParsedMessage> messages, double totalElapsed, int numMessages) {
        this.messages = messages;
        this.totalElapsed = totalElapsed;
        this.numMessages = numMessages;
    }

    public Deque<ParsedMessage> getMessages() {
        return messages;
    }

   public ParsedMessage peekLast() {
        return messages.peekLast();
   }

   public void removeLast() {
        ParsedMessage removed = messages.removeLast();
        this.totalElapsed -= removed.elapsed();
        this.numMessages--;
   }

   public void addFirst(ParsedMessage message) {
        messages.addFirst(message);
        this.totalElapsed += message.elapsed();
        this.numMessages++;
   }

   public double getAverageElapsed() {
        return this.totalElapsed / this.numMessages;
   }

    @Override
    public String toString() {
        return "Window{" +
                "totalElapsed=" + totalElapsed +
                ", numMessages=" + numMessages +
                ", messages=" + messages +
                '}';
    }
}
