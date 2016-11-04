package com.ibaseit.scraping.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ibaseit.scraping.ResponseHandler;

public class HtmlHandlerForAuroraId implements ResponseHandler {

    @Override
    public String handleResponse(HttpResponse response,
	    Map<String, Object> currentClientInfo) throws IOException {

	String aurora_id = "";
	String page = responseString(response).replaceAll("&nbsp;", "");

	Elements element = Jsoup.parse(page).select("a[href]");

	for (Element pageAnchorelement : element) {
	    String auroraAnchor = pageAnchorelement.toString();

	    if (auroraAnchor.indexOf("tabs.php?aurora_id=") > 0) {
		aurora_id = pageAnchorelement.attr("href").split("=")[1];

		break;
	    }
	}
	currentClientInfo.put("aurora_id", aurora_id);
	return "";
    }

    public String responseString(HttpResponse response) throws IOException {
	BufferedReader rd = new BufferedReader(new InputStreamReader(response
		.getEntity().getContent()));
	StringBuffer result = new StringBuffer();
	String line = "";
	while ((line = rd.readLine()) != null) {
	    result.append(line);
	}
	return result.toString();
    }

}
