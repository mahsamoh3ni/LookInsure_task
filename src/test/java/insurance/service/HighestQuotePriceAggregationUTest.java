package insurance.service;

import insurance.domain.Quote;
import insurance.service.strategy.HighestPriceAggregationStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.wildfly.common.Assert.assertTrue;

public class HighestQuotePriceAggregationUTest {

    private final HighestPriceAggregationStrategy strategy =
            new HighestPriceAggregationStrategy();

    @Test
    void aggregate_sortsByHighestPrice() {
        var q1 = Quote.builder().price(BigDecimal.valueOf(100)).build();
        var q2 = Quote.builder().price(BigDecimal.valueOf(300)).build();

        var result = strategy.aggregate(List.of(q1, q2));

        assertEquals(BigDecimal.valueOf(300), result.getFirst().getPrice());
    }

    @Test
    void aggregate_nullInput_returnsEmpty() {
        var result = strategy.aggregate(null);

        assertTrue(result.isEmpty());
    }
}
