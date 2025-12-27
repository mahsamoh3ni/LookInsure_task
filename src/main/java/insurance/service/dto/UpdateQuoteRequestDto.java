package insurance.service.dto;

import insurance.service.dto.enumeration.CoverageTypeDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateQuoteRequestDto {
    @NotNull
    private Long quoteId;

    private BigDecimal price;
    private CoverageTypeDto coverageTypeDto;
}
