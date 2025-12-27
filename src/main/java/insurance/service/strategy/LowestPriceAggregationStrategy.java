package insurance.service.strategy;

import insurance.domain.Quote;
import insurance.domain.enumaration.AggregationType;
import insurance.service.dto.AggregationResultDto;
import insurance.service.dto.QuoteResponseDto;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;

@Component
@Getter
public class LowestPriceAggregationStrategy implements QuoteAggregationStrategy {

    @Override
    public AggregationType getType() {
        return AggregationType.LOWEST_PRICE;
    }

    @Override
    public List<Quote> aggregate(List<Quote> quotes) {
        if (quotes == null || CollectionUtils.isEmpty(quotes)) {
            return List.of();
        }

        return quotes.stream()
                .sorted(Comparator.comparing(Quote::getPrice))
                .toList();
    }
}
