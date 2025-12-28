package insurance.service;

import insurance.domain.Provider;
import insurance.domain.Quote;
import insurance.domain.enumaration.CoverageType;
import insurance.repository.ProviderRepository;
import insurance.repository.QuoteRepository;
import insurance.service.dto.QuoteResponseDto;
import insurance.service.dto.mapper.DtoMapperImpl;
import insurance.web.rest.errors.InsuranceErrorType;
import insurance.web.rest.errors.InsuranceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@SpringBootTest(classes = {QuoteService.class, DtoMapperImpl.class})
public class GetQuoteServiceUTest {

    @Autowired
    private QuoteService quoteService;

    @MockitoBean
    private QuoteRepository quoteRepository;
    @MockitoBean
    private ProviderRepository providerRepository;

    private Quote quote;

    @BeforeEach
    void setUp() {
        var provider = Provider.builder()
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
    @Test
    void getQuote_notFound_throwsException() {
        doReturn(Optional.empty()).when(quoteRepository).findByIdAndDeletedAtIsNull(any());

        var ex = assertThrows(InsuranceException.class,
                () -> quoteService.getQuote(10L));

        assertEquals(InsuranceErrorType.NOT_FOUND, ex.getInsuranceErrorType());
    }

    @Test
    void getQuote_happyFlow() {
        doReturn(Optional.of(quote)).when(quoteRepository).findByIdAndDeletedAtIsNull(any());

        QuoteResponseDto response = quoteService.getQuote(10L);

        assertEquals(quote.getCoverageType(), CoverageType.valueOf(response.getCoverageType().name()));
        assertEquals(quote.getPrice(), response.getPrice());
    }

}
