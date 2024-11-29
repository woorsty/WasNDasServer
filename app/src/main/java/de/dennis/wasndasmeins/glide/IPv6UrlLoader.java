package de.dennis.wasndasmeins.glide;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class IPv6UrlLoader implements ModelLoader<String, InputStream> {

    @Override
    public LoadData<InputStream> buildLoadData(String model, int width, int height, Options options) {
        return new LoadData<>(new GlideUrl(model), new IPv6DataFetcher(model));
    }

    @Override
    public boolean handles(String model) {
        return model.startsWith("http://") || model.startsWith("https://");
    }

    public static class IPv6DataFetcher implements DataFetcher<InputStream> {
        private final String url;

        public IPv6DataFetcher(String url) {
            this.url = url;
        }

        @Override
        public void loadData(Priority priority, DataCallback<? super InputStream> callback) {
            try {
                URL formattedUrl = new URL(url); // Hier wird die URL verarbeitet
                HttpURLConnection connection = (HttpURLConnection) formattedUrl.openConnection();
                connection.connect();
                callback.onDataReady(connection.getInputStream());
            } catch (Exception e) {
                callback.onLoadFailed(e);
            }
        }

        @Override
        public void cleanup() {
            // Ressourcen aufräumen, falls nötig
        }

        @Override
        public void cancel() {
            // Anfrage abbrechen, falls nötig
        }

        @Override
        public Class<InputStream> getDataClass() {
            return InputStream.class;
        }

        @Override
        public DataSource getDataSource() {
            return DataSource.REMOTE;
        }
    }
}
