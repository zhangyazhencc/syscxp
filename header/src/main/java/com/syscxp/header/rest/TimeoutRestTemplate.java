package com.syscxp.header.rest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by lining on 2017/6/12.
 */
public class TimeoutRestTemplate extends RestTemplate {
    private static final CLogger logger = Utils.getLogger(TimeoutRestTemplate.class);

    public TimeoutRestTemplate(ClientHttpRequestFactory requestFactory) {
        super();
        setDefaultCharset();
        this.setRequestFactory(requestFactory);
    }

    // connectTimeout millisecond
    // readTimeout millisecond
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, String requestId, long connectTimeout, long readTimeout) throws RestClientException {
        assert connectTimeout >= 0;
        assert readTimeout >= 0;
        assert requestId != null;

        this.setRequestConfig(connectTimeout, readTimeout);

        long startTime = System.currentTimeMillis();
        ResponseEntity<T> rsp = null;
        try {
            rsp = this.exchange(url, method, requestEntity, responseType);
        } catch (Throwable t) {
            long endTime = System.currentTimeMillis();
            logger.warn(String.format("MyRestTemplate exchange fail, requestId=%s, connectTimeout=%s, readTimeout=%s, spendTime=%s", requestId, connectTimeout, readTimeout, endTime - startTime), t);
            throw t;
        } finally {
            // liningTODO
            // clean info log
            long endTime = System.currentTimeMillis();
            logger.info(String.format("MyRestTemplate timeout info, requestId=%s, connectTimeout=%s, readTimeout=%s, spendTime=%s", requestId, connectTimeout, readTimeout, endTime - startTime));
            if (endTime - startTime > (connectTimeout + 3000) || endTime - startTime > (readTimeout + 3000)) {
                logger.error(String.format("MyRestTemplate timeout error, requestId=%s, connectTimeout=%s, readTimeout=%s, spendTime=%s", requestId, connectTimeout, readTimeout, endTime - startTime));
            }
        }

        return rsp;
    }

    private void setRequestConfig(long connectTimeout, long readTimeout) {
        this.getMyRequestFactory().setRequestTimeoutConfig(connectTimeout, readTimeout);
    }


    private TimeoutHttpComponentsClientHttpRequestFactory getMyRequestFactory() {
        TimeoutHttpComponentsClientHttpRequestFactory rf = (TimeoutHttpComponentsClientHttpRequestFactory) this.getRequestFactory();
        return rf;
    }

    private void setDefaultCharset() {
        List<HttpMessageConverter<?>> converters = getMessageConverters();
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof StringHttpMessageConverter)
                converters.set(converters.indexOf(converter), new StringHttpMessageConverter(Charset.defaultCharset()));
        }
    }
}
