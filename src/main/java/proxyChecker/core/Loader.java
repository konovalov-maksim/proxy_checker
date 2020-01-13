package proxyChecker.core;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

class Loader implements Callback {
    private ExtendedProxy extProxy;
    private OkHttpClient client;
    private Request request;
    private LoadingListener loadingListener;

    Loader(ExtendedProxy extProxy, long timeout, String url, LoadingListener loadingListener) {
        this.extProxy = extProxy;
        this.loadingListener = loadingListener;
        if (extProxy.getLogin() != null && extProxy.getPass() != null)
            client = new OkHttpClient.Builder()
                    .proxy(extProxy.getProxy())
                    .authenticator(extProxy.getAuth())
                    .callTimeout(timeout, TimeUnit.MILLISECONDS)
                    .build();
        else client = new OkHttpClient.Builder()
                .proxy(extProxy.getProxy())
                .callTimeout(timeout, TimeUnit.MILLISECONDS)
                .build();
        request = new Request.Builder()
                .url(url)
                .build();
    }

    void run() {
        client.newCall(request).enqueue(this);
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        extProxy.addResponseCode(408);
        extProxy.addResponseTime(0L);
        loadingListener.onLoadingComplete(extProxy);
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        extProxy.addResponseCode(response.code());
        extProxy.addResponseTime(response.receivedResponseAtMillis() - response.sentRequestAtMillis());
        loadingListener.onLoadingComplete(extProxy);
    }

    interface LoadingListener {
        void onLoadingComplete(ExtendedProxy extProxy);
    }
}
