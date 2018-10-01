package com.nowsecure.auto.circleci.domain;

import java.io.File;

public interface NSAutoParameters {

    String getApiUrl();

    String getApiKey();

    String getGroup();

    File getFile();

    int getWaitMinutes();

    int getScoreThreshold();

}