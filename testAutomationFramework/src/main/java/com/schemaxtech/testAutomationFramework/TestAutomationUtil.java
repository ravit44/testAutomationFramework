package com.schemaxtech.testAutomationFramework;

import static io.restassured.RestAssured.given;

import java.io.FileInputStream;
import java.io.FileReader;
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
import java.util.Properties;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;

import au.com.bytecode.opencsv.CSVReader;
import io.restassured.http.ContentType;
import io.restassured.response.ResponseBody;

public class TestAutomationUtil {

	private static String testSenarioColumnName = "TestCase_Id";

	/**
	 * It reads a JSON Object from a JSON file
	 * 
	 * @param jsonPath relative path of JSON file
	 * @return JSONObject
	 * @throws IOException
	 */
	public static JSONObject getJsonObject(final String jsonPath) throws IOException {
		InputStream inputStream = new FileInputStream(jsonPath);
		JSONTokener tokener = new JSONTokener(inputStream);
		JSONObject jsonObject = new JSONObject(tokener);
		return jsonObject;
	}

	/**
	 * It fetches test data as a Map Object from a CSV file
	 * 
	 * @param csvPath relative path of CSV file
	 * @return Map object of test data
	 * @throws IOException
	 */

	public static Map<String, Map<String, Object>> fetchCsvTestData(final String csvPath) throws IOException {
		Map<String, Map<String, Object>> attributeValues = new HashMap<String, Map<String, Object>>();
		List<String[]> values = readCsv(csvPath);
		String[] headers = (String[]) values.get(0);
		for (int i = 1; i < values.size(); i++) {
			Map<String, Object> testData = new HashMap<String, Object>();
			Object[] testValues = values.get(i);
			for (int j = 0; j < testValues.length; j++) {
				// Adding code for rerun using same data
				if (testValues[j].toString().contains("{{XS}}")) {
					testValues[j] = testValues[j].toString().replace("{{XS}}", getPropertyByName("suffix"));
					testData.put(headers[j], parseString((String) testValues[j]));
				} else {
					testData.put(headers[j], parseString((String) testValues[j]));
				}
			}
			String testCase = (String) testData.get(testSenarioColumnName);
			testData.remove(testSenarioColumnName);
			attributeValues.put(testCase, testData);
		}
		return attributeValues;

	}

	/**
	 * It fetches mapper paths as Map object from a CSV file
	 * 
	 * @param mapperPath relative path of Mapper file
	 * @return Map object of Mappper Paths
	 */

	public static Map<String, String> fetchAttrPaths(final String mapperPath) {
		Map<String, String> attrPaths = new HashMap<String, String>();
		List<String[]> values = readCsv(mapperPath);
		for (int i = 1; i < values.size(); i++) {
			attrPaths.put(values.get(i)[0], values.get(i)[1]);
		}
		return attrPaths;
	}

