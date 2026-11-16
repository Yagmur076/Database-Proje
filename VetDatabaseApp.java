package Vet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class VetDatabaseApp extends JFrame {

// DB_URL kısmında VET yerine MySQL üzerinden kullanılan host adı girilecek

    private static final String DB_URL = "jdbc:mysql://localhost:3306/VET?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=utf8";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "şifre"; // Kendi şifrenizi girin

    private Connection connection;

    // Form Components
    private JTextField txtOwnerName, txtOwnerPhone, txtPetName, txtSpecies, txtBreed, txtBirthDate;
    private JComboBox<ComboItem> cmbVet;
    private JComboBox<String> cmbAppointmentDate;
    private JComboBox<String> cmbAppointmentTime;
    private JComboBox<String> cmbExamType;
    
    private JTable resultTable;
    private DefaultTableModel tableModel;

    // Her veteriner için özel tarihler ve saatler havuzu
    private final Map<String, String[]> vetDatesMap = new HashMap<>();
    private final Map<String, String[]> vetTimesMap = new HashMap<>();

    public VetDatabaseApp() {
        setTitle("Veterinary Database Management System - Smart Scheduling");
        setSize(1150, 820);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initDatabaseConnection();
        initVetSchedules();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Create Appointment & Treatment", createCustomerPanel());
        tabbedPane.add("Database Table Viewer", createTableViewerPanel());

        add(tabbedPane);
        loadVetsToComboBox();
    }

    private void initDatabaseConnection() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                    "Database connection error:\n" + e.getMessage() + 
                    "\n\nLütfen DB_USER ve DB_PASSWORD bilgilerini kontrol edin.", 
                    "Connection Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initVetSchedules() {
        vetDatesMap.put("Dr. Can Öztürk", new String[]{"2026-06-01", "2026-06-03", "2026-06-05"});
        vetTimesMap.put("Dr. Can Öztürk", new String[]{"09:00:00", "10:30:00", "14:00:00"});

        vetDatesMap.put("Dr. Ayşe Demir", new String[]{"2026-06-02", "2026-06-04", "2026-06-06"});
        vetTimesMap.put("Dr. Ayşe Demir", new String[]{"11:00:00", "13:30:00", "16:00:00"});

        vetDatesMap.put("Dr. Mehmet Kaya", new String[]{"2026-06-01", "2026-06-02", "2026-06-07"});
        vetTimesMap.put("Dr. Mehmet Kaya", new String[]{"08:30:00", "12:00:00", "15:30:00"});

        vetDatesMap.put("Dr. Zeynep Çelik", new String[]{"2026-06-03", "2026-06-05", "2026-06-08"});
        vetTimesMap.put("Dr. Zeynep Çelik", new String[]{"09:30:00", "14:30:00", "17:00:00"});
    }

    private void loadVetsToComboBox() {
        if (connection == null) return;
        cmbVet.removeAllItems();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT vet_id, name_surname FROM Vet")) {
            while (rs.next()) {
                cmbVet.addItem(new ComboItem(rs.getInt("vet_id"), rs.getString("name_surname")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateVetScheduleUI() {
        ComboItem selectedVet = (ComboItem) cmbVet.getSelectedItem();
        if (selectedVet == null) return;

        String vetName = selectedVet.getName();
        
        cmbAppointmentDate.removeAllItems();
        String[] dates = vetDatesMap.getOrDefault(vetName, new String[]{"2026-06-10"});
        for (String date : dates) {
            cmbAppointmentDate.addItem(date);
        }

        cmbAppointmentTime.removeAllItems();
        String[] times = vetTimesMap.getOrDefault(vetName, new String[]{"10:00:00"});
        for (String time : times) {
            cmbAppointmentTime.addItem(time);
        }
    }

    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formPanel = new JPanel(new GridLayout(4, 1, 10, 10));

        // 1. Owner & Pet Information Panel
        JPanel ownerPetPanel = new JPanel(new GridBagLayout());
        ownerPetPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), 
                "1. Owner and Pet Information", 
                javax.swing.border.TitledBorder.LEFT, 
                javax.swing.border.TitledBorder.TOP, 
                new Font("Arial", Font.BOLD, 13)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        txtOwnerName = new JTextField(15);
        txtOwnerPhone = new JTextField(12);
        txtPetName = new JTextField(12);
        txtSpecies = new JTextField(12);
        txtBreed = new JTextField(12);
        txtBirthDate = new JTextField("2023-01-01", 12);

        Font labelFont = new Font("Arial", Font.BOLD, 12);
        Font fieldFont = new Font("Arial", Font.PLAIN, 12);

        Component[][] components = {
            {createLabel("Owner Full Name:", labelFont), txtOwnerName, createLabel("Pet Name:", labelFont), txtPetName},
            {createLabel("Phone Number:", labelFont), txtOwnerPhone, createLabel("Species:", labelFont), txtSpecies},
            {createLabel("Birth Date (YYYY-MM-DD):", labelFont), txtBirthDate, createLabel("Breed:", labelFont), txtBreed}
        };

        for (int row = 0; row < components.length; row++) {
            for (int col = 0; col < components[row].length; col++) {
                gbc.gridx = col;
                gbc.gridy = row;
                if (components[row][col] instanceof JTextField) {
                    ((JTextField) components[row][col]).setFont(fieldFont);
                    ((JTextField) components[row][col]).setPreferredSize(new Dimension(160, 26));
                }
                ownerPetPanel.add(components[row][col], gbc);
            }
        }

        // 2. Appointment Details Panel
        JPanel apptPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        apptPanel.setBorder(BorderFactory.createTitledBorder("2. Appointment Details & Vet Schedule"));
        
        cmbVet = new JComboBox<>();
        cmbVet.setPreferredSize(new Dimension(160, 26));
        
        cmbAppointmentDate = new JComboBox<>();
        cmbAppointmentDate.setPreferredSize(new Dimension(130, 26));

        cmbAppointmentTime = new JComboBox<>();
        cmbAppointmentTime.setPreferredSize(new Dimension(100, 26));

        apptPanel.add(new JLabel("Veterinarian:")); apptPanel.add(cmbVet);
        apptPanel.add(new JLabel("Date:")); apptPanel.add(cmbAppointmentDate);
        apptPanel.add(new JLabel("Time:")); apptPanel.add(cmbAppointmentTime);

        cmbVet.addActionListener(e -> updateVetScheduleUI());

        // 3. Treatment Selection Panel
        JPanel treatmentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        treatmentPanel.setBorder(BorderFactory.createTitledBorder("3. Treatment Type Selection"));
        
        String[] examTypes = {
            "General Checkup", 
            "Vaccination", 
            "Parasite Treatment", 
            "Surgical Operation", 
            "Dental Care"
        };
        cmbExamType = new JComboBox<>(examTypes);
        cmbExamType.setPreferredSize(new Dimension(160, 26));

        treatmentPanel.add(new JLabel("Select Exam Type:")); treatmentPanel.add(cmbExamType);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnSubmit = new JButton("Save Information and Show Treatment Output");
        btnSubmit.setBackground(new Color(27, 54, 93));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFont(new Font("Arial", Font.BOLD, 14));
        btnSubmit.setPreferredSize(new Dimension(380, 35));
        buttonPanel.add(btnSubmit);

        formPanel.add(ownerPetPanel);
        formPanel.add(apptPanel);
        formPanel.add(treatmentPanel);
        formPanel.add(buttonPanel);

        panel.add(formPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        resultTable = new JTable(tableModel);
        panel.add(new JScrollPane(resultTable), BorderLayout.CENTER);

        btnSubmit.addActionListener(e -> {
            if (connection == null) {
                JOptionPane.showMessageDialog(this, "Database connection not established!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                ComboItem selectedVet = (ComboItem) cmbVet.getSelectedItem();
                if (selectedVet == null) {
                    JOptionPane.showMessageDialog(this, "Please select a registered veterinarian!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int selectedVetId = selectedVet.getId();
                String selectedDate = (String) cmbAppointmentDate.getSelectedItem();
                String selectedTime = (String) cmbAppointmentTime.getSelectedItem();

                // ÇAKIŞMA KONTROLÜ: Aynı doktor, aynı tarih ve aynı saatte başka randevu var mı?
                String checkSql = "SELECT COUNT(*) FROM Appointments WHERE vet_id = ? AND appointment_date = ? AND appointment_time = ?";
                try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                    checkStmt.setInt(1, selectedVetId);
                    checkStmt.setDate(2, java.sql.Date.valueOf(selectedDate));
                    checkStmt.setTime(3, java.sql.Time.valueOf(selectedTime));
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            JOptionPane.showMessageDialog(this, 
                            		"An appointment already exists for this veterinarian at the selected date and time!\nPlease choose another date or time.", 
                                    "Appointment Conflict", 
                                    JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                }

                connection.setAutoCommit(false);

                int nextPetId = getNextId("Pets", "pet_id");
                int nextApptId = getNextId("Appointments", "appointment_id");
                int nextTreatmentId = getNextId("Treatments", "treatment_id");

                // 1. Pets Tablosuna Kayıt
                String sqlPet = "INSERT INTO Pets (pet_id, owner_name, owner_phone, pet_name, species, gender, is_neutured, breed, birth_date) VALUES (?, ?, ?, ?, ?, 1, 1, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlPet)) {
                    pstmt.setInt(1, nextPetId);
                    pstmt.setString(2, txtOwnerName.getText().trim());
                    pstmt.setString(3, txtOwnerPhone.getText().trim());
                    pstmt.setString(4, txtPetName.getText().trim());
                    pstmt.setString(5, txtSpecies.getText().trim());
                    pstmt.setString(6, txtBreed.getText().trim());
                    pstmt.setDate(7, java.sql.Date.valueOf(txtBirthDate.getText().trim()));
                    pstmt.executeUpdate();
                }

                // 2. Appointments Tablosuna Kayıt
                String selectedExam = (String) cmbExamType.getSelectedItem();
                String sqlAppt = "INSERT INTO Appointments (appointment_id, pet_id, vet_id, appointment_date, appointment_time, reason) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlAppt)) {
                    pstmt.setInt(1, nextApptId);
                    pstmt.setInt(2, nextPetId);
                    pstmt.setInt(3, selectedVetId);
                    pstmt.setDate(4, java.sql.Date.valueOf(selectedDate));
                    pstmt.setTime(5, java.sql.Time.valueOf(selectedTime));
                    pstmt.setString(6, selectedExam);
                    pstmt.executeUpdate();
                }

                // 3. Treatments Tablosuna Kayıt
                String summaryText = generateSummary(selectedExam);
                String sqlTreatment = "INSERT INTO Treatments (treatment_id, appointment_id, exam_type, summary) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlTreatment)) {
                    pstmt.setInt(1, nextTreatmentId);
                    pstmt.setInt(2, nextApptId);
                    pstmt.setString(3, selectedExam);
                    pstmt.setString(4, summaryText);
                    pstmt.executeUpdate();
                }

                connection.commit();
                JOptionPane.showMessageDialog(this, "Record successfully created, loading treatment output!");

                loadTreatmentResult(nextTreatmentId);

            } catch (Exception ex) {
                try { connection.rollback(); } catch (SQLException ignored) {}
                JOptionPane.showMessageDialog(this, "Error Occurred:\n" + ex.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try { connection.setAutoCommit(true); } catch (SQLException ignored) {}
            }
        });

        return panel;
    }

    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }

    private int getNextId(String tableName, String idColumnName) {
        int maxId = 0;
        String query = "SELECT MAX(" + idColumnName + ") FROM " + tableName;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                maxId = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maxId + 1;
    }

    private String generateSummary(String examType) {
        switch (examType) {
            case "General Checkup":
                return "Physical examination performed, general health status is stable.";
            case "Vaccination":
                return "Scheduled annual vaccinations administered, no side effects observed.";
            case "Parasite Treatment":
                return "Internal and external parasite treatments successfully completed.";
            case "Surgical Operation":
                return "Surgery successfully completed, patient is under observation.";
            case "Dental Care":
                return "Dental scaling and oral hygiene control performed.";
            default:
                return "Examination completed, necessary recommendations provided.";
        }
    }

    private void loadTreatmentResult(int treatmentId) {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        String sql = "SELECT t.treatment_id, p.owner_name, p.pet_name, p.species, " +
                     "v.name_surname AS vet_name, a.appointment_date, a.appointment_time, t.exam_type, t.summary " +
                     "FROM Treatments t " +
                     "JOIN Appointments a ON t.appointment_id = a.appointment_id " +
                     "JOIN Pets p ON a.pet_id = p.pet_id " +
                     "JOIN Vet v ON a.vet_id = v.vet_id " +
                     "WHERE t.treatment_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, treatmentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                int cols = md.getColumnCount();

                Vector<String> colNames = new Vector<>();
                for (int i = 1; i <= cols; i++) colNames.add(md.getColumnLabel(i));
                tableModel.setColumnIdentifiers(colNames);

                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    for (int i = 1; i <= cols; i++) row.add(rs.getObject(i));
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading treatment: " + e.getMessage());
        }
    }

    private JPanel createTableViewerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        String[] tables = {"Pets", "Vet", "Appointments", "Treatments"};
        
        DefaultTableModel viewerModel = new DefaultTableModel();
        JTable viewerTable = new JTable(viewerModel);

        for (String tableName : tables) {
            JButton btn = new JButton(tableName);
            btn.addActionListener(e -> {
                viewerModel.setRowCount(0);
                viewerModel.setColumnCount(0);
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {
                    ResultSetMetaData md = rs.getMetaData();
                    int cols = md.getColumnCount();
                    Vector<String> colNames = new Vector<>();
                    for (int i = 1; i <= cols; i++) colNames.add(md.getColumnLabel(i));
                    viewerModel.setColumnIdentifiers(colNames);

                    while (rs.next()) {
                        Vector<Object> row = new Vector<>();
                        for (int i = 1; i <= cols; i++) row.add(rs.getObject(i));
                        viewerModel.addRow(row);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            });
            topButtonPanel.add(btn);
        }

        panel.add(topButtonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(viewerTable), BorderLayout.CENTER);
        return panel;
    }

    private static class ComboItem {
        private final int id;
        private final String name;

        public ComboItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VetDatabaseApp().setVisible(true));
    }
}
