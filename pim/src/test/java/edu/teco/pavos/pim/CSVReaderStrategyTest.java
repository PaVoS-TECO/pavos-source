package edu.teco.pavos.pim;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

/**
 * Test of the CSVReaderStrategy
 * @author Jean Baumgarten
 */
public class CSVReaderStrategyTest {

	@Test
	public void getObservedPropertyTest() {
		
		CSVReaderStrategy reader = new CSVReaderStrategy("");
		String[] data = {
				"", "1", "name", "description", "definition"
		};
		
		Method method;
		try {
			
			method = CSVReaderStrategy.class.getDeclaredMethod("getObservedProperty", String[].class);
			method.setAccessible(true);
			String json = (String) method.invoke(reader, new Object[] { data });
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(json);
			
			assertTrue(((String) obj.get("@iot.id")).equals("1"));
			assertTrue(((String) obj.get("name")).equals("name"));
			assertTrue(((String) obj.get("description")).equals("description"));
			assertTrue(((String) obj.get("definition")).equals("definition"));
			
		} catch (NoSuchMethodException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (SecurityException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (IllegalAccessException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (IllegalArgumentException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (InvocationTargetException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (ParseException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		}
		
	}

	@Test
	public void getSensorTest() {
		
		CSVReaderStrategy reader = new CSVReaderStrategy("");
		String[] data = {
				"", "1", "name", "description", "encodingType", "metadata"
		};
		
		Method method;
		try {
			
			method = CSVReaderStrategy.class.getDeclaredMethod("getSensor", String[].class);
			method.setAccessible(true);
			String json = (String) method.invoke(reader, new Object[] { data });
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(json);
			
			assertTrue(((String) obj.get("@iot.id")).equals("1"));
			assertTrue(((String) obj.get("name")).equals("name"));
			assertTrue(((String) obj.get("description")).equals("description"));
			assertTrue(((String) obj.get("encodingType")).equals("encodingType"));
			assertTrue(((String) obj.get("metadata")).equals("metadata"));
			
		} catch (NoSuchMethodException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (SecurityException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (IllegalAccessException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (IllegalArgumentException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (InvocationTargetException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (ParseException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		}
		
	}

	@Test
	public void getLocationTest() {
		
		CSVReaderStrategy reader = new CSVReaderStrategy("");
		String[] data = {
				"", "1", "name", "description", "encodingType", "{\"a\": \"1\"}"
		};
		
		Method method;
		try {
			
			method = CSVReaderStrategy.class.getDeclaredMethod("getLocation", String[].class);
			method.setAccessible(true);
			String json = (String) method.invoke(reader, new Object[] { data });
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(json);
			
			assertTrue(((String) obj.get("@iot.id")).equals("1"));
			assertTrue(((String) obj.get("name")).equals("name"));
			assertTrue(((String) obj.get("description")).equals("description"));
			assertTrue(((String) obj.get("encodingType")).equals("encodingType"));
			assertTrue(((JSONObject) obj.get("location")).toJSONString().equals("{\"a\":\"1\"}"));
			
		} catch (NoSuchMethodException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (SecurityException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (IllegalAccessException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (IllegalArgumentException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (InvocationTargetException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (ParseException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		}
		
	}

	@Test
	public void getFoITest() {
		
		CSVReaderStrategy reader = new CSVReaderStrategy("");
		String[] data = {
				"", "1", "name", "description", "encodingType", "{\"a\": \"1\"}"
		};
		
		Method method;
		try {
			
			method = CSVReaderStrategy.class.getDeclaredMethod("getFoI", String[].class);
			method.setAccessible(true);
			String json = (String) method.invoke(reader, new Object[] { data });
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(json);
			
			assertTrue(((String) obj.get("@iot.id")).equals("1"));
			assertTrue(((String) obj.get("name")).equals("name"));
			assertTrue(((String) obj.get("description")).equals("description"));
			assertTrue(((String) obj.get("encodingType")).equals("encodingType"));
			assertTrue(((JSONObject) obj.get("feature")).toJSONString().equals("{\"a\":\"1\"}"));
			
		} catch (NoSuchMethodException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (SecurityException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (IllegalAccessException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (IllegalArgumentException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (InvocationTargetException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (ParseException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		}
		
	}

	@Test
	public void getThingTest() {
		
		CSVReaderStrategy reader = new CSVReaderStrategy("");
		String[] data = {
				"", "1", "name", "description", "{\"a\":\"1\"}", "1;2"
		};
		
		Method method;
		try {
			
			method = CSVReaderStrategy.class.getDeclaredMethod("getThing", String[].class);
			method.setAccessible(true);
			String json = (String) method.invoke(reader, new Object[] { data });
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(json);
			
			assertTrue(true);
			assertTrue(((String) obj.get("@iot.id")).equals("1"));
			assertTrue(((String) obj.get("name")).equals("name"));
			assertTrue(((String) obj.get("description")).equals("description"));
			assertTrue(((JSONObject) obj.get("properties")).toJSONString().equals("{\"a\":\"1\"}"));
			JSONArray a = (JSONArray) obj.get("Locations");
			for (int i = 0; i < a.size(); i++) {
				String o = "" + a.get(i);
				assertTrue(o.equals("{\"@iot.id\":\"1\"}") || o.equals("{\"@iot.id\":\"2\"}"));
			}
			
		} catch (NoSuchMethodException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (SecurityException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (IllegalAccessException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (IllegalArgumentException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (InvocationTargetException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		} catch (ParseException e) {
			System.out.println(e.getLocalizedMessage());
			assertTrue(false);
		}
		
	}

}
