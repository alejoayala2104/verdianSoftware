package modelo;

public class Usuario {

	private String usuario;
	private String contrasena;
	private String permiso;
	
	public Usuario(String usuario, String contrasena, String permiso) {
		this.usuario = usuario;
		this.contrasena = contrasena;
		this.permiso = permiso;
	}
	
	public Usuario() {
		
	}

	public String getUsuario() {
		return usuario;
	}

	public String getContrasena() {
		return contrasena;
	}

	public String getPermiso() {
		return permiso;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public void setPermiso(String permiso) {
		this.permiso = permiso;
	}

	@Override
	public String toString() {
		return "Usuario [usuario=" + usuario + ", contrasena=" + contrasena + ", permiso=" + permiso + ", getUsuario()="
				+ getUsuario() + ", getContrasena()=" + getContrasena() + ", getPermiso()=" + getPermiso()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}
	
	
}
