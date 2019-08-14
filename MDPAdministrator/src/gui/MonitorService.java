package gui;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Base64;
import javax.imageio.ImageIO;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import main.Main;

public class MonitorService extends Thread{
	public static String CHAT_HOST,KEY_STORE_PATH,KEY_STORE_PASSWORD;
	public static int MONITORING_PORT;
	String initialMessage;
	SSLSocket socket;
	PrintWriter printWriter;
	boolean isInterrupted=false;
	Image image;
	ImageView imageView;
	static Stage stage;
	
	public MonitorService(String initialMessage)
	{
		this.initialMessage=initialMessage;
	}
	
	public void run()
	{
		javafx.application.Platform.runLater(()->{
			while(image==null)
			{
				try {
					Thread.sleep(150);
				}catch(Exception e)
				{
					e.printStackTrace();
					Main.setErrorLog(e);
				}
			}
			imageView=new ImageView(image);
		BorderPane borderPane=new BorderPane();
		borderPane.getChildren().add(imageView);
		Scene scene=new Scene(borderPane,768,450);
		stage=new Stage();
		stage.setScene(scene);
		stage.setOnCloseRequest(e-> {
			isInterrupted=true;
			try {
			printWriter.println("OVER");
			}catch(Exception ex) {
				ex.printStackTrace();
				Main.setErrorLog(ex);
				}
			});
		stage.show();
		});
		System.setProperty("javax.net.ssl.trustStore", KEY_STORE_PATH);
        System.setProperty("javax.net.ssl.trustStorePassword", KEY_STORE_PASSWORD);

        SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try{
            socket=(SSLSocket) sf.createSocket(CHAT_HOST, MONITORING_PORT);
			printWriter=new PrintWriter(socket.getOutputStream(),true);
			printWriter.println(initialMessage);
			BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
		while(!isInterrupted)
		{
			String imageString=reader.readLine();
			if(imageString.contains("OVER"))
			{
				isInterrupted=true;
				javafx.application.Platform.runLater(()->stage.close());
			}
			System.out.println("UZEO SLIKUU"+imageString);
			byte[] imageBytes=Base64.getDecoder().decode(imageString);
			ByteArrayInputStream bais=new ByteArrayInputStream(imageBytes);
			System.out.println(bais.toString());
			BufferedImage originalImage=ImageIO.read(bais);
			System.out.println(originalImage.toString()+" "+imageString);
			image=SwingFXUtils.toFXImage(originalImage, null);
			javafx.application.Platform.runLater(()->imageView.setImage(image));
		}
		}catch(Exception e)
		{
			e.printStackTrace();
			Main.setErrorLog(e);
		}
		
	}

}
