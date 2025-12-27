package insurance.service;


import insurance.config.CacheNames;
import insurance.repository.QuoteRepository;
import insurance.service.dto.AggregationRequestDto;
import insurance.service.dto.AggregationResultDto;
import insurance.service.dto.mapper.DtoMapper;
import insurance.service.strategy.AggregationStrategyResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AggregationService {
    private final AggregationStrategyResolver aggregationStrategyResolver;
    private final DtoMapper dtoMapper;
    private final QuoteRepository quoteRepository;

    @Cacheable(cacheNames = CacheNames.AGGREGATED_DATA, key = "{#requestDto.aggregationType,#requestDto.coverageTypeDto}")
    public AggregationResultDto getAggregatedData(AggregationRequestDto requestDto) {
        var coverageType = dtoMapper.getCoverageType(requestDto.getCoverageTypeDto());

        var quotes = quoteRepository.findAllByCoverageType(List.of(coverageType));
        if (CollectionUtils.isEmpty(quotes)) {
            log.warn("No quotes found for coverage type: {}", coverageType);
            return AggregationResultDto.builder()
                    .sortedQuotes(List.of())
                    .best(null)
                    .build();
        }

        var aggregationType = dtoMapper.getAggregationType(requestDto.getAggregationType());
        var sortedData = aggregationStrategyResolver
                .resolve(aggregationType)
                .aggregate(quotes);

        var sortedDataDto = sortedData.stream().map(dtoMapper::getQuoteResponseDto).toList();

        return AggregationResultDto.builder()
                .sortedQuotes(sortedDataDto)
                .best(sortedDataDto.getFirst())
                .build();
    }
}
