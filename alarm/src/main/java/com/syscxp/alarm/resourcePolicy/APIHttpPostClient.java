package com.syscxp.alarm.resourcePolicy;

import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class APIHttpPostClient {

    private static String apiURL = null;
    private HttpClient httpClient = null;
    private HttpPost method = null;

    public APIHttpPostClient(String url) {
        if (url != null) {
            this.apiURL = url;
        }
        if (apiURL != null) {
            httpClient = new DefaultHttpClient();
            method = new HttpPost(apiURL);

        }
    }

    public String post(String parameters) {
        String body = null;

        if (method != null & parameters != null
                && !"".equals(parameters.trim())) {
            try {
                method.addHeader("Content-type","application/json; charset=utf-8");
                method.setHeader("Accept", "application/json");
                method.setEntity(new StringEntity(parameters, Charset.forName("UTF-8")));
                HttpResponse response = httpClient.execute(method);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    System.err.println("Method failed:" + response.getStatusLine());
                }

                body = EntityUtils.toString(response.getEntity());

            } catch (IOException e) {
                System.err.println("Internet wrong:");
            } finally {
            }

        }
        return body;
    }

    public static void main(String[] args) {
        APIHttpPostClient ac = new APIHttpPostClient(apiURL);
        JsonArray arry = new JsonArray();
        JsonObject j = new JsonObject();
        j.addProperty("orderId", "中文");
        j.addProperty("createTimeOrder", "2015-08-11");
        arry.add(j);
        System.out.println(ac.post(arry.toString()));
    }

}