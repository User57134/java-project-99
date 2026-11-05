package hexlet.code.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "task_statuses")
@NoArgsConstructor
@Getter
@Setter
public class TaskStatus implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(min = 1, message = "Название статуса должно содержать как минимум 1 символ") @Column(unique = true, nullable = false)
    private String name;

    @NotBlank @Size(min = 1, message = "Слаг статуса должен содержать как минимум 1 символ") @Column(unique = true, nullable = false)
    private String slug;

    @CreatedDate
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof TaskStatus other) {
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
