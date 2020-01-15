package proxyChecker.core;


import okhttp3.internal.http2.Header;
import java.nio.charset.StandardCharsets;

public class HeaderWrapper {
    Header header;

    public HeaderWrapper(String name, String value) {
        header = new Header(name, value);
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public String getName() {
        return header.name.string(StandardCharsets.UTF_8);
    }

    public String getValue() {
        return header.value.string(StandardCharsets.UTF_8);
    }

}
