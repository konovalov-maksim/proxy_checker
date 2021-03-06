package proxyChecker.core;

import okhttp3.Headers;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;


public class Checker implements Loader.LoadingListener {
    private int maxThreads = 5;
    private int threads;
    private int checksCount = 5;
    private int processedCount;
    private int timeout = 5000;
    private String url;
    private boolean isRunning = false;
    private Deque<Loader> loaders = new ConcurrentLinkedDeque<>();
    private List<ExtendedProxy> proxies = new ArrayList<>();
    private CheckingListener checkingListener;
    private Headers headers = new Headers.Builder()
            .add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:73.0) Gecko/20100101 Firefox/73.0").build();

    public Checker(String url, List<ExtendedProxy> proxies, CheckingListener checkingListener) {
        this.url = url;
        this.proxies.addAll(proxies);
        this.checkingListener = checkingListener;
    }

    public void start() {
        isRunning = true;
        createLoaders();
        runNewLoaders();
    }

    public void stop() {
        isRunning = false;
        checkingListener.onFinish();
    }

    private void createLoaders() {
        for (int i = 0; i < checksCount; i++)
            for (ExtendedProxy extProxy : proxies)
                loaders.add(new Loader(extProxy, timeout, url, headers, this));
    }

    private void runNewLoaders() {
        while (isRunning && loaders.size() > 0 && threads < maxThreads) {
            loaders.pop().run();
            threads++;
        }
    }

    @Override
    public synchronized void onLoadingComplete(ExtendedProxy extProxy) {
        threads--;
        processedCount++;
        if (isRunning) {
            checkingListener.onCheckComplete(extProxy);
            if (threads == 0 && loaders.isEmpty()) stop();
            else runNewLoaders();
        }
    }

    public interface CheckingListener {
        void onCheckComplete(ExtendedProxy extProxy);
        void onFinish();
    }

    public double getProgress() {
        return processedCount * 1.0 / (proxies.size() * checksCount);
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public int getChecksCount() {
        return checksCount;
    }

    public void setChecksCount(int checksCount) {
        this.checksCount = checksCount;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getThreads() {
        return threads;
    }

    public int getProcessedCount() {
        return processedCount;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }
}
