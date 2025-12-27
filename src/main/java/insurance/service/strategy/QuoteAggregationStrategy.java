package insurance.service.strategy;

import insurance.domain.Quote;
import insurance.domain.enumaration.AggregationType;

import java.util.List;

public interface QuoteAggregationStrategy {
     AggregationType getType();

     List<Quote> aggregate(List<Quote> quotes);
}
