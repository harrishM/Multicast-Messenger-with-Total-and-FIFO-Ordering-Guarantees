package edu.buffalo.cse.cse486586.groupmessenger2;

import java.util.Objects;
import java.util.UUID;

public class BufferedMessage implements Comparable<BufferedMessage> {
    private float sequence;
    private UUID id;
    private String fromNode;
    private int agreedPid;
    private String message;
    private Status status = Status.UNDELIVERABLE;

    public enum Status {
        DELIVERABLE, UNDELIVERABLE
    }

    public BufferedMessage(float sequence, UUID id, String fromNode, String message, int agreedPid) {
        this.sequence = sequence;
        this.id = id;
        this.fromNode = fromNode;
        this.agreedPid = agreedPid;
        this.message = message;
    }

    @Override
    public int compareTo(BufferedMessage another) {
        int compare = Float.compare(sequence, another.sequence);
        if (compare == 0) {
            if (status == Status.UNDELIVERABLE && another.status == Status.DELIVERABLE) {
                return -1;
            } else if (status == Status.DELIVERABLE && another.status == Status.UNDELIVERABLE) {
                return 1;
            } else {
                return Integer.compare(agreedPid, another.agreedPid);
            }
        }
        return compare;
    }

    public float getSequence() {
        return sequence;
    }

    public UUID getId() {
        return id;
    }

    public int getAgreedPid() {
        return agreedPid;
    }

    public String getMessage() {
        return message;
    }

    public Status getStatus() {
        return status;
    }

    public void setSequence(float sequence) {
        this.sequence = sequence;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setAgreedPid(int agreedPid) {
        this.agreedPid = agreedPid;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getFromNode() {
        return fromNode;
    }

    public void setFromNode(String fromNode) {
        this.fromNode = fromNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BufferedMessage that = (BufferedMessage) o;
        return Objects.equals(sequence, that.sequence) &&
                Objects.equals(id, that.id) &&
                Objects.equals(fromNode, that.fromNode) &&
                Objects.equals(agreedPid, that.agreedPid) &&
                Objects.equals(message, that.message) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequence, id, fromNode, agreedPid, message, status);
    }

    @Override
    public String toString() {
        return "BufferedMessage{" +
                "sequence=" + sequence +
                ", id=" + id +
                ", fromNode=" + fromNode +
                ", agreedPid=" + agreedPid +
                ", message='" + message + '\'' +
                ", status=" + status +
                '}';
    }
}