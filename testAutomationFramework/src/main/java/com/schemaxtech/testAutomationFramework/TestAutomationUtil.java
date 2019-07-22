package com.schemaxtech.testAutomationFramework;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.jackson.map.util.JSONPObject;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;

import au.com.bytecode.opencsv.CSVReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;

public class TestAutomationUtil {
	public static String csvPath, jsonpath1, mapperpath, resultcsvPath, expectedmapperpath;

	private static String testSenarioColumnName = "TestCase_Id";

//	public static JSONPObject readJsonObject(String jsonFilePath) throws IOException {
//
//		byte[] jsonBytes = Files.readAllBytes(Paths.get(jsonFilePath));
//
//		ObjectMapper objectMapper = new ObjectMapper();
//
//		Object json = objectMapper.readValue(jsonBytes, Object.class);
//
//		System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
//		return null;
//	}
//
//	public static JSONObject getJsonObject(String jsonPath) {
//		try {
//			byte[] jsonBytes = Files.readAllBytes(Paths.get(jsonPath));
//
//			ObjectMapper objectMapper = new ObjectMapper();
//
//			return new JSONObject();
//		} catch (Exception e) {
//
//		}
//		return null;
//	}

	public static JSONObject getJsonObjectnew(String jsonPath) throws IOException {
		InputStream is = new FileInputStream(jsonPath);
		JSONTokener tokener = new JSONTokener(is);
		JSONObject object = new JSONObject(tokener);
		return object;
	}

	public static Map<String, Map<String, Object>> fetchCsvTestData(final String csvPath) {
		Map<String, Map<String, Object>> attributeValues = new HashMap<String, Map<String, Object>>();
		List<String[]> values = readCsv(csvPath);
		String[] headers = (String[]) values.get(0);
		for (int i = 1; i < values.size(); i++) {
			Map<String, Object> testData = new HashMap<String, Object>();
			Object[] testValues = values.get(i);
			for (int j = 0; j < testValues.length; j++) {
				testData.put(headers[j], testValues[j]);
			}
			String testCase = (String) testData.get(testSenarioColumnName);
			testData.remove(testSenarioColumnName);
			attributeValues.put(testCase, testData);
		}
		return attributeValues;

	}

	public static Map<String, String> fetchAttrPaths(final String mapperpath) {
		Map<String, String> attrPaths = new HashMap<String, String>();
		List<String[]> values = readCsv(mapperpath);
		for (int i = 1; i < values.size(); i++) {
			attrPaths.put(values.get(i)[0], values.get(i)[1]);
		}
		return attrPaths;
	}

	public static List<String[]> readCsv(String csvPath) {
		try {
			Reader reader = Files.newBufferedReader(Paths.get(csvPath));
			CSVReader csvReader = new CSVReader(reader);
			return csvReader.readAll();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return null;
	}
	
	
	

	// Updating JSON values with the data in excel
	public static Map<String, JSONObject> updatJsonWithTestData(Map<String, Map<String, Object>> attributeValues,
			Map<String, String> attrPaths, JSONObject jsonObject) throws Exception {

		JSONObject rootNode = new JSONObject();
		rootNode.similar(jsonObject);

		Map<String, JSONObject> inputtestData = new HashMap<String, JSONObject>();
		for (String testname : attributeValues.keySet()) {
			JSONObject tempJson = new JSONObject();
			for (String attrname : attributeValues.get(testname).keySet()) {
				String[] attrPath = attrPaths.get(attrname).split("/");
				tempJson = UpdateJsonValue(attributeValues.get(testname).get(attrname), rootNode, attrPath);

			}
			inputtestData.put(testname, tempJson);
			rootNode = jsonObject;
		}

		return inputtestData;
	}

	// recursive method to replace all the values
	public static JSONObject UpdateJsonValue(Object value, JSONObject jsonObject, String[] keys) throws Exception {
		String currentKey = keys[0];
		if (keys.length == 1) {
			return jsonObject.put(currentKey, value);
		} else if (!jsonObject.has(currentKey)) {
			throw new Exception(currentKey + "is not a valid key.");
		}
		JSONObject nestedJsonObjectVal = jsonObject.getJSONObject(currentKey);
		String[] remainingKeys = Arrays.copyOfRange(keys, 1, keys.length);
		JSONObject updatedNestedValue = UpdateJsonValue(value, nestedJsonObjectVal, remainingKeys);
		return jsonObject.put(currentKey, updatedNestedValue);
	}

	public static TestCaseInfo updatJsonWithTestDataMaster(String jsonpath1, String csvPath, String mapperpath,
			String resultcsvPath, String expectedmapperpath) throws Exception {
		JSONObject jsonObject = TestAutomationUtil.getJsonObjectnew(jsonpath1);
		Map<String, Map<String, Object>> attributeValues = TestAutomationUtil.fetchCsvTestData(csvPath);
		Map<String, String> attributePaths = TestAutomationUtil.fetchAttrPaths(mapperpath);

		Map<String, Map<String, Object>> attributeValuesres = TestAutomationUtil.fetchCsvTestData(resultcsvPath);
		Map<String, String> attributePathsres = TestAutomationUtil.fetchAttrPaths(expectedmapperpath);

		TestCaseInfo dataForTest = new TestCaseInfo(updatJsonWithTestData(attributeValues, attributePaths, jsonObject),
				attributePathsres, attributeValuesres);
		return dataForTest;
	}

	public static ResponseBody methodForPost(String uri, String node, JSONObject requestJsonObject) throws Exception {

		return given().contentType(ContentType.JSON).body(requestJsonObject).post(uri + "/" + node).then().extract()
				.response().getBody();

	}

	public static void verifyResponse(ResponseBody response, Map<String, Object> expectedAttributeValues,
			Map<String, String> responseAttributePaths) {
		System.out.println("POST Response\n" + response.asString());
		for (String attr : expectedAttributeValues.keySet()) {
			if(responseAttributePaths.get(attr)!=null)
			{
			String attrPath = responseAttributePaths.get(attr).replace("/", ".");

			Object actualResponse = response.jsonPath().get(attrPath);
			Assert.assertEquals(actualResponse.toString(), expectedAttributeValues.get(attr).toString(), "Sample message ");
			}
		}

	}

	public static Map<String, String> generateFileNames(String methodName) {
		Map<String, String> mapOfPaths = new HashMap<String, String>();
		csvPath = "C:\\Schemax\\Sample JSON\\" + methodName + "_InputTestData.csv";
		jsonpath1 = "C:\\Schemax\\Sample JSON\\" + methodName + "_Input.json";
		mapperpath = "C:\\Schemax\\Sample JSON\\" + methodName + "_InputAttributeMapper.csv";
		resultcsvPath = "C:\\Schemax\\Sample JSON\\" + methodName + "_OutputExpectedData.csv";
		expectedmapperpath = "C:\\Schemax\\Sample JSON\\" + methodName + "_OutputAttributeMapper.csv";
		mapOfPaths.put("csvPath", csvPath);
		mapOfPaths.put("jsonpath1", jsonpath1);
		mapOfPaths.put("mapperpath", mapperpath);
		mapOfPaths.put("resultcsvPath", resultcsvPath);
		mapOfPaths.put("expectedmapperpath", expectedmapperpath);

		return mapOfPaths;
	}
}
