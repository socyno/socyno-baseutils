package com.socyno.base.httputil.test;

import java.io.IOException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.Assert;
import org.junit.Test;

import com.socyno.base.bscmixutil.HttpUtil;

public class TestHtttpUtil {
    
    @Test
    public void testsHttps() throws IOException {
        CloseableHttpResponse resp = null;
        try {
            resp = HttpUtil.get("https://api.douban.com/v2/book/1220562");
            Assert.assertTrue(HttpUtil.getResponseJson(resp) != null);
        } finally {
            HttpUtil.close(resp);
        }
    }
    
}
