package mx.edu.utng.ws;

public class ActividadProfesor {
	private int id;
	private int horasAsignadas;
	private String actividad;
	private String maestro;
	private int periodo;
	
	public ActividadProfesor(int id, int horasAsignadas, String actividad, String maestro, int periodo) {
		super();
		this.id = id;
		this.horasAsignadas = horasAsignadas;
		this.actividad = actividad;
		this.maestro = maestro;
		this.periodo = periodo;
	}
	
	public ActividadProfesor(){
		this(0,0,"","",0);
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getHorasAsignadas() {
		return horasAsignadas;
	}
	public void setHorasAsignadas(int horasAsignadas) {
		this.horasAsignadas = horasAsignadas;
	}
	public String getActividad() {
		return actividad;
	}
	public void setActividad(String actividad) {
		this.actividad = actividad;
	}
	public String getMaestro() {
		return maestro;
	}
	public void setMaestro(String maestro) {
		this.maestro = maestro;
	}
	public int getPeriodo() {
		return periodo;
	}
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	@Override
	public String toString() {
		return "ActividadProfesor [id=" + id + ", horasAsignadas=" + horasAsignadas + ", actividad=" + actividad
				+ ", maestro=" + maestro + ", periodo=" + periodo + "]";
	}
	
	
	
	

}
