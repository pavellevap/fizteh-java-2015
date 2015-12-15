package ru.fizteh.fivt.students.pavellevap.twitter;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.junit.Test;
import org.junit.Assert;
import org.mockito.Mock;
import twitter4j.*;

import static org.mockito.Mockito.*;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

@RunWith(MockitoJUnitRunner.class)
class TextFormatterTest {
    @Mock
    private
    Status tweet;

    @Mock
    private
    Status retweeted;

    @Mock
    private
    User user1;

    @Mock
    private
    User user2;

    @Before
    public void setUp() {
        tweet = mock(Status.class);
        retweeted = mock(Status.class);
        user1 = mock(User.class);
        user2 = mock(User.class);
    }

    @Test
    public void testGetFormattedDate() {
        Calendar baseDate = new GregorianCalendar();
        Calendar someDate = new GregorianCalendar();

        someDate.add(Calendar.MINUTE, 1);
        Assert.assertEquals(TextFormatter.getFormattedDate(baseDate.getTime(), someDate.getTime()),
                "Только что");
        someDate.add(Calendar.MINUTE, 2);
        Assert.assertEquals(TextFormatter.getFormattedDate(baseDate.getTime(), someDate.getTime()),
                "3 минут назад");
        someDate.add(Calendar.HOUR, 1);
        Assert.assertEquals(TextFormatter.getFormattedDate(baseDate.getTime(), someDate.getTime()),
                "1 часов назад");
        someDate.add(Calendar.DAY_OF_YEAR, 1);
        Assert.assertEquals(TextFormatter.getFormattedDate(baseDate.getTime(), someDate.getTime()),
                "Вчера");
        someDate.add(Calendar.DAY_OF_YEAR, 1);
        Assert.assertEquals(TextFormatter.getFormattedDate(baseDate.getTime(), someDate.getTime()),
                "2 дней назад");
    }

    @Test
    public void testGetTextForTweet() {
        when(tweet.getText()).thenReturn("some tweet text");
        when(tweet.getCreatedAt()).thenReturn(new Date());
        when(tweet.isRetweet()).thenReturn(false);
        when(tweet.isRetweeted()).thenReturn(false);
        when(tweet.getUser()).thenReturn(user1);
        when(user1.getName()).thenReturn("user1");
        when(tweet.getRetweetedStatus()).thenReturn(retweeted);
        when(retweeted.getUser()).thenReturn(user2);
        when(user2.getName()).thenReturn("user2");
        when(tweet.getRetweetCount()).thenReturn(100);

        StringBuilder formattedDate = new StringBuilder();
        formattedDate.append("[").append(TextFormatter.makeColor("Только что", TextFormatter.RED))
                .append("] ");
        StringBuilder user1Nick = new StringBuilder();
        user1Nick.append("@").append(TextFormatter.makeColor("user1", TextFormatter.BLUE)).append(": ");
        StringBuilder user2Nick = new StringBuilder();
        user2Nick.append("@").append(TextFormatter.makeColor("user2", TextFormatter.BLUE)).append(": ");
        StringBuilder tweetText = new StringBuilder("some tweet text");

        // test1
        StringBuilder rightAnswer = new StringBuilder();
        rightAnswer.append(formattedDate).append(user1Nick).append(tweetText);
        Assert.assertEquals(rightAnswer.toString(), TextFormatter.getTextForTweet(tweet, false));

        // test2
        rightAnswer = new StringBuilder();
        rightAnswer.append(user1Nick).append(tweetText);
        Assert.assertEquals(rightAnswer.toString(), TextFormatter.getTextForTweet(tweet, true));

        //test3
        when(tweet.isRetweet()).thenReturn(true);
        rightAnswer = new StringBuilder();
        rightAnswer.append(formattedDate).append(user1Nick).append("ретвитнул ").
                append(user2Nick).append(tweetText);
        Assert.assertEquals(rightAnswer.toString(), TextFormatter.getTextForTweet(tweet, false));

        //test 4
        when(tweet.isRetweet()).thenReturn(false);
        when(tweet.isRetweeted()).thenReturn(true);
        rightAnswer = new StringBuilder();
        rightAnswer.append(formattedDate).append(user1Nick).append(tweetText).append("(100 ретвитов)");
        Assert.assertEquals(rightAnswer.toString(), TextFormatter.getTextForTweet(tweet, false));
    }
}