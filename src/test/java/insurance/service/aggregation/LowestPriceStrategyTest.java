package insurance.service.aggregation;

import insurance.domain.Provider;
import insurance.domain.Quote;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LowestPriceStrategyTest {
//
//    private final LowestPriceStrategy strategy = new LowestPriceStrategy();
//
//    @Test
//    void sortsByPriceAscendingAndPicksLowest() {
//        Provider p = Provider.builder().id(1L).name("P").code("P").build();
//
//        Quote q1 = Quote.builder().id(1L).coverageType("car").price(new BigDecimal("200.00")).provider(p).build();
//        Quote q2 = Quote.builder().id(2L).coverageType("car").price(new BigDecimal("100.00")).provider(p).build();
//        Quote q3 = Quote.builder().id(3L).coverageType("car").price(new BigDecimal("150.00")).provider(p).build();
//
//        List<Quote> input = Arrays.asList(q1, q2, q3);
//
//        List<Quote> sorted = strategy.sort(input);
//
//        assertThat(sorted).extracting(Quote::getPrice).containsExactly(new BigDecimal("100.00"), new BigDecimal("150.00"), new BigDecimal("200.00"));
//
//        assertThat(strategy.pickBest(input)).isPresent().contains(sorted.get(0));
//    }
}

