package com.schemaxtech.testAutomationFramework;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
//import org.hamcrest.Matchers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import au.com.bytecode.opencsv.CSVReader;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.sf.json.JSONObject;


public class TestAutomation_Util3 {
	private static String testSenarioColumnName = "TestCase_Id";
	final static String ROOT_URI = "http://192.168.0.155:4003";
	

	public static   JsonObject readJsonObject () throws IOException {
	
		//CSV
		
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
           // System.out.println(testData);


        }
        //System.out.println(testData);
      //Mapper path
		
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
	    
	     //   System.out.println(attrPaths);

	      }
        
//Json
       byte[] jsonBytes = Files.readAllBytes(Paths.get("C:\\Users\\Gateway\\Desktop\\schemax\\Company\\TestData_Create_PMS"));

		ObjectMapper objectMapper = new ObjectMapper();
		Object json =  objectMapper.readValue( jsonBytes, Object.class ) ;
 
		System.out.println(json);
		Gson gson =new Gson();
		JsonElement jelem = gson.fromJson(json.toString(), JsonElement.class);
		JsonObject jobj = jelem.getAsJsonObject();
		 
 
	
        for(String attr : testData.keySet()) {
        	//System.out.println(attr);
        	
            String attrPath = testData.get(attr);        	
            	//System.out.println(attrPath);
        	 jobj.addProperty(attr, attrPath);
         	System.out.println(jobj);
         	Response response = given().
        			contentType(ContentType.JSON)
        			.accept(ContentType.JSON)
        			//.body("{\"customer_code\": \"222\",\"customer_name\": \"abcdefg\",\"customer_description\": \"demo\",\"isActive\":\"true\"}")
        			//.body(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString( jobj ))
        			.body( json )

        			.when()
        			.post(ROOT_URI + "/customer/createCustomer");
        	System.out.println("POST Response\n" + response.asString());
        	System.out.println("Status Code :" + response.getStatusCode());
        	}
        
    	//System.out.println(jobj);
    	
        	
	
	
		return jobj;
        	
	}
	}

