package com.ibaseit.scraping.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpEntity;
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
import com.ibaseit.scraping.dto.HumboldtNMAHtmlData;
import com.ibaseit.scraping.dto.PaysafeHtmlData;

public class HumboldtNMAHandler implements ResponseHandler{

    @Override
    public String handleResponse(HttpResponse response, Map<String, Object> currentClientInfo) throws IOException {
	
	List<HumboldtNMAHtmlData> logDetails = new ArrayList<HumboldtNMAHtmlData>();
	String page = new HtmlHandler().handleResponse(response,currentClientInfo);
	System.out.println("my Page"+page);
	for (Element table : Jsoup.parse(page.substring(page.split("<table")[0].length())).getElementsByTag("table")) {
	    Elements trGroup = table.getElementsByTag("tr");
	    //System.out.println("table : " + table + " ,  trgroup : " + trGroup);
	    for (int trNo = 0; trNo < trGroup.size() - 1; trNo++) {
		Element tr = trGroup.get(trNo);
		Elements noOfrows = tr.getElementsByTag("td");
		HumboldtNMAHtmlData htmlDataObj =null;
		if(noOfrows.size() == 12 && trNo == 0){
		    htmlDataObj = new HumboldtNMAHtmlData(noOfrows);
		    logDetails.add(htmlDataObj);
		}
		else if (noOfrows.size() == 12 && "OPEN".equalsIgnoreCase(noOfrows.get(11).text())) {
		    System.out.println("tr : " + tr + " ,  rows : " + noOfrows);
		    htmlDataObj = new HumboldtNMAHtmlData(noOfrows);
		    logDetails.add(htmlDataObj);
		}
	    }
	}
	ChargeBackDAO cbDao = new ChargeBackDAO();
	cbDao.insert(getEntities(logDetails));
	cbDao.insertMongo(getEntities(logDetails));
	generateLogFile(logDetails, currentClientInfo);
	return "";
	
    }
	
	
	

	private void generateLogFile(List<HumboldtNMAHtmlData> logDetails, Map<String, Object> currentClientInfo) throws IOException {
	    	
	    	String getWay = currentClientInfo.get("Getway").toString();
		String userName = currentClientInfo.get("Username").toString();
		Files.createDirectories(Paths.get("output\\" + getWay));
		Files.createDirectories(Paths.get("output\\" + getWay + "\\" + userName));

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("ChargeBackDetail");
		
		XSSFRow headerRow = sheet.createRow(0);
		headerRow.createCell(0).setCellValue("Input date");
		headerRow.createCell(1).setCellValue("Due date");
		headerRow.createCell(2).setCellValue("Case Number");
		headerRow.createCell(3).setCellValue("Reference Number");
		headerRow.createCell(4).setCellValue("Amount");
		headerRow.createCell(5).setCellValue("Reason");
		headerRow.createCell(6).setCellValue("Transaction Date");
		headerRow.createCell(7).setCellValue("First Six and Last Four");
		 
		for (int rowIndex = 1; rowIndex <= logDetails.size() - 1; rowIndex++) {
		    XSSFRow row = sheet.createRow(rowIndex);
		    HumboldtNMAHtmlData currentObj = logDetails.get(rowIndex);
		    row.createCell(0).setCellValue(currentObj.getInputDate());
		    row.createCell(1).setCellValue(currentObj.getDueDate());
		    row.createCell(2).setCellValue(currentObj.getCaseNumber());
		    row.createCell(3).setCellValue(currentObj.getReferenceNumber());
		    row.createCell(4).setCellValue(currentObj.getAmount());
		    row.createCell(5).setCellValue(currentObj.getReason());
		    row.createCell(6).setCellValue(currentObj.getTransactionDate());
		    row.createCell(7).setCellValue(currentObj.getFirstSixAndLastFour());
		}

		String filePath = "output\\" + getWay + "\\" + userName + "\\Report"+ System.currentTimeMillis() + ".xlsx";
		FileOutputStream fileOutputStream = new FileOutputStream(filePath);
		wb.write(fileOutputStream);
		fileOutputStream.close();
		wb.close();
		System.out.println("writing is done and Excel Received...!!");
	
    }

    private List<LogSheetData> getEntities(List<HumboldtNMAHtmlData> logDetails) {
	List<LogSheetData> entityObjs = new ArrayList<LogSheetData>();
	for (int i = 1; i <= logDetails.size() - 1; i++) {
	    HumboldtNMAHtmlData paysafe = logDetails.get(i);

	    entityObjs.add(map(paysafe));
	}
	return entityObjs;
    }

    private LogSheetData map(HumboldtNMAHtmlData htmlDataObj) {
	LogSheetData logData = new LogSheetData();
	try {
	    logData.setInput_date(new java.sql.Date(new SimpleDateFormat("MM/dd/yyyy", Locale.US).parse(htmlDataObj.getInputDate())
		    .getTime()));
	    logData.setDue_date(new java.sql.Date(new SimpleDateFormat("MM/dd/yyyy", Locale.US).parse(htmlDataObj.getDueDate()).getTime()));
	    logData.setCase_number(htmlDataObj.getCaseNumber());
	    logData.setReference_number(htmlDataObj.getReferenceNumber());
	    logData.setAmount(Double.parseDouble(htmlDataObj.getAmount()));
	    logData.setReason_description(htmlDataObj.getReason());
	    logData.setTransaction_date(new java.sql.Date(new SimpleDateFormat("MM/dd/yyyy", Locale.US).parse(htmlDataObj.getTransactionDate())
		    .getTime()));
	    logData.setCc_first_6(htmlDataObj.getFirstSixAndLastFour().substring(0, 6));
	    logData.setCc_last_4(htmlDataObj.getFirstSixAndLastFour().substring(htmlDataObj.getFirstSixAndLastFour().length() - 4,
			    htmlDataObj.getFirstSixAndLastFour().length()));

	} catch (ParseException pe) {
	    pe.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return logData;
    }
	
	/*System.out.println("hello Response for HumboldtNMAHndler");
	String getWay = currentClientInfo.get("Getway").toString();
	String userName = currentClientInfo.get("Username").toString();
	Files.createDirectories(Paths.get("output\\" + getWay));
	Files.createDirectories(Paths.get("output\\" + getWay + "\\" + userName));
	*/
	
	
/*	HttpEntity entity1 = response.getEntity();
	if (entity1 != null) {
	    System.out.println("Entity isn't null ::: " + entity1);
	    InputStream is = entity1.getContent();
	    String filePath = "output\\" + getWay + "\\" + userName + "\\Report"
			+ System.currentTimeMillis() + ".xls";
	    FileOutputStream fos = new FileOutputStream(new File(filePath));

	    byte[] buffer = new byte[5600];
	    int inByte;
	    while ((inByte = is.read(buffer)) > 0)
		fos.write(buffer, 0, inByte);

	    is.close();
	    fos.close();
	}
	System.out.println("Excel is Received..");
	return "";*/
    

}
