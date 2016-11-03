package com.ibaseit.scraping.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ibaseit.scraping.ResponseHandler;
import com.ibaseit.scraping.dao.ChargeBackDAO;
import com.ibaseit.scraping.dao.LogSheetData;
import com.ibaseit.scraping.dto.AuroraHtmlData;

public class AuroraHandler implements ResponseHandler {

	@Override
	public String handleResponse(HttpResponse response,
			Map<String, Object> currentClientInfo) throws IOException {

		System.out.println("hello Response for ExcelHndler");

		List<AuroraHtmlData> auroraList = new ArrayList<AuroraHtmlData>();
		String comment = "Your account has been debited due to a pre-arbitration attempt";

		//System.out.println("InputStream=" + response.toString());

		String path = new HtmlHandler().handleResponse(response, currentClientInfo);
		//System.out.println("path=" + path);
		for (Element tbls : Jsoup.parse(
				path.substring(path.split("<table")[0].length())).getElementsByTag(
				"table")) {
			Elements trows = tbls.getElementsByTag("tr");
			//System.out.println("trows=" + trows);
			for (int tridx = 0; tridx < trows.size(); tridx++) {
				Elements rec = null;
				Element trow = trows.get(tridx);
				//System.out.println("trow=" + trow);

				if (tridx == 0) {
					rec = trow.getElementsByTag("th");
				} else {
					rec = trow.getElementsByTag("td");
					String invest_Comment = rec.get(9).text();
					if (invest_Comment.equalsIgnoreCase(comment)) {
						continue;
					}
				}

				AuroraHtmlData ah = new AuroraHtmlData(rec);
				auroraList.add(ah);

			}
		}

		try {
			generateExcel(auroraList, currentClientInfo);
			ChargeBackDAO cbDao = new ChargeBackDAO();
			cbDao.insert(getEntities(auroraList));
			cbDao.insertMongo(getEntities(auroraList));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Excel Received..");

		return "";
	}

	private void generateExcel(List<AuroraHtmlData> auroraList,
			Map<String, Object> currentClientInfo) throws IOException {
		// TODO Auto-generated method stub
		String getWay = (String) currentClientInfo.get("Getway");
		String userName = (String) currentClientInfo.get("Username");
		Files.createDirectories(Paths.get("output\\" + getWay + "\\" + userName));

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("ChargeBackDetail");

		for (int row = 0; row < auroraList.size() - 1; row++) {
			XSSFRow rowdata = sheet.createRow(row);
			if (row == 0) {
				rowdata.createCell(0).setCellValue("Input Date");
				rowdata.createCell(1).setCellValue("Case Number");
				rowdata.createCell(2).setCellValue("Amount");
				rowdata.createCell(3).setCellValue("Trans date");
				rowdata.createCell(4).setCellValue("Reason");
				rowdata.createCell(5).setCellValue("First Six and Last Four");
				continue;
			}

			//System.out.println("list1=" + auroraList.get(row));
			AuroraHtmlData data = auroraList.get(row + 1);
			//System.out.println("trowdata=" + data);

			rowdata.createCell(0).setCellValue(data.getInputDate());
			rowdata.createCell(1).setCellValue(data.getCaseNumber());
			rowdata.createCell(2).setCellValue(data.getAmount());
			rowdata.createCell(3).setCellValue(data.getTransdate());
			rowdata.createCell(4).setCellValue(data.getReason());
			rowdata.createCell(5).setCellValue(data.getFirstSixAndLastFour());
		}
		String filePath = "output\\" + getWay + "\\" + userName + "\\Report"
				+ System.currentTimeMillis() + ".xlsx";
		FileOutputStream fos = new FileOutputStream(new File(filePath));
		wb.write(fos);
		fos.close();
		wb.close();

	}

	private List<LogSheetData> getEntities(List<AuroraHtmlData> logDetails)
			throws ParseException {
		List<LogSheetData> entityObjs = new ArrayList<LogSheetData>();
		for (int i = 1; i < logDetails.size(); i++) {
			AuroraHtmlData aurora = logDetails.get(i);

			entityObjs.add(map(aurora));
		}
		return entityObjs;
	}

	private LogSheetData map(AuroraHtmlData aurora) throws ParseException {
		LogSheetData logData = new LogSheetData();
		logData.setInput_date(new java.sql.Date(new SimpleDateFormat(
				getRegex(aurora.getInputDate()), Locale.US)
				.parse(aurora.getInputDate()).getTime()));
		logData.setTransaction_date(new java.sql.Date(new SimpleDateFormat(
				getRegex(aurora.getTransdate()), Locale.US)
				.parse(aurora.getTransdate()).getTime()));
		logData.setCc_first_6(aurora.getFirstSixAndLastFour().substring(0, 6));
		logData.setCc_last_4(aurora.getFirstSixAndLastFour().substring(
				aurora.getFirstSixAndLastFour().length() - 4,
				aurora.getFirstSixAndLastFour().length()));
		logData.setAmount(Double.parseDouble(aurora.getAmount().split("\\$")[1]
				.replace(")", "")));
		logData.setReason_description(aurora.getReason());
		logData.setCase_number(aurora.getCaseNumber());

		return logData;
	}

	public String getRegex(String dt) throws ParseException {
		Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(dt);
		String regexp = null;
		if (m.find()) {
			char c = dt.charAt(m.start());
			if (c == '-')
				regexp = "yyyy-MM-dd";
			else
				regexp = "MM/dd/yyyy";
		}
		return regexp;
	}
}
