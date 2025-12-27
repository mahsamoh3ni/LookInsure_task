package insurance.service.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteResponseDto {
    private String coverageType;
    private BigDecimal price;
    private String providerName;
}

