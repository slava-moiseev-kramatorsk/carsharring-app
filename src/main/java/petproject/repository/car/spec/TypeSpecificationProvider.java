package petproject.repository.car.spec;

import java.util.Arrays;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import petproject.model.Car;
import petproject.repository.SpecificationProvider;

@Component
public class TypeSpecificationProvider implements SpecificationProvider<Car> {
    private static final String TYPE_KEY = "type";

    @Override
    public String getKey() {
        return TYPE_KEY;
    }

    @Override
    public Specification<Car> getSpecification(String[] params) {
        List<Car.Type> types = Arrays.stream(params)
                .map(String::toUpperCase)
                .map(Car.Type::valueOf)
                .toList();
        return (root, query, criteriaBuilder)
                -> root.get(TYPE_KEY).in(types);
    }
}