	/**
	 * It fetches the data from CSV file
	 * 
	 * @param csvPath relative path of CSV file
	 * @return CSV Reader object
	 */
	@SuppressWarnings("resource")
	public static List<String[]> readCsv(final String csvPath) {
		try {
			Reader reader = Files.newBufferedReader(Paths.get(csvPath));
			CSVReader csvReader = new CSVReader(reader);
			return csvReader.readAll();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return null;
	}

	/**
	 * It constructs JSON Object for all the test cases
	 * 
	 * @param attributeValues Map object of Input test data
	 * @param attrPaths       Map Object of Input Attribute Paths
	 * @param jsonObject      JSONObject from Input Json File
	 * @return Map of JSON Object for each test case
	 * @throws Exception
	 */
	public static Map<String, JSONObject> updateJsonWithTestData(Map<String, Map<String, Object>> attributeValues,
			Map<String, String> attrPaths, JSONObject jsonObject) throws Exception {

		JSONObject rootNode = new JSONObject(jsonObject);
		Map<String, JSONObject> inputtestData = new HashMap<String, JSONObject>();
		for (String testname : attributeValues.keySet()) {
			JSONObject tempJson = new JSONObject();
			for (String attrname : attributeValues.get(testname).keySet()) {
				String[] attrPath = attrPaths.get(attrname).split("/");
				tempJson = UpdateJsonValue(attributeValues.get(testname).get(attrname), rootNode, attrPath);

			}
			inputtestData.put(testname, tempJson);
			rootNode = new JSONObject(jsonObject);
		}

		return inputtestData;
	}

	/**
	 * It replaces each node recursively in JSON object and constructs the JSON
	 * object with data
	 * 
	 * @param value         input value to be updated in JSON Object
	 * @param recjsonObject input JSONObject
	 * @param keys
	 * @return JSONObject with updated value
	 * @throws Exception
	 */
	public static JSONObject UpdateJsonValue(Object value, JSONObject recjsonObject, String[] keys) throws Exception {
		String currentKey = keys[0];
		if (keys.length == 1) {

			return recjsonObject.put(currentKey, value);
		} else if (!recjsonObject.has(currentKey)) {
			throw new Exception(currentKey + "is not a valid key.");
		}
		JSONObject nestedJsonObjectVal = recjsonObject.getJSONObject(currentKey);
		String[] remainingKeys = Arrays.copyOfRange(keys, 1, keys.length);
		JSONObject updatedNestedValue = UpdateJsonValue(value, nestedJsonObjectVal, remainingKeys);
		return recjsonObject.put(currentKey, updatedNestedValue);
	}

	/**
	 * It constructs data as TestCaseInfo object for the data provider
	 * 
	 * @param inputjsonPath      relative path of the input JSON file
	 * @param inputcsvpath       relative path of the input CSV file
	 * @param inputmapperpath    relative path of the input mapper path
	 * @param outputcsvPath      relative path of the expected CSV file
	 * @param expectedmapperpath relative path of the expected mapper file
	 * @return TestCaseInfo Object
	 * @throws Exception
	 */

	public static TestCaseInfo updatJsonWithTestDataMaster(String inputjsonPath, String inputcsvpath,
			String inputmapperpath, String outputcsvPath, String expectedmapperpath) throws Exception {
		JSONObject inputJsonObject = TestAutomationUtil.getJsonObject(inputjsonPath);
		Map<String, Map<String, Object>> inputAttributeValues = TestAutomationUtil.fetchCsvTestData(inputcsvpath);
		Map<String, String> inputAttributePaths = TestAutomationUtil.fetchAttrPaths(inputmapperpath);

		Map<String, Map<String, Object>> outputAttributeValues = TestAutomationUtil.fetchCsvTestData(outputcsvPath);
		Map<String, String> outputAttributePaths = TestAutomationUtil.fetchAttrPaths(expectedmapperpath);

		TestCaseInfo dataForTestCase = new TestCaseInfo(
				updateJsonWithTestData(inputAttributeValues, inputAttributePaths, inputJsonObject),
				outputAttributePaths, outputAttributeValues);
		return dataForTestCase;
	}

	/**
	 * It fetches the data from a service using post method
	 * 
	 * @param uri               path of the API uri
	 * @param node              node path of the API
	 * @param requestJsonObject input JSONObject
	 * @return ResponseBody of the response
	 * @throws Exception
	 */
	public static ResponseBody methodForPost(String uri, String node, JSONObject requestJsonObject) throws Exception {

		return given().contentType(ContentType.JSON).body(requestJsonObject.toString()).post(uri + "/" + node).then()
				.extract().response().getBody();

	}

	/**
	 * It asserts the response of the API with the expected values
	 * 
	 * @param response                ResponseBody type object from the service call
	 * @param expectedAttributeValues Map object of expected values
	 * @param responseAttributePaths  Map object of expected paths
	 */

	@SuppressWarnings("unchecked")
	public static void verifyResponse(ResponseBody response, Map<String, Object> expectedAttributeValues,
			Map<String, String> responseAttributePaths) {
		int j = 0;
		System.out.println("POST Response\n" + response.asString());
		for (String attr : expectedAttributeValues.keySet()) {
			if (responseAttributePaths.get(attr) != null && expectedAttributeValues.get(attr) != null
					&& !expectedAttributeValues.get(attr).equals("")) {
				String attrPath = responseAttributePaths.get(attr).replace("/", ".");
				Object actualResponse = response.jsonPath().get(attrPath);
				if (actualResponse.getClass().getSimpleName().contains("Array")) {
					List<Object> responseArray = new ArrayList<>();
					responseArray = (List<Object>) actualResponse;
					if (j == 0) {
						for (int i = 0; i < responseArray.size(); i++) {
							if (expectedAttributeValues.get(attr).toString().equals(responseArray.get(i).toString())) {
								j = i;
								break;
							}
						}
					}
					Assert.assertEquals(responseArray.get(j).toString(), expectedAttributeValues.get(attr).toString(),
							"Verification of '" + attr + "' with value '" + expectedAttributeValues.get(attr).toString()
									+ "' in response json failed");
				} else {
					Assert.assertEquals(parseString(actualResponse.toString()),
							parseString( expectedAttributeValues.get(attr).toString()),
							"Verification of '" + attr + "' with value '" + expectedAttributeValues.get(attr)
									+ "' in response json failed");
				}

			}
		}

	}

	/**
	 * It generates the file paths dynamically using the base folder and the test
	 * case name
	 * 
	 * @param methodName name of the test method
	 * @return map object of all the paths
	 * @throws IOException
	 */
	public static Map<String, String> generateFileNames(String methodName) throws IOException {

		Map<String, String> mapOfPaths = new HashMap<String, String>();
		String inputcsvPath = getPropertyByName("baseFolder") + "\\" + methodName + "\\" + methodName
				+ "_InputTestData.csv";
		String inputJsonPath = getPropertyByName("baseFolder") + "\\" + methodName + "\\" + methodName + "_Input.json";
		String mapperPath = getPropertyByName("baseFolder") + "\\" + methodName + "\\" + methodName
				+ "_InputAttributeMapper.csv";
		String resultcsvPath = getPropertyByName("baseFolder") + "\\" + methodName + "\\" + methodName
				+ "_OutputExpectedData.csv";
		String expectedMapperPath = getPropertyByName("baseFolder") + "\\" + methodName + "\\" + methodName
				+ "_OutputAttributeMapper.csv";

		mapOfPaths.put("inputcsvpathkey", inputcsvPath);
		mapOfPaths.put("inputJsonpathkey", inputJsonPath);
		mapOfPaths.put("mapperpathkey", mapperPath);
		mapOfPaths.put("resultcsvpathkey", resultcsvPath);
		mapOfPaths.put("expectedmapperpathkey", expectedMapperPath);

		return mapOfPaths;
	}

	/**
	 * It fetches the property value based on the name
	 * 
	 * @param propertyName name of the property
	 * @return value of the property
	 * @throws IOException
	 */
	public static String getPropertyByName(String propertyName) throws IOException {
		FileReader reader = new FileReader("properties");

		Properties p = new Properties();
		p.load(reader);
		return p.getProperty(propertyName);

	}

	/**
	 * It parses the string to other data types based on value
	 * 
	 * @param value string value that needs to be parsed
	 * @return parsed value
	 */
	public static Object parseString(String value) {

		if (value.matches("[+-]?[0-9][0-9]*")) {
			return Integer.parseInt(value);
		} else if (value.toLowerCase().matches("true|false")) {
			return Boolean.parseBoolean(value);
		} else {
			return value;
		}

	}

}
