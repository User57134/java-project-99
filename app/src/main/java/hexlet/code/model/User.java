package hexlet.code.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private String firstName;

    private String lastName;

    @NotBlank @Email(message = "Некорректный формат для электронной почты") @Column(name = "email", unique = true, nullable = false)
    private String email;

    @NotBlank @Column(nullable = false)
    private String password;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Setter(AccessLevel.NONE)
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof User other) {
            Long otherId = other.getId();

            if (this.id != null && otherId != null) {
                return this.id.equals(otherId);
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        // Константный hashCode гарантирует, что при обновлении объект не исчезнет из коллекции
        return getClass().hashCode();
    }
}
