package view;

import javax.swing.*;
import java.awt.*;
import models.Customer;
import models.Voyage;
import services.DatabaseService;
import java.util.List;
import java.util.ArrayList;

public class ReservationsPanel extends JPanel {
    private final Customer customer;
    private final String adminEmail;
    private final boolean isAdmin;
    private final MainView mainView;

    private JPanel cardPanel; // Boş ve içerik panellerini tutacak ana panel
    private JPanel emptyPanel;
    private JPanel contentPanel; // Rezervasyon kartlarını tutacak ana panel
    private JPanel reservationsContainer; // Rezervasyon kartlarını dikeyde sıralayacak panel

    public ReservationsPanel(Customer customer, String adminEmail, boolean isAdmin, MainView mainView) {
        this.customer = customer;
        this.adminEmail = adminEmail;
        this.isAdmin = isAdmin;
        this.mainView = mainView;
        setLayout(new BorderLayout()); // Ana panel için BorderLayout kullan
        initializeComponents();
        loadReservations();
    }

    private void initializeComponents() {
        // Create card panel with CardLayout
        cardPanel = new JPanel(new CardLayout());
        cardPanel.setOpaque(false);
        
        // Create empty panel
        emptyPanel = new JPanel(new BorderLayout());
        emptyPanel.setOpaque(false);
        JLabel emptyLabel = new JLabel("Rezervasyonunuz bulunmamaktadır.", SwingConstants.CENTER);
        emptyLabel.setFont(new Font("Arial", Font.BOLD, 24));
        emptyLabel.setForeground(new Color(100, 100, 100));
        emptyPanel.add(emptyLabel, BorderLayout.CENTER);
        
        // Create content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        
        // Create a scrollable container for reservations
        reservationsContainer = new JPanel();
        reservationsContainer.setLayout(new BoxLayout(reservationsContainer, BoxLayout.Y_AXIS));
        reservationsContainer.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(reservationsContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add panels to card panel
        cardPanel.add(emptyPanel, "empty");
        cardPanel.add(contentPanel, "content");
        
        // Add card panel to main panel
        add(cardPanel, BorderLayout.CENTER);
    }

    public void refreshReservations() {
        loadReservations();
    }

    private void loadReservations() {
        reservationsContainer.removeAll(); // Mevcut kartları temizle

        List<Object[]> reservations = new ArrayList<>();
        if (isAdmin) {
            List<DatabaseService.ReservationInfo> adminReservations = DatabaseService.getAllReservations();
            for (DatabaseService.ReservationInfo res : adminReservations) {
                Object[] reservation = new Object[]{
                    res.voyageId,
                    res.seatNumber,
                    res.gender,
                    res.reservationDate
                };
                reservations.add(reservation);
            }
        } else {
            List<DatabaseService.ReservationInfo> userReservations = DatabaseService.getReservationsForUser(Integer.parseInt(customer.getId()));
            for (DatabaseService.ReservationInfo res : userReservations) {
                Object[] reservation = new Object[]{
                    res.voyageId,
                    res.seatNumber,
                    res.gender,
                    res.reservationDate
                };
                reservations.add(reservation);
            }
        }

        CardLayout cl = (CardLayout)(cardPanel.getLayout());
        if (reservations.isEmpty()) {
            cl.show(cardPanel, "empty"); // Boş paneli göster
        } else {
            for (Object[] reservation : reservations) {
                int voyageId = (int) reservation[0];
                int seatNumber = (int) reservation[1];
                String gender = (String) reservation[2];
                String created_at = (String) reservation[3];
                
                Voyage voyage = DatabaseService.getVoyageById(voyageId);
                if (voyage != null) {
                    ReservationCardPanel card = new ReservationCardPanel(
                        customer,
                        voyage,
                        seatNumber,
                        gender,
                        voyage.getType(),
                        this,
                        adminEmail,
                        mainView,
                        created_at
                    );
                    reservationsContainer.add(card);
                    reservationsContainer.add(Box.createVerticalStrut(10));
                }
            }
            reservationsContainer.add(Box.createVerticalGlue()); // Kartları üste it
            cl.show(cardPanel, "content"); // İçerik panelini göster
        }

        // cardPanel'i doğrula ve yeniden çiz
        cardPanel.revalidate();
        cardPanel.repaint();
    }
} 