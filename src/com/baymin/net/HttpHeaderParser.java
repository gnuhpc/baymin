package com.baymin.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.http.protocol.HTTP;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.baymin.net.cache.Cache;
import com.baymin.net.error.ServerError;
import com.baymin.net.toolbox.ByteArrayPool;
import com.baymin.net.toolbox.PoolingByteArrayOutputStream;

public class HttpHeaderParser {

	private static final String DEFAULT_CONTENT_CHARSET = "UTF-8";

	/** Reads the contents of HttpEntity into a byte[]. */
	public static byte[] responseToBytes(Request<?> request, HttpResponse response, ResponseDelivery delivery) 
	        throws IOException, ServerError {
		HttpEntity entity = response.getEntity();
		PoolingByteArrayOutputStream bytes =
				new PoolingByteArrayOutputStream(ByteArrayPool.get(), (int) entity.getContentLength());
		byte[] buffer = null;
		long totalSize = entity.getContentLength();
		try {
			InputStream in = entity.getContent();
			if (isGzipContent(response) && !(in instanceof GZIPInputStream)) {
				in = new GZIPInputStream(in);
			}

			if (in == null) {
				throw new ServerError();
			}

			buffer = ByteArrayPool.get().getBuf(1024);
			int count;
			int transferredBytes = 0;
			while ((count = in.read(buffer)) != -1) {
				bytes.write(buffer, 0, count);
				transferredBytes += count;
				delivery.postProgress(request, totalSize, transferredBytes);
			}
			return bytes.toByteArray();
		} finally {
			try {
				// Close the InputStream and release the resources by "consuming the content".
				entity.consumeContent();
			} catch (IOException e) {
				// This can happen if there was an exception above that left the entity in
				// an invalid state.
				VolleyLog.v("Error occured when calling consumingContent");
			}
			ByteArrayPool.get().returnBuf(buffer);
			bytes.close();
		}
	}

	/**
	 * Returns the charset specified in the Content-Type of this header.
	 */
	public static String getCharset(HttpResponse response) {
		Header header = response.getFirstHeader(HTTP.CONTENT_TYPE);
		if (header != null) {
			String contentType = header.getValue();
			if (!TextUtils.isEmpty(contentType)) {
				String[] params = contentType.split(";");
				for (int i = 1; i < params.length; i++) {
					String[] pair = params[i].trim().split("=");
					if (pair.length == 2) {
						if (pair[0].equals("charset")) {
							return pair[1];
						}
					}
				}
			}
		}
		return null;
	}

	public static String getHeader(HttpResponse response, String key) {
		Header header = response.getFirstHeader(key);
		return header == null ? null : header.getValue();
	}

	public static boolean isSupportRange(HttpResponse response) {
		if (TextUtils.equals(getHeader(response, "Accept-Ranges"), "bytes")) {
			return true;
		}
		String value = getHeader(response, "Content-Range");
		return value != null && value.startsWith("bytes");
	}

	public static boolean isGzipContent(HttpResponse response) {
		return TextUtils.equals(getHeader(response, "Content-Encoding"), "gzip");
	}

	public static NetworkResponse parseBitmapCacheHeaders(Bitmap bitmap) {
        NetworkResponse response = null;
        if(null != bitmap){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            byte[] byteArray = stream.toByteArray();
            response = new NetworkResponse(byteArray, HTTP.UTF_8);
        }
        return response;
    }
	
    /**
     * Returns the charset specified in the Content-Type of this header,
     * or the HTTP default (ISO-8859-1) if none can be found.
     */
    public static String parseCharset(Map<String, String> headers) {
        String contentType = headers.get(HTTP.CONTENT_TYPE);
        if (contentType != null) {
            String[] params = contentType.split(";");
            for (int i = 1; i < params.length; i++) {
                String[] pair = params[i].trim().split("=");
                if (pair.length == 2) {
                    if (pair[0].equals("charset")) {
                        return pair[1];
                    }
                }
            }
        }

        return DEFAULT_CONTENT_CHARSET;
    }
    
    /**
     * Extracts a {@link Cache.Entry} from a {@link NetworkResponse}.
     *
     * @param response The network response to parse headers from
     * @return a cache entry for the given response, or null if the response is not cacheable.
     */
    public static Cache.Entry parseCacheHeaders(NetworkResponse response) {
        long now = System.currentTimeMillis();

        Map<String, String> headers = response.headers;

        long serverDate = 0;
        long serverExpires = 0;
        long softExpire = 0;
        long maxAge = 0;
        boolean hasCacheControl = false;

        String serverEtag = null;
        String headerValue;

        headerValue = headers.get("Date");
        if (headerValue != null) {
            serverDate = parseDateAsEpoch(headerValue);
        }

        headerValue = headers.get("Cache-Control");
        if (headerValue != null) {
            hasCacheControl = true;
            String[] tokens = headerValue.split(",");
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i].trim();
                if (token.equals("no-cache") || token.equals("no-store")) {
                    return null;
                } else if (token.startsWith("max-age=")) {
                    try {
                        maxAge = Long.parseLong(token.substring(8));
                    } catch (Exception e) {
                    }
                } else if (token.equals("must-revalidate") || token.equals("proxy-revalidate")) {
                    maxAge = 0;
                }
            }
        }

        headerValue = headers.get("Expires");
        if (headerValue != null) {
            serverExpires = parseDateAsEpoch(headerValue);
        }

        serverEtag = headers.get("ETag");

        // Cache-Control takes precedence over an Expires header, even if both exist and Expires
        // is more restrictive.
        if (hasCacheControl) {
            softExpire = now + maxAge * 1000;
        } else if (serverDate > 0 && serverExpires >= serverDate) {
            // Default semantic for Expire header in HTTP specification is softExpire.
            softExpire = now + (serverExpires - serverDate);
        }

        Cache.Entry entry = new Cache.Entry();
        entry.data = response.data;
        entry.etag = serverEtag;
        entry.softTtl = softExpire;
        entry.ttl = entry.softTtl;
        entry.serverDate = serverDate;
        entry.responseHeaders = headers;

        return entry;
    }
    
    /**
     * Parse date in RFC1123 format, and return its value as epoch
     */
    public static long parseDateAsEpoch(String dateStr) {
        try {
            // Parse date in RFC1123 format if this header contains one
            return DateUtils.parseDate(dateStr).getTime();
        } catch (DateParseException e) {
            // Date in invalid format, fallback to 0
            return 0;
        }
    }
}
