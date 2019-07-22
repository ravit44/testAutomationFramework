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
import com.schemaxtech.testAutomationFramework.TestAutomation_Util3;


public class TestAutomationUtilTest3 {
	String csvPath = "C:\\Users\\Gateway\\Desktop\\schemax\\Company\\TestData_Create_PMS.csv";
	String jsonpath = "C:\\Users\\Gateway\\Desktop\\schemax\\Company\\TestData_Create_PMS";
	String mapperpath = "C:\\Users\\Gateway\\Desktop\\Mapper.csv";
	final static String ROOT_URI = "http://192.168.0.155:4003";
	
	@Test
	public void generateJsonThrough_Mapper1() throws IOException {
		
		
		JsonObject jsondata = TestAutomation_Util3.readJsonObject();
		//System.out.println(jsondata);

	}
	
	
}
