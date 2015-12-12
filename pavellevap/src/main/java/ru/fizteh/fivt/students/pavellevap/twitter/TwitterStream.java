package ru.fizteh.fivt.students.pavellevap.twitter;

import java.util.*;
import com.beust.jcommander.JCommander;
import twitter4j.*;

public class TwitterStream {
    static final int SECOND = 1000;
    static final int MAX_AMOUNT_OF_TRIES = 2;

    public static void main(String[] args) {
        TwitterStreamArgs twitterStreamArgs = new TwitterStreamArgs();
        JCommander jCommander = new JCommander(twitterStreamArgs, args);

        if (twitterStreamArgs.isHelpMode()) {
            jCommander.setProgramName("TwitterStream");
            jCommander.usage();
        } else {
            if (twitterStreamArgs.getKeyword() == "") {
                System.out.println("Необходимо указать ключевые слова для поиска\n"
                        + "Для справки см. TwitterStream --help");
            } else {
                if (twitterStreamArgs.isStreamMode()) {
                    workInStreamMode(twitterStreamArgs);
                } else {
                    try {
                        workInNormalMode(twitterStreamArgs);
                    } catch (TwitterException ex) {
                        System.err.println("Не удалось выполнить запрос. Возможно ошибка соединения");
                        System.err.println(ex.getMessage());
                        System.exit(1);
                    }
                }
            }
        }
    }

    static void workInStreamMode(TwitterStreamArgs args) {
        Queue<Status> tweets = prepareForListening(args);

        while (true) {
            if (!tweets.isEmpty()) {
                System.out.println(TextFormatter.getTextForTweet(tweets.poll(), true));
            }

            try {
                Thread.sleep(SECOND);
            } catch (Exception ex) {
                throw new RuntimeException();
            }
        }
    }

    static void workInNormalMode(TwitterStreamArgs args) throws TwitterException {
        Twitter twitter;
        Query query;
        List<Status> tweets = new LinkedList<>();

        int amountOfTries = 0;
        while (amountOfTries < MAX_AMOUNT_OF_TRIES) {
            try {
                twitter = TwitterFactory.getSingleton();
                query = new Query(args.getKeyword());
                query.setCount(Integer.MAX_VALUE);

                tweets = twitter.search(query).getTweets();

                amountOfTries = MAX_AMOUNT_OF_TRIES;
            } catch (TwitterException ex) {
                amountOfTries++;
                if (amountOfTries == MAX_AMOUNT_OF_TRIES) {
                    throw ex;
                }
            }
        }

        int amountOfPrintedTweets = 0;
        for (Status tweet : tweets) {
            if (!args.isHideRetweets() || !tweet.isRetweet()) {
                System.out.println(TextFormatter.getTextForTweet(tweet, false));
                amountOfPrintedTweets++;
            }
            if (amountOfPrintedTweets >= args.getLimit()) {
                break;
            }
        }

    }

    static Queue<Status> prepareForListening(TwitterStreamArgs args) {
        Queue<Status> tweets = new LinkedList<>();

        twitter4j.TwitterStream twitterStream = new TwitterStreamFactory().getSingleton();
        twitter4j.StatusListener listener = new StatusAdapter() {
            @Override
            public void onStatus(Status tweet) {
                if (!args.isHideRetweets() || !tweet.isRetweet()) {
                    tweets.add(tweet);
                }
            }

            @Override
            public void onException(Exception e) {
                System.err.println("Что то пошло не так во время прослушивания.");
                System.err.println(e.getMessage());
            }
        };

        twitterStream.addListener(listener);
        twitterStream.filter(new FilterQuery().track(args.getKeyword()));

        return tweets;
    }
}
