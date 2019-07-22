package com.schemaxtech.testAutomationFramework;

import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

public class tempfiletest {
	
	@Test
	public void readCsvTest_ValidFile() {
		String csvPath = null;
		List<String[]> csvData = TestAutomationUtil.readCsv(csvPath);
		Assert.assertEquals(csvData.size(), 6);
	}

	// @Test
	public void readCsvTest_InValidFile() {
		String invalidCsvPath = "./src/test/resource/TestData.csv";
		List<String[]> csvData = TestAutomationUtil.readCsv(invalidCsvPath);
		Assert.assertNull(csvData);
	}

	@Test
	public void fetchAttrPaths_ValiFile() {
		String mapperpath = null;
		Map<String, String> attributePaths = TestAutomationUtil.fetchAttrPaths(mapperpath);
		Assert.assertEquals(attributePaths.keySet().size(), 5);
	}

	@Test
	public void fetchCsvTestData_ValidFile() {
		String csvPath = null;
		Map<String, Map<String, Object>> attributeValues = TestAutomationUtil.fetchCsvTestData(csvPath);
		Assert.assertEquals(attributeValues.keySet().size(), 5);
	}

	@Test
	public void jsonObject() {
		String jsonpath1 = null;
		//Object jsondata = TestAutomationUtil.getJsonObject(jsonpath1);
		//Assert.assertNotNull(jsondata);
	}


}
