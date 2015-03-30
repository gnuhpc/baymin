package com.baymin.net.request;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.baymin.net.HttpHeaderParser;
import com.baymin.net.Listener;
import com.baymin.net.NetworkResponse;
import com.baymin.net.Request;
import com.baymin.net.Response;
import com.baymin.net.error.AuthFailureError;
import com.baymin.net.error.ParseError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


public class GsonRequest<T> extends Request<T> {
    private final Gson gson = new Gson();
    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final Listener<T> listener;
 
    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest(String url, Class<T> clazz, Map<String, String> headers,
            Listener<T> listener) {
        super(Method.GET, url, listener);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
    }
 
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }
 
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(
                    gson.fromJson(json, clazz), response);
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
