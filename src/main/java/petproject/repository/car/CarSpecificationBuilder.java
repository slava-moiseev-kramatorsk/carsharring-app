package petproject.repository.car;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import petproject.dto.car.CarSearchParams;
import petproject.model.Car;
import petproject.repository.SpecificationBuilder;
import petproject.repository.SpecificationProviderManager;

@Component
@RequiredArgsConstructor
public class CarSpecificationBuilder implements SpecificationBuilder<Car> {
    private static final String BRAND_KEY = "brand";
    private static final String MODEL_KEY = "model";
    private static final String TYPE_KEY = "type";
    private final SpecificationProviderManager<Car> specificationProviderManager;

    @Override
    public Specification<Car> build(CarSearchParams searchParams) {
        Specification<Car> spec = Specification.where(null);
        if (searchParams.model() != null && searchParams.model().length > 0) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider(MODEL_KEY)
                    .getSpecification(searchParams.model()));
        }
        if (searchParams.brand() != null && searchParams.brand().length > 0) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider(BRAND_KEY)
                    .getSpecification(searchParams.brand()));
        }
        if (searchParams.type() != null && searchParams.type().length > 0) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider(TYPE_KEY)
                    .getSpecification(searchParams.type()));
        }
        return spec;
    }
}
