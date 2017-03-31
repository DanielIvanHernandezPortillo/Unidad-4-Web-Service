package mx.edu.utng.ws;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ActividadProfesorWS {
	private Connection connection;
	private Statement statement;
	private ResultSet resultSet;
	private PreparedStatement ps;
	
	public ActividadProfesorWS() {
		conectar();
	}
	
	private void conectar(){
		
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(
					"jdbc:postgresql://localhost:5432/wstst",
					"postgres","12345");
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}catch (SQLException e) {
			e.printStackTrace();
			
		}
		
	}
	
	public int addActividadProfesor(ActividadProfesor actividadProfesor){
		int result = 0;
		if(connection!=null){
			try {
				ps = connection.prepareStatement(
						"INSERT INTO actividad_profesor(horas_asignadas, actividad, maestro, periodo)"
						+"VALUES (?,?,?,?);");
				ps.setInt(1, actividadProfesor.getHorasAsignadas());
				ps.setString(2, actividadProfesor.getActividad());
				ps.setString(3, actividadProfesor.getMaestro());
				ps.setInt(4, actividadProfesor.getPeriodo());
				result = ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public int editActividadProfesor(ActividadProfesor actividadProfesor){
		int result = 0;
		if(connection!=null){
			try {
				ps = connection.prepareStatement(
						"UPDATE actividad_profesor SET  horas_asignadas=?,"
						+ "actividad= ?,"
						+ "maestro = ?,"
						+ "periodo =?"
						+ "WHERE id =?;");
				ps.setInt(1, actividadProfesor.getHorasAsignadas());
				ps.setString(2, actividadProfesor.getActividad());
				ps.setString(3, actividadProfesor.getMaestro());
				ps.setInt(4, actividadProfesor.getPeriodo());
				ps.setInt(5, actividadProfesor.getId());
				result = ps.executeUpdate();
			} catch (SQLException e) {
		e.printStackTrace();
			}
		}
		return result;
	}
	
	public int removeActividadProfesor(int id){
		int result = 0;
		if(connection!=null){
			try {
				ps = connection.prepareStatement(
						"DELETE FROM actividad_profesor WHERE id =?;");
				ps.setInt(1, id);
				result = ps.executeUpdate();
			} catch (SQLException e) {
		e.printStackTrace();
			}
		}
		return result;
	}
	
	public ActividadProfesor[] getActividadProfesores(){
		ActividadProfesor[] result = null;
		List<ActividadProfesor> actividadProfesores = new ArrayList<ActividadProfesor>();
		
		if(connection!=null){
			try {
				statement = connection.createStatement();
				resultSet = statement.executeQuery(
						"SELECT * FROM actividad_profesor;");
				while(resultSet.next()){
					ActividadProfesor actividadProfesor = new ActividadProfesor (
							resultSet.getInt("id"),
							resultSet.getInt("horas_asignadas"),
							resultSet.getString("actividad"),
							resultSet.getString("maestro"),
							resultSet.getInt("periodo"));
					
					actividadProfesores.add(actividadProfesor);
				
				}
			} catch (SQLException e) {
		e.printStackTrace();
			}
		}
		result = actividadProfesores.toArray(new ActividadProfesor[actividadProfesores.size()]);
		return result;
	}
	
	public ActividadProfesor getActividadProfesorById(int id){
		ActividadProfesor actividadProfesor = null;
		
		if(connection!=null){
			try {
				ps = connection.prepareStatement("SELECT * FROM actividad_profesor WHERE id= ?");
				ps.setInt(1, id);
				resultSet = ps.executeQuery();
						
				if(resultSet.next()){
					actividadProfesor = new ActividadProfesor (
							resultSet.getInt("id"),
							resultSet.getInt("horas_asignadas"),
							resultSet.getString("actividad"),
							resultSet.getString("maestro"),
							resultSet.getInt("periodo"));
					 
					
				
				}
			} catch (SQLException e) {
		e.printStackTrace();
			}
		}
		return actividadProfesor;
	}
	
	
}

