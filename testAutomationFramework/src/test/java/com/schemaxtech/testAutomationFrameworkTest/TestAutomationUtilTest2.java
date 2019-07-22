package com.schemaxtech.testAutomationFrameworkTest;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.util.JSONPObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.gson.JsonObject;
import com.opencsv.*;
import com.schemaxtech.testAutomationFramework.TestAutomationUtil;
import com.schemaxtech.testAutomationFramework.TestAutomation_Util2;


public class TestAutomationUtilTest2 {
	String csvPath = "C:\\Users\\Gateway\\Desktop\\schemax\\Company\\TestData_Create_PMS.csv";
	String jsonpath = "C:\\Users\\Gateway\\Desktop\\schemax\\Company\\TestData_Create_PMS";
	String mapperpath = "C:\\Users\\Gateway\\Desktop\\Mapper.csv";
	//@Test
	public void readCsvTest() throws IOException {
		Map<String, Map<String, String>> attributeValues = TestAutomation_Util2.fetchCsvTestData(csvPath);
		Assert.assertEquals(attributeValues.size(), 5);
	}
	//@Test
	public void readmapper() throws IOException {
		Map<String, String> mapperdata = TestAutomation_Util2.attrPaths(mapperpath);
		//Assert.assertEquals(csvData.size(), 5);
		//System.out.println(mapperdata);
	}
	
	//@Test
	public void jsonObject() throws IOException {
		JsonObject jsondata = TestAutomation_Util2.readJsonObject(jsonpath);
		//Assert.assertEquals(csvData.size(), 6);
		//System.out.println(jsondata);
	}
	
	@Test
	public void generateJsonThrough_Mapper() throws IOException {
	
		Map<String, Map<String, String>> attributeValues = TestAutomation_Util2.fetchCsvTestData(csvPath);
		Map<String, String> readJson_attrpaths = TestAutomation_Util2.attrPaths(mapperpath);
		JsonObject readJsonObject = TestAutomation_Util2.readJsonObject(jsonpath);

		JsonObject jsondata = TestAutomation_Util2.generateJsonThroughMapper(attributeValues, readJson_attrpaths, readJsonObject);
		//System.out.println(jsondata);

	}
	
	
	
	
	
}
