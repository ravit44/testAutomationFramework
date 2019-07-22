package com.schemaxtech.testAutomationFrameworkTest;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.*;
import org.openqa.selenium.By;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.opencsv.CSVReader;

public class DDT {
	public WebDriver driver;
	
	@BeforeSuite
	public void launchApp(){
	System.setProperty("webdriver.chrome.driver", "D:\\selenium\\chromedriver.exe");
	driver = new ChromeDriver();
	//driver.get("https://www.google.com/");
	//driver.manage().window().maximize();
	driver.get("http://www.newtours.demoaut.com/");
	}
	
	public static String testSenarioColumnName = "TestCase";
	
	  private  JSONObject getJsonObject(String jsonFilePath) throws IOException
	  
	  {
		
		byte[] jsonBytes = Files.readAllBytes(Paths.get("jsonpath"));

		ObjectMapper objectMapper = new ObjectMapper();

		Object json = objectMapper.readValue( jsonBytes, Object.class );

		System.out.println( objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString( json ) );
		return null;
		
	}
	
	private Map<String,Map<String,String>> getAttributeValues(String csvPath){
		try {
				Map<String,Map<String,String>> attributeValues = new HashMap<String,Map<String,String>>();
	            Reader reader = Files.newBufferedReader(Paths.get(csvPath));
	            CSVReader csvReader = new CSVReader(reader);
	            List<String[]> values = csvReader.readAll();
	            String[] headers = values.get(0);
	            for(int i=1;i<values.size();i++) {
	            	Map<String,String> testData = new HashMap<String,String>();
	            	String[] testValues = values.get(i);
	            	for(int j=0;j<testValues.length;j++) {
	            		testData.put(headers[j], testValues[j]);
	            	}
	            	String testCase = testData.get(testSenarioColumnName);
	            	testData.remove(testSenarioColumnName);
	            	attributeValues.put(testCase, testData);
	            }
	            return attributeValues;
	            
	            
		}catch(Exception ex) {
			
		}
		return null;
	}
	
	private void generateJsonThroughMapper(Map<String,Object> attrValues, Map<String,String> attrPaths, JSONObject jsonObject){
		for(String attr : attrValues.keySet()) {
			String[] attrPath = attrPaths.get(attr).split("/");
			JSONObject currNode = jsonObject;
			for(int i=0;i<attrPath.length-1;i++) {
				currNode = currNode.getJSONObject(attrPath[i]);
			}
			currNode.put(attrPath[attrPath.length-1], attrValues.get(attr));
		}
	}
	
	
	
	
	
	@Test(dataProvider = "getData")
	public void testSenchaLogin(String Username, String Password) throws InterruptedException{
		driver.findElement(By.name("userName")).sendKeys(Username);
		driver.findElement(By.name("password")).sendKeys(Password);
		driver.findElement(By.name("login")).click();
		Thread.sleep(3000);
		Assert.assertTrue(driver.findElement(By.xpath("/html/body/div/table/tbody/tr/td[2]/table/tbody/tr[4]/td/table/tbody/tr/td[2]/table/tbody/tr[3]/td/p/font")).isDisplayed(),"Login Failed");
	    //System.out.println("Login successful");
	}
	
	@DataProvider
	public Object[][] getData(){
		Object[][] data = new Object[3][2];
		data[0][0] = "username1@gmail.com";
		data[0][1] = "pssword1";
		data[1][0] = "username3@gmail.com";
		data[1][1] = "password3";
		data[2][0] = "sch";
		data[2][1] = "sch";
		return data;
	}
	
	
	@AfterSuite
	public void closeBrowser(){
		driver.quit();
	}
}