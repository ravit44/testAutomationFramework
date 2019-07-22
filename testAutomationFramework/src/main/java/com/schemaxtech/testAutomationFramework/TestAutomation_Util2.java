package com.schemaxtech.testAutomationFramework;


import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.util.JSONPObject;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import au.com.bytecode.opencsv.CSVReader;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.sf.json.JSONObject;



public class TestAutomation_Util2 {
	final static String ROOT_URI = "http://192.168.0.155:4003";

	private static String testSenarioColumnName = "TestCase_Id";
	Map<String,Map<String,String>> attributeValues = new HashMap<String,Map<String,String>>();
	static Map<String,String> testData = new HashMap<String,String>();
	
	public static   JsonObject readJsonObject(String jsonFilePath) throws   IOException{
		byte[] jsonBytes = Files.readAllBytes(Paths.get("C:\\Users\\Gateway\\Desktop\\schemax\\Company\\TestData_Create_PMS"));

		ObjectMapper objectMapper = new ObjectMapper();
		Object json =  objectMapper.readValue( jsonBytes, Object.class ) ;
 
		//System.out.println(json);
		return null;
			
	}
	public static Map<String,Map<String,String>> fetchCsvTestData(final String csvPath) throws IOException {
		
   
		Map<String,Map<String,String>> attributeValues = new HashMap<String,Map<String,String>>();
		Map<String,String> testData = new HashMap<String,String>();
   
		Reader reader = Files.newBufferedReader(Paths.get("C:\\Users\\Gateway\\Desktop\\schemax\\Company\\TestData_Create_PMS.csv"));
        CSVReader csvReader = new CSVReader(reader);
        List<String[]> values = csvReader.readAll();
        String[] headers = values.get(0);
        for(int i=1;i<values.size();i++) {
        	
        	String[] testValues = values.get(i);
        	for(int j=0;j<testValues.length;j++) {
        	testData.put(headers[j], testValues[j]);
        	}
        	String testCase = testData.get(testSenarioColumnName);
        	testData.remove(testSenarioColumnName);
        	attributeValues.put(testCase, testData);
            System.out.println(testData);

        }
      //  System.out.println(testData);
		return attributeValues;
	}
	
	public static Map<String, String> attrPaths(final String mapperpath) throws IOException {
		 Map<String,String> attributeValues1 = new HashMap<String,String>();
			Map<String,String> attrPaths = new HashMap<String,String>();
		// Map<String,Map<String,String>> attributeValues1 = new HashMap<String,Map<String,String>>();
	      Reader reader1 = Files.newBufferedReader(Paths.get("C:\\Users\\Gateway\\Desktop\\Mapper.csv"));
	      CSVReader csvReader1 = new CSVReader(reader1);
	      List<String[]> values1 = csvReader1.readAll();
	      String[] headers1 = values1.get(0);
	      for(int i=1;i<values1.size();i++) {
	      
	      	String[] testValues1 = values1.get(i);
	      	for(int j=0;j<testValues1.length;j++) {
	      		attrPaths.put(headers1[j], testValues1[j]);
	      	}
	    
	        System.out.println(attrPaths);

	      }
		return attrPaths;
    
	}


	
	
	/*public static  void generateJsonThroughMapper(Map<String,String> attributeValues, Map<String,String> attrPaths, JSONObject jsonObject){
		for(String attr : attributeValues.keySet()) {
		String[] attrPath = attrPaths.get(attr).split("/");
		JSONObject currNode = jsonObject;
		for(int i=0;i<attrPath.length-1;i++) {
		currNode = currNode.getJSONObject(attrPath[i]);
		}
		currNode.put(attrPath[attrPath.length-1], attributeValues.get(attr));
		}
		}*/
	

	public static  JsonObject generateJsonThroughMapper(Map<String, Map<String, String>> attributeValues, Map<String,String> attrPaths, JsonObject readJsonObject) throws IOException{
		
		
		byte[] jsonBytes = Files.readAllBytes(Paths.get("C:\\Users\\Gateway\\Desktop\\schemax\\Company\\TestData_Create_PMS"));

		ObjectMapper objectMapper = new ObjectMapper();
		Object json =  objectMapper.readValue( jsonBytes, Object.class ) ;
 
		System.out.println(json);
	//	System.out.println( objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
		//System.out.println(testData);.
		Gson gson =new Gson();
		JsonElement jelem = gson.fromJson(json.toString(), JsonElement.class);
		JsonObject jobj = jelem.getAsJsonObject();
        for(String attr : testData.keySet()) {
        	//System.out.println(attr);
        	
            String attrPath = testData.get(attr);        	
            	//System.out.println(attrPath);
        	 jobj.addProperty(attr, attrPath);
        	// System.out.println(jobj);
        	
        	}
		return jobj;
        
    	//System.out.println(jobj);
    
       /* Response response = given().
    			contentType(ContentType.JSON)
    			.accept(ContentType.JSON)
    			//.body("{\"customer_code\": \"222\",\"customer_name\": \"abcdefg\",\"customer_description\": \"demo\",\"isActive\":\"true\"}")
    			.body(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString( json ))
    			.when()
    			.post(ROOT_URI + "/customer/createCustomer");
    	System.out.println("POST Response\n" + response.asString());
    	System.out.println("Status Code :" + response.getStatusCode());
			return readJsonObject;
	    
	        	
		
}
	*/
	

}

}	
	
	
	
	
	
	
	
