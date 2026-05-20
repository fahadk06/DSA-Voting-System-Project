package com.votingsystem.dsa;

public class CandidateLinkedList {

    // ── Single node = one candidate
    private static class Node {
        int    id;
        String name;
        String party;
        String area;
        int    votes;
        Node   next;

        Node(int id, String name, String party, String area, int votes) {
            this.id    = id;
            this.name  = name;
            this.party = party;
            this.area  = area;
            this.votes = votes;
            this.next  = null;
        }
    }

    private Node head = null;
    private int  size = 0;

    // Add candidate to end of list
    public void add(int id, String name, String party, String area, int votes) {
        Node newNode = new Node(id, name, party, area, votes);
        if (head == null) {
            head = newNode;
        } else {
            Node curr = head;
            while (curr.next != null) curr = curr.next;
            curr.next = newNode;
        }
        size++;
    }

    // Clear list
    public void clear() {
        head = null;
        size = 0;
    }

    // Next index — circular forward
    public int nextIndex(int current) {
        return (current + 1) % size;
    }

    // Prev index — circular backward
    public int prevIndex(int current) {
        return (current - 1 + size) % size;
    }

    public int size() { return size; }
}