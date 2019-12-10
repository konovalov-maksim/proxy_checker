package proxyChecker.processor;

public class Loader {
    private ExtendedProxy extProxy;

    public Loader(ExtendedProxy extProxy) {
        this.extProxy = extProxy;
//        if (extProxy.getLogin() != null && extProxy.getPass() != null)
//            loaders.add(new OkHttpClient.Builder()
//                    .proxy(extProxy.getProxy())
//                    .authenticator(extProxy.getAuth())
//                    .callTimeout(timeout, TimeUnit.MILLISECONDS)
//                    .build());
//        else
//            loaders.add(new OkHttpClient.Builder()
//                    .proxy(extProxy.getProxy())
//                    .callTimeout(timeout, TimeUnit.MILLISECONDS)
//                    .build());
    }
}
