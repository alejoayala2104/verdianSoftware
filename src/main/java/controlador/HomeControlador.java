package controlador;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import modelo.Usuario;

public class HomeControlador {
	
	private Usuario usuario;
	private Collection<Stage> ventanasAbiertas;
	
	public void setUsuario(Usuario user) {
		this.usuario = user;
	}
	
	@FXML
	Image clie_logo = new Image (getClass().getResourceAsStream("/vista/icon/clie_logo.png"));
	@FXML
	ImageView clie_logo_view = new ImageView (clie_logo);
		
	@FXML
	Image trans_logo = new Image (getClass().getResourceAsStream("/vista/icon/trans_logo.png"));
	@FXML
	ImageView trans_logo_view = new ImageView (trans_logo);
	
	@FXML
	Image eval_logo = new Image (getClass().getResourceAsStream("/vista/icon/eval_logo.png"));
	@FXML
	ImageView eval_logo_view = new ImageView (eval_logo);
	
	@FXML
	Image ajus_logo = new Image (getClass().getResourceAsStream("/vista/icon/ajus_logo.png"));
	
	@FXML
	ImageView ajus_logo_view = new ImageView (ajus_logo);
	
	@FXML
	ImageView pass_logo_view = new ImageView (new Image (getClass().getResourceAsStream("/vista/icon/password_logo.png")));
	
	@FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private HBox hbxPanel;
    @FXML
    private VBox vbxEvaluacion;
    @FXML
    private VBox vbxAdministracion;
    
    @FXML
    private VBox vbxBienvenida;
    @FXML
    private Label lblTipoUsuario;
    @FXML
    private StackPane stpHome;
    @FXML
    private Button btnCerrarSesion;

	
	@FXML
	private void entrarClientes(ActionEvent event) throws IOException {
		
		Parent interfazClientes = FXMLLoader.load(getClass().getResource("/vista/clientes.fxml"));
		Scene escena = new Scene(interfazClientes);	
		Stage ventana = new Stage();
		ventana.setScene(escena);		
		ventana.show();
		ventanasAbiertas.add(ventana);
	}
	
	@FXML
	private void entrarTrans(ActionEvent event) throws IOException {
		
		Parent interfazTrans = FXMLLoader.load(getClass().getResource("/vista/transacciones.fxml"));
		Scene escena = new Scene(interfazTrans);	
		Stage ventana = new Stage();
		ventana.setScene(escena);		
		ventana.show();
		ventanasAbiertas.add(ventana);
		
//		Parent interfazTrans = FXMLLoader.load(getClass().getResource("/vista/transacciones.fxml"));
//		Scene escenaTrans = new Scene(interfazTrans);
//		Window nodo = ((Node) event.getSource()).getScene().getWindow();
//		Stage ventana = (Stage)(nodo);
//		ventana.setScene(escenaTrans);
//		ventana.show();
	}
	
	@FXML
	private void entrarEvaluacion(ActionEvent event) throws IOException {
		Parent interfazEval = FXMLLoader.load(getClass().getResource("/vista/evaluacion.fxml"));
		Scene escena = new Scene(interfazEval);	
		Stage ventana = new Stage();
		ventana.setScene(escena);		
		ventana.show();
		ventanasAbiertas.add(ventana);
	}
	
   @FXML
    private void entrarPagos(ActionEvent event) throws IOException {
	  	Parent interfazPagos = FXMLLoader.load(getClass().getResource("/vista/pagos.fxml"));
	  	Scene escena = new Scene(interfazPagos);	
		Stage ventana = new Stage();
		ventana.setScene(escena);		
		ventana.show();
		ventanasAbiertas.add(ventana);
    }
   
   @FXML
   public void entrarPassword(ActionEvent event) throws IOException {
	   
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/password.fxml"));
   		Parent interfazPassword = loader.load();
   		
	    PasswordControlador passwordControlador =  loader.getController();
	    passwordControlador.setUser(this.usuario);
	    
	    Scene escena = new Scene(interfazPassword);	
		Stage ventana = new Stage();
		ventana.setScene(escena);		
		ventana.show();
		ventanasAbiertas.add(ventana);
		
   }
   
   @FXML
   public void entrarAdmin(ActionEvent event) throws IOException {
	    Parent interfazAdmin = FXMLLoader.load(getClass().getResource("/vista/administracion.fxml"));
	    Scene escena = new Scene(interfazAdmin);	
		Stage ventana = new Stage();
		ventana.setScene(escena);		
		ventana.show();
		ventanasAbiertas.add(ventana);
   }
   
   @FXML
   public void cerrarSesion(ActionEvent event) throws IOException {
	   
	   ventanasAbiertas.forEach(window ->{
		   window.close();
	   });
	   
		Parent interfazLogin = FXMLLoader.load(getClass().getResource("/vista/login.fxml"));
		Scene escenaLogin = new Scene(interfazLogin);
		Window nodo = ((Node) event.getSource()).getScene().getWindow();
		Stage ventana = (Stage)(nodo);
		ventana.setScene(escenaLogin);
		ventana.show();
   }
   
   @FXML
   public void mostrarPanel(ActionEvent event) {
	   
//	   System.out.println(this.permiso);
	   if(this.usuario.getPermiso().equals("JEFE")) {
		   hbxPanel.getChildren().remove(vbxAdministracion);
		   lblTipoUsuario.setText("Jefe de crédito");		   
	   }else if(this.usuario.getPermiso().equals("AUXILIAR")){
		   hbxPanel.getChildren().remove(vbxAdministracion);
		   hbxPanel.getChildren().remove(vbxEvaluacion);
		   lblTipoUsuario.setText("Auxiliar de crédito");
	   }else {
		   lblTipoUsuario.setText("Administrador");
	   }
	   
	   this.lblTipoUsuario.setVisible(true);
	   this.stpHome.setVisible(true);
	   this.btnCerrarSesion.setVisible(true);
	   this.vbxBienvenida.setVisible(false);
   }
   
   @FXML
   public void initialize() {   
	   this.ventanasAbiertas = FXCollections.observableArrayList();
    }	
}
