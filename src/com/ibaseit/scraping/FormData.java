package com.ibaseit.scraping;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class FormData {

	public List<NameValuePair> getFormParams(String htmlResponse,
			HttpStep httpStep, Map<String, Object> currentClientInfo)
			throws UnsupportedEncodingException {

		System.out.println("Extracting form's data...");
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		Map<String, String> reqParam = httpStep.getReqParam();
		if (httpStep.getName().startsWith("Login")) {
			if(htmlResponse!=null){
			for (Element element : Jsoup.parse(htmlResponse).getElementsByTag("form")) {
				for (Element inputElement : element.getElementsByTag("input")) {
					String key = inputElement.attr("name");
					String value = inputElement.attr("value");
					if (key.equals(getKey(httpStep, key)))
						value = currentClientInfo.get(reqParam.get(key).replace("$", ""))
								.toString();
					paramList.add(new BasicNameValuePair(key, value));
				}
			}
			}
		} else {
			for (Map.Entry<String, String> param : reqParam.entrySet()) {

				if ("VIMAS"
						.equalsIgnoreCase(currentClientInfo.get("Getway").toString())
						&& "SelectedPageCtrl".equals(param.getKey())) {
					paramList.add(new BasicNameValuePair(param.getKey(),
							currentClientInfo.get("SelectedPageCtrl") != null ? String
									.valueOf(Integer.parseInt(currentClientInfo.get(
											"SelectedPageCtrl").toString()) - 1) : "0"));
				} else
					paramList
							.add(new BasicNameValuePair(param.getKey(), param.getValue()
									.contains("?") ? currentClientInfo.get(
									param.getValue().replace("?", "")).toString() : param
									.getValue()));

			}
			System.out.println(paramList.toString());
		}

		return paramList;
	}

	private String getKey(HttpStep httpStep, String key) {
		String myKey = null;
		for (String currentKey : httpStep.getReqParam().keySet()) {
			if (key.equals(currentKey)) {
				myKey = key;
				break;
			}
		}
		return myKey;
	}
}
