package com.ibaseit.scraping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;

public class ResProcHandler {
	public Map<String, String> resHeaderInfo = new HashMap<String, String>();

	public String handleResponse(HttpResponse response, String className,
			Map<String, Object> currentClientInfo) throws Exception {

		ResponseHandler handler = (ResponseHandler) Class.forName(className)
				.newInstance();
		Method methodInstance = handler.getClass().getMethod("handleResponse",
				HttpResponse.class, Map.class);

		return (String) methodInstance.invoke(handler, response, currentClientInfo);
	}

}
