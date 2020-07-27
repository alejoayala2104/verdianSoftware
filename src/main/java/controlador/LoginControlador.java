package controlador;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import modelo.Usuario;

public class LoginControlador {
	
	String usuario,password,tipoUsuario;
	ControladorGeneral controlGeneral = new ControladorGeneral();

    @FXML
    private TextField txfUsuario;
    @FXML
    private PasswordField txfPassword;
    @FXML
    private Label lblMensaje;    
    
    @FXML
    public void login(ActionEvent event) throws SQLException, IOException {
    	this.usuario = txfUsuario.getText();
    	this.password = txfPassword.getText();
    	if(this.usuario.isEmpty() || this.password.isEmpty()) {
    		controlGeneral.mostrarAlerta(AlertType.ERROR, "ERROR: Campos vacíos", "Campos vacíos", "Por favor rellene todos los campos");
    		return;
    	}    	
    	
    	
    	String sentenciaSQL = "select * from usuarios where usuario like '"+this.usuario+"'";
    	ResultSet buscarUsuario = controlGeneral.ejecutarSentencia(sentenciaSQL);
    	
    	if(buscarUsuario.next()) {
    		if(this.password.equals(buscarUsuario.getString("contrasena"))) {
    			Usuario user = new Usuario(this.usuario, this.password, buscarUsuario.getString("permiso")); 
    			cargarHome(user);
    			//Cierra la ventana
    	    	Window nodo = ((Node) event.getSource()).getScene().getWindow();
    			Stage ventana = (Stage)(nodo);    	    	
    			ventana.close();
    		}
    		else {
    			lblMensaje.setText("Contraseña incorrecta");
    		}
    	}else {
    		controlGeneral.mostrarAlerta(AlertType.ERROR, "Usuario inexistente", "Usuario no encontrado", "El usuario ingresado no se encuentra en la base de datos");
    		return;
    	}    	
    }

    @FXML
    public void salir(ActionEvent event) {
    	Platform.exit();
    }

    @FXML
    public void initialize() {
    	
    }
    
    public void cargarHome(Usuario user) throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/home.fxml"));
    	Parent interfazHome = loader.load();
    	
    	HomeControlador homeControlador = loader.getController();
    	homeControlador.setUsuario(user);
    	Scene escenaHome = new Scene(interfazHome);	
		Stage ventanaHome = new Stage();
		ventanaHome.setScene(escenaHome);
		ventanaHome.setTitle("Inicio");
		ventanaHome.show();
    }
}
