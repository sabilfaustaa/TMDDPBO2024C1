package views;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class MainMenu extends JFrame {
    private JPanel mainPanel;
    private JTextField inUsername;
    private JButton btnStart;
    private JButton btnExit;
    private JTable tableScore;
    private JLabel lblTitle;
    private Image backgroundImage;

    public MainMenu() {
        // Set up frame properties
        setTitle("TMD DPBO 2024");
        setSize(700, 700); // Perpanjang canvas untuk mengakomodasi semua elemen
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                    g.setColor(new Color(0, 0, 0, 150)); // Overlay hitam dengan opacity
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        setBackgroundImage("../assets/background.jpg"); // Set path ke background image
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding 3%
        setContentPane(mainPanel);

        // Create title label
        lblTitle = new JLabel("UP DOWN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(new Color(255, 215, 0)); // Warna kuning sesuai modal game over
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Create center panel for user input and table
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false); // Set panel transparent

        // Add username input field
        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        usernamePanel.setOpaque(false); // Set panel transparent
        usernamePanel.add(createLabel("Username:"));
        inUsername = new JTextField(15);
        usernamePanel.add(inUsername);

        centerPanel.add(usernamePanel);

        // Create table to display scores
        tableScore = new JTable();
        customizeTable(tableScore);
        JScrollPane scrollPane = new JScrollPane(tableScore);
        scrollPane.setPreferredSize(new Dimension(800, 300)); // Set size for the scroll pane
        centerPanel.add(scrollPane);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Add buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false); // Set panel transparent
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 20)); // Set layout to FlowLayout
        btnStart = createButton("Play");
        btnExit = createButton("Quit");
        buttonsPanel.add(btnStart);
        buttonsPanel.add(btnExit);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
    }

    // Helper method to create labels
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 18));
        label.setForeground(new Color(255, 255, 255)); // Warna putih
        return label;
    }

    // Helper method to create buttons
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(255, 215, 0)); // Warna kuning sesuai modal game over
        button.setForeground(new Color(60, 179, 113)); // Warna hijau sesuai modal game over
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 40)); // Set size for the buttons
        return button;
    }

    // Customize table appearance
    private void customizeTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 18));
        table.setRowHeight(30);
        table.setBackground(new Color(60, 179, 113)); // Warna hijau
        table.setForeground(new Color(255, 255, 255)); // Warna putih
        table.setShowGrid(false); // Menghilangkan grid

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 18));
        header.setBackground(new Color(255, 215, 0)); // Warna kuning
        header.setForeground(new Color(60, 179, 113)); // Warna hijau
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    // Method to set background image
    public void setBackgroundImage(String path) {
        backgroundImage = new ImageIcon(getClass().getResource(path)).getImage();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JTextField getInUsername() {
        return inUsername;
    }

    public JButton getBtnStart() {
        return btnStart;
    }

    public JButton getBtnExit() {
        return btnExit;
    }

    public JTable getTableScore() {
        return tableScore;
    }

    public JLabel getLblTitle() {
        return lblTitle;
    }
}
