package proxyChecker.processor;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

class Loader implements Callback {
    private ExtendedProxy extProxy;
    private OkHttpClient client;
    private Request request;

    Loader(ExtendedProxy extProxy, long timeout, String url) {
        this.extProxy = extProxy;
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
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

    }

    interface loadingListener {

    }
}
