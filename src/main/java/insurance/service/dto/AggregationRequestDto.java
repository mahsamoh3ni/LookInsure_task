package insurance.service.dto;

import insurance.service.dto.enumeration.AggregationTypeDto;
import insurance.service.dto.enumeration.CoverageTypeDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AggregationRequestDto {
    @NotNull
    private AggregationTypeDto aggregationType;

    @NotNull
    private CoverageTypeDto coverageTypeDto;
}
