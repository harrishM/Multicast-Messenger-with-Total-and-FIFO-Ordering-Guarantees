package edu.buffalo.cse.cse486586.groupmessenger2;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Payload implements Serializable {
    private static final long serialVersionUID = -2820391903021457372L;

    private Type type;
    private String fromNode; // port number

    private UUID id;
    private String message;

    private int proposedSequence;
    private int agreedSequence;

    public enum Type {
        INITIAL_MESSAGE,
        SEQUENCE_PROPOSAL,
        SEQUENCE_AGREEMENT
    }

    public static Payload newMessage(String message, String fromNode) {
        Payload payload = new Payload();
        payload.type = Type.INITIAL_MESSAGE;
        payload.message = message;
        payload.fromNode = fromNode;
        payload.id = UUID.randomUUID();
        return payload;
    }

    public static Payload newProposal(UUID id, int proposedSequence, String fromNode) {
        Payload payload = new Payload();
        payload.type = Type.SEQUENCE_PROPOSAL;
        payload.fromNode = fromNode;
        payload.id = id;
        payload.proposedSequence = proposedSequence;
        return payload;
    }

    public static Payload newAgreement(UUID id, int agreedSequence, String fromNode) {
        Payload payload = new Payload();
        payload.type = Type.SEQUENCE_AGREEMENT;
        payload.id = id;
        payload.fromNode = fromNode;
        payload.agreedSequence = agreedSequence;
        return payload;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getFromNode() {
        return fromNode;
    }

    public void setFromNode(String fromNode) {
        this.fromNode = fromNode;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getProposedSequence() {
        return proposedSequence;
    }

    public void setProposedSequence(int proposedSequence) {
        this.proposedSequence = proposedSequence;
    }

    public int getAgreedSequence() {
        return agreedSequence;
    }

    public void setAgreedSequence(int agreedSequence) {
        this.agreedSequence = agreedSequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payload payload = (Payload) o;
        return Objects.equals(proposedSequence, payload.proposedSequence) &&
                Objects.equals(agreedSequence, payload.agreedSequence) &&
                Objects.equals(type, payload.type) &&
                Objects.equals(fromNode, payload.fromNode) &&
                Objects.equals(id, payload.id) &&
                Objects.equals(message, payload.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, fromNode, id, message, proposedSequence, agreedSequence);
    }

    @Override
    public String toString() {
        return "Payload{" +
                "type=" + type +
                ", fromNode='" + fromNode + '\'' +
                ", id=" + id +
                ", message='" + message + '\'' +
                ", proposedSequence=" + proposedSequence +
                ", agreedSequence=" + agreedSequence +
                '}';
    }
}
