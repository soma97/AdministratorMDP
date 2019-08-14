package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class ActiveTask{
	public static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	public static void refreshActive()
    {
        	scheduler.scheduleAtFixedRate(()-> {
        		try {
        			ArrayList<String> list=readAllJSON();
        			javafx.application.Platform.runLater(()->{
        			Main.listView.getItems().clear();
        			for(String x:list)
        				Main.listView.getItems().add(x);
        			});
        		}catch(Exception e)
        		{
        			e.printStackTrace();
        			Main.setErrorLog(e);
        		}
        		
        	} , 0, 10, TimeUnit.SECONDS);
    	
    }
    private static ArrayList<String> readAllJSON() throws IOException, JSONException {
        InputStream is = new URL(Main.BASE_URL).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONArray json = new JSONArray(jsonText);
            ArrayList<String> result=new ArrayList<>();
            for(Object x:json)
            {
            	if((boolean)((JSONObject)x).get("signedIn")==true)
            		result.add((String)((JSONObject)x).get("username"));
            }
            return result;
        } finally {
            is.close();
        }
    }
    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
