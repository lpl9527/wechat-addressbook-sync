package com.supwisdom.platform.framework.util;

import java.io.IOException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;

import net.sf.json.JSONObject;

public class HttpClientUtil {

    public static String get(String url) throws IOException {
        return get(url, null, "UTF-8");
    }

    public static String get(String url, NameValuePair[] data) throws IOException {
        return get(url, data, "UTF-8");
    }

    public static String get(String url, NameValuePair[] data, String encoding) throws IOException {
        return get(url, data, encoding, 0);
    }

    public static String get(String url, NameValuePair[] data, String encoding, int timeout) throws IOException {
        HttpClient client = new HttpClient();

        if (timeout > 0) {
            client.setTimeout(timeout);
        }

        GetMethod method = new GetMethod(url);

        if (data != null) {
            method.setQueryString(data);
        }

        client.executeMethod(method);

        // System.out.println(method.getStatusLine());

        // System.out.println(method.getResponseBodyAsString());

        // int status = method.getStatusCode();
        String response = method.getResponseBodyAsString();

        method.releaseConnection();

        return response;
    }

    public static String get(String url, NameValuePair[] data, Header[] headers, String encoding, int timeout) throws IOException {
        HttpClient client = new HttpClient();

        if (timeout > 0) {
            client.setTimeout(timeout);
        }

        GetMethod method = new GetMethod(url);

        if (data != null) {
            method.setQueryString(data);
        }
        method.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=" + encoding);
        for (Header header : headers) {
            method.addRequestHeader(header);
        }
        client.executeMethod(method);

        // System.out.println(method.getStatusLine());

        // System.out.println(method.getResponseBodyAsString());

        // int status = method.getStatusCode();
        String response = method.getResponseBodyAsString();

        method.releaseConnection();

        return response;
    }

    public static String post(String url, NameValuePair[] data) throws IOException {
        return post(url, data, "UTF-8");
    }

    public static String post(String url, NameValuePair[] data, String encoding) throws IOException {
        return post(url, data, encoding, 0);
    }

    public static String post(String url, NameValuePair[] data, String encoding, int timeout) throws IOException {
        return post(url, data, null, encoding, timeout);
    }

    public static String post(String url, NameValuePair[] data, Header[] headers, String encoding, int timeout) throws IOException {
        HttpClient client = new HttpClient();

        if (timeout > 0) {
            client.setTimeout(timeout);
        }

        PostMethod method = new PostMethod(url);
        if (headers != null) {
            for (Header header : headers) {
                method.addRequestHeader(header);
            }
        }

        if (data != null) {
            method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, encoding);
            method.setRequestBody(data);
        }
        client.executeMethod(method);

        // System.out.println(method.getStatusLine());

        // System.out.println(method.getResponseBodyAsString());

        String response = method.getResponseBodyAsString();

        method.releaseConnection();

        return response;
    }

    public static String post(String url, JSONObject json) throws IOException {
        return post(url, json, null, "UTF-8", 0);
    }

    public static String post(String url, JSONObject json, int timeout) throws IOException {
        return post(url, json, null, "UTF-8", timeout);
    }

    // 发送json格式数据
    public static String post(String url, JSONObject json, Header[] headers, String encoding, int timeout) throws IOException {
        HttpClient client = new HttpClient();

        if (timeout > 0) {
            client.setTimeout(timeout);
        }

        PostMethod method = new PostMethod(url);
        method.setRequestHeader("Content-Type", "application/json; charset=" + encoding);
        if (headers != null) {
            for (Header header : headers) {
                method.addRequestHeader(header);
            }
        }
        method.setRequestEntity(new StringRequestEntity(json.toString(), "application/json", "UTF-8"));
        client.executeMethod(method);

        // System.out.println(method.getStatusLine());

        // System.out.println(method.getResponseBodyAsString());

        String response = method.getResponseBodyAsString();

        method.releaseConnection();

        return response;
    }

    public class StatusResponse {
        private int statusCode;
        private String statusText;
        private String statusLine;

        private long responseContentLength;
        private String responseBody;
        private String responseCharSet;

        public StatusResponse() {

        }

        public StatusResponse(int statusCode, String statusText, String statusLine, long responseContentLength, String responseBody,
                String responseCharSet) {
            this.statusCode = statusCode;
            this.statusText = statusText;
            this.statusLine = statusLine;

            this.responseContentLength = responseContentLength;
            this.responseBody = responseBody;
            this.responseCharSet = responseCharSet;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getStatusText() {
            return statusText;
        }

        public void setStatusText(String statusText) {
            this.statusText = statusText;
        }

        public String getStatusLine() {
            return statusLine;
        }

        public void setStatusLine(String statusLine) {
            this.statusLine = statusLine;
        }

        public long getResponseContentLength() {
            return responseContentLength;
        }

        public void setResponseContentLength(long responseContentLength) {
            this.responseContentLength = responseContentLength;
        }

        public String getResponseBody() {
            return responseBody;
        }

        public void setResponseBody(String responseBody) {
            this.responseBody = responseBody;
        }

        public String getResponseCharSet() {
            return responseCharSet;
        }

        public void setResponseCharSet(String responseCharSet) {
            this.responseCharSet = responseCharSet;
        }
    }
}
