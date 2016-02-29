package edu.buffalo.cse.cse486586.groupmessenger2;

import java.util.Objects;
import java.util.UUID;

public class BufferedMessage implements Comparable<BufferedMessage> {
    private int sequence;
    private UUID id;
    private String message;
    private boolean isAgreed = false;

    public BufferedMessage(UUID id, int sequence, String message) {
        this.id = id;
        this.sequence = sequence;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getSequence() {
        return sequence;
    }

    public UUID getId() {
        return id;
    }

    public void markAgreed() {
        this.isAgreed = true;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public boolean isAgreed() {
        return isAgreed;
    }

    @Override
    public int compareTo(BufferedMessage another) {
        return Integer.compare(sequence, another.sequence);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BufferedMessage that = (BufferedMessage) o;
        return Objects.equals(sequence, that.sequence) &&
                Objects.equals(isAgreed, that.isAgreed) &&
                Objects.equals(id, that.id) &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequence, id, message, isAgreed);
    }

    @Override
    public String toString() {
        return "BufferedMessage{" +
                "sequence=" + sequence +
                ", id=" + id +
                ", message='" + message + '\'' +
                ", isAgreed=" + isAgreed +
                '}';
    }
}
