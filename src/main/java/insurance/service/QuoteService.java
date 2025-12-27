package insurance.service;

import insurance.annotations.CleanAllCaches;
import insurance.config.CacheNames;
import insurance.domain.Quote;
import insurance.domain.enumaration.CoverageType;
import insurance.repository.ProviderRepository;
import insurance.repository.QuoteRepository;
import insurance.service.dto.CreateQuoteRequestDto;
import insurance.service.dto.GetQuoteListRequestDto;
import insurance.service.dto.QuoteResponseDto;
import insurance.service.dto.UpdateQuoteRequestDto;
import insurance.service.dto.mapper.DtoMapper;
import insurance.web.rest.errors.InsuranceErrorType;
import insurance.web.rest.errors.InsuranceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuoteService {
    private final QuoteRepository quoteRepository;
    private final ProviderRepository providerRepository;
    private final DtoMapper dtoMapper;

    @Transactional
    @CleanAllCaches
    public void createQuote(CreateQuoteRequestDto requestDto) {
        var provider = providerRepository.findByIdAndDeletedAtIsNull(requestDto.getProviderId()).orElseThrow(() -> {
                    log.error("Provider with id {} not found", requestDto.getProviderId());
                    return new InsuranceException(InsuranceErrorType.NOT_FOUND);
                }
        );

        var coverageType = dtoMapper.getCoverageType(requestDto.getCoverageTypeDto());
        duplicateQuoteValidation(coverageType, provider.getId());

        var quote = Quote.builder()
                .provider(provider)
                .coverageType(coverageType)
                .price(requestDto.getPrice())
                .build();

        quoteRepository.save(quote);
    }

    @CleanAllCaches
    @Transactional
    public void updateQuote(UpdateQuoteRequestDto requestDto) {
        var quote = quoteRepository.findByIdAndDeletedAtIsNull(requestDto.getQuoteId()).orElseThrow(() -> {
            log.error("Quote with id {} not found", requestDto.getQuoteId());
            return new InsuranceException(InsuranceErrorType.NOT_FOUND);
        });

        var isDirty = false;

        var coverageTypeDto = requestDto.getCoverageTypeDto();
        var coverageType = dtoMapper.getCoverageType(coverageTypeDto);
        if (coverageTypeDto != null && !quote.getCoverageType().equals(coverageType)) {
            duplicateQuoteValidation(coverageType, quote.getProvider().getId());

            quote.setCoverageType(coverageType);
            isDirty = true;
        }

        var updatedPrice = requestDto.getPrice();
        if (updatedPrice != null && !quote.getPrice().equals(updatedPrice)) {
            quote.setPrice(updatedPrice);
            isDirty = true;
        }

        if (isDirty) {
            quoteRepository.save(quote);
        }
    }

    private void duplicateQuoteValidation(CoverageType coverageType, long providerId) {
        quoteRepository.findByCoverageTypeAndProviderIdIsAndDeletedAtIsNull(coverageType, providerId).ifPresent(existingQuote -> {
            log.error("Quote with coverage type {} for provider id {} already exists", coverageType, providerId);
            throw new InsuranceException(InsuranceErrorType.BAD_REQUEST);
        });
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.QUOTE_CACHE, key = "#id")
    public QuoteResponseDto getQuote(Long id) {
        var quote = quoteRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> {
            log.error("Quote with id {} not found", id);
            return new InsuranceException(InsuranceErrorType.NOT_FOUND);
        });

        return dtoMapper.getQuoteResponseDto(quote);
    }

    @Transactional
    @CleanAllCaches
    public void deleteQuote(Long id) {
        quoteRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> {
            log.error("Quote with id {} not found", id);
            return new InsuranceException(InsuranceErrorType.NOT_FOUND);
        });

        quoteRepository.deleteById(LocalDateTime.now(), id);
    }

    public List<QuoteResponseDto> getQuoteList(GetQuoteListRequestDto requestDto) {
        var coverageTypeDtoList = requestDto.getCoverageTypeDto();
        var coverageTypeList = !CollectionUtils.isEmpty(coverageTypeDtoList) ?
                dtoMapper.getCoverageTypeList(coverageTypeDtoList) :
                List.<CoverageType>of();

        var quotes = quoteRepository.findAllByCoverageType(coverageTypeList);

        if (CollectionUtils.isEmpty(quotes)) {
            return List.of();
        }

        return quotes.stream().map(dtoMapper::getQuoteResponseDto).toList();
    }
}
