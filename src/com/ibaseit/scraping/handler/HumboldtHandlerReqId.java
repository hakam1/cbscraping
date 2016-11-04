package com.ibaseit.scraping.handler;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;

import com.ibaseit.scraping.ResponseHandler;

public class HumboldtHandlerReqId implements ResponseHandler {

    @Override
    public String handleResponse(HttpResponse response,
	    Map<String, Object> currentClientInfo) throws IOException {
	String page=new HtmlHandler().handleResponse(response, currentClientInfo);
	String reqId=Jsoup.parse(page).select("legend[style=\"font-size:12px\"]").text().split("Access: Location")[1].trim();
	currentClientInfo.put("strRqstID", reqId);
	
	return page;
    }

}
