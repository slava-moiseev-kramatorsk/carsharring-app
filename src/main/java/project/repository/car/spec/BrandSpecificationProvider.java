package project.repository.car.spec;

import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import project.model.Car;
import project.repository.SpecificationProvider;

@Component
public class BrandSpecificationProvider implements SpecificationProvider<Car> {
    private static final String BRAND_KEY = "brand";

    @Override
    public String getKey() {
        return BRAND_KEY;
    }

    @Override
    public Specification<Car> getSpecification(String[] params) {
        return (root, query, criteriaBuilder)
                -> root.get(BRAND_KEY).in(Arrays.stream(params).toArray());
    }
}
