package ru.fizteh.fivt.students.pavellevap.twitter;

import com.beust.jcommander.*;

public class TwitterStreamArgs {
    @Parameter (names = {"--query", "-q"}, description = "keyword for search")
    private String keyword = "";

    @Parameter (names = {"--stream", "-s"}, description = "if this parameter is set, then "
            + "the program will print twits with 1sec delay")
    private Boolean streamMode = false;

    @Parameter (names = {"--hideRetweets"}, description = "if this parameter is set, then "
            + "the program will not show retweets")
    private Boolean hideRetweets = false;

    @Parameter (names = {"--limit", "-l"}, description = "restriction on amount of tweets")
    private Integer limit = Integer.MAX_VALUE;

    @Parameter (names = {"--help", "-h"}, description = "use help mode")
    private Boolean helpMode = false;

    public String getKeyword() {
        return keyword;
    }

    public Boolean isStreamMode() {
        return streamMode;
    }

    public Boolean isHideRetweets() {
        return hideRetweets;
    }

    public Integer getLimit() {
        return limit;
    }

    public Boolean isHelpMode() {
        return helpMode;
    }
};
