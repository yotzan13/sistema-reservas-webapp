package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class ConectarBD {

	//declaramos la variables
		public static String url="jdbc:mysql://localhost:3306/elsultan_db";
		public static String usuario="root";
		public static String password="tu-contraseña-aqui";
		private static Connection cn;
		//creamos el metodo conectar
		public static Connection getConexion(){
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				cn=DriverManager.getConnection(url,usuario,password);
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}  
			
			return cn;	
		}  
} 
