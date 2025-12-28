package insurance.service;

import insurance.domain.Quote;
import insurance.service.strategy.LowestPriceAggregationStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.wildfly.common.Assert.assertTrue;

public class LowestPriceAggregationStrategyUTest {

    private final LowestPriceAggregationStrategy strategy =
            new LowestPriceAggregationStrategy();

    @Test
    void aggregate_sortsByLowestPrice() {
        var q1 = Quote.builder().price(BigDecimal.valueOf(300)).build();
        var q2 = Quote.builder().price(BigDecimal.valueOf(100)).build();

        var result = strategy.aggregate(List.of(q1, q2));

        assertEquals(BigDecimal.valueOf(100), result.getFirst().getPrice());
    }

    @Test
    void aggregate_emptyList_returnsEmpty() {
        var result = strategy.aggregate(List.of());

        assertTrue(result.isEmpty());
    }
}
