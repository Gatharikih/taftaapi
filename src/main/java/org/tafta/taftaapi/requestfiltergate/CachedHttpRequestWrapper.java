package org.tafta.taftaapi.requestfiltergate;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StreamUtils;

import java.io.*;

/**
 * @author Gathariki Ngigi
 * Created on July 24, 2023.
 * Time 1442h
 */

public class CachedHttpRequestWrapper extends HttpServletRequestWrapper {
    private final byte[] cachedPayLoad;

    public CachedHttpRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        InputStream requestInputStream = request.getInputStream();
        this.cachedPayLoad = StreamUtils.copyToByteArray(requestInputStream);
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedServletInputStream(this.cachedPayLoad);
    }

    @Override
    public BufferedReader getReader() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedPayLoad);
        return new BufferedReader(new InputStreamReader(byteArrayInputStream));
    }
}