package com.ibaseit.scraping.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import com.ibaseit.scraping.ResponseHandler;

public class ExcelHandler implements ResponseHandler {

	@Override
	public String handleResponse(HttpResponse response,
			Map<String, Object> currentClientInfo) throws IllegalStateException,
			IOException {

		System.out.println("hello Response for ExcelHndler");
		String getWay = currentClientInfo.get("Getway").toString();

		HttpEntity entity = response.getEntity();

		if (entity != null) {
			System.out.println("Entity isn't null");
			InputStream is = entity.getContent();
			String filePath = "output\\" + getWay + "\\Report"
					+ System.currentTimeMillis() + ".xls";
			FileOutputStream fos = new FileOutputStream(new File(filePath));

			byte[] buffer = new byte[5600];
			int inByte;
			while ((inByte = is.read(buffer)) > 0)
				fos.write(buffer, 0, inByte);

			is.close();
			fos.close();
		}
		System.out.println("Excel Received..");
		return "";
	}

}
