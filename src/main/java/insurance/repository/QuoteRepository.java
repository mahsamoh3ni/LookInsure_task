package insurance.repository;

import insurance.domain.Quote;
import insurance.domain.enumaration.CoverageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    Optional<Quote> findByCoverageTypeAndProviderIdIsAndDeletedAtIsNull(CoverageType coverageType, long provider);

    Optional<Quote> findByIdAndDeletedAtIsNull(Long id);
    @Modifying
    @Query("""
            update Quote set deletedAt = :deletedAt where id = :id
            """)
    void deleteById(LocalDateTime deletedAt, long id);

    @Query("""
                     select distinct q from Quote q
                     join fetch q.provider p
                     where q.deletedAt is null and
                     p.deletedAt is null and
                     (:#{#coverageTypes.size()} = 0 or q.coverageType in :coverageTypes)
                     order by q.createdAt desc
            
            """)
    List<Quote> findAllByCoverageType(List<CoverageType> coverageTypes);
}


