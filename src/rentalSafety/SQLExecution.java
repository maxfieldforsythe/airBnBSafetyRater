package rentalSafety;
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

	public ResultSet makeStatementWithPreference(String city, String safety) throws SQLException {
		
		String prefQuery;
		if(safety.equals("Highest")) {
			prefQuery = " Select * from"
					+ " listings, listing_ratings where listings.city like ? and listing_id = id order by rating DESC";
		}else {
			prefQuery = " Select * from"
					+ " listings, listing_ratings where listings.city like ? and listing_id = id order by rating ASC";
		}

		
		PreparedStatement prepared = db.prepareStatement(prefQuery);
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

	public ResultSet getAllListings() throws SQLException {
		String query3 = " Select * from listings";

		PreparedStatement prepared = db.prepareStatement(query3);

		return prepared.executeQuery();
	}

	public ResultSet getAllCrimes() throws SQLException {
		String query4 = " Select * from crimes";

		PreparedStatement prepared = db.prepareStatement(query4);

		return prepared.executeQuery();
	}

	public void insertRatings(int id, int rating) throws SQLException {
		String query5 = " Insert into listing_ratings (listing_id, rating) values (?,?)";

		PreparedStatement prepared = db.prepareStatement(query5);
		prepared.setInt(1, id);
		prepared.setInt(2, rating);

		prepared.execute();
	}

}
