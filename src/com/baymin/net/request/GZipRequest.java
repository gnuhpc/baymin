
package com.baymin.net.request;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import com.baymin.net.Listener;
import com.baymin.net.NetworkResponse;
import com.baymin.net.Response;
import com.baymin.net.error.ParseError;

public class GZipRequest extends StringRequest {
    
    public GZipRequest(String url, Listener<String> listener) {
        super(url, listener);
    }
    
    public GZipRequest(int method, String url, Listener<String> listener) {
        super(method, url, listener);
    }

    // parse the gzip response using a GZIPInputStream
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String output = "";
        try {
            GZIPInputStream zipStream = new GZIPInputStream(new ByteArrayInputStream(response.data));
            InputStreamReader reader = new InputStreamReader(zipStream);
            BufferedReader in = new BufferedReader(reader);
            String read;
            while ((read = in.readLine()) != null) {
                output += read;
            }
            reader.close();
            in.close();
            zipStream.close();
        } catch (IOException e) {
            return Response.error(new ParseError());
        }
        return Response.success(output, response);
    }
}
