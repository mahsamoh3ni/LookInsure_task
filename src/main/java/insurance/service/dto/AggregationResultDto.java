package insurance.service.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AggregationResultDto {
    private QuoteResponseDto best;
    private List<QuoteResponseDto> sortedQuotes;
}

