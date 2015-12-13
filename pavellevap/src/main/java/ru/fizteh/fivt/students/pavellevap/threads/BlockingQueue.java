package ru.fizteh.fivt.students.pavellevap.threads;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BlockingQueue<T> {
    private int maxSize;
    private Queue<T> data;

    BlockingQueue(int maxAmountOfElements) {
        maxSize = maxAmountOfElements;
        data = new LinkedList<>();
    }

    public synchronized void offer(List<T> elements) throws InterruptedException {
        while (data.size() + elements.size() > maxSize) {
            wait();
        }
        data.addAll(elements);
        notifyAll();
    }

    public synchronized List<T> take(int amountOfElements) throws InterruptedException {
        while (data.size() < amountOfElements) {
            wait();
        }

        List<T> result = new LinkedList<>();
        while (amountOfElements > 0) {
            result.add(data.remove());
            amountOfElements--;
        }

        notifyAll();
        return result;
    }

    public int getMaxSize() {
        return maxSize;
    }
}
