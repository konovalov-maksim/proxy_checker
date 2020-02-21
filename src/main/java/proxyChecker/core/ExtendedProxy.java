package proxyChecker.core;


import okhttp3.*;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExtendedProxy {

    private String address;
    private int port;
    private String login;
    private String pass;

    private Proxy proxy;
    private Authenticator auth;

    private List<Long> responseTimeList = new ArrayList<>();
    private List<Integer> responsesCodes = new ArrayList<>();

    public ExtendedProxy(String address, int port) {
        this.address = address;
        this.port = port;
        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(address, port));
    }

    public ExtendedProxy(String address, int port, String login, String pass) {
        this(address, port);
        setCredentials(login, pass);
    }

    public void setCredentials(String login, String pass) {
        this.login = login;
        this.pass = pass;
        this.auth = (route, response) -> {
            String credential = Credentials.basic(login, pass);
            return response.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
        };
    }

    public void addResponseTime(long responseTime) {
        responseTimeList.add(responseTime);
    }

    public void addResponseCode(int responseCode) {
        responsesCodes.add(responseCode);
    }

    public boolean getIsAllOk() {
        if (responsesCodes == null || responsesCodes.isEmpty()) return false;
        for (Integer code : responsesCodes) if (code != 200) return false;
        return true;
    }

    public long getAvgTime() {
        return Math.round(responseTimeList.stream().mapToDouble(t -> t).average().orElse(0d));
    }

    public int getChecksCount() {
        return responseTimeList.size();
    }

    public Integer getLastCode() {
        return responsesCodes.isEmpty() ? null : responsesCodes.get(responsesCodes.size() - 1);
    }

    public Long getLastResponseTime() {
        return responseTimeList.isEmpty() ? null : responseTimeList.get(responseTimeList.size() - 1);
    }

    public List<Long> getResponseTimeList() {
        return responseTimeList;
    }

    public List<Integer> getResponsesCodes() {
        return responsesCodes;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getLogin() {
        return login;
    }

    public String getPass() {
        return pass;
    }

    public Authenticator getAuth() {
        return auth;
    }

    public String getIp() {
        return this.toString();
    }

    @Override
    public String toString() {
        return this.getAddress() + ":" + this.getPort();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtendedProxy that = (ExtendedProxy) o;
        return port == that.port &&
                address.equals(that.address) &&
                Objects.equals(login, that.login) &&
                Objects.equals(pass, that.pass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, port, login, pass);
    }
}
