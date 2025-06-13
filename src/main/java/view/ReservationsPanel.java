package view;

import javax.swing.*;
import java.awt.*;
import models.Customer;
import models.Voyage;
import services.DatabaseService;
import java.util.List;

public class ReservationsPanel extends JPanel {
    private final Customer customer;
    private final String adminEmail;
    private final boolean isAdmin;
    private JPanel reservationsContainer;

    public ReservationsPanel(Customer customer, String adminEmail, boolean isAdmin) {
        this.customer = customer;
        this.adminEmail = adminEmail;
        this.isAdmin = isAdmin;
        setLayout(new BorderLayout());
        initializeComponents();
        loadReservations();
    }

    private void initializeComponents() {
        // Create a scrollable container for reservations
        reservationsContainer = new JPanel();
        reservationsContainer.setLayout(new BoxLayout(reservationsContainer, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(reservationsContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshReservations() {
        reservationsContainer.removeAll();
        loadReservations();
        reservationsContainer.revalidate();
        reservationsContainer.repaint();
    }

    private void loadReservations() {
        try {
            String userId = isAdmin ? 
                DatabaseService.getUserByEmail(adminEmail).getId() : 
                customer.getId();
            
            List<Object[]> reservations = DatabaseService.getReservationsByUserId(Integer.parseInt(userId));
            
            if (reservations.isEmpty()) {
                JLabel noReservationsLabel = new JLabel("Henüz rezervasyonunuz bulunmamaktadır.");
                noReservationsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                reservationsContainer.add(noReservationsLabel);
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
                            null,
                            created_at
                        );
                        reservationsContainer.add(card);
                        reservationsContainer.add(Box.createVerticalStrut(10));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JLabel errorLabel = new JLabel("Rezervasyonlar yüklenirken bir hata oluştu.");
            errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            reservationsContainer.add(errorLabel);
        }
    }
} 