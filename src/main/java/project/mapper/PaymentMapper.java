package project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import project.config.MapperConfig;
import project.dto.payment.CreatePaymentDto;
import project.dto.payment.PaymentDto;
import project.model.Payment;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    @Mapping(source = "rental.id", target = "rentalId")
    PaymentDto toPaymentDto(Payment payment);

    Payment toModel(CreatePaymentDto createPaymentDto);
}
