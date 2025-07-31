package project.repository.car;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.model.Car;
import project.repository.SpecificationProvider;
import project.repository.SpecificationProviderManager;

@RequiredArgsConstructor
@Component
public class CarSpecificationProviderManager implements SpecificationProviderManager<Car> {
    private final List<SpecificationProvider<Car>> specificationProviderList;

    @Override
    public SpecificationProvider<Car> getSpecificationProvider(String key) {
        return specificationProviderList.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Can`t find car by this key " + key)
        );
    }
}
