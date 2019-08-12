package com.schemaxtech.testAutomationFramework;

import java.util.Map;

import org.json.JSONObject;


public class TestCaseInfo {
	public Map<String, JSONObject> requestJsonObject;
	public Map<String, String> responseAttributePaths;
	public Map<String, Map<String, Object>> expectedAttributeValues;

	TestCaseInfo(Map<String, JSONObject> requestJsonObject, Map<String, String> responseAttributePaths,
			Map<String, Map<String, Object>> expectedAttributeValues) {
		this.requestJsonObject = requestJsonObject;
		this.responseAttributePaths = responseAttributePaths;
		this.expectedAttributeValues = expectedAttributeValues;
	}

}