package insurance.service.dto;

import insurance.service.dto.enumeration.CoverageTypeDto;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateQuoteRequestDto {
    @NotNull
    private Long providerId;
    @NotNull
    private CoverageTypeDto coverageTypeDto;
    @NotNull
    private BigDecimal price;
}
