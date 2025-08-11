package project.repository;

import org.springframework.data.jpa.domain.Specification;
import project.dto.car.CarSearchParams;

public interface SpecificationBuilder<T> {
    Specification<T> build(CarSearchParams searchParams);
}
