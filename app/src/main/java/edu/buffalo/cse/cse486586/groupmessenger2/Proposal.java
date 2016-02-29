package edu.buffalo.cse.cse486586.groupmessenger2;

import java.util.Objects;

public class Proposal {
    private int id;
    private String fromNode;
    private float proposedSequence;

    public Proposal(int id, String fromNode, float proposedSequence) {
        this.id = id;
        this.fromNode = fromNode;
        this.proposedSequence = proposedSequence;
    }

    public int getId() {
        return id;
    }

    public String getFromNode() {
        return fromNode;
    }

    public float getProposedSequence() {
        return proposedSequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proposal proposal = (Proposal) o;
        return Objects.equals(proposedSequence, proposal.proposedSequence) &&
                Objects.equals(id, proposal.id) &&
                Objects.equals(fromNode, proposal.fromNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fromNode, proposedSequence);
    }

    @Override
    public String toString() {
        return "Proposal{" +
                "id=" + id +
                ", fromNode='" + fromNode + '\'' +
                ", proposedSequence=" + proposedSequence +
                '}';
    }
}