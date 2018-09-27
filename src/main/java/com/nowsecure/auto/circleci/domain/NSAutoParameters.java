package com.nowsecure.auto.circleci.domain;

import java.io.File;

public interface NSAutoParameters {

    String getApiUrl();

    String getApiKey();

    String getGroup();

    File getBinaryName();

    int getWaitMinutes();

    int getScoreThreshold();

}