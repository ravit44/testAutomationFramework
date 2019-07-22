package com.schemaxtech.testAutomationFrameworkTest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.schemaxtech.testAutomationFramework.TestAutomationUtil;
import com.schemaxtech.testAutomationFramework.TestCaseInfo;


import io.restassured.response.ResponseBody;	

public class TestAutomationUtilTest {
	
	
			
	@DataProvider(name="sampleDataProvider")
	public  Object[][] sampleDataProvider(Method method) throws Exception {
		
		Map<String,String> mapForPathProvider=new HashMap<String,String>();
		mapForPathProvider=TestAutomationUtil.generateFileNames(method.getName());
		TestCaseInfo testcaseinfo=TestAutomationUtil.updatJsonWithTestDataMaster(mapForPathProvider.get("jsonpath1"), mapForPathProvider.get("csvPath"),  mapForPathProvider.get("mapperpath"), mapForPathProvider.get("resultcsvPath"),  mapForPathProvider.get("expectedmapperpath"));
		Object[][] data = new Object[testcaseinfo.requestJsonObject.size()][4];
		int i=0;
		for (String testcase: testcaseinfo.requestJsonObject.keySet())
		{
			
			data[i][0] = testcase;
			data[i][1] = testcaseinfo.requestJsonObject.get(testcase);
			data[i][2] = testcaseinfo.expectedAttributeValues.get(testcase);
			data[i][3] = testcaseinfo.responseAttributePaths;
			i++;
		}
		
		return data;
	}
	
	@SuppressWarnings("rawtypes")
	@Test(dataProvider="sampleDataProvider")
	public void postSampleTest(String testCase, JSONObject requestJsonObject,Map<String, Object> expectedAttributeValues,Map<String,String> responseAttributePaths) throws Exception {
		 
		
		ResponseBody response= TestAutomationUtil.methodForPost("https://reqres.in", "api/users",requestJsonObject);
	
		TestAutomationUtil.verifyResponse(response, expectedAttributeValues, responseAttributePaths);
	}
	
//	@Test(dataProvider="sampleDataProvider",dependsOnMethods="postSampleTest")
//	public void postSampleTest1(String testCase, JSONObject requestJsonObject,Map<String, Object> expectedAttributeValues,Map<String,String> responseAttributePaths) throws Exception {
//		 
//		
//		ResponseBody response= TestAutomationUtil.methodForPost("https://reqres.in", "api/users",requestJsonObject);
//	
//		TestAutomationUtil.verifyResponse(response, expectedAttributeValues, responseAttributePaths);
//	}

}
