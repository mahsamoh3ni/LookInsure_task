package insurance.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "provider")
public class Provider extends BaseEntity {
    @Column(name = "name", nullable = false, unique = true)
    private String name;
}

