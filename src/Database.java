import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;

public class Database {
    private final String MYSQL_USERNAME = "myuser";
    private final String MYSQL_PASSWORD = "xxxx";
    private final String MYSQL_DIRECTORY = "\\myWebProject\\mysql\\bin";

    Database() {
    }

    public void connectToDB() {
        try {
            Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"cd" + MYSQL_DIRECTORY + " &&" +
                    " mysqld --console\"");
        } catch (Exception e) {
            System.out.println("Something went wrong.");
            e.printStackTrace();
        }
    }
    public boolean checkUsername(String username) {
        int check = 0;
        try {
            String sqlCheck = "SELECT * FROM userinfo WHERE username = ?";
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/passwordstore", MYSQL_USERNAME, MYSQL_PASSWORD);
            PreparedStatement p = con.prepareStatement(sqlCheck);
            p.setString(1, username);
            ResultSet rset = p.executeQuery();
            while (rset.next()) {
                check++;
            }
            con.close();
        } catch (SQLException e) {
            System.out.println("Username doesn't exist");
            e.printStackTrace();
        }
        if (check == 0) {
            return true;
        } else {
            return false;
        }
    }
    public void addUser(User user) {
        try {
            String sqlInsert = "INSERT INTO userinfo VALUES (?, ?, ?)";
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/passwordstore", MYSQL_USERNAME, MYSQL_PASSWORD);
            PreparedStatement p = con.prepareStatement(sqlInsert);
            p.setString(1, user.getUsername());
            p.setString(2, user.getPassword());
            p.setString(3, user.getSalt());
            p.executeUpdate();

            String sqlCreateTable = "CREATE TABLE IF NOT EXISTS " + user.getUsername()
                    + " (Website varchar(50), Username varchar(50), Password varchar(50))";
            PreparedStatement p2 = con.prepareStatement(sqlCreateTable);
            p2.executeUpdate();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean loginUser(User user) {
        String passCheck = "";
        String salt = "";
        try {
            String sqlSelect = "SELECT * FROM userinfo WHERE username = ?";
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/passwordstore", MYSQL_USERNAME, MYSQL_PASSWORD);
            PreparedStatement p = con.prepareStatement(sqlSelect);
            p.setString(1, user.getUsername());
            ResultSet rset = p.executeQuery();
            rset.next();
            passCheck = rset.getString(2);
            salt = rset.getString(3);
            user.setPlainPassword(user.hashPass(salt, user.getPassword()));
            con.close();
        } catch (SQLException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (user.getPassword().equals(passCheck)) {
            return true;
        } else {
            return false;
        }
    }
    public String[][] getData(User user) { //gets the data stored in user password table
        String[][] data = new String[tableSize(user)][];
        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/passwordstore",MYSQL_USERNAME, MYSQL_PASSWORD);

            Statement stmt = con.createStatement();
            String strSelect = "select * from " + user.getUsername();

            ResultSet rset = stmt.executeQuery(strSelect);
            int i = 0;
            while (rset.next()) { //retrieves info stored in SQL table
                String site = rset.getString("Website");
                String usern = rset.getString("Username");
                String pass = rset.getString("Password");
                String[] temp = {site, usern, pass};
                data[i] = temp;
                i++;
            }
            con.close();
        }
        catch(Exception e) {
            System.out.println(e);
        }
        return data; //returns info stored in SQL
    }
    public int tableSize(User user) {
        int size = 0;
        try{
            Connection con = DriverManager.getConnection( //connecting to database
                    "jdbc:mysql://localhost:3306/passwordstore", MYSQL_USERNAME, MYSQL_PASSWORD);

            Statement stmt=con.createStatement();
            String strSelect = "select * from " + user.getUsername();

            ResultSet rset = stmt.executeQuery(strSelect);
            while (rset.next()) { //checks how long to make our jtable based on SQL table size
                size++;
            }
            con.close();
        }catch(Exception e){
            System.out.println(e);}
        return size; //returns size JTable should be
    }
    public void addToUser(User user, Login login, String website, String username, String pass) {
        if (login.validStatus()) {
            try {
                String sqlAdd = "INSERT INTO " + user.getUsername() + " VALUES (?, ?, ?)";
                Connection con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/passwordstore", MYSQL_USERNAME, MYSQL_PASSWORD);
                PreparedStatement p = con.prepareStatement(sqlAdd);
                //p.setString(1, user.getUsername());
                p.setString(1, website);
                p.setString(2, username);
                p.setString(3, pass);
                p.executeUpdate();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public void removeFromUser(User user, Login login, String valueWebsite, String valueUsername, String valuePassword) {
        if (login.validStatus()) {
            try {
               String sqlRemove = "DELETE FROM " + user.getUsername() + " WHERE Website = ? AND Username = ? " +
                       "AND Password = ?";
                Connection con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/passwordstore", MYSQL_USERNAME, MYSQL_PASSWORD);
                PreparedStatement p = con.prepareStatement(sqlRemove);
                p.setString(1, valueWebsite);
                p.setString(2, valueUsername);
                p.setString(3, valuePassword);
                p.executeUpdate();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}