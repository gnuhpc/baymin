package com.baymin.net.request;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.baymin.net.Listener;
import com.baymin.net.NetworkResponse;
import com.baymin.net.Request;
import com.baymin.net.Response;

/**
 * A canned request for retrieving the response body at a given URL as a String.
 */
public class DownloadRequest extends Request<String> {
	private final String mDownloadPath;

	/**
	 * Creates a new request with the given method.
	 * 
	 * @param method
	 *            the request {@link Method} to use
	 * @param url
	 *            URL to fetch the string at
	 * @param listener
	 *            Listener to receive the String response
	 * @param errorListener
	 *            Error listener, or null to ignore errors
	 */
	public DownloadRequest(String url, String download_path, Listener<String> listener) {
		super(Method.GET, url, listener);
		mDownloadPath = download_path;
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		String parsed = "";
		try {
			byte[] data = response.data;
			// convert array of bytes into file
			FileOutputStream fileOuputStream = new FileOutputStream(mDownloadPath, true);
			fileOuputStream.write(data);
			fileOuputStream.flush();
			fileOuputStream.close();
			parsed = mDownloadPath;
		} catch (UnsupportedEncodingException e) {
			parsed = new String(response.data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return Response.success(parsed, response);
	}
}