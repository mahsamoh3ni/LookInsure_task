package insurance.service.dto;

import insurance.service.dto.enumeration.CoverageTypeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetQuoteListRequestDto {
    private List<CoverageTypeDto> coverageTypeDto;
}
