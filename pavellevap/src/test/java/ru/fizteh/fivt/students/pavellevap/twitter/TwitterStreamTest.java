package ru.fizteh.fivt.students.pavellevap.twitter;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.api.mockito.PowerMockito;
import twitter4j.*;

import static org.mockito.Mockito.*;
import static org.mockito.Matchers.any;
import static org.hamcrest.Matchers.*;

import java.util.LinkedList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ TwitterFactory.class, TwitterStream.class, TextFormatter.class })
public class TwitterStreamTest {
    @Mock
    private static Twitter twitter;

    @Mock
    private static TwitterStreamArgs args;

    private static List<Status> tweets;

    @BeforeClass
    static public void setUp() throws Exception {
        twitter = mock(Twitter.class);
        args = mock(TwitterStreamArgs.class);
        tweets = Twitter4jTestUtils.tweetsFromJson("/twitterResponse.json");

        PowerMockito.mockStatic(TextFormatter.class);

        PowerMockito.mockStatic(TwitterFactory.class);
        PowerMockito.when(TwitterFactory.getSingleton()).thenReturn(twitter);

        QueryResult resultForJava = mock(QueryResult.class);
        when(resultForJava.getTweets()).thenReturn(tweets);
        when(twitter.search(argThat(hasProperty("query", equalTo("java"))))).thenReturn(resultForJava);

        QueryResult emptyResult = mock(QueryResult.class);
        when(emptyResult.getTweets()).thenReturn(new LinkedList<>());
        when(twitter.search(argThat(hasProperty("query", not(equalTo("java")))))).thenReturn(emptyResult);
    }

    @Test (expected = TwitterException.class)
    public void throwingExceptionTest() throws Exception {
        when(twitter.search(any(Query.class))).thenThrow(new TwitterException(""));
        when(args.getKeyword()).thenReturn("java");

        try {
            TwitterStream.workInNormalMode(args);
        } finally {
            verify(twitter, times(TwitterStream.MAX_AMOUNT_OF_TRIES)).search(any(Query.class));
        }
    }
}