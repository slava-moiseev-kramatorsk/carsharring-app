package petproject.repository;

import org.springframework.data.jpa.domain.Specification;
import petproject.dto.car.CarSearchParams;

public interface SpecificationBuilder<T> {
    Specification<T> build(CarSearchParams searchParams);
}
