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
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.*;
import java.util.Base64;

public class Register extends JFrame {
    //JFrame for allowing new users to register their info
    protected static JButton registerButton;
    protected static JButton goBackButton;
    protected static JTextArea usernameArea; //these fields are here to be accessed by listener class
    protected static JTextArea passwordArea;
    protected static JTextArea confirmPasswordArea;
    JFrame frame = this;

    Register() {
        setTitle("New User");
        setPreferredSize(new Dimension(400, 250));
        setResizable(false); //prevents strange layouts
        setLayout(null); //allows custom placement
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Border border = new LineBorder(Color.BLACK);

        //creating components for the frame
        JLabel loginLabel = new JLabel("Enter your new account credentials.");
        loginLabel.setBounds(5, 5, 250, 20);

        JLabel username = new JLabel("Username: ");
        username.setBounds(5, 40, 100, 20);
        usernameArea = new JTextArea();
        usernameArea.setBounds(125, 40, 150, 20);
        usernameArea.setBorder(border);

        JLabel password = new JLabel("Password: ");
        password.setBounds(5, 80, 100, 20);
        passwordArea = new JTextArea();
        passwordArea.setBounds(125, 80, 150, 20);
        passwordArea.setBorder(border);

        JLabel confirmPassword = new JLabel("Confirm Password: ");
        confirmPassword.setBounds(5, 120, 150, 20);
        confirmPasswordArea = new JTextArea();
        confirmPasswordArea.setBounds(125, 120, 150, 20);
        confirmPasswordArea.setBorder(border);

        registerButton = new JButton("Create account");
        registerButton.setBounds(30, 160, 150, 20);

        goBackButton = new JButton("Go back");
        goBackButton.setBounds(200, 160, 150, 20);

        usernameArea.addKeyListener(new KeyAdapter() { //key listener for tab functionality
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    passwordArea.requestFocus();
                    e.consume();
                }
            }
        });

        passwordArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    confirmPasswordArea.requestFocus();
                    e.consume();
                }
            }
        });

        confirmPasswordArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    usernameArea.requestFocus();
                    e.consume();
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (passwordCheck()) {
                    dupeCheck();
                }
                if (!passwordCheck()) {
                    JOptionPane.showMessageDialog(frame, "The passwords do not match.");
                    passwordArea.setText("");
                    confirmPasswordArea.setText("");
                }
            }
        });

        goBackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmPasswordArea.setText("");
                passwordArea.setText("");
                dispose();
                LoginArea loginArea = new LoginArea();
            }
        });

        add(username); //adding all components to the JFrame
        add(usernameArea);
        add(password);
        add(passwordArea);
        add(loginLabel);
        add(registerButton);
        add(goBackButton);
        add(confirmPassword);
        add(confirmPasswordArea);
        pack();
        setLocationRelativeTo(null); //open centered
        setVisible(true);
    }
    private boolean passwordCheck() { //this method checks that the passwords in passwordArea and confirmPasswordArea match
        return passwordArea.getText().equals(confirmPasswordArea.getText());
    }
    private void dupeCheck() { //this method is called after password is checked. This is the check that adds the user into the system.
        boolean isDuplicate = false;
        String tempUser;
        String username = usernameArea.getText();
        String password = passwordArea.getText();
        Connection con = null;
        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/passwordstore", PasswordDatabase.MYSQL_LOGIN, PasswordDatabase.MYSQL_PASSWORD);
            Statement stmt = con.createStatement();
            Statement stmt2 = con.createStatement();

            String checkPassStmt = "select * from userinfo;";
            ResultSet rset = stmt.executeQuery(checkPassStmt);
            while (rset.next()) {
                tempUser = rset.getString("username");
                if (tempUser.equalsIgnoreCase(usernameArea.getText())) { //checking every username, and returning true if a name already exists in the DB
                    isDuplicate = true;
                }
            }
            if (isDuplicate) { //if duplicate, clear entries, and give error message
                JOptionPane.showMessageDialog(frame, "That username already exists. Please try again.");
                usernameArea.setText("");
                passwordArea.setText("");
                confirmPasswordArea.setText("");
            }
            if (!isDuplicate) { //not duplicate
                if (allowedUsername(username)) {
                    byte[] salt = getSalt(); //getting the custom salt for this user
                    password = hashPass(salt, password); //hashing user password with custom salt
                    String storeSalt = Base64.getEncoder().encodeToString(salt); //encoding salt in a user friendly format to be stored
                    LoginArea.currentUser = usernameArea.getText();
                    String strStatement = "CREATE TABLE IF NOT EXISTS " + username
                            + " (Website varchar(50), Username varchar(50), Password varchar(50));"; //creating their own personal table
                    stmt2.execute(strStatement);

                    strStatement = "INSERT INTO userinfo VALUES ('" + username + "', '" + password + "', '" + storeSalt + "');"; //storing login info
                    stmt2.execute(strStatement);
                    usernameArea.setText("");
                    passwordArea.setText("");
                    confirmPasswordArea.setText("");
                    JOptionPane.showMessageDialog(frame, "Your account has been created successfully!" +
                            " You may now log in."); //success message
                    dispose();
                    LoginArea loginArea = new LoginArea(); //directing user back to login screen after successful account creation
                }
                if (!allowedUsername(username)) {
                    JOptionPane.showMessageDialog(frame, "Invalid username. Please try again.");
                    usernameArea.setText("");
                    passwordArea.setText("");
                    confirmPasswordArea.setText("");
                }
            }
        } catch (SQLException | InvalidKeySpecException | NoSuchAlgorithmException throwables) {
            throwables.printStackTrace();
        }
    }
    private String hashPass(byte[] salt, String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String string = Base64.getEncoder().encodeToString(salt); //salt decoder
        byte[] saltDecode = Base64.getDecoder().decode(string);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded(); //hashed password

        String hashedPass = Base64.getEncoder().encodeToString(hash);
        return hashedPass;
    }
    private byte[] getSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt); //generating a random salt that will be used to hash password

        return salt;
    }
    private boolean allowedUsername(String username) { //checking for SQL injection statements
        if     (username.toUpperCase().contains("INSERT") ||
                username.toUpperCase().contains("DELETE") ||
                username.toUpperCase().contains("SELECT") ||
                username.toUpperCase().contains("DROP") ||
                username.toUpperCase().contains("%") ||
                username.toUpperCase().contains("*")) {
            return false;
        }
        return true;
    }
}