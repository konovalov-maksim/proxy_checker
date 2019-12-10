package proxyChecker.processor;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;


public class Checker {
    private int maxThreads = 1;
    private int checksCount;
    private int timeout = 5000;
    private String url;
    private Deque<Loader> loaders = new ConcurrentLinkedDeque<>();
    private List<ExtendedProxy> proxies = new ArrayList<>();


    public Checker(String url, List<ExtendedProxy> proxies) {
        this.url = url;
        this.proxies.addAll(proxies);
    }

    public void start() {
        createLoaders();
    }

    private void createLoaders() {
        for (int i = 0; i < checksCount; i++)
            for (ExtendedProxy extProxy : proxies)
                loaders.add(new Loader(extProxy));
    }

    private void startNewChecks() {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        Request request = new Request.Builder()
                .url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                results.indexOf()
            }
        });
    }

    public interface loadingListener {
        public void onResponse();
    }
}
