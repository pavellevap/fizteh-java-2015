package ru.fizteh.fivt.students.pavellevap.threads;

import java.util.Random;

public class Asker {

    static public class Runner {
        volatile int questionCounter = 0;
        volatile boolean areAllReady = false;
        volatile int amountOfAnswers = 0;
        Random random;

        public class AnswerPrinter implements Runnable {
            int currQuestionNumber = 1;

            @Override
            public void run() {
                while (true) {
                    synchronized (Runner.this) {
                        try {
                            while (currQuestionNumber != questionCounter)
                                Runner.this.wait();
                            if (random.nextDouble() < 0.1) {
                                System.out.println("NO");
                                areAllReady = false;
                            } else {
                                System.out.println("YES");
                            }
                            amountOfAnswers++;
                            currQuestionNumber++;
                            Runner.this.notifyAll();
                        } catch (InterruptedException ex) {}
                    }
                }
            }
        }

        void run(int amountOfThreads) {
            random = new Random(3);

            for (int i = 0; i < amountOfThreads; i++)
                (new Thread(new AnswerPrinter())).start();



            while (!areAllReady) {
                synchronized (this) {
                    try {
                        System.out.println("Are you ready?");
                        questionCounter++;
                        areAllReady = true;
                        amountOfAnswers = 0;
                        this.notifyAll();

                        while (amountOfAnswers != amountOfThreads)
                            this.wait();

                        if (areAllReady)
                            break;
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {}
                }
            }
        }
    }


    public static void main(String[] args) {
        (new Runner()).run(10);
    }
}
