package dev.leialoha.plugins.figuraserver.figura.types;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.gson.Gson;

@SuppressWarnings("unused")
public class Releases {
    
    private transient static Releases INSTANCE = null;

    public String prerelease = "0.0.0";
    public String release = "0.0.0";

    private Releases() {
        checkForUpdates();
    }

    public final static Releases getInstance() {
        if (INSTANCE == null) INSTANCE = new Releases();
        return INSTANCE;
    }

    // https://api.github.com/repos/FiguraMC/Figura/releases
    public final void checkForUpdates() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/repos/FiguraMC/Figura/releases"))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            Gson gson = new Gson();
            GitHubRelease[] releases = gson.fromJson(response.body(), GitHubRelease[].class);

            Optional<GitHubRelease> currentPrerelease = Stream.of(releases).filter(r -> r.prerelease).findFirst();
            Optional<GitHubRelease> currentRelease = Stream.of(releases).filter(r -> !r.prerelease).findFirst();

            this.release = currentRelease.get().tag_name;
            this.prerelease = currentPrerelease.orElse(currentRelease.get()).tag_name;
        } catch (IOException | InterruptedException | NoSuchElementException e) {
            e.printStackTrace();
        }
    }

    private class GitHubRelease {
        String url;
        String assets_url;
        String upload_url;
        String html_url;
        int id;
        GitHubAuthor author;
        String node_id;
        String tag_name;
        String target_commitish;
        String name;
        boolean draft;
        boolean prerelease;
        Date created_at;
        Date published_at;
        GitHubAssets[] assets;
        String tarball_url;
        String zipball_url;
        String body;
        String discussion_url;
        int mentions_count;
    }

    private class GitHubAuthor {
        String login;
        int id;
        String node_id;
        String avatar_url;
        String gravatar_id;
        String url;
        String html_url;
        String followers_url;
        String following_url;
        String gists_url;
        String starred_url;
        String subscriptions_url;
        String organizations_url;
        String repos_url;
        String events_url;
        String received_events_url;
        String type;
        boolean site_admin;
    }

    private class GitHubAssets {
        String url;
        int id;
        String node_id;
        String name;
        String label;
        GitHubAuthor uploader;
        String content_type;
        String state;
        int size;
        int download_count;
        Date created_at;
        Date updated_at;
        String browser_download_url;
    }

}
