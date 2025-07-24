package petproject.repository.rental;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import petproject.model.Rental;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    Optional<Rental> findByIdAndUserId(Long id, Long userId);

    @Query(value = "SELECT r "
            + "FROM Rental r "
            + "JOIN r.user u "
            + "WHERE u.id = :userId AND r.isActive = :active")
    List<Rental> findByUserIdAndActive(Long userId, boolean isActive);

    List<Rental> findByUserId(Long id);

    @Query("SELECT r FROM Rental r WHERE r.isActive = :active")
    List<Rental> findAllByActive(@Param("active") boolean isActive);
}
