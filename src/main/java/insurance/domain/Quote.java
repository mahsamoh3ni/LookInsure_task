package insurance.domain;


import insurance.domain.enumaration.CoverageType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "quote", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"coverage_type", "provider_id", "deleted_at"}, name = "UK_quote_coverage_type_provider_deleted_at")
})
public class Quote extends BaseEntity {
    @Column(name = "coverage_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CoverageType coverageType;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;
}

