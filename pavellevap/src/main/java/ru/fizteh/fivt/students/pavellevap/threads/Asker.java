package ru.fizteh.fivt.students.pavellevap.threads;

import java.util.Random;

public class Asker {

    public static class Runner {
        private int questionCounter = 0;
        private boolean areAllReady = false;
        private int amountOfAnswers = 0;
        private Random random;

        public class AnswerPrinter implements Runnable {
            private int currQuestionNumber = 1;

            @Override
            public void run() {
                while (true) {
                    synchronized (Runner.this) {
                        try {
                            while (currQuestionNumber != questionCounter) {
                                Runner.this.wait();
                            }
                            if (random.nextDouble() < 0.1) {
                                System.out.println("NO");
                                areAllReady = false;
                            } else {
                                System.out.println("YES");
                            }
                            amountOfAnswers++;
                            currQuestionNumber++;
                            Runner.this.notifyAll();
                        } catch (InterruptedException ex) {
                            System.err.println("Interrupted");
                            return;
                        }
                    }
                }
            }
        }

        void run(int amountOfThreads) {
            random = new Random(3);

            for (int i = 0; i < amountOfThreads; i++) {
                (new Thread(new AnswerPrinter())).start();
            }


            synchronized (this) {
                while (!areAllReady) {
                    try {
                        System.out.println("Are you ready?");
                        questionCounter++;
                        areAllReady = true;
                        amountOfAnswers = 0;
                        this.notifyAll();

                        while (amountOfAnswers != amountOfThreads) {
                            this.wait();
                        }

                        if (areAllReady) {
                            break;
                        }
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        System.err.println("Interrupted");
                        return;
                    }
                }
            }
        }
    }


    public static void main(String[] args) {
        int n = 0;
        if (args.length > 0) {
            n = Integer.parseInt(args[0]);
        }

        (new Runner()).run(n);
    }
}
