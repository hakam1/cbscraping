package com.ibaseit.scraping.handler;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import com.ibaseit.scraping.ResponseHandler;
import com.ibaseit.scraping.dao.ChargeBackDAO;
import com.ibaseit.scraping.dao.LogSheetData;
import com.ibaseit.scraping.dto.VimasHtmlData;

public class VimasRecordHandler implements ResponseHandler {

	@Override
	public String handleResponse(HttpResponse response,
			Map<String, Object> currentClientInfo) throws IOException {

		String page = responseString(response).replaceAll("&nbsp;", "");

		Elements trElements0 = Jsoup.parse(page).select("span[id=\"CardNumber\"]");
		Elements trElements1 = Jsoup.parse(page).select("span[id=\"CardType\"]");
		String cardNumber = trElements0.get(0).text();
		String cardType = trElements1.get(0).text();
		currentClientInfo.put("cardNumber", cardNumber);
		currentClientInfo.put("cardType", cardType);

		return null;
	}

	public void completeProcess(Map<String, Object> currentClientInfo) {
		List<VimasHtmlData> finalData = (List<VimasHtmlData>) currentClientInfo
				.get("vimasHtmlData");
		try {
			generateLogFile(finalData, currentClientInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ChargeBackDAO cbDao = new ChargeBackDAO();
		cbDao.insert(getEntities(finalData));
		cbDao.insertMongo(getEntities(finalData));
	}

	private void generateLogFile(List<VimasHtmlData> logDetails,
			Map<String, Object> currentClientInfo) throws IOException {

		String getWay = currentClientInfo.get("Getway").toString();
		String userName = currentClientInfo.get("Username").toString();
		Files.createDirectories(Paths.get("output\\" + getWay));
		Files.createDirectories(Paths.get("output\\" + getWay + "\\" + userName));

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("ChargeBackDetail");

		XSSFRow headerRow = sheet.createRow(0);
		headerRow.createCell(0).setCellValue("Input date");
		headerRow.createCell(1).setCellValue("Due date");
		headerRow.createCell(2).setCellValue("Amount");
		headerRow.createCell(3).setCellValue("Transaction date");
		headerRow.createCell(4).setCellValue("Reason");
		headerRow.createCell(5).setCellValue("Family ID");
		headerRow.createCell(6).setCellValue("Reference Number");
		headerRow.createCell(7).setCellValue("Last Four");
		headerRow.createCell(8).setCellValue("Card Type");

		for (int rowIndex = 1; rowIndex <= logDetails.size() ; rowIndex++) {
			XSSFRow row = sheet.createRow(rowIndex);

			VimasHtmlData currentObj = logDetails.get(rowIndex-1);

			row.createCell(0).setCellValue(currentObj.getReceivedDate());
			row.createCell(1).setCellValue(currentObj.getDueDate());
			row.createCell(2).setCellValue(currentObj.getAmount());
			row.createCell(3).setCellValue(currentObj.getTransactionDate());
			row.createCell(4).setCellValue(currentObj.getReasonCodeDesc());
			row.createCell(5).setCellValue(currentObj.getFamilyId());
			row.createCell(6).setCellValue(currentObj.getRefNum());
			row.createCell(7).setCellValue(currentObj.getCardNumber());
			row.createCell(8).setCellValue(currentObj.getCardType());

		}

		String filePath = "output\\" + getWay + "\\" + userName + "\\Report"
				+ System.currentTimeMillis() + ".xlsx";
		FileOutputStream fileOutputStream = new FileOutputStream(filePath);
		wb.write(fileOutputStream);
		fileOutputStream.close();
		wb.close();
		System.out.println("writing is done and Excel Received...!!");
	}

	private List<LogSheetData> getEntities(List<VimasHtmlData> logDetails) {
		List<LogSheetData> entityObjs = new ArrayList<LogSheetData>();
		for (VimasHtmlData logdata : logDetails) {
			entityObjs.add(map(logdata));
		}

		return entityObjs;
	}

	private LogSheetData map(VimasHtmlData vimasdata) {
		LogSheetData logData = new LogSheetData();
		try {
			logData
					.setInput_date(new java.sql.Date(new SimpleDateFormat("MM/dd/yyyy",
							Locale.US).parse(vimasdata.getReceivedDate()).getTime()));
			logData.setDue_date(new java.sql.Date(new SimpleDateFormat("MM/dd/yyyy",
					Locale.US).parse(vimasdata.getDueDate()).getTime()));
			logData.setAmount(Double
					.parseDouble(vimasdata.getAmount().split("\\$")[1].replace(")", "")));
			logData.setTransaction_date(new java.sql.Date(new SimpleDateFormat(
					"MM/dd/yyyy", Locale.US).parse(vimasdata.getTransactionDate())
					.getTime()));
			logData.setReason_description(vimasdata.getReasonCodeDesc());
			logData.setFamily_id(vimasdata.getFamilyId());
			logData.setReference_number(vimasdata.getRefNum());
			logData.setCc_last_4(vimasdata.getCardNumber());
			logData.setCc_type(vimasdata.getCardType());
			logData.setCase_number("");
			logData.setCc_first_6("");
		} catch (ParseException pe) {
			pe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return logData;
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
