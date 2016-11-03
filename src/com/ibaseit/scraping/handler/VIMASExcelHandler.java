package com.ibaseit.scraping.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ibaseit.scraping.ResponseHandler;
import com.ibaseit.scraping.dto.VimasHtmlData;

public class VIMASExcelHandler implements ResponseHandler {

	@Override
	public String handleResponse(HttpResponse response,
			Map<String, Object> currentClientInfo) throws IOException {

		String pageCount = "";
		String page = responseString(response).replaceAll("&nbsp;", "");

		Elements element = Jsoup.parse(page).select("span[id=\"PagesList\"]");
		Elements pageRec = element.get(0).getElementsByTag("a");
		for (Element pageCnt : pageRec) {
			pageCount = pageCnt.text();
		}

		List<VimasHtmlData> htmlDataList = (currentClientInfo.get("vimasHtmlData") != null) ? (List<VimasHtmlData>) currentClientInfo
				.get("vimasHtmlData") : new ArrayList<VimasHtmlData>();

		Elements trElements0 = Jsoup.parse(page).select("tr[class=\"GridItem\"]");
		Elements trElements1 = Jsoup.parse(page).select(
				"tr[class=\"GridAltColor\"]");
		for (int i = 0; i < trElements0.size(); i++) {
			VimasHtmlData data;
			if (i < trElements0.size()) {
				Elements rec0 = null;
				Element trow0 = trElements0.get(i);
				rec0 = trow0.getElementsByTag("td");
				data = new VimasHtmlData(rec0);
				htmlDataList.add(data);
			}
			if (i < trElements1.size()) {
				Elements rec1 = null;
				Element trow1 = trElements1.get(i);
				rec1 = trow1.getElementsByTag("td");
				data = new VimasHtmlData(rec1);
				htmlDataList.add(data);
			}

		}

		currentClientInfo.put("SelectedPageCtrl", pageCount);
		currentClientInfo.put("vimasHtmlData", htmlDataList);

		return null;
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
