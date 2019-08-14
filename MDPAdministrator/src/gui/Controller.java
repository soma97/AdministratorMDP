package gui;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import java.time.Duration;
import java.time.LocalDateTime;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.MessageDigest;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Optional;
import axis.SoapClient;
import javafx.geometry.Insets;
import main.*;

public class Controller {

    @FXML
    private ListView<String> activeList;

    @FXML
    private TextArea textArea;

    @FXML
    private Button activityButton,monitorButton,sendButton,createButton,blockButton;

    @FXML
    private void initialize()
    {
    	Main.listView=activeList;
    	activityButton.setDisable(true);
    	monitorButton.setDisable(true);
    	activeList.setOnMouseClicked( event -> {activityButton.setDisable(activeList.getSelectionModel().getSelectedItem()==null);
        monitorButton.setDisable(activeList.getSelectionModel().getSelectedItem()==null);
        });
    }
    
    @FXML
    public void showMonitor()
    {
    	if(activeList.getSelectionModel().getSelectedItem()!=null)
    	{
    		MonitorService monitoring=new MonitorService("Monitoring---:---"+activeList.getSelectionModel().getSelectedItem());
    		monitoring.setDaemon(true);
    		monitoring.start();
    	}
    }
    
    @FXML
    public void getActivity()
    {
    	if(activeList.getSelectionModel().getSelectedItem()==null)
    		return;
    	TableView table=new TableView(); 
    	Scene scene = new Scene(new Group());
    	Stage stage=new Stage();
        stage.setTitle("Activity table");
        stage.setWidth(520);
        stage.setHeight(500);
 
        final Label label = new Label("Activity of "+activeList.getSelectionModel().getSelectedItem());
 
        table.setEditable(true);
 
        TableColumn<String,SessionData> loginColumn = new TableColumn<>("Login");
        TableColumn<String,SessionData> logoutColumn = new TableColumn<>("Logout");
        TableColumn<String,SessionData> sessionDurationColumn = new TableColumn<>("Session duration");
        
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        logoutColumn.setCellValueFactory(new PropertyValueFactory<>("logout"));
        sessionDurationColumn.setCellValueFactory(new PropertyValueFactory<>("session"));
        
        table.getColumns().addAll(loginColumn, logoutColumn, sessionDurationColumn);        
 
        final VBox vbox = new VBox();
        vbox.setMinWidth(500);
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table);
 
        ((Group) scene.getRoot()).getChildren().addAll(vbox);
        
    	String activity=Main.readActivity(activeList.getSelectionModel().getSelectedItem());
    	String[] sessions=activity.split(System.getProperty("line.separator"));
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    	for(int i=0;i<sessions.length;++i)
    	{
    		if(sessions[i].contains("Logged in"))
    		{
    			LocalDateTime dt1=LocalDateTime.parse(sessions[i].replace("Logged in: ",""),formatter);
    			LocalDateTime dt2=null;
    			if((i+1)<sessions.length)
    			{
    				if(sessions[i+1].contains("Logged in"))
    				{
    					dt2=dt1.plusMinutes(10);
    				}
    				else dt2=LocalDateTime.parse(sessions[i+1].replace("Logged out: ",""),formatter);
    			}
    			if(dt2!=null)
    			{
    				table.getItems().add(new SessionData(sessions[i].split(": ")[1],sessions[i+1].split(": ")[1],Duration.between(dt1, dt2).toString().replace("PT", "")));
    			}
    			else {
    				table.getItems().add(new SessionData(sessions[i].split(": ")[1],"","currentlyActive"));
    			}
    		}
    	}
    	stage.setScene(scene);
        stage.show();
    }
        
    @FXML
    public void sendNotification()
    {
    	MulticastSocket socket = null;
		try {
			socket = new MulticastSocket();
			InetAddress address = InetAddress.getByName(Main.HOST);
			socket.joinGroup(address);
			byte[] buffer = new byte[textArea.getText().getBytes().length+1];
			buffer = textArea.getText().getBytes();
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, Main.PORT);
			socket.send(packet);
		} catch (IOException ex) {
			ex.printStackTrace();
			Main.setErrorLog(ex);
		}
		textArea.clear();
		textArea.setPromptText("Notification is sent successfully!");
    }
    

    @FXML
    public void blockUser()
    {
    	employeeControl(false);
    }
    @FXML
    public void registerUser()
    {
        employeeControl(true);
    }
    
    public void employeeControl(boolean isRegistration)
    {
    	Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle(isRegistration?"Registration":"Blocking"+" Dialog");
        dialog.setHeaderText(isRegistration?"Registration":"Blocking"+" on MDPEmployee");
        
        ButtonType registerButton = new ButtonType(isRegistration?"Register":"Block", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(registerButton, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        if(isRegistration)
        {
        	grid.add(new Label("Password:"), 0, 1);
        	grid.add(password, 1, 1);
        }
        else {
        	password.setText("NONE");
        }

        Node loginButton = dialog.getDialogPane().lookupButton(registerButton);
        loginButton.setDisable(true);

        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty() || password.getText().isEmpty());
        });

        password.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty() || username.getText().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        javafx.application.Platform.runLater(() -> username.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registerButton) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(usernamePassword -> {
        	boolean successful=false;
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                byte[] hashedPasswordByte = messageDigest.digest(usernamePassword.getValue().getBytes("UTF8"));
                String hashedPassword = Base64.getEncoder().encodeToString(hashedPasswordByte);
                if(isRegistration)
                	successful=SoapClient.addEmployee(usernamePassword.getKey(), hashedPassword);
                else successful=SoapClient.blockEmployee(usernamePassword.getKey(), hashedPassword);
           }catch (Exception e){
        	   e.printStackTrace();
        	   Main.setErrorLog(e);
           }
           if(!successful)
           {
        	   String messageAlert="Username already exists";
        	   if(!isRegistration)
        		   messageAlert="Username does not exists";
               Alert alert = new Alert(AlertType.ERROR, messageAlert + " !", ButtonType.OK);
               alert.showAndWait();
               return;
           }
            dialog.close();
        });
    }
}
