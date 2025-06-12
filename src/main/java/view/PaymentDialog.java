package view;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import models.Customer;
import models.Voyage;
import services.DatabaseService;
import javax.swing.border.AbstractBorder;
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
        JLabel titleLabel = new JLabel("Ödeme Bilgileri", SwingConstants.CENTER);
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
        cardNumberField.setPlaceholder("Kart Numarası");
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
        nameField.setPlaceholder("Kart Üzerindeki İsim");
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(30));

        cardPanel.add(formPanel);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        // Cancel button
        JButton cancelButton = new JButton("İptal") {
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
        JButton payButton = new JButton("Öde") {
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
            // Ödeme işlemi başarılı olduğunda
            for (SelectedSeat seat : selectedSeats) {
                // Rezervasyonu kaydet
                DatabaseService.addReservation(
                    Integer.parseInt(customer.getId()),
                    voyageId,
                    seat.seatNum,
                    seat.gender
                );
                
                // Bildirimi ekle
                try {
                    String notification = "🎫 [Rezervasyon] " + 
                        Voyage.getVoyageHashMap().get(voyageId).getOrigin() + " - " + 
                        Voyage.getVoyageHashMap().get(voyageId).getDestination() + 
                        " seferi için " + seat.seatNum + " numaralı koltuk ayrıldı. Ödeme başarıyla tamamlandı.";
                    
                    System.out.println("\n=== Bildirim Ekleme Başladı ===");
                    System.out.println("User ID: " + customer.getId());
                    System.out.println("Bildirim mesajı: " + notification);
                    
                    DatabaseService.addNotification(Integer.parseInt(customer.getId()), notification);
                    System.out.println("✅ Bildirim başarıyla eklendi!");
                } catch (Exception ex) {
                    System.err.println("❌ Bildirim eklenirken hata oluştu: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            
            // Rezervasyonlar panelini güncelle
            if (mainView != null && reservationsPanel != null) {
                mainView.updateReservationsPanel(customer, reservationsPanel);
            }
            
            // Dialog'u kapat
            dispose();
            
            // Koltuk seçim penceresini kapat
            Window window = SwingUtilities.getWindowAncestor(seatSelectionPanel);
            if (window != null) window.dispose();
            
            // Başarılı mesajı göster
            JOptionPane.showMessageDialog(this, "Rezervasyonunuz başarıyla tamamlandı!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
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

// Oval border
class RoundedBorder extends AbstractBorder {
    private final int radius;
    public RoundedBorder(int radius) { this.radius = radius; }
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(220,220,220));
        g2.drawRoundRect(x, y, width-1, height-1, radius, radius);
        g2.dispose();
    }
}

// Oval buton
class RoundedButton extends JButton {
    private final Color bgColor;
    public RoundedButton(String text, Color bgColor) {
        super(text);
        this.bgColor = bgColor;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 16));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(new RoundedBorder(20));
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        super.paintComponent(g2);
        g2.dispose();
    }
} 