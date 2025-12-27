package insurance.service.dto.mapper;

import insurance.domain.Quote;
import insurance.domain.enumaration.AggregationType;
import insurance.domain.enumaration.CoverageType;
import insurance.service.dto.QuoteResponseDto;
import insurance.service.dto.enumeration.AggregationTypeDto;
import insurance.service.dto.enumeration.CoverageTypeDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface DtoMapper {

    CoverageType getCoverageType(CoverageTypeDto coverageType);
    List<CoverageType> getCoverageTypeList(List<CoverageTypeDto> coverageTypes);

    QuoteResponseDto getQuoteDto(Quote quote);
    default QuoteResponseDto getQuoteResponseDto(Quote quote){
        var quoteDto = getQuoteDto(quote);
        quoteDto.setProviderName(quote.getProvider().getName());
        return quoteDto;
    }

    default AggregationType getAggregationType(AggregationTypeDto aggregationTypeDto) {
        if (aggregationTypeDto == null) {
            return null;
        }

        return switch (aggregationTypeDto) {
            case MOST_EXPENSIVE -> AggregationType.HIGHEST_PRICE;
            case CHEAPEST -> AggregationType.LOWEST_PRICE;
        };
    }
}
