/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baymin.net.stack;

import android.net.http.AndroidHttpClient;

import com.baymin.net.Request;
import com.baymin.net.Request.Method;
import com.baymin.net.error.AuthFailureError;
import com.baymin.net.multipart.FilePart;
import com.baymin.net.multipart.MultipartEntity;
import com.baymin.net.multipart.StringPart;
import com.baymin.net.request.MultiPartRequest;
import com.baymin.net.request.MultiPartRequest.MultiPartParam;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An HttpStack that performs request over an {@link HttpClient}.
 */
public class HttpClientStack implements HttpStack {
    protected final HttpClient mClient;
    
    private final UrlRewriter mUrlRewriter;
    
    public HttpClientStack(String userAgent) {
        this(AndroidHttpClient.newInstance(userAgent), null);
    }

    public HttpClientStack(HttpClient client) {
        this(client, null);
    }
    
	public HttpClientStack(HttpClient client, UrlRewriter urlRewriter) {
        mClient = client;
        mUrlRewriter = urlRewriter;
    }

    private void addHeaders(HttpUriRequest httpRequest, Map<String, String> headers) {
        for (String key : headers.keySet()) {
            httpRequest.setHeader(key, headers.get(key));
        }
    }

    @SuppressWarnings("unused")
    private List<NameValuePair> getPostParameterPairs(Map<String, String> postParams) {
        List<NameValuePair> result = new ArrayList<NameValuePair>(postParams.size());
        for (String key : postParams.keySet()) {
            result.add(new BasicNameValuePair(key, postParams.get(key)));
        }
        return result;
    }

	@Override
	public HttpResponse performRequest(Request<?> request) throws IOException, AuthFailureError {
		HttpUriRequest httpRequest = createHttpRequest(request);
		onPrepareRequest(httpRequest);
		addHeaders(httpRequest, request.getHeaders());
		HttpParams httpParams = httpRequest.getParams();
		int timeoutMs = request.getTimeoutMs();
		// TODO: Reevaluate this connection timeout based on more wide-scale
		// data collection and possibly different for wifi vs. 3G.
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		HttpConnectionParams.setSoTimeout(httpParams, timeoutMs);
		return mClient.execute(httpRequest);
	}

    /**
     * Creates the appropriate subclass of HttpUriRequest for passed in request.
     */
	private HttpUriRequest createHttpRequest(Request<?> request) throws AuthFailureError, IOException {
	    String url = request.getUrl();
        if (mUrlRewriter != null) {
            String rewritten = mUrlRewriter.rewriteUrl(request);
            if (rewritten == null) {
                throw new IOException("URL blocked by rewriter: " + url);
            }
            url = rewritten;
        }
        
        switch (request.getMethod()) {
            case Method.GET:
                return new HttpGet(request.getUrl());
            case Method.DELETE:
                return new HttpDelete(request.getUrl());
            case Method.POST: {
                HttpPost postRequest = new HttpPost(request.getUrl());
                postRequest.addHeader(HTTP.CONTENT_TYPE, request.getBodyContentType());
                setEntityIfNonEmptyBody(postRequest, request);
                return postRequest;
            }
            case Method.PUT: {
                HttpPut putRequest = new HttpPut(request.getUrl());
                putRequest.addHeader(HTTP.CONTENT_TYPE, request.getBodyContentType());
                setEntityIfNonEmptyBody(putRequest, request);
                return putRequest;
            }
            case Method.HEAD:
                return new HttpHead(request.getUrl());
            case Method.OPTIONS:
                return new HttpOptions(request.getUrl());
            case Method.TRACE:
                return new HttpTrace(request.getUrl());
            case Method.PATCH: {
                HttpPatch patchRequest = new HttpPatch(request.getUrl());
                patchRequest.addHeader(HTTP.CONTENT_TYPE, request.getBodyContentType());
                setEntityIfNonEmptyBody(patchRequest, request);
                return patchRequest;
            }
            default:
                throw new IllegalStateException("Unknown request method.");
        }
    }

    private void setEntityIfNonEmptyBody(HttpEntityEnclosingRequestBase httpRequest,
            Request<?> request) throws AuthFailureError, IOException {
        
        if (request instanceof MultiPartRequest) {
            final Map<String, MultiPartParam> multipartParams = ((MultiPartRequest<?>) request).getMultipartParams();
            final Map<String, String> filesToUpload = ((MultiPartRequest<?>) request).getFilesToUpload();

            MultipartEntity multipartEntity = new MultipartEntity();

            for (String key : multipartParams.keySet()) {
                multipartEntity.addPart(new StringPart(key, multipartParams.get(key).value));
            }

            for (String key : filesToUpload.keySet()) {
                File file = new File(filesToUpload.get(key));
                
                if(!file.exists()) {
                    throw new IOException(String.format("File not found: %s", file.getAbsolutePath()));
                }
                
                if(file.isDirectory()) {
                    throw new IOException(String.format("File is a directory: %s", file.getAbsolutePath()));
                }
                
                multipartEntity.addPart(new FilePart(key, file, null, null));
            }
            httpRequest.setEntity(multipartEntity);

        } else {
            byte[] body = request.getBody();
            if (body != null) {
                HttpEntity entity = new ByteArrayEntity(body);
                httpRequest.setEntity(entity);
            }
        }
    }

    /**
     * Called before the request is executed using the underlying HttpClient.
     *
     * <p>Overwrite in subclasses to augment the request.</p>
     */
    protected void onPrepareRequest(HttpUriRequest request) throws IOException {
        request.addHeader("Accept-Encoding", "gzip");
	}

	/**
	 * The HttpPatch class does not exist in the Android framework, so this has been defined here.
	 */
	public static final class HttpPatch extends HttpEntityEnclosingRequestBase {
		public final static String METHOD_NAME = "PATCH";

		public HttpPatch() {
			super();
		}

		public HttpPatch(final URI uri) {
			super();
			setURI(uri);
		}

		/**
		 * @throws IllegalArgumentException if the uri is invalid.
		 */
		public HttpPatch(final String uri) {
			super();
			setURI(URI.create(uri));
		}

		@Override
		public String getMethod() {
			return METHOD_NAME;
		}
	}
}
