package edu.buffalo.cse.cse486586.groupmessenger2;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Payload implements Serializable {
    private static final long serialVersionUID = -2820391903021457372L;

    private Type type;
    private String fromNode; // port number

    private int sequence;
    private String message;

    private float proposedSequence;
    private float agreedSequence;
    private int agreedNode;
    private UUID id;

    public enum Type {
        INITIAL_MESSAGE,
        SEQUENCE_PROPOSAL,
        SEQUENCE_AGREEMENT
    }

    public static Payload newMessage(String message, String fromNode, int sequence) {
        Payload payload = new Payload();
        payload.type = Type.INITIAL_MESSAGE;
        payload.message = message;
        payload.fromNode = fromNode;
        payload.sequence = sequence;
        payload.id = UUID.randomUUID();
        return payload;
    }

    public void toProposal(float proposedSequence, String fromNode) {
        this.type = Type.SEQUENCE_PROPOSAL;
        this.fromNode = fromNode;
        this.proposedSequence = proposedSequence;
    }

    public void toAgreement(String fromNode, float agreedSequence) {
        this.type = Type.SEQUENCE_AGREEMENT;
        this.fromNode = fromNode;
        this.agreedSequence = agreedSequence;
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

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public float getProposedSequence() {
        return proposedSequence;
    }

    public void setProposedSequence(float proposedSequence) {
        this.proposedSequence = proposedSequence;
    }

    public float getAgreedSequence() {
        return agreedSequence;
    }

    public void setAgreedSequence(float agreedSequence) {
        this.agreedSequence = agreedSequence;
    }

    public int getAgreedNode() {
        return agreedNode;
    }

    public void setAgreedNode(int agreedNode) {
        this.agreedNode = agreedNode;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payload payload = (Payload) o;
        return Objects.equals(sequence, payload.sequence) &&
                Objects.equals(proposedSequence, payload.proposedSequence) &&
                Objects.equals(agreedSequence, payload.agreedSequence) &&
                Objects.equals(agreedNode, payload.agreedNode) &&
                Objects.equals(type, payload.type) &&
                Objects.equals(fromNode, payload.fromNode) &&
                Objects.equals(message, payload.message) &&
                Objects.equals(id, payload.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, fromNode, sequence, message, proposedSequence, agreedSequence, agreedNode, id);
    }

    @Override
    public String toString() {
        return "Payload{" +
                "type=" + type +
                ", fromNode='" + fromNode + '\'' +
                ", sequence=" + sequence +
                ", message='" + message + '\'' +
                ", proposedSequence=" + proposedSequence +
                ", agreedSequence=" + agreedSequence +
                ", agreedNode=" + agreedNode +
                ", id=" + id +
                '}';
    }
}