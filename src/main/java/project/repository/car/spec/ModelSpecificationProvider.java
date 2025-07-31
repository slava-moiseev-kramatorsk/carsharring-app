package project.repository.car.spec;

import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import project.model.Car;
import project.repository.SpecificationProvider;

@Component
public class ModelSpecificationProvider implements SpecificationProvider<Car> {
    private static final String MODEL_KEY = "model";

    @Override
    public String getKey() {
        return MODEL_KEY;
    }

    @Override
    public Specification<Car> getSpecification(String[] params) {
        return (root, query, criteriaBuilder)
                -> root.get(MODEL_KEY).in(Arrays.stream(params).toArray());
    }
}
