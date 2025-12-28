package insurance.service;

import insurance.domain.Provider;
import insurance.domain.Quote;
import insurance.repository.ProviderRepository;
import insurance.repository.QuoteRepository;
import insurance.service.dto.UpdateQuoteRequestDto;
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
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {QuoteService.class, DtoMapperImpl.class})
public class UpdateQuoteServiceUTest {
    @Autowired
    private QuoteService quoteService;

    @MockitoBean
    private QuoteRepository quoteRepository;
    @MockitoBean
    private ProviderRepository providerRepository;

    @Captor
    private ArgumentCaptor<Quote> quoteCaptor;

    private Quote quote;
    private UpdateQuoteRequestDto requestDto;

    @BeforeEach
    void setUp() {
        setUpData();
        doReturn(Optional.of(quote)).when(quoteRepository).findByIdAndDeletedAtIsNull(any());
    }

    @Test
    void updateQuote_quoteNotFound_throwsException() {
        doReturn(Optional.empty()).when(quoteRepository).findByIdAndDeletedAtIsNull(any());

        var ex = assertThrows(InsuranceException.class,
                () -> quoteService.updateQuote(requestDto));

        assertEquals(InsuranceErrorType.NOT_FOUND, ex.getInsuranceErrorType());
    }

    @Test
    void updateQuote_priceChanged_saveCalled() {
        quoteService.updateQuote(requestDto);

        verify(quoteRepository).save(quoteCaptor.capture());
        assertEquals(BigDecimal.valueOf(2000), quoteCaptor.getValue().getPrice());
    }

    @Test
    void updateQuote_noChange_saveNotCalled() {
        var request = UpdateQuoteRequestDto.builder().quoteId(10L).build();

        quoteService.updateQuote(request);

        verify(quoteRepository, never()).save(any());
    }

    private void setUpData() {
        requestDto = UpdateQuoteRequestDto.builder()
                .quoteId(10L)
                .price(BigDecimal.valueOf(2000))
                .build();


        var provider = Provider.builder()
                .id(1L)
                .name("Provider A")
                .build();

        quote = Quote.builder()
                .id(10L)
                .provider(provider)
                .price(BigDecimal.valueOf(1500))
                .build();
    }
}
