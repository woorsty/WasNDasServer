package de.dennis.wasndasmeins.glide;

import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.InputStream;

public class IPv6UrlLoaderFactory implements ModelLoaderFactory<String, InputStream> {
    @Override
    public ModelLoader<String, InputStream> build(MultiModelLoaderFactory multiFactory) {
        return new IPv6UrlLoader();
    }

    @Override
    public void teardown() {
        // Hier gibt es nichts aufzuräumen
    }
}
