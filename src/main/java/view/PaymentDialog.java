package view;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import models.Customer;
import services.DatabaseService;
import view.SeatSelectionPanel.SelectedSeat;

public class PaymentDialog extends JDialog {
    private List<SelectedSeat> selectedSeats;
    private Customer customer;
    private int voyageId;
    private SeatSelectionPanel seatSelectionPanel;
    private MainView mainView;
    private JPanel reservationsPanel;

    public PaymentDialog(List<SelectedSeat> selectedSeats, Customer customer, int voyageId, SeatSelectionPanel seatSelectionPanel, MainView mainView, JPanel reservationsPanel) {
        super((Frame) SwingUtilities.getWindowAncestor(seatSelectionPanel), "Ödeme", true);
        this.selectedSeats = selectedSeats;
        this.customer = customer;
        this.voyageId = voyageId;
        this.seatSelectionPanel = seatSelectionPanel;
        this.mainView = mainView;
        this.reservationsPanel = reservationsPanel;

        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 250));
        getContentPane().setBackground(new Color(245, 247, 250));

        // Card panel
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(245, 247, 250),
                    0, getHeight(), new Color(230, 232, 235)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 32, 32);
                g2.setColor(new Color(230,230,230));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 32, 32);
                g2.dispose();
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Title
        JLabel titleLabel = new JLabel("Payment Information", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 30, 30));
        cardPanel.add(titleLabel);
        cardPanel.add(Box.createVerticalStrut(20));

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        // Card number
        RoundedTextFieldModern cardNumberField = new RoundedTextFieldModern(16);
        cardNumberField.setPlaceholder("Card Number");
        formPanel.add(cardNumberField);
        formPanel.add(Box.createVerticalStrut(15));

        // Expiry date
        RoundedTextFieldModern expiryField = new RoundedTextFieldModern(5);
        expiryField.setPlaceholder("MM/YY");
        formPanel.add(expiryField);
        formPanel.add(Box.createVerticalStrut(15));

        // CVV
        RoundedTextFieldModern cvvField = new RoundedTextFieldModern(3);
        cvvField.setPlaceholder("CVV");
        formPanel.add(cvvField);
        formPanel.add(Box.createVerticalStrut(15));

        // Name
        RoundedTextFieldModern nameField = new RoundedTextFieldModern(20);
        nameField.setPlaceholder("Name on Card");
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(30));

        cardPanel.add(formPanel);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        // Cancel button
        JButton cancelButton = new JButton("Cancel") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(220, 53, 69));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBorderPainted(false);
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setPreferredSize(new Dimension(120, 38));
        cancelButton.addActionListener(e -> dispose());

        // Pay button
        JButton payButton = new JButton("Pay") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(46, 204, 113).darker(),
                    getWidth(), getHeight(), new Color(46, 204, 113)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        payButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        payButton.setForeground(Color.WHITE);
        payButton.setBorderPainted(false);
        payButton.setFocusPainted(false);
        payButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        payButton.setPreferredSize(new Dimension(120, 38));
        payButton.addActionListener(e -> {
            // Verify customer exists in database first
            try {
                if (customer == null || customer.getId() == null) {
                    JOptionPane.showMessageDialog(this, "Müşteri bilgisi bulunamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Verify customer exists in database
                if (!DatabaseService.verifyCustomerExists(Integer.parseInt(customer.getId()))) {
                    JOptionPane.showMessageDialog(this, "Müşteri kaydı bulunamadı! Lütfen tekrar giriş yapın.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Loading dialog'u göster
                CircularProgressIndicator loadingDialog = new CircularProgressIndicator((Frame) SwingUtilities.getWindowAncestor(this), "Ödeme işlemi gerçekleştiriliyor...");
                
                // Ödeme işlemini arka planda yap
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        try {
                            // Rezervasyonları veritabanına kaydet
                            for (SeatSelectionPanel.SelectedSeat seat : selectedSeats) {
                                DatabaseService.addReservation(
                                    Integer.parseInt(customer.getId()),
                                    voyageId,
                                    seat.seatNum,
                                    seat.gender
                                );
                            }
                            
                            // Başarılı rezervasyon bildirimi ekle
                            models.Voyage voyage = models.Voyage.getVoyageHashMap().get(voyageId);
                            // Call makeReservation for each selected seat through the Customer object
                            for (view.SeatSelectionPanel.SelectedSeat seat : selectedSeats) {
                                customer.makeReservation(voyage, seat.seatNum);
                            }
                            
                            return null;
                        } catch (Exception ex) {
                            throw ex;
                        }
                    }
                    
                    @Override
                    protected void done() {
                        try {
                            // Loading dialog'u kapat
                            loadingDialog.dispose();
                            
                            // Başarılı mesajı göster
                            JOptionPane.showMessageDialog(
                                PaymentDialog.this,
                                "Ödeme başarıyla tamamlandı!",
                                "Başarılı",
                                JOptionPane.INFORMATION_MESSAGE
                            );
                            
                            // Rezervasyonlar panelini güncelle
                            if (reservationsPanel != null) {
                                reservationsPanel.removeAll();
                                reservationsPanel.revalidate();
                                reservationsPanel.repaint();
                            }
                            
                            // Pencereyi kapat
                            Window window = SwingUtilities.getWindowAncestor(PaymentDialog.this);
                            if (window != null) {
                                window.dispose();
                            }
                            
                            // Ana görünümü göster
                            if (mainView != null) {
                                mainView.setVisible(true);
                            }
                        } catch (Exception ex) {
                            loadingDialog.dispose();
                            JOptionPane.showMessageDialog(
                                PaymentDialog.this,
                                "Ödeme işlemi sırasında bir hata oluştu: " + ex.getMessage(),
                                "Hata",
                                JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                };
                
                worker.execute();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(payButton);
        cardPanel.add(buttonPanel);

        add(cardPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }
}

// Modern yuvarlak köşeli text field
class RoundedTextFieldModern extends JTextField {
    private String placeholder;

    public RoundedTextFieldModern(int columns) {
        super(columns);
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setForeground(new Color(30, 30, 30));
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        setOpaque(false);
        setPreferredSize(new Dimension(300, 40));
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Background
        g2.setColor(new Color(240, 240, 240));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
        
        // Border
        if (hasFocus()) {
            g2.setColor(new Color(52, 152, 219));
        } else {
            g2.setColor(new Color(200, 200, 200));
        }
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
        
        // Placeholder
        if (getText().isEmpty() && placeholder != null && !hasFocus()) {
            g2.setColor(new Color(150, 150, 150));
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            FontMetrics fm = g2.getFontMetrics();
            int x = 15;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, x, y);
        }
        
        g2.dispose();
        super.paintComponent(g);
    }
}