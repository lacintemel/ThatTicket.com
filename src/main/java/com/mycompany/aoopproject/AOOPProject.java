package com.mycompany.aoopproject;

import javax.swing.*;

import services.DatabaseService;
import view.LoginView;
import view.RegisterView;

import java.awt.*;

public class AOOPProject extends JFrame {
    private LoginView loginView;
    private RegisterView registerView;
    private JPanel currentPanel; // O an görüntülenen paneli tutacak

    public AOOPProject() {
        setTitle("Rezervasyon Sistemi");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        loginView = new LoginView(this);
        registerView = new RegisterView(this);

        // Başlangıçta login paneli göster
        currentPanel = loginView; // loginView'ı başlangıç paneli olarak ayarla
        add(currentPanel, BorderLayout.CENTER); // Başlangıç panelini frame'e ekle

        // LoginView'da Sign Up tıklanınca RegisterView'a geç
        loginView.getSignUpLabel().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showRegister();
            }
        });

        // RegisterView'da Sign In tıklanınca LoginView'a geç
        registerView.signInLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showLogin();
            }
        });
    }

    public void showRegister() {
        remove(currentPanel); // Mevcut paneli kaldır
        currentPanel = registerView; // registerView'ı yeni panel olarak ayarla
        add(currentPanel, BorderLayout.CENTER); // Yeni paneli frame'e ekle
        revalidate();
        repaint();
    }

    public void showLogin() {
        remove(currentPanel); // Mevcut paneli kaldır
        currentPanel = loginView; // loginView'ı yeni panel olarak ayarla
        add(currentPanel, BorderLayout.CENTER); // Yeni paneli frame'e ekle
        revalidate();
        repaint();
    }

    public void showMainView(JPanel mainPanel) {
        System.out.println("showMainView called with panel: " + mainPanel.getClass().getName()); // Debug message
        remove(currentPanel); // Mevcut paneli kaldır
        currentPanel = mainPanel; // mainPanel'i yeni panel olarak ayarla
        add(currentPanel, BorderLayout.CENTER); // Yeni paneli frame'e ekle
        revalidate();
        repaint();
        System.out.println("showMainView completed"); // Debug message
    }

    public static void main(String[] args) {
        System.out.println("\n=== THATTICKET.COM APPLICATION STARTING ===");
        System.out.println("Java version: " + System.getProperty("java.version"));
        System.out.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
        
        // Veritabanını başlat
        SwingUtilities.invokeLater(() -> {
            System.out.println("Initializing database...");
            DatabaseService.initialize();
            System.out.println("Creating main application window...");
            new AOOPProject().setVisible(true);
            System.out.println("✅ Application started successfully");
            System.out.println("=== APPLICATION STARTUP COMPLETED ===\n");
        });
    }
}