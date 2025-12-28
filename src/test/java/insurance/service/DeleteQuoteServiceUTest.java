package insurance.service;

import insurance.domain.Provider;
import insurance.domain.Quote;
import insurance.domain.enumaration.CoverageType;
import insurance.repository.ProviderRepository;
import insurance.repository.QuoteRepository;
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
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {QuoteService.class, DtoMapperImpl.class})
class DeleteQuoteServiceUTest {

    @Autowired
    private QuoteService quoteService;

    @MockitoBean
    private QuoteRepository quoteRepository;
    @MockitoBean
    private ProviderRepository providerRepository;

    private Quote quote;

    @BeforeEach
    void setup() {
        var provider = Provider.builder().id(1L).name("Test Provider").build();
        quote = Quote.builder()
                .id(10L)
                .provider(provider)
                .coverageType(CoverageType.CAR)
                .price(BigDecimal.valueOf(1000))
                .build();
    }
    @Test
    void deleteQuote_notFound_throwsException() {
        doReturn(Optional.empty()).when(quoteRepository).findByIdAndDeletedAtIsNull(any());

        var ex = assertThrows(InsuranceException.class,
                () -> quoteService.deleteQuote(10L));

        assertEquals(InsuranceErrorType.NOT_FOUND, ex.getInsuranceErrorType());
    }

    @Test
    void deleteQuote_happyFlow() {
        doReturn(Optional.of(quote)).when(quoteRepository).findByIdAndDeletedAtIsNull(any());

        quoteService.deleteQuote(10L);

        verify(quoteRepository).deleteById(any(), anyLong());
    }
}
