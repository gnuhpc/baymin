
package com.baymin.net.request;

import com.baymin.net.Listener;
import com.baymin.net.NetworkResponse;
import com.baymin.net.Request;
import com.baymin.net.Response;
import com.baymin.net.Request.Method;

public class ByteRequest extends Request<byte[]> {

    /**
     * Creates a new GET request.
     *
     * @param url URL to fetch the string at
     * @param listener Listener to receive the byte array response
     */
    public ByteRequest(String url, Listener<byte[]> listener) {
        super(url, listener);
    }

    /**
     * Creates a new request with the given method.
     *
     * @param method the request {@link Method} to use
     * @param url URL to fetch the byte array at
     * @param listener Listener to receive the byte array response or error message
     */
    public ByteRequest(int method, String url, Listener<byte[]> listener) {
        super(method, url, listener);
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response.data, response);
    }

    @Override
    public String getBodyContentType() {
        return "application/octet-stream";
    }
}
