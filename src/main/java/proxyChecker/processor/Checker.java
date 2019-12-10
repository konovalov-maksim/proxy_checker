package proxyChecker.processor;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;


public class Checker{
    private int maxThreads = 1;
    private int threads;
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
                loaders.add(new Loader(extProxy, timeout, url));
    }

    private void runNewLoader() {
        while (loaders.size() > 0 && threads < maxThreads) {
            loaders.pop().run();
            threads++;
        }
    }

    public interface loadingListener {
        void onResponse();
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

}
