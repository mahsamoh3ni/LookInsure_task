package insurance.service.dto;

import insurance.service.dto.enumeration.CoverageTypeDto;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteResponseDto {
    private CoverageTypeDto coverageType;
    private BigDecimal price;
    private String providerName;
}

