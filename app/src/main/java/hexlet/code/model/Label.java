package hexlet.code.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "labels")
@NoArgsConstructor
@Getter
@Setter
public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(min = 3, max = 1000, message = "Длинна названия метки должна быть от 3 до 1000 символов") @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "labels", fetch = FetchType.LAZY)
    private Set<Task> tasks = new HashSet<>();

    @CreatedDate
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null) {
            return false;
        }

        Class<?> oEffectiveClass =
                o instanceof HibernateProxy
                        ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                        : o.getClass();
        Class<?> thisEffectiveClass =
                this instanceof HibernateProxy
                        ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                        : this.getClass();

        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }

        Label label = (Label) o;
        return getId() != null && Objects.equals(getId(), label.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this)
                        .getHibernateLazyInitializer()
                        .getPersistentClass()
                        .hashCode()
                : getClass().hashCode();
    }
}
