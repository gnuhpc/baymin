package com.baymin.net.request;

import java.io.UnsupportedEncodingException;

import javax.xml.transform.ErrorListener;

import com.alibaba.fastjson.JSON;
import com.baymin.log.AppLogger;
import com.baymin.net.DefaultRetryPolicy;
import com.baymin.net.HttpHeaderParser;
import com.baymin.net.Listener;
import com.baymin.net.NetworkResponse;
import com.baymin.net.Request;
import com.baymin.net.Response;
import com.baymin.net.error.ParseError;

public class FastJSONRequest<T> extends Request<T> {
	private final Listener<T> mListener;
	private Class<T> mCls;

	public FastJSONRequest(int method, String url, Class<T> cls,Listener<T> listener) {
		super(method, url, listener);
		 this.mListener = listener;
		 this.mCls = cls;
		this.setRetryPolicy(new DefaultRetryPolicy(1000,6,1f));
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			AppLogger.d(HttpHeaderParser.parseCharset(response.headers));
			T parsedJSON = getJsonBeanInstance(jsonString, mCls);
			return Response.success(parsedJSON,response);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return Response.error(new ParseError(e));
		}
	}

	private T getJsonBeanInstance(String jsonStr, Class<T> cls) {
		AppLogger.i(jsonStr);
		T t = JSON.parseObject(jsonStr, cls);
		return t;
	}
}
