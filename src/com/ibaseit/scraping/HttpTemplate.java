package com.ibaseit.scraping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ibaseit.scraping.utils.ExcelUtils;

public class HttpTemplate {
	String templateName;
	List<HttpStep> allSteps = new ArrayList<HttpStep>();

	public HttpTemplate() {
	}

	public HttpTemplate(XSSFWorkbook wb, String getWay) {
		this.templateName = getWay;
		XSSFSheet sheet = wb.getSheet(getWay);
		System.out.println(sheet.getSheetName());
		int rowIndex = 0;
		int nullcnt = 0;
		for (rowIndex = 2; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			String cellValue = ExcelUtils.asString(sheet, rowIndex, 0);

			if ("".equals(cellValue))
				nullcnt++;
			if (nullcnt == 5)
				break;
			if (cellValue.startsWith("Step"))
				allSteps.add(new HttpStep(sheet, rowIndex));
		}
	}

	public String getTemplateName() {
		return templateName;
	}

	public void execute(Map<String, Object> currentClientInfo) throws Exception {
		HttpContext httpContext = new BasicHttpContext();
		httpContext.setAttribute(HttpClientContext.COOKIE_STORE,
				new BasicCookieStore());

		for (HttpStep httpStep : allSteps) {
			httpStep.execute(currentClientInfo, httpContext);
		}

	}
}
