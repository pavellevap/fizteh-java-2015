package ru.fizteh.fivt.students.pavellevap.threads;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;

public class BlockingQueueTest {
    BlockingQueue<Integer> queue;

    @Before
    public void setUp() {
        queue = new BlockingQueue<>(4);
    }

    @Test
    public void testOverflowing() throws Exception {
        Thread thr = new Thread(() -> {
                try {
                    queue.offer(Arrays.asList(1, 2, 3, 4, 5));
                } catch (InterruptedException ex) {
            }
        });
        thr.start();
        thr.join(100);
        assertThat(thr.isAlive(), is(true));
        thr.interrupt();
    }

    @Test
    public void testEmptyQueue() throws Exception{
        Thread thr = new Thread(() -> {
           try {
               queue.take(3);
           } catch(InterruptedException ex) { }
        });
        thr.start();
        thr.join(100);
        assertThat(thr.isAlive(), is(true));
        thr.interrupt();
    }

    @Test
    public void testOneThread() throws Exception {
        List<Integer> array1 = Arrays.asList(1);
        List<Integer> array2 = Arrays.asList(2, 3, 4);
        queue.offer(array1);
        queue.offer(array2);
        array2 = queue.take(3);
        array1 = queue.take(1);
        assertThat(array1, hasSize(1));
        assertThat(array1, contains(4));
        assertThat(array2, hasSize(3));
        assertThat(array2, contains(1, 2, 3));
    }

    @Test
    public void testTwoThread() throws Exception {
        queue.offer(Arrays.asList(1, 2, 3, 4));
        Thread thr1 = new Thread(() -> {
            try {
                queue.offer(queue.take(1));
            } catch (InterruptedException ex) { }
        });
        Thread thr2 = new Thread(() -> {
            try {
                queue.offer(queue.take(3));
            } catch (InterruptedException ex) { }
        });
        thr1.start();
        thr2.start();
        thr1.join();
        thr2.join();
        assertThat(queue.take(4), containsInAnyOrder(1, 2, 3, 4));
    }
}