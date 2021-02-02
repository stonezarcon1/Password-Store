import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.*;
import java.util.Base64;

public class LoginArea extends JFrame {
    public static String currentUser;
    private static String currentPass;
    protected static JButton loginButton;
    protected static JButton registerButton;

    LoginArea() { //creating components, and listeners
        setTitle("Welcome");
        setPreferredSize(new Dimension(300, 200));
        setResizable(false);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Border border = new LineBorder(Color.BLACK);

        JLabel loginLabel = new JLabel("Please enter your login credentials.");
        loginLabel.setBounds(5, 5, 200, 20);

        JLabel username = new JLabel("Username: ");
        username.setBounds(5, 40, 100, 20);
        JTextArea usernameArea = new JTextArea();
        usernameArea.setBounds(80, 40, 150, 20);
        usernameArea.setBorder(border);

        JLabel password = new JLabel("Password: ");
        password.setBounds(5, 80, 100, 20);
        JPasswordField passwordArea = new JPasswordField(30);
        passwordArea.setBounds(80, 80, 150, 20);
        passwordArea.setBorder(border);

        registerButton = new JButton("Register");
        registerButton.setBounds(150, 120, 100, 20);

        loginButton = new JButton("Login");
        loginButton.setBounds(30, 120, 100, 20);

        usernameArea.addKeyListener(new KeyAdapter() { //allows tab to next component
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    passwordArea.requestFocus();
                    e.consume();
                }
            }
        });

        passwordArea.addKeyListener(new KeyAdapter() { //allows tab to next component
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    usernameArea.requestFocus();
                    e.consume();
                }
            }
        });

        registerButton.addActionListener(new ActionListener() { //opens register frame on click
            @Override
            public void actionPerformed(ActionEvent ae) {
                dispose();
                Register register = new Register(); //create register object to display frame.
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentUser = usernameArea.getText();
                currentPass = passwordArea.getText();
                checkLogin();
            }
        });

        add(username);
        add(usernameArea);
        add(password);
        add(passwordArea);
        add(loginLabel);
        add(loginButton);
        add(registerButton);
        pack();
        setLocationRelativeTo(null); //open centered
        setVisible(true);
    }
    private void checkLogin() { //checks if user can log in
        try {
            Connection con = DriverManager.getConnection( //connecting to database
                    "jdbc:mysql://localhost:3306/passwordstore", PasswordDatabase.MYSQL_LOGIN, PasswordDatabase.MYSQL_PASSWORD);
            Statement stmt = con.createStatement();
            String sqlString = "SHOW TABLES LIKE '" + currentUser + "';";
            ResultSet rset = stmt.executeQuery(sqlString);
            int size = 0;
            while (rset.next()) { //checking if a table with the currently entered username exists.
                size++;
            }
            if (size == 0) { //doesn't exist
                JOptionPane.showMessageDialog(this, "Your username or password is incorrect."); //error message
            } else { //if it does exist
                decode(); //turning readable salt back into byte[]
                Statement stmt2 = con.createStatement();
                sqlString = "SELECT password FROM userinfo WHERE username = '" + currentUser + "';";
                rset = stmt2.executeQuery(sqlString);
                String tempPass = "";
                while (rset.next()) {
                    tempPass = rset.getString("password"); //retreiving password
                }
                if (!tempPass.equals(currentPass)) { //error case
                    JOptionPane.showMessageDialog(this, "Your username or password is incorrect.");
                }
                if (tempPass.equals(currentPass)) { //successful
                    theGUI gui = new theGUI(); //directing to the main screen, and uses the current users info
                    dispose();
                    currentPass = "";
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    private void decode() { //decodes salt
        String encodedSalt = "";
        String password = currentPass;
        try {
            Connection con = DriverManager.getConnection( //connecting to database
                    "jdbc:mysql://localhost:3306/passwordstore", PasswordDatabase.MYSQL_LOGIN, PasswordDatabase.MYSQL_PASSWORD);
            Statement stmt = con.createStatement();
            String sqlString = "SELECT salt FROM userinfo WHERE username = '" + currentUser + "';";
            ResultSet rset = stmt.executeQuery(sqlString);
            while (rset.next()) {
                encodedSalt = rset.getString("salt");
            }
            byte[] salt = Base64.getDecoder().decode(encodedSalt);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            currentPass = Base64.getEncoder().encodeToString(hash);

        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException throwables) {
            throwables.printStackTrace();
        }
    }
}