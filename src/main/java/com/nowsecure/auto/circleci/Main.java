package com.nowsecure.auto.circleci;

import java.io.File;
import java.io.IOException;

import com.nowsecure.auto.circleci.domain.NSAutoParameters;
import com.nowsecure.auto.circleci.gateway.AutoGateway;

/**
 * This class defines business logic for uploading mobile binary and retrieving
 * results and score. It would fail the job if score is below user-defined
 * threshold.
 * 
 * @author sbhatti
 *
 */
public class Main implements NSAutoParameters {
    private static final String DEFAULT_URL = "https://lab-api.nowsecure.com";
    private File artifactsDir;
    private String apiUrl = DEFAULT_URL;
    private String group;
    private File binaryName;
    private int waitMinutes;
    private boolean breakBuildOnScore;
    private int scoreThreshold;
    private String apiKey;

    /*
     * (non-Javadoc)
     * 
     * @see com.nowsecure.auto.jenkins.plugin.NSAutoParameters#getArtifactsDir()
     */
    public File getArtifactsDir() {
        return artifactsDir;
    }

    public void setArtifactsDir(File artifactsDir) {
        this.artifactsDir = artifactsDir;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nowsecure.auto.jenkins.plugin.NSAutoParameters#getApiUrl()
     */
    @Override
    public String getApiUrl() {
        return apiUrl != null && apiUrl.length() > 0 ? apiUrl : DEFAULT_URL;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nowsecure.auto.jenkins.plugin.NSAutoParameters#getGroup()
     */
    @Override
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nowsecure.auto.jenkins.plugin.NSAutoParameters#getBinaryName()
     */
    @Override
    public File getBinaryName() {
        return binaryName;
    }

    public void setBinaryName(File binaryName) {
        this.binaryName = binaryName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nowsecure.auto.jenkins.plugin.NSAutoParameters#getWaitMinutes()
     */
    @Override
    public int getWaitMinutes() {
        return waitMinutes;
    }

    public void setWaitMinutes(int waitMinutes) {
        this.waitMinutes = waitMinutes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.nowsecure.auto.jenkins.plugin.NSAutoParameters#getScoreThreshold()
     */
    @Override
    public int getScoreThreshold() {
        return scoreThreshold;
    }

    public void setScoreThreshold(int scoreThreshold) {
        this.scoreThreshold = scoreThreshold;
    }

    public void execute() throws IOException {
        new AutoGateway(this).execute();
    }

    @Override
    public String toString() {
        return "NSAutoPlugin [apiUrl=" + apiUrl + ", group=" + group + ", binaryName=" + binaryName + ", waitMinutes="
               + waitMinutes + ", breakBuildOnScore=" + breakBuildOnScore + ", scoreThreshold=" + scoreThreshold
               + ", apiKey=" + (apiKey != null ? "****" : "undefined") + "]";
    }

    private static void usage(String msg) {
        System.err.println(msg);
        System.err
                .println(
                        "Usage: ./gradlew run -Dauto.url=auto-url -Dauto.token=api-token -Dauto.file=mobile-binary-file -Dauto.dir=artifacts-dir"
                         + " -Dauto.group=user-group -Dauto.file=binary-file -Dauto.wait=wait-for-completion-in-minutes -Dauto.score=min-score-to-pass");
        System.err.println("\tDefault url is " + DEFAULT_URL);
        System.err.println("\tDefault auto.wait is 0, which means just upload without waiting for results");
        System.err.println(
                "\tDefault auto.score is 0, which means build won't break, otherwise build will break if the app score is lower than this number");
        System.exit(1);
    }

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        main.apiUrl = System.getProperty("auto.url", DEFAULT_URL);
        if (System.getProperty("auto.file") == null) {
            usage("auto.file system property is not defined");
        }
        main.binaryName = new File(System.getProperty("auto.file"));
        if (System.getProperty("auto.dir") == null) {
            usage("auto.dir system property is not defined for artifacts");
        }
        main.artifactsDir = new File(System.getProperty("auto.dir"));
        if (!main.artifactsDir.exists() && !main.artifactsDir.mkdirs()) {
            usage("auto.dir [" + main.artifactsDir + "] could not be created");
        }
        main.apiKey = System.getProperty("auto.token");
        if (main.apiKey == null) {
            usage("auto.token system property is not defined");
        }
        main.waitMinutes = Integer.parseInt(System.getProperty("auto.wait", "0"));
        main.scoreThreshold = Integer.parseInt(System.getProperty("auto.score", "0"));
        main.group = System.getProperty("auto.group");
        main.execute();
    }
}
