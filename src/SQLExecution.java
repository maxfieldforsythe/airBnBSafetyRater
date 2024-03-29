import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class SQLExecution {
	private Connection db;

	public SQLExecution() {

		
		LoginGUI login = new LoginGUI(this);
		login.open();
		
		//This commented out stuff was for me to be able to just put login info in file and not have
		//to enter it in every time.

//				FileReader reader = null;
//				try {
//					reader = new FileReader("login.txt");
//				} catch (FileNotFoundException e1) {
//					e1.printStackTrace();
//				}
//				
//				Scanner fileScan = new Scanner(reader);
//		        String username = fileScan.nextLine();
//		        String password = fileScan.nextLine();

//		Scanner scan = new Scanner(System.in);
//
//		System.out.println("Userame: ");
//		String username = scan.nextLine();
//		//I don't know how to make the invisible password thing work
//		System.out.println("Password: ");
//		String password = scan.nextLine();


//		String connectString = "jdbc:postgresql://bartik.mines.edu/csci403";
//
//		try {
//			db = DriverManager.getConnection(connectString, username, password);
//			System.out.println("Successfully connected to database.");
//		} 
//		catch (SQLException e) {
//			System.out.println("Error connecting to database: " + e);
//			return;
//		}
		
		

		try {
			//Testing that function works
			this.makeStatement("Austin");
			this.makeCrimeStatement("Austin");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public ResultSet makeStatement(String city) throws SQLException {

		String query = " Select * from listings where city like ?";

		PreparedStatement prepared = db.prepareStatement(query);


		prepared.setString(1, city + "%");

		return prepared.executeQuery();

	}

	public ResultSet getCities() throws SQLException {
		String query = " Select * from city;";

		PreparedStatement prepared = db.prepareStatement(query);
		return prepared.executeQuery();
	}
	
	
	public void login(String username, String password) throws SQLException, ClassNotFoundException {

        Class.forName("org.postgresql.Driver");
        String connectString = "jdbc:postgresql://bartik.mines.edu/csci403";

        db = DriverManager.getConnection(connectString, username, password);
        
        System.out.println("Successfully connected to database.");
    }
	
	
	public ResultSet makeCrimeStatement(String city) throws SQLException {

		String query2 = " Select * from crimes where city = LOWER(?)";

		PreparedStatement prepared2 = db.prepareStatement(query2, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);


		prepared2.setString(1, city);

		return prepared2.executeQuery();

	}


}
