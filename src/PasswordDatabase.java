import java.sql.*;
class PasswordDatabase {
    public static final String MYSQL_LOGIN = "myuser"; //change this to match your personal mysql server
    public static final String MYSQL_PASSWORD = "xxxx"; //change this to match your personal mysql server
    private static final String MYSQL_DIRECTORY = "\\myWebProject\\mysql\\bin"; //change to your personal mysql directory
    public static void main(String[] args){
        try
        {
            //opening local sql server
            Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"cd " + MYSQL_DIRECTORY + " &&" +
                    " mysqld --console\"");
        }
        catch (Exception e)
        {
            System.out.println("HEY Buddy ! U r Doing Something Wrong ");
            e.printStackTrace();
        }
        LoginArea loginScreen = new LoginArea();
    }

    public static int tableSize() {
        int size = 0;
        try{
            Connection con = DriverManager.getConnection( //connecting to database
                    "jdbc:mysql://localhost:3306/passwordstore", MYSQL_LOGIN, MYSQL_PASSWORD);

            Statement stmt=con.createStatement();
            String strSelect = "select * from " + LoginArea.currentUser;

            ResultSet rset = stmt.executeQuery(strSelect);
            while (rset.next()) { //checks how long to make our jtable based on SQL table size
                size++;
            }

        }catch(Exception e){ System.out.println(e);}
        return size; //returns size JTable should be
    }

    public static String[][] getData() { //gets the data stored in user password table
        String[][] data = new String[tableSize()][];
        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/passwordstore",MYSQL_LOGIN, MYSQL_PASSWORD);

            Statement stmt = con.createStatement();
            String strSelect = "select * from " + LoginArea.currentUser;
            //TODO
            //replace 'userpasswords' with logged in user.

            ResultSet rset = stmt.executeQuery(strSelect);
            int i = 0;
            while (rset.next()) { //retrieves info stored in SQL table
                String site = rset.getString("Website");
                String user = rset.getString("Username");
                String pass = rset.getString("Password");
                String[] temp = {site, user, pass};
                data[i] = temp;
                i++;
            }

        }
        catch(Exception e) {
            System.out.println(e);
        }
        return data; //returns info stored in SQL
    }
}