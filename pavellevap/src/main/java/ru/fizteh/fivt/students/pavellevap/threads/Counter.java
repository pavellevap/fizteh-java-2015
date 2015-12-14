package ru.fizteh.fivt.students.pavellevap.threads;

public class Counter {

    public static class Runner {
        private int currNumber = 1;

        public class NumberPrinter implements Runnable {
            NumberPrinter(int number) {
                this.number = number;
            }

            private int number;

            @Override
            public void run() {
                while (true) {
                    synchronized (Runner.this) {
                        try {
                            while (currNumber != number) {
                                Runner.this.wait();
                            }
                            System.out.println("Thread-" + number);
                            Thread.sleep(1000);
                            currNumber++;
                            Runner.this.notifyAll();
                        } catch (InterruptedException ex) {
                            System.err.println("Interrupted " + number);
                            return;
                        }
                    }
                }
            }
        }

        void run(int amountOfThreads) {
            for (int i = 0; i < amountOfThreads; i++) {
                (new Thread(new NumberPrinter(i + 1))).start();
            }

            while (true) {
                synchronized (this) {
                    try {
                        while (currNumber != amountOfThreads + 1) {
                            this.wait();
                        }
                        currNumber = 1;
                        this.notifyAll();
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
