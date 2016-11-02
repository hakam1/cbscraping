package com.ibaseit.scraping.handler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.ibaseit.scraping.ResponseHandler;
import com.ibaseit.scraping.dao.ChargeBackDAO;
import com.ibaseit.scraping.dao.LogSheetData;
import com.ibaseit.scraping.dto.PaysafeHtmlData;


public class PaysafeHandler implements ResponseHandler {
    
    @Override
    public String handleResponse(HttpResponse response,
	    Map<String, Object> currentClientInfo) throws  IOException{


	System.out.println("hello Response for PaysafeHndler");
		
	List<PaysafeHtmlData> logDetails=new ArrayList<PaysafeHtmlData>();
	String page=new HtmlHandler().handleResponse(response, currentClientInfo);
	for (Element table : Jsoup.parse(page.substring(page.split("<table")[0].length())).getElementsByTag("table")) {
	    Elements trGroup = table.getElementsByTag("tr");
	    //System.out.println("table : " + table + " ,  trgroup : " + trGroup);
	    for (int trNo = 0; trNo <= trGroup.size() - 1; trNo++) {
		Element tr = trGroup.get(trNo);
		Elements noOfrows = null;

		if (trNo == 0)
		    noOfrows = tr.getElementsByTag("th");
		 if (trNo > 0)
		    noOfrows = tr.getElementsByTag("td");

		//System.out.println("tr : " + tr + " ,  rows : " + noOfrows);
		PaysafeHtmlData paysafeLogDetails=new PaysafeHtmlData(noOfrows);
		logDetails.add(paysafeLogDetails);
	    }
	}
	new ChargeBackDAO().insert(getEntities(logDetails));
	generateLogFile(logDetails,currentClientInfo);
	return "";
    }

    private void generateLogFile(List<PaysafeHtmlData> logDetails,
	    Map<String, Object> currentClientInfo) throws IOException {

	String getWay = currentClientInfo.get("Getway").toString();
	String userName = currentClientInfo.get("Username").toString();
	Files.createDirectories(Paths.get("output\\"+getWay));
	Files.createDirectories(Paths.get("output\\" + getWay + "\\" + userName));
	
	XSSFWorkbook wb = new XSSFWorkbook();
	XSSFSheet sheet = wb.createSheet("ChargeBackDetail");
	
	for (int rowIndex = 1; rowIndex <= logDetails.size() - 1; rowIndex++) {
	    XSSFRow row = sheet.createRow(rowIndex);
	    PaysafeHtmlData currentObj = logDetails.get(rowIndex);
	    row.createCell(0).setCellValue(currentObj.getInputDate());
	    row.createCell(1).setCellValue(currentObj.getTransDate());
	    row.createCell(2).setCellValue(currentObj.getCaseNumber());
	    row.createCell(3).setCellValue(currentObj.getFirstSixAndLastFour());
	    row.createCell(4).setCellValue(currentObj.getAmount());
	    row.createCell(5).setCellValue(currentObj.getReason());
	}
	
	String filePath = "output\\" + getWay + "\\" + userName + "\\Report" + System.currentTimeMillis() + ".xlsx";
	FileOutputStream fileOutputStream = new FileOutputStream(filePath);
	wb.write(fileOutputStream);
	fileOutputStream.close();
	wb.close();
	System.out.println("writing is done and Excel Received...!!");
    }

    private List<LogSheetData> getEntities(List<PaysafeHtmlData> logDetails)  {
	List<LogSheetData> entityObjs=new ArrayList<LogSheetData>();
	for (int i = 1 ; i < logDetails.size()-1 ; i++){
	    PaysafeHtmlData paysafe = logDetails.get(i); 

	    entityObjs.add(map(paysafe));
	}
	return entityObjs;
    }

    private LogSheetData map(PaysafeHtmlData paysafe)  {
	LogSheetData logData=new LogSheetData();
	try {
	logData.setInput_date(new java.sql.Date(new SimpleDateFormat("MM/dd/yyyy", Locale.US).parse(paysafe.getInputDate()).getTime()));
	logData.setTransaction_date(new java.sql.Date(new SimpleDateFormat("MM/dd/yyyy", Locale.US).parse(paysafe.getTransDate()).getTime()));
	
	logData.setCase_number(paysafe.getCaseNumber());
	logData.setCc_first_6(paysafe.getFirstSixAndLastFour().substring(0, 6));
	logData.setCc_last_4(paysafe.getFirstSixAndLastFour().substring(paysafe.getFirstSixAndLastFour().length() - 4,
			    paysafe.getFirstSixAndLastFour().length()));
	logData.setAmount(Double.parseDouble(paysafe.getAmount().split("\\$")[1].replace(")","")));
	logData.setReason_description(paysafe.getReason());
	}
	catch (ParseException pe)
	{
	    pe.printStackTrace();
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	return logData;
    }
}
