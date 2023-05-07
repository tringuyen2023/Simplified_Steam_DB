import java.io.PrintStream;
import java.sql.*;
import java.util.Calendar;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;    


public class App {
	private static final Scanner in = new Scanner(System.in);
	private static final PrintStream out = System.out;
	private static final String db_url = "jdbc:mysql://localhost:3306/steam";
    private static final String db_user = "root";
    private static final String db_pw = "Mrnm123Mrnm123@";

	public static void main(String[] args) {
		try (Connection conn = DriverManager.getConnection(db_url, db_user, db_pw)) {
            displayMenu();
			loop: while (true) {
				switch (requestString("Selection (0 to quit, 9 for menu)? ")) {
				case "0": // Quit
					break loop;

				case "1": // Reset
					list_games(conn);
					break;

				case "2": // List students
					list_users(conn);         // listStudents(conn) --> list_users(conn)
					break;

				case "3": // Show transcript
					show_libraries(conn);     // showTranscript(conn) --> show_libraries(conn)
					break;

				case "4": // Add student
					add_user(conn);         // addStudent(conn) --> add_user(conn)
					break;

				case "5": // Add enrollment
					add_game(conn);    // addEnrollment(conn) --> add_games(conn)
					break;
				
				case "6":
					add_game_to_lib(conn);
					break;
				
				case "7":
					can_refund(conn);
					break;
				
				case "8":
					remove_game(conn);
					break;

				default:
					displayMenu();
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		out.println("Done");
	}


	private static void displayMenu() {
		out.println("0: Quit");
		out.println("1: Show available Games");
		out.println("2: List users");
		out.println("3: Show user's libraries");
		out.println("4: Add user");
		out.println("5: Add game for sale");
		out.println("6: Add game to user's library");
		out.println("7: Check Refund Validity");
		out.println("8: Remove game from user's library");
	}

	private static String requestString(String prompt) {
		out.print(prompt);
		out.flush();
		return in.nextLine();
	}

    private static void list_games(Connection conn){
        StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM Games");

		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query.toString())){
			out.printf("%-3s %-35s %-25s %-20s %-15s %-8s\n", "ID", "Title", "Developer", "Release Date", "Price", "Genre");
			out.println("----------------------------------------------------------------------------------");

			while (rs.next()){
				int game_id = rs.getInt("game_id");
				String title = rs.getString("title");
				String developer = rs.getString("developer");
				Date release_date = rs.getDate("release_date");
				float price = rs.getFloat("price");
				String genre = rs.getString("genre");
				out.printf("%-3s %-35s %-25s %-20s %-15s %-8s\n", game_id, title, developer, release_date, price, genre);
			} 
		} catch (SQLException e){
			e.printStackTrace();
		}
    }

	private static void list_users(Connection conn) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM Users");

		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query.toString())) {
			// out.printf("%-3s %-10s %-4s %-8s\n", "Id", "Name", "Year", "Major");  // "%-3s %-10s %-4s %-8s\n" used to format the print
			// out.println("----------------------------");
			out.printf("%-3s %-15s %-30s %-20s %-8s\n", "ID", "Username", "Email", "DOB", "Country");
			out.println("------------------------------------------------------------------");
			
			while (rs.next()) { // Iterate through each row 
				int user_id = rs.getInt("user_id");				// get value of col 'sid'
				String username = rs.getString("username");
				String email = rs.getString("email");
				Date dob = rs.getDate("dob");
				String country = rs.getString("country");
				out.printf("%-3s %-15s %-30s %-20s %-8s\n", user_id, username, email, dob, country);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void show_libraries(Connection conn) {
		String user_id = requestString("User ID? ");

		StringBuilder query = new StringBuilder();
		query.append("SELECT g.title, g.developer, g.genre, l.play_time, l.date_purchased");
		query.append("  FROM Games g");
        query.append("  JOIN Libraries l ON l.game_id = g.game_id");
		query.append("  WHERE l.user_id = ?");


		try (PreparedStatement pstmt = conn.prepareStatement(query.toString())) {
			pstmt.setString(1, user_id); // Set the ? part to what user input
			ResultSet rs = pstmt.executeQuery();

			out.printf("%-30s %-15s %-20s %-15s %-8s\n", "Title", "Genre", "Developer", "Play Time", "Date Purchased");
			out.println("----------------------------------------------------------------------------------------------");
			while (rs.next()) {
				String title = rs.getString("title");
                String developer = rs.getString("developer");
				String genre = rs.getString("genre");
				int play_time = rs.getInt("play_time");
				Date date_purchased = rs.getDate("date_purchased");

				out.printf("%-30s %-15s %-20s %-15s %-8s\n", title, developer, genre, play_time, date_purchased);
			}

			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void add_user(Connection conn) {
		String user_id = requestString("Id number? ");
		String username = requestString("Username? ");
		String email = requestString("Email? ");
		String dob = requestString("DOB? (Please enter in the following format: yyyy-mm-dd) ");
		while (!dob.matches("\\d{4}-\\d{2}-\\d{2}") || (Integer.parseInt(dob.substring(5, 7)) > 12)) {
            System.out.println("Invalid date format. Please enter your date of birth (yyyy-mm-dd): ");
            dob = requestString("");
        }

		String country = requestString("Country? ");

		StringBuilder command = new StringBuilder();
		command.append("INSERT INTO Users(user_id, username, email, dob, country)");
		command.append("  SELECT ?, ?, ?, ?, ?");

		try (PreparedStatement pstmt = conn.prepareStatement(command.toString())) {
			pstmt.setString(1, user_id);
			pstmt.setString(2, username);
			pstmt.setString(3, email);
			pstmt.setString(4, dob);
			pstmt.setString(5, country);
			int count = pstmt.executeUpdate();

			out.println(count + " user(s) inserted");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void add_game(Connection conn) {
		String game_id = requestString("Id number? ");
		String title = requestString("Game title? ");
		String developer = requestString("Developer? ");
		String release_date = requestString("Date released? Please enter in the following format: yyyy-mm-dd ");
		while (!release_date.matches("\\d{4}-\\d{2}-\\d{2}")|| (Integer.parseInt(release_date.substring(5, 7)) > 12)) {
            System.out.println("Invalid date format. Please enter your date of birth (yyyy-mm-dd): ");
            release_date = requestString("");
        }
		//LocalDate ld_release = LocalDate.parse(str_release);
        //Timestamp timestamp_release = Timestamp.valueOf(ld_release.atStartOfDay());
        String price = requestString("Price? ");
        String genre = requestString("Genre? ");

		StringBuilder command = new StringBuilder();
		command.append("INSERT INTO Games(game_id, title, developer, release_date, price, genre)");
		command.append("  SELECT ?, ?, ?, ?, ?, ?");

		try (PreparedStatement pstmt = conn.prepareStatement(command.toString())) {
			pstmt.setString(1, game_id);
			pstmt.setString(2, title);
			pstmt.setString(3, developer);
			pstmt.setString(4, release_date);
			pstmt.setString(5, price);
			pstmt.setString(6,  genre);
			int count = pstmt.executeUpdate();

			out.println(count + " record(s) inserted");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void add_game_to_lib(Connection conn) {
		String username = requestString("Username? ");
		String game_title = requestString("Game title? ");
		LocalDateTime ldt_date_purchased = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String str_date_purchased = ldt_date_purchased.format(formatter);
        
        StringBuilder command = new StringBuilder();
		command.append("INSERT INTO Libraries(user_id, game_id, play_time, date_purchased)");
		command.append("  SELECT u.user_id, g.game_id, 0, ?");
		command.append("  FROM Users u, Games g");
		command.append("  WHERE u.username = ?");
		command.append("    AND g.title = ?");
		
		try (PreparedStatement pstmt = conn.prepareStatement(command.toString())) {
			pstmt.setString(1, str_date_purchased);
			pstmt.setString(2, username);
			pstmt.setString(3, game_title);
			int count = pstmt.executeUpdate();

			out.println(count + " game(s) inserted");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void can_refund(Connection conn) {
		String user_id = requestString("User ID? ");
		String game_id = requestString("Game ID? ");
		StringBuilder command = new StringBuilder();
		command.append("SELECT l.play_time, l.date_purchased");
		command.append("  FROM Users u, Games g, Libraries l");
		command.append("  WHERE u.user_id = ?");
		command.append("  AND g.game_id = ?;");
		
		int play_time = 0;
		Timestamp date_purchased = new Timestamp(System.currentTimeMillis());
		
		
		try (PreparedStatement pstmt = conn.prepareStatement(command.toString())) {
			pstmt.setString(1, user_id);
			pstmt.setString(2, game_id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				date_purchased = rs.getTimestamp("date_purchased"); 
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// set up to check if the user has bought the product for more than two weeks
		Calendar cal = Calendar.getInstance();
		cal.setTime(date_purchased);
		cal.add(Calendar.DAY_OF_WEEK, 14);
		Timestamp two_weeks_from_purchased = new Timestamp(cal.getTime().getTime());
		LocalDateTime curr_date = LocalDateTime.now();
		LocalDateTime ldt_two_weeks_from_purchased = two_weeks_from_purchased.toLocalDateTime();
		
        
		if (curr_date.isBefore(ldt_two_weeks_from_purchased) && play_time <= 120) {
			out.println("This product can be refunded. Select 7 at the menu to remove the game from this user's library");
		}
		else {
			out.println("This product cannot be refunded");
		}		
	}
	
	private static void remove_game(Connection conn) {
		String user_id = requestString("User's ID? ");
		String game_id = requestString("Game's ID? ");
		
		StringBuilder command = new StringBuilder();
		command.append("DELETE FROM Libraries");
		command.append("  WHERE user_id = ?");
		command.append("  AND game_id = ?");
		
		try (PreparedStatement pstmt = conn.prepareStatement(command.toString())) {
			pstmt.setString(1, user_id);
			pstmt.setString(2, game_id);
			int count = pstmt.executeUpdate();

			out.println(count + " game(s) deleted");
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
}
