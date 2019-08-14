package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileInputStream;
import org.json.JSONObject;
import axis.SoapClient;
import gui.MonitorService;

public class Main extends Application {
	public static ListView listView;
	public static String BASE_URL=null,HOST=null;
	public static int PORT=0;
	static Stage stage;
	static final Logger LOGGER = Logger.getLogger("Logger");
    static FileHandler handler;
	
	
	private void loadPropertiesAndRunTask(){
		Properties prop = new Properties();
        try {
        	//PROBLEM SA PROPERTIES FAJLOM PA SAM SAMO U OVO PROJEKTU RUCNO UCITAO KOMPLETNU PUTANJU DOK JE U DRUGIM PROJEKTIMA NORMALNO UCITAN PROP :(
            prop.load(new FileInputStream("C:\\Users\\milos\\eclipse-workspace\\MDPAdministrator\\WebContent\\WEB-INF\\resources\\config.properties"));
            BASE_URL = prop.getProperty("BASE_URL");
            HOST=prop.getProperty("HOST");
            PORT=Integer.parseInt(prop.getProperty("PORT"));
            MonitorService.CHAT_HOST=prop.getProperty("CHAT_HOST");
            MonitorService.MONITORING_PORT=Integer.parseInt(prop.getProperty("MONITORING_PORT"));
            MonitorService.KEY_STORE_PATH=prop.getProperty("KEY_STORE_PATH");
            MonitorService.KEY_STORE_PASSWORD=prop.getProperty("KEY_STORE_PASSWORD");
            handler=new FileHandler("error.log");
            LOGGER.addHandler(handler);
        } catch (Exception ex) {
            ex.printStackTrace();
            setErrorLog(ex);
        }
        ActiveTask.refreshActive();
	}
    @Override
    public void start(Stage primaryStage) throws Exception{
    	loadPropertiesAndRunTask();
    	stage=primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/gui/GUI.fxml"));
        primaryStage.setTitle("Administrator");
        primaryStage.setScene(new Scene(root, 1024, 512));
        primaryStage.setOnCloseRequest(e->ActiveTask.scheduler.shutdown());
        primaryStage.show();
    }


    public static void main(String[] args) {
    	launch(args);
    }
    

    public static String readActivity(String username) {
        try(InputStream is = new URL(BASE_URL + username).openStream())
        {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = ActiveTask.readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return (String)json.get("activity");
        }catch(Exception e)
        {
        	e.printStackTrace();
        	setErrorLog(e);
        }
        return "";
    }
    public static void setErrorLog(Exception exception)
    {
        StackTraceElement elements[] = exception.getStackTrace();
        for (StackTraceElement element:elements) 
            LOGGER.log(Level.WARNING, element.toString());
    }
    
}
