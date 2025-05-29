package com.mycompany.aoopproject;

import javax.swing.*;

import services.DatabaseService;
import view.LoginView;
import view.RegisterView;
import view.AdminVoyagePanel;
import services.Admin;
import models.Customer;

import java.awt.*;

public class AOOPProject extends JFrame {
    private LoginView loginView;
    private RegisterView registerView;

    public AOOPProject() {
        setTitle("Rezervasyon Sistemi");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        loginView = new LoginView();
        registerView = new RegisterView();

        // Başlangıçta login paneli göster
        showLogin();

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

    private void showLogin() {
        getContentPane().removeAll();
        getContentPane().add(loginView, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showRegister() {
        getContentPane().removeAll();
        getContentPane().add(registerView, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void showMainView(JPanel mainPanel) {
        getContentPane().removeAll();
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // ADMIN PANEL FONKSİYONU
    public void showAdminPanel(Admin admin) {
        // Giriş yapan admin'den Customer nesnesi oluştur
        Customer customer = new Customer(admin.getId(), admin.getName(), "", admin.getEmail(), admin.getPassword());
        customer.setUser_type("Admin");
        AdminVoyagePanel adminPanel = new AdminVoyagePanel(admin, customer, this);
        getContentPane().removeAll();
        getContentPane().add(adminPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        // Veritabanını başlat
        SwingUtilities.invokeLater(() -> {
            DatabaseService.initialize();
            new AOOPProject().setVisible(true);
        });
    }
}