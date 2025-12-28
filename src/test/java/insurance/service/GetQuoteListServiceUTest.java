package insurance.service;

import insurance.domain.Provider;
import insurance.domain.Quote;
import insurance.domain.enumaration.CoverageType;
import insurance.repository.ProviderRepository;
import insurance.repository.QuoteRepository;
import insurance.service.dto.GetQuoteListRequestDto;
import insurance.service.dto.enumeration.CoverageTypeDto;
import insurance.service.dto.mapper.DtoMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.wildfly.common.Assert.*;

@SpringBootTest(classes = {QuoteService.class, DtoMapperImpl.class})
public class GetQuoteListServiceUTest {
    @Autowired
    private QuoteService quoteService;

    @MockitoBean
    private QuoteRepository quoteRepository;
    @MockitoBean
    private ProviderRepository providerRepository;

    @Captor
    private ArgumentCaptor<List<CoverageType>> coverageTypeListCaptor;

    private Provider provider;

    @BeforeEach
    void setUp() {
        provider = Provider.builder()
                .id(1L)
                .name("Provider1")
                .build();
    }

    @Test
    void getQuoteList_emptyCoverageFilter_callsRepositoryWithEmptyList() {
        // arrange
        var request = GetQuoteListRequestDto.builder()
                .coverageTypeDto(List.of())
                .build();

        doReturn(List.of()).when(quoteRepository).findAllByCoverageType(any());

        // act
        var result = quoteService.getQuoteList(request);

        // assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(quoteRepository).findAllByCoverageType(coverageTypeListCaptor.capture());
        assertTrue(coverageTypeListCaptor.getValue().isEmpty());
        verifyNoMoreInteractions(quoteRepository);
    }

    @Test
    void getQuoteList_nullCoverageFilter_callsRepositoryWithEmptyList() {
        // arrange
        var request = GetQuoteListRequestDto.builder()
                .coverageTypeDto(null)
                .build();

        doReturn(List.of()).when(quoteRepository).findAllByCoverageType(any());

        // act
        var result = quoteService.getQuoteList(request);

        // assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(quoteRepository).findAllByCoverageType(coverageTypeListCaptor.capture());
        assertTrue(coverageTypeListCaptor.getValue().isEmpty());
    }

    @Test
    void getQuoteList_nonEmptyCoverageFilter_happyFlow() {
        // arrange
        var request = GetQuoteListRequestDto.builder()
                .coverageTypeDto(List.of(CoverageTypeDto.CAR))
                .build();

        var q1 = Quote.builder()
                .id(1L)
                .provider(provider)
                .coverageType(CoverageType.CAR)
                .price(BigDecimal.valueOf(1000))
                .build();

        var q2 = Quote.builder()
                .id(2L)
                .provider(provider)
                .coverageType(CoverageType.CAR)
                .price(BigDecimal.valueOf(2000))
                .build();

        doReturn(List.of(q1, q2)).when(quoteRepository).findAllByCoverageType(any());

        // act
        var result = quoteService.getQuoteList(request);

        // assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Provider1", result.getFirst().getProviderName());
        assertEquals("Provider1", result.get(1).getProviderName());

        verify(quoteRepository).findAllByCoverageType(coverageTypeListCaptor.capture());
        var passedCoverageTypes = coverageTypeListCaptor.getValue();
        assertEquals(1, passedCoverageTypes.size());
        assertEquals(CoverageType.CAR, passedCoverageTypes.getFirst());
    }
}
