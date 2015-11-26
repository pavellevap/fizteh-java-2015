package ru.fizteh.fivt.students.pavellevap.reverse;

public class Main {
    public static void main(String[] args) {
        print(reverse(split(args)));
    }

    public static String[] reverse(String[] args) {
        for (int i = 0; i < args.length / 2; i++) {
            String tmp = args[i];
            args[i] = args[args.length - i - 1];
            args[args.length - i - 1] = tmp;
        }

        return args;
    }

    public static String[] split(String[] args) {
        String res = "";
        for (String s : args) {
            res += s + " ";
        }

        return res.split("[ \n\t\r]");
    }

    public static void print(String[] args) {
        for (String s : args) {
            System.out.print(s + " ");
        }
        System.out.println();
    }

}
