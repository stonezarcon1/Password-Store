import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class theGUI extends JFrame {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 500;
    private static final String[] column ={"WEBSITE","USERNAME","PASSWORD"};
    private static final Border border = BorderFactory.createLineBorder(Color.BLACK);
    private static JTable jt;
    private static JTextArea websiteInfo;
    private static JTextArea usernameInfo;
    private static JTextArea passwordInfo;

    theGUI() {
        //database info
        setTitle("Secure Password Storage");
        setLayout(null);
        setPreferredSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBounds(0, 100, 800, 400);
        add(panel);

        String infoString = "Type the name of the website, the username," +
                "and the password that you use for the website. Press add when you're finished." +
                " If you would like to delete a selection, click the row, and press 'Remove'.";
        JLabel infoLabel = new JLabel("<html>" + infoString + "</html>");
        infoLabel.setBounds(5,2,600, 40);

        //creating the website area
        JLabel websiteLabel = new JLabel("Website: ");
        websiteLabel.setBounds(50,40,100,18);
        websiteInfo = new JTextArea();
        websiteInfo.setBorder(border);
        websiteInfo.setBounds(120, 40, 200, 18);

        JLabel usernameLabel = new JLabel("Username: ");
        usernameLabel.setBounds(50,60,100,18);
        usernameInfo = new JTextArea();
        usernameInfo.setBorder(border);
        usernameInfo.setBounds(120, 60, 200, 18);

        JLabel passwordLabel = new JLabel("Password: ");
        passwordLabel.setBounds(50,80,100,18);
        passwordInfo = new JTextArea();
        passwordInfo.setBorder(border);
        passwordInfo.setBounds(120, 80, 200, 18);

        jt = new JTable(PasswordDatabase.getData(),column);
        jt.setPreferredSize(new Dimension(800, 350));
        setActionMap();
        DefaultTableModel tableModel = new DefaultTableModel(PasswordDatabase.getData(), column) {
            @Override
            public boolean isCellEditable (int row, int column) {
                //all cells uneditable
                return false;
            }
        };

        jt.setModel(tableModel);
        JScrollPane sp=new JScrollPane(jt);
        sp.setBorder(border);

        panel.add(sp, BorderLayout.PAGE_END);

        JButton addButton = new JButton("Add");
        addButton.setBounds(350, 50, 100, 20);

        JButton removeButton = new JButton("Remove");
        removeButton.setBounds(500, 50, 100, 20);

        websiteInfo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    usernameInfo.requestFocus();
                    e.consume();
                }
            }
        });
        usernameInfo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    passwordInfo.requestFocus();
                    e.consume();
                }
            }
        });

        JTable jt = theGUI.getJt();
        removeButton.addActionListener((new ActionListener() { //listener to remove entries
            @Override
            public void actionPerformed(ActionEvent ae) {
                int row = jt.getSelectedRow();
                String value = (String) jt.getValueAt(row, 1);
                try {
                    Connection con = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/passwordstore", PasswordDatabase.MYSQL_LOGIN, PasswordDatabase.MYSQL_PASSWORD);

                    Statement stmt = con.createStatement();
                    String strDelete = "delete from " + LoginArea.currentUser + " where Username = '" + value + "'";
                    stmt.executeUpdate(strDelete); //string to delete unwanted entries from SQL table
                    DefaultTableModel tableModel1 = new DefaultTableModel(PasswordDatabase.getData(), column) {
                        //here tableModel is overwritten with new tableModel
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            //all cells uneditable
                            return false;
                        }
                    };
                    jt.setModel(tableModel1); //resetting table model. This shows the updated table
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }));

        addButton.addActionListener(new ActionListener() { //listener for adding new entries to sql table
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    Connection con = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/passwordstore", PasswordDatabase.MYSQL_LOGIN, PasswordDatabase.MYSQL_PASSWORD);

                    Statement stmt = con.createStatement();
                    String strInsert = "insert into " + LoginArea.currentUser + " " + "values ('" + theGUI.websiteInfo.getText()
                            + "', '" + theGUI.usernameInfo.getText() + "', '" + theGUI.passwordInfo.getText() + "')";
                    stmt.executeUpdate(strInsert);
                    theGUI.websiteInfo.setText("");
                    theGUI.usernameInfo.setText(""); //retrieves all entries, and sets the field to ""
                    theGUI.passwordInfo.setText("");
                    DefaultTableModel tableModel1 = new DefaultTableModel(PasswordDatabase.getData(), column) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            //all cells uneditable
                            return false;
                        }
                    };
                    jt.setModel(tableModel1); //resetting table model. This shows the updated table
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
        add(infoLabel);

        add(websiteLabel);
        add(websiteInfo);

        add(usernameLabel);
        add(usernameInfo);

        add(passwordLabel);
        add(passwordInfo);

        add(addButton);
        add(removeButton);

        pack(); //packing contents
        setLocationRelativeTo(null); //open centered
        setVisible(true);
    }
    public static void setActionMap() {
        jt.getActionMap().put("copy", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String cellValue = jt.getModel().getValueAt(jt.getSelectedRow(), jt.getSelectedColumn()).toString();
                StringSelection stringSelection = new StringSelection(cellValue);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
            }
        });
    }
    public static JTable getJt() {
        return jt;
    }
}