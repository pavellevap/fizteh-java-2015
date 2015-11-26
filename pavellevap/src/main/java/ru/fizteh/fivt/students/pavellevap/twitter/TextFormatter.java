package ru.fizteh.fivt.students.pavellevap.twitter;

import twitter4j.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class TextFormatter {

    static final char ESC = 27;

    static final String NORMAL_MODE = "[0m";

    static final String BLACK =       "[30m";
    static final String RED =         "[31m";
    static final String GREEN =       "[32m";
    static final String YELLOW =      "[33m";
    static final String BLUE =        "[34m";
    static final String WHITE =       "[37m";

    static final Long ZERO =              0L;
    static final Long ONE =               1L;
    static final Long TWO =               2L;

    static String makeColor(String text, String color) {
        return ESC + color + text + ESC + NORMAL_MODE;
    }

    static String getTweetDate(Status tweet) {
        StringBuilder dateInText = new StringBuilder();

        LocalDateTime currDate = LocalDateTime.now();
        LocalDateTime tweetDate = tweet.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        if (ChronoUnit.MINUTES.between(tweetDate, currDate) < TWO) {
            dateInText.append("Только что");
        } else if (ChronoUnit.HOURS.between(tweetDate, currDate) == ZERO) {
            dateInText.append(ChronoUnit.MINUTES.between(tweetDate, currDate)).append(" минут назад");
        } else if (ChronoUnit.DAYS.between(tweetDate, currDate) == ZERO) {
            dateInText.append(ChronoUnit.HOURS.between(tweetDate, currDate)).append(" часов назад");
        } else if (ChronoUnit.DAYS.between(tweetDate, currDate) == ONE) {
            dateInText.append("Вчера");
        } else {
            dateInText.append(ChronoUnit.DAYS.between(tweetDate, currDate)).append(" дней назад");
        }

        return dateInText.toString();
    }

    static String printNick(Status tweet) {
        return "@" + makeColor(tweet.getUser().getName(), BLUE);
    }

    static String getTextForTweet(Status tweet, boolean isStream) {
        StringBuilder textForTweet = new StringBuilder();

        if (!isStream) {
            textForTweet.append("[").
                    append(makeColor(getTweetDate(tweet), RED)).
                    append("] ");
        }

        textForTweet.append(printNick(tweet)).append(": ");

        if (tweet.isRetweet()) {
            textForTweet.append("ретвитнул ").
                    append(printNick(tweet.getRetweetedStatus())).
                    append(": ");
        }

        textForTweet.append(tweet.getText());

        if (!tweet.isRetweet() && tweet.isRetweeted()) {
            textForTweet.append("(").
                    append(tweet.getRetweetCount()).
                    append(" ретвитов)");
        }

        return textForTweet.toString();
    }
};
