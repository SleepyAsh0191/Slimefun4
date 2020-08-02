package io.github.thebusybiscuit.slimefun4.core.services.github;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONException;
import me.mrCookieSlime.Slimefun.api.Slimefun;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

abstract class GitHubConnector {

    protected File file;
    protected String repository;
    protected final GitHubService github;

    public GitHubConnector(GitHubService github, String repository) {
        this.github = github;
        this.repository = repository;
    }

    public abstract String getFileName();

    public abstract String getURLSuffix();

    public abstract void onSuccess(JsonNode element);

    public void onFailure() {
        // Don't do anything by default
    }

    public void pullFile() {
        file = new File("plugins/Slimefun/cache/github/" + getFileName() + ".json");

        if (github.isLoggingEnabled()) {
            Slimefun.getLogger().log(Level.INFO, "Retrieving {0}.json from GitHub...", getFileName());
        }

        try {
            HttpResponse<JsonNode> resp = Unirest.get("https://api.github.com/repos/" + repository + getURLSuffix())
                    .header("User-Agent", "Slimefun4 (https://github.com/Slimefun)")
                    .asJson();

            if (resp.isSuccess()) {
                onSuccess(resp.getBody());
                writeCacheFile(resp.getBody());
            } else {
                if (github.isLoggingEnabled()) {
                    Slimefun.getLogger().log(Level.WARNING, "Failed to fetch {0}: {1} - {2}", new Object[]{repository + getURLSuffix(), resp.getStatus(), resp.getBody()});
                }

                // It has the cached file, let's just read that then
                if (file.exists()) {
                    JsonNode cache = readCacheFile();

                    if (cache != null) {
                        onSuccess(cache);
                    }
                }
            }
        } catch (UnirestException e) {
            if (github.isLoggingEnabled()) {
                Slimefun.getLogger().log(Level.WARNING, "Could not connect to GitHub in time.");
            }

            // It has the cached file, let's just read that then
            if (file.exists()) {
                JsonNode cache = readCacheFile();

                if (cache != null) {
                    onSuccess(cache);
                    return;
                }
            }

            // If the request failed and it failed to read the cache then call onFailure.
            onFailure();
        }
    }

    private JsonNode readCacheFile() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            return new JsonNode(br.readLine());
        } catch (IOException | JSONException e) {
            Slimefun.getLogger().log(Level.WARNING, "Failed to read Github cache file: {0}", file.getName());
            return null;
        }
    }

    private void writeCacheFile(JsonNode node) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(node.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            Slimefun.getLogger().log(Level.WARNING, "Failed to populate GitHub cache");
        }
    }
}