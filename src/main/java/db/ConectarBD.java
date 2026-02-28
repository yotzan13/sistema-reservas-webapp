package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class ConectarBD {

	//declaramos la variables...
		public static String url="jdbc:mysql://localhost:3306/elsultan_db";
		public static String usuario="root";
		public static String password="admin#130401";
		private static Connection cn;
		//creamos el metodo conectar...
		public static Connection getConexion(){
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				//para conectarnos con la base de datos
				
				cn=DriverManager.getConnection(url,usuario,password);
				//emitimos un mensaje sobre la conexion...
				//aplicamos una condicion
				//if(cn!=null) JOptionPane.showMessageDialog(null,
					//	"hay conexion en BD","MENSAJE",JOptionPane.INFORMATION_MESSAGE);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  //fin del catch...
			//retornamos la conexion.
			return cn;	
		}  //fin del metodo.....
} //fin de la clase....
