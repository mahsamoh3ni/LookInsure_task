package insurance.service.strategy;

import insurance.domain.enumaration.AggregationType;
import insurance.web.rest.errors.InsuranceErrorType;
import insurance.web.rest.errors.InsuranceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AggregationStrategyResolver {
    private final List<QuoteAggregationStrategy> aggregationStrategies;

    public QuoteAggregationStrategy resolve(AggregationType type) {
        return aggregationStrategies.stream()
                .filter(quoteAggregationStrategy -> quoteAggregationStrategy.getType() == type)
                .findAny()
                .orElseThrow(() -> {
                    log.error("No aggregation strategy found for type: {}", type);
                    return new InsuranceException(InsuranceErrorType.GENERAL_ERROR);
                });
    }
}
