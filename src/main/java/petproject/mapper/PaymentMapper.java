package petproject.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import petproject.config.MapperConfig;
import petproject.dto.payment.CreatePaymentDto;
import petproject.dto.payment.PaymentDto;
import petproject.model.Payment;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    @Mapping(source = "rental.id", target = "rentalId")
    PaymentDto toPaymentDto(Payment payment);

    Payment toModel(CreatePaymentDto createPaymentDto);
}
