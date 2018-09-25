package com.nowsecure.auto.circleci.utils;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BiPredicate;

public class IOHelper {
    private static final String GRADLE_PROPERTIES = "gradle.properties";
    private static final String USER_AGENT = "User-Agent";
    private static final String GET = "GET";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String AUTHORIZATION = "Authorization";
    private static final String POST = "POST";
    private static final int TIMEOUT = 60000;

    public static byte[] load(String file) throws IOException {
        return Files.readAllBytes(Paths.get(file));
    }

    public static String getVersion() {
        Properties props = new Properties();
        try {
            FileInputStream is = new FileInputStream(GRADLE_PROPERTIES);
            props.load(is);
            is.close();
        } catch (Exception e) {
        }
        return System.getProperty("version", "0.1-SNAPSHOTx");
    }

    public static File find(File parent, File file) throws IOException {
        if (file.isFile() && file.exists()) {
            return file;
        }
        Optional<Path> matched = Files
                .find(Paths.get(parent.getCanonicalPath()), 10, new BiPredicate<Path, BasicFileAttributes>() {
                    @Override
                    public boolean test(Path t, BasicFileAttributes u) {
                        return t.toString().endsWith(file.getName());
                    }
                }, FileVisitOption.FOLLOW_LINKS).distinct().findFirst();
        if (matched.isPresent()) {
            return matched.get().toFile();
        }
        return null;
    }

    public static void save(String path, String contents) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8))) {
            writer.write(contents.trim());
        }
    }

    public static byte[] load(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = in.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    public static String get(String uri, String apiKey) throws IOException {
        URL url = new URL(uri);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(GET);
        con.setRequestProperty(CONTENT_TYPE, "application/json");
        con.setRequestProperty(AUTHORIZATION, "Bearer " + apiKey);
        con.setRequestProperty(USER_AGENT, "Jenkins-Plugin v" + getVersion());
        con.setConnectTimeout(TIMEOUT);
        con.setReadTimeout(TIMEOUT);
        con.setInstanceFollowRedirects(false);
        InputStream in = con.getInputStream();
        String json = new String(load(in), StandardCharsets.UTF_8);
        in.close();
        con.disconnect();
        return json.trim();
    }

    public static String post(String uri, String apiKey) throws IOException {
        URL url = new URL(uri);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(POST);
        con.setRequestProperty(CONTENT_TYPE, "application/json");
        con.setRequestProperty(USER_AGENT, "Jenkins-Plugin v" + getVersion());
        con.setRequestProperty(AUTHORIZATION, "Bearer " + apiKey);
        con.setConnectTimeout(TIMEOUT);
        con.setReadTimeout(TIMEOUT);
        con.setInstanceFollowRedirects(false);
        InputStream in = con.getInputStream();
        String json = new String(load(in), StandardCharsets.UTF_8);
        in.close();
        con.disconnect();
        return json;
    }

    public static String upload(String uri, String apiKey, String file) throws IOException {
        URL url = new URL(uri);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(POST);
        con.setRequestProperty(CONTENT_TYPE, "application/json");
        con.setRequestProperty(USER_AGENT, "Jenkins-Plugin v" + getVersion());
        con.setRequestProperty(AUTHORIZATION, "Bearer " + apiKey);
        con.setConnectTimeout(TIMEOUT);
        con.setReadTimeout(TIMEOUT);
        con.setInstanceFollowRedirects(false);
        con.setDoOutput(true);
        OutputStream out = con.getOutputStream();
        byte[] binary = load(file);
        out.write(binary);
        out.flush();
        out.close();
        InputStream in = con.getInputStream();
        String json = new String(load(in), StandardCharsets.UTF_8);
        in.close();
        con.disconnect();
        return json;
    }

}
