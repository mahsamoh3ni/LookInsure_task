package insurance.service;

import insurance.domain.Provider;
import insurance.domain.Quote;
import insurance.domain.enumaration.CoverageType;
import insurance.repository.QuoteRepository;
import insurance.service.dto.AggregationRequestDto;
import insurance.service.dto.AggregationResultDto;
import insurance.service.dto.enumeration.AggregationTypeDto;
import insurance.service.dto.enumeration.CoverageTypeDto;
import insurance.service.dto.mapper.DtoMapperImpl;
import insurance.service.strategy.AggregationStrategyResolver;
import insurance.service.strategy.HighestPriceAggregationStrategy;
import insurance.service.strategy.LowestPriceAggregationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;

@SpringBootTest(classes = {
        AggregationService.class,
        AggregationStrategyResolver.class,
        LowestPriceAggregationStrategy.class,
        HighestPriceAggregationStrategy.class,
        DtoMapperImpl.class
})
class AggregationServiceUTest {
    @Autowired
    private AggregationService aggregationService;

    @MockitoBean
    private QuoteRepository quoteRepository;

    private Quote cheapQuote;
    private Quote expensiveQuote;
    private AggregationRequestDto request;

    @BeforeEach
    void setUp() {
        setUpData();

        doReturn(List.of(expensiveQuote, cheapQuote)).when(quoteRepository).findAllByCoverageType(anyList());

    }

    @Test
    void getAggregatedData_noQuotes_returnsEmptyResult() {
        // arrange
        doReturn(List.of()).when(quoteRepository).findAllByCoverageType(anyList());

        // act
        AggregationResultDto result = aggregationService.getAggregatedData(request);

        // assert
        assertNotNull(result);
        assertTrue(result.getSortedQuotes().isEmpty());
        assertNull(result.getBest());
    }

    @Test
    void getAggregatedData_lowestPriceAggregation_happyFlow() {
        // act
        AggregationResultDto result = aggregationService.getAggregatedData(request);

        // assert
        assertEquals(2, result.getSortedQuotes().size());

        var coverageTypeDto = result.getBest().getCoverageType();
        assertEquals(cheapQuote.getCoverageType(), CoverageType.valueOf(coverageTypeDto.name()));
        assertEquals(
                cheapQuote.getPrice(),
                result.getSortedQuotes().getFirst().getPrice()
        );
    }

    @Test
    void getAggregatedData_highestPriceAggregation_happyFlow() {
        // arrange
        request = AggregationRequestDto.builder()
                .coverageTypeDto(CoverageTypeDto.CAR)
                .aggregationType(AggregationTypeDto.MOST_EXPENSIVE)
                .build();

        // act
        var result = aggregationService.getAggregatedData(request);

        // assert
        assertEquals(2, result.getSortedQuotes().size());

        var coverageTypeDto = result.getBest().getCoverageType();
        assertEquals(expensiveQuote.getCoverageType(), CoverageType.valueOf(coverageTypeDto.name()));
        assertEquals(
                expensiveQuote.getPrice(),
                result.getSortedQuotes().getFirst().getPrice()
        );
    }

    private void setUpData() {
        request = AggregationRequestDto.builder()
                .coverageTypeDto(CoverageTypeDto.CAR)
                .aggregationType(AggregationTypeDto.CHEAPEST)
                .build();

        var provider = Provider.builder().id(1L).build();

        cheapQuote = Quote.builder()
                .id(1L)
                .provider(provider)
                .coverageType(CoverageType.CAR)
                .price(BigDecimal.valueOf(100))
                .build();

        expensiveQuote = Quote.builder()
                .id(2L)
                .provider(provider)
                .coverageType(CoverageType.CAR)
                .price(BigDecimal.valueOf(500))
                .build();
    }
}
