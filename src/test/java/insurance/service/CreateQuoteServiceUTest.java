package insurance.service;

import insurance.domain.Provider;
import insurance.domain.Quote;
import insurance.domain.enumaration.CoverageType;
import insurance.repository.ProviderRepository;
import insurance.repository.QuoteRepository;
import insurance.service.dto.CreateQuoteRequestDto;
import insurance.service.dto.enumeration.CoverageTypeDto;
import insurance.service.dto.mapper.DtoMapperImpl;
import insurance.web.rest.errors.InsuranceErrorType;
import insurance.web.rest.errors.InsuranceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {QuoteService.class, DtoMapperImpl.class})
public class CreateQuoteServiceUTest {
    @Autowired
    private QuoteService quoteService;

    @MockitoBean
    private QuoteRepository quoteRepository;
    @MockitoBean
    private ProviderRepository providerRepository;

    @Captor
    private ArgumentCaptor<Quote> quoteCaptor;

    private Provider provider;
    private Quote quote;
    private CreateQuoteRequestDto requestDto;

    @BeforeEach
    void setUp() {
        setUpData();

        doReturn(Optional.of(provider)).when(providerRepository).findByIdAndDeletedAtIsNull(any());
        doReturn(Optional.empty())
                .when(quoteRepository)
                .findByCoverageTypeAndProviderIdIsAndDeletedAtIsNull(any(), anyLong());
    }

    @Test
    void createQuote_providerNotFound_throwsException() {

        doReturn(Optional.empty()).when(providerRepository).findByIdAndDeletedAtIsNull(any());

        var ex = assertThrows(InsuranceException.class,
                () -> quoteService.createQuote(requestDto));

        assertEquals(InsuranceErrorType.NOT_FOUND, ex.getInsuranceErrorType());
        verify(quoteRepository, never()).save(any());
    }

    @Test
    void createQuote_duplicateQuote_throwsException() {
        doReturn(Optional.of(quote))
                .when(quoteRepository)
                .findByCoverageTypeAndProviderIdIsAndDeletedAtIsNull(any(), anyLong());

        var ex = assertThrows(InsuranceException.class, () -> quoteService.createQuote(requestDto));

        assertEquals(InsuranceErrorType.BAD_REQUEST, ex.getInsuranceErrorType());
        verify(quoteRepository, never()).save(any());
    }

    @Test
    void createQuote_happyFlow() {
        var request = CreateQuoteRequestDto.builder()
                .providerId(1L)
                .coverageTypeDto(CoverageTypeDto.CAR)
                .price(BigDecimal.TEN)
                .build();

        quoteService.createQuote(request);

        verify(quoteRepository).save(quoteCaptor.capture());
        assertEquals(provider.getId(), quoteCaptor.getValue().getProvider().getId());
        assertEquals(CoverageType.CAR, quoteCaptor.getValue().getCoverageType());
    }

    private void setUpData() {
        requestDto = CreateQuoteRequestDto.builder()
                .providerId(1L)
                .coverageTypeDto(CoverageTypeDto.CAR)
                .price(BigDecimal.TEN)
                .build();

        provider = Provider.builder()
                .id(1L)
                .name("Provider1")
                .build();

        quote = Quote.builder()
                .id(1L)
                .provider(provider)
                .coverageType(CoverageType.CAR)
                .price(BigDecimal.TEN)
                .build();
    }
}
