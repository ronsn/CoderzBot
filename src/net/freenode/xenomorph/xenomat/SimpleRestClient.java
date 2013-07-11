package net.freenode.xenomorph.xenomat;


/**
 * 2013 - Jeffrey Robbins
 * SimpleRestClient - Building a REST client from the standard java libsns
 * This is by no means an example on the best way to do things, just a bit of fun in learning
 * Comments on making it better welcomed
 *
 * Licensed under the Apache License, V2.0
 */


import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SimpleRestClient {

    protected RequestMethod method;
    protected String url;
    protected String requestBody;
    protected HashMap<String,String> requestParams;
    protected HashMap<String,String> requestHeaders;
    protected HttpsURLConnection urlConn;
    protected String responseBody;
    protected int responseCode;

    public SimpleRestClient(){
       /* this.requestParams = new Hashtable<String,String>();
        this.requestHeaders = new Hashtable<String,String>();*/
    }

    public SimpleRestClient(RequestMethod method, String url, String requestBody, HashMap<String, String> requestParams) {
        this.method = method;
        this.url = url;
        this.requestBody = requestBody;
        this.requestParams = requestParams;
    }

    /**
     * Just handles HTTPS connections for the time
     * @throws IOException
     */
    public String sendRequest() throws IOException {

        //Setup the connection
        long time = System.currentTimeMillis();

        System.out.println("===> Setting up the connection");
        urlConn = (HttpsURLConnection) getUrlWithParams().openConnection();
        urlConn.setRequestMethod(getMethodStr());
        urlConn.setAllowUserInteraction(true);
        urlConn.setDoOutput(true);

        //Set any headers
       System.out.println("===> Setting headers");
       if(this.requestHeaders != null && this.requestHeaders.size() > 0) {
            for (Map.Entry<String, String> header : this.requestHeaders.entrySet()) {
                urlConn.setRequestProperty(header.getKey(), header.getValue());
            }
        }

        //Set the body
        if(this.requestBody != null) {
            OutputStream os = urlConn.getOutputStream();
            os.write(this.requestBody.getBytes());
            os.close();
        }

        urlConn.connect();

        StringBuilder sb = new StringBuilder();
        InputStreamReader isr;

        this.setResponseCode(urlConn.getResponseCode());
        if(urlConn.getResponseCode() >= 400) {
            isr = new InputStreamReader(urlConn.getErrorStream());
        } else {
            isr = new InputStreamReader(urlConn.getInputStream());
        }

        BufferedReader br = new BufferedReader(isr);

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();

        this.setResponseBody(sb.toString());

        urlConn.disconnect();
        time = System.currentTimeMillis() - time;

        System.out.println("====> Requst roundtrip time ("+ getUrl() +") : " + time + " ms");
        System.out.println("====> Response received : \n" + getResponseBody());

        return getResponseBody();
    }

    private String createParamString() {
        StringBuilder sb = new StringBuilder();
        if(this.requestParams != null && this.requestParams.size() > 0) {
            sb.append("?");

            for (Map.Entry<String, String> param : getRequestParams().entrySet()) {
                sb.append(param.getKey()).append("=").append(param.getValue());
                sb.append("&");
            }
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }

    public URL getUrlWithParams() throws MalformedURLException {
        StringBuilder sb = new StringBuilder(getUrl().toString());
        if(getRequestParams() != null && getRequestParams().size() > 0)
            sb.append(createParamString());

        return new URL(sb.toString());
    }

    /**
     * Convenience method to output everything about the request
     */
    public void dumpRequest() {
        StringBuilder sb = new StringBuilder();
        sb.append("Dumping REST request information:");
        sb.append("\n").append("=======================================================");
        sb.append("\n==> ").append("URL: ").append(getUrl());
        sb.append("\n==> ").append("Method: ").append(getMethodStr());

        if(getRequestHeaders() != null && getRequestHeaders().size() > 0){
            for(Map.Entry<String,String> header : getRequestHeaders().entrySet())
                sb.append("\n===> ").append("Header: ").append(header.getKey()).append(": ").append(header.getValue());
        }

        if(getRequestParams() != null && getRequestParams().size() > 0){
            for(Map.Entry<String,String> param : getRequestParams().entrySet())
                sb.append("\n===> ").append("Param: ").append(param.getKey()).append("=").append(param.getValue());
        }

        sb.append("\n==> ").append("Body: ").append(getRequestBody());

        sb.append("\n").append("=======================================================");

        System.out.println(sb.toString());
    }

    public RequestMethod getMethod() {
        return method;
    }

    public String getMethodStr() {
        return method.toString();
    }

    public void setMethod(RequestMethod method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public HttpsURLConnection getUrlConn() {
        return urlConn;
    }

    public void setUrlConn(HttpsURLConnection urlConn) {
        this.urlConn = urlConn;
    }

    public HashMap<String, String> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(HashMap<String, String> requestParams) {
        this.requestParams = requestParams;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public void addParam(String key, String value) {
        if(getRequestParams() == null || getRequestParams().isEmpty()) {
            setRequestParams(new HashMap<String,String>());
        }
        getRequestParams().put(key,value);
    }

    public HashMap<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(HashMap<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public void addHeader(String key, String value) {
        if(getRequestHeaders() == null || getRequestHeaders().isEmpty()) {
            setRequestHeaders(new HashMap<String,String>());
        }
        getRequestHeaders().put(key,value);
    }

    public void addHeader(String key, int value){
        addHeader(key,Integer.toString(value));
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public enum RequestMethod {
        POST,GET;
    }
}