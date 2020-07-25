package controlador;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;

public class AdminControlador {
	
	ControladorGeneral controlGeneral = new ControladorGeneral();
	String usuario, contraseña, tipoUsuario;

    @FXML
    private BorderPane bpnTransMain;
    @FXML
    private Label lblBievenido;
    @FXML
    private ImageView inicio_logo;
    @FXML
    private ImageView reg_logo;
    @FXML
    private StackPane stpCenter;
    @FXML
    private AnchorPane anpCrearUsuario;
    @FXML
    private TextField txfCrearUsuario;
    @FXML
    private AnchorPane anpCUPassword;
    @FXML
    private Label lblPassword;
    @FXML
    private ComboBox<String> cbxTipoUsuario;
    
    @FXML
    private AnchorPane anpActUsuario;
    @FXML
    private TextField txfActUsuario;
    @FXML
    private AnchorPane anpAUPassword;
    @FXML
    private Label lblPasswordAU;

    @FXML
    public void crearUsuario(ActionEvent event) {
    	esconderPanesMenosIndicado(anpCrearUsuario);
    	txfCrearUsuario.clear();
    	cbxTipoUsuario.getSelectionModel().clearSelection();    	
    }

    @FXML
    public void genPasswordCU(ActionEvent event) throws SQLException {
    	
    	this.usuario = txfCrearUsuario.getText();
    	this.tipoUsuario = cbxTipoUsuario.getSelectionModel().getSelectedItem();
    	if(this.usuario.isEmpty() || tipoUsuario==null) {
    		controlGeneral.mostrarAlerta(AlertType.ERROR, "Campos vacíos", "Campos vacíos", "Por favor rellene todos los campos");
    		return;
    	}
    	
    	if(controlGeneral.validarUsuario(this.usuario)) {
    		controlGeneral.mostrarAlerta(AlertType.ERROR, "Usuario existente", "Usuario existente", "El usuario con las credenciales ingresadas ya existe");
    		return;
    	}
    	
    	this.contraseña = generateRandomPassword(10);
    	lblPassword.setText("Contraseña del usuario " + this.usuario + " generada: " + this.contraseña); 
    	esconderPanesMenosIndicado(anpCUPassword);    	   	
    }
    
    @FXML
    public void finCrearUsuario(ActionEvent event) {
    	
    	String sentenciaSQL = "insert into usuarios values('"+this.usuario+"','"+this.contraseña+"','"+this.tipoUsuario+"');";
    	controlGeneral.ejecutarSentenciaInsert(sentenciaSQL);
    	
    	controlGeneral.mostrarAlerta(AlertType.INFORMATION, "Usuario creado", "Usuario creado", "Credenciales listas para usar");
    }
    
    @FXML
    public void actUsuario(ActionEvent event) {
    	esconderPanesMenosIndicado(anpActUsuario);
    	txfActUsuario.clear();
    }
    
    @FXML
    public void genPasswordAU(ActionEvent event) throws SQLException {

    	this.usuario = txfActUsuario.getText();  	
    	if(this.usuario.isEmpty()) {
    		controlGeneral.mostrarAlerta(AlertType.ERROR, "Campos vacíos", "Campos vacíos", "Por favor rellene todos los campos");
    		return;
    	}
    	
    	if(controlGeneral.validarUsuario(usuario)) {
    		this.contraseña = generateRandomPassword(10);
        	lblPasswordAU.setText("Contraseña del usuario " + this.usuario + " generada: " + this.contraseña); 
        	esconderPanesMenosIndicado(anpAUPassword); 
    	}else {
    		controlGeneral.mostrarAlerta(AlertType.ERROR, "Usuario no encontrado", "Usuario no encontrado", "El usuario encontrado no existe");
    		return;
    	}
    	
    	   	
    }
    
    @FXML
    public void finActUsuario(ActionEvent event) {

    	String updateSQL ="update usuarios set contrasena= '"+this.contraseña+"' where usuario like '"+this.usuario+"';";    	
    	controlGeneral.ejecutarSentenciaInsert(updateSQL);
    	
    	controlGeneral.mostrarAlerta(AlertType.INFORMATION, "Usuario actualizado", "Usuario actualizado con éxito", "Credenciales listas para usar");
    }

    @FXML
    public void initialize() {
    	this.lblBievenido.setVisible(true);
    	cbxTipoUsuario.setItems(FXCollections.observableArrayList("ADMINISTRADOR","AUXILIAR","JEFE"));
    }
    
    @FXML    
    public void entrarHome(ActionEvent event) throws IOException {
		Parent home = FXMLLoader.load(getClass().getResource("/vista/home.fxml"));
		Scene homeScene = new Scene(home);
		Window nodo = ((Node) event.getSource()).getScene().getWindow();
		Stage ventana = (Stage)(nodo);
		ventana.setScene(homeScene);
		ventana.show();
    }
    
    public void esconderPanesMenosIndicado(Node nodo) { 
    	
		ObservableList<Node> hijos = stpCenter.getChildren();
		for(Node hijo : hijos) {
			hijo.setVisible(false);
		}
		nodo.setVisible(true);
	}
    
    
	public static String generateRandomPassword(int len)
	{
		// ASCII range - alphanumeric (0-9, a-z, A-Z)
		final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

		SecureRandom random = new SecureRandom();
		StringBuilder sb = new StringBuilder();

		// each iteration of loop choose a character randomly from the given ASCII range
		// and append it to StringBuilder instance

		for (int i = 0; i < len; i++) {
			int randomIndex = random.nextInt(chars.length());
			sb.append(chars.charAt(randomIndex));
		}

		return sb.toString();
	}

}
