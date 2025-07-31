package project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@Entity
@Table(name = "payments")
@Where(clause = "is_deleted = false")
@SQLDelete(sql = "UPDATE payments SET is_deleted = true WHERE id = ?")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;
    @OneToOne
    @MapsId
    @JoinColumn(name = "rental_id", nullable = false)
    private Rental rental;
    @URL
    @Column(nullable = false, unique = true)
    private String sessionUrl;
    @Column(nullable = false, unique = true)
    private String sessionId;
    @Column(nullable = false)
    private BigDecimal amount;

    public enum Status {
        PENDING,
        PAID
    }

    public enum Type {
        PAYMENT,
        FINE
    }
}
