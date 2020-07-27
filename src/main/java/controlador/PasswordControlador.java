package controlador;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import modelo.Usuario;

public class PasswordControlador {	
	
	private ControladorGeneral controlGeneral = new ControladorGeneral();
	private Usuario user;
	private String actual,nueva,confirmarNueva;
	
	public void setUser(Usuario user) {
		this.user = user;
	}
 
    @FXML
    private ImageView inicio_logo;
    @FXML
    private ImageView reg_logo;
    @FXML
    private AnchorPane anpCambiarContrasena;
    @FXML
    private PasswordField txfActual;
    @FXML
    private PasswordField txfNueva;
    @FXML
    private PasswordField txfConfirmarNueva;

    @FXML
    public void cambiarContrasena(ActionEvent event) {
    	this.actual = txfActual.getText();
    	this.nueva = txfNueva.getText();
    	this.confirmarNueva = txfConfirmarNueva.getText(); 
    	
    	if(this.actual.isEmpty() || this.nueva.isEmpty() || this.confirmarNueva.equals("")) {    		
    		controlGeneral.mostrarAlerta(AlertType.ERROR, "ERROR: Campos vacíos", "Campos vacíos", "Por favor rellene todos los campos");
    		return;
    	}
    	
    	
    	if(!this.nueva.equals(this.confirmarNueva)) {
    		controlGeneral.mostrarAlerta(AlertType.ERROR, "Contraseña errónea", "Contraseñas distintas", "La contraseña nueva no coincide con la confirmación");
    		return;
    	}
    	
    	if(!this.user.getContrasena().equals(this.actual)) {
    		controlGeneral.mostrarAlerta(AlertType.ERROR, "Contraseña errónea", "Contraseña actual errónea", "La contraseña actual no coincide con la contraseña actual del usuario");
    		return;
    	}
    	
    	String sentenciaSQL = "update usuarios set contrasena= '"+this.nueva+"' where usuario like '"+this.user.getUsuario()+"';";
    	controlGeneral.ejecutarSentenciaInsert(sentenciaSQL);
    	
    	controlGeneral.mostrarAlerta(AlertType.INFORMATION, "Contraseña actualizada", "Contraseña cambiada exitosamente", "Puede iniciar sesión con la nueva contraseña");
    	limpiarTodo();
    }

    @FXML
    public void entrarContrasena(ActionEvent event) {
    	limpiarTodo();
    }

    @FXML    
    public void entrarHome(ActionEvent event) throws IOException {
    	//Cierra la ventana
    	Window nodo = ((Node) event.getSource()).getScene().getWindow();
		Stage ventana = (Stage)(nodo);    	    	
		ventana.close();
    }

    @FXML
    public void initialize() {
    	this.user = new Usuario();
    }
    
    public void limpiarTodo() {
    	this.txfActual.clear();
    	this.txfNueva.clear();
    	this.txfConfirmarNueva.clear();
    }
}
