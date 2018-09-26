package com.nowsecure.auto.circleci;

import java.io.File;
import java.io.IOException;

import com.nowsecure.auto.circleci.domain.NSAutoParameters;
import com.nowsecure.auto.circleci.gateway.NSAutoGateway;

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
        new NSAutoGateway(this).execute();
    }

    @Override
    public String toString() {
        return "Main [artifactsDir=" + artifactsDir + ", apiUrl=" + apiUrl + ", group=" + group + ", binaryName="
               + binaryName + ", waitMinutes=" + waitMinutes + ", breakBuildOnScore=" + breakBuildOnScore
               + ", scoreThreshold=" + scoreThreshold + ", apiKey=" + apiKey + "]";
    }

    private static int parseInt(String name) {
        String value = System.getProperty(name, "").trim();
        if (value.length() == 0) {
            value = System.getenv(name);
            if (value == null) {
                return 0;
            }
            value = value.trim();
        }
        value = value.replaceAll("\\D+", "");
        if (value.length() == 0) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    private static String getString(String name, String def) {
        String value = System.getProperty(name, "").trim();
        if (value.length() == 0) {
            value = System.getenv(name);
            if (value == null) {
                return def;
            }
            value = value.trim();
        }
        value = value.replace("<nil>", "");
        return value.length() == 0 ? def : value;
    }

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        main.parseArgs(args);

        main.execute();
    }

    private void usage(String msg) {
        System.err.println(this);

        System.err.println(msg);
        System.err.println("Usage:\n");
        System.err.println(
                "\tgradle run --args=\"-u auto-url -t api-token -t mobile-binary-file -d artifacts-dir -g user-group -f binary-file -w wait-for-completion-in-minutes -s min-score-to-pass\"");
        System.err.println("\tOR");
        System.err
                .println(
                        "Usage: gradle run -Dauto.url=auto-url -Dauto.token=api-token -Dauto.file=mobile-binary-file -Dauto.dir=artifacts-dir"
                         + " -Dauto.group=user-group -Dauto.file=binary-file -Dauto.wait=wait-for-completion-in-minutes -Dauto.score=min-score-to-pass");
        System.err.println("\tDefault url is " + DEFAULT_URL);
        System.err.println("\tDefault auto-wait is 0, which means just upload without waiting for results");
        System.err.println(
                "\tDefault auto-score is 0, which means build won't break, otherwise build will break if the app score is lower than this number");
        System.exit(1);
    }

    private static boolean isEmpty(String m) {
        return m == null || m.trim().length() == 0;
    }

    private void parseArgs(String[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            if ("-u".equals(args[i])) {
                this.apiUrl = args[i + 1].trim();
            } else if ("-g".equals(args[i])) {
                this.apiUrl = args[i + 1].trim();
            } else if ("-f".equals(args[i])) {
                this.binaryName = new File(args[i + 1].trim());
            } else if ("-d".equals(args[i])) {
                this.artifactsDir = new File(args[i + 1].trim());
            } else if ("-t".equals(args[i])) {
                this.apiKey = args[i + 1].trim();
            } else if ("-w".equals(args[i])) {
                this.waitMinutes = Integer.parseInt(args[i + 1].trim());
            } else if ("-s".equals(args[i])) {
                this.scoreThreshold = Integer.parseInt(args[i + 1].trim());
            }
        }
        if (isEmpty(this.group)) {
            this.group = getString("auto.group", "");
        }
        if (isEmpty(this.apiUrl)) {
            this.apiUrl = getString("auto.url", DEFAULT_URL);
        }
        if (isEmpty(this.apiKey)) {
            this.apiKey = getString("auto.token", "");
            if (this.apiKey.length() == 0) {
                this.usage("auto-token is not defined");
            }
        }
        if (binaryName == null) {
            String val = getString("auto.file", "");
            if (val.length() == 0) {
                this.usage("auto-file is not defined");
            }
            this.binaryName = new File(val);
        }
        if (artifactsDir == null) {
            String val = getString("auto.dir", "");
            if (val.length() == 0) {
                this.usage("auto-dir is not defined for artifacts");
            }
            this.artifactsDir = new File(val);
            if (!this.artifactsDir.exists() && !this.artifactsDir.mkdirs()) {
                this.usage("auto-dir [" + this.artifactsDir + "] could not be created");
            }
        }
        if (this.waitMinutes == 0) {
            this.waitMinutes = parseInt("auto.wait");
        }
        if (this.scoreThreshold == 0) {
            this.scoreThreshold = parseInt("auto.score");
        }
        System.err.println(this);
    }
}
