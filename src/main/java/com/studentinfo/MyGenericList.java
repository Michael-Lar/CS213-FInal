package com.studentinfo;

import java.io.Serializable;

/**
 * Generic linked list implementation with sorting capability.
 * @param <T> Type that implements Comparable
 */
class MyGenericList<T extends Comparable<T>> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static class Node<T> implements Serializable {
        private static final long serialVersionUID = 2L;
        T value;
        Node<T> next;
        
        Node(T value) {
            this.value = value;
            this.next = null;
        }
    }
    
    private Node<T> first = null;
    private int count = 0;
    
    public void add(T element) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot add null element to list");
        }
        
        Node<T> newNode = new Node<>(element);
        if (first == null) {
            first = newNode;
        } else {
            Node<T> lastNode = getLastNode(first);
            lastNode.next = newNode;
        }
        count++;
    }
    
    public T get(int pos) {
        if (pos < 0 || pos >= count) {
            throw new IndexOutOfBoundsException("Position " + pos + " is out of bounds");
        }
        
        Node<T> current = first;
        for (int i = 0; i < pos; i++) {
            current = current.next;
        }
        return current.value;
    }
    
    public void delete(int pos) {
        if (pos < 0 || pos >= count) {
            throw new IndexOutOfBoundsException("Position " + pos + " is out of bounds");
        }
        
        if (pos == 0) {
            first = first.next;
        } else {
            Node<T> previous = first;
            for (int i = 0; i < pos - 1; i++) {
                previous = previous.next;
            }
            previous.next = previous.next.next;
        }
        count--;
    }
    
    public void sort() {
        if (count <= 1) return;
        
        boolean swapped;
        do {
            swapped = false;
            Node<T> current = first;
            Node<T> previous = null;
            
            while (current != null && current.next != null) {
                if (current.value.compareTo(current.next.value) > 0) {
                    Node<T> next = current.next;
                    current.next = next.next;
                    next.next = current;
                    
                    if (previous == null) {
                        first = next;
                    } else {
                        previous.next = next;
                    }
                    
                    previous = next;
                    swapped = true;
                } else {
                    previous = current;
                    current = current.next;
                }
            }
        } while (swapped);
    }
    
    public int size() {
        return count;
    }
    
    private Node<T> getLastNode(Node<T> node) {
        if (node == null) {
            return null;
        }
        if (node.next == null) {
            return node;
        }
        return getLastNode(node.next);
    }
} 