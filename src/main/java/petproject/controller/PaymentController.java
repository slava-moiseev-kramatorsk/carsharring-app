package petproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import petproject.dto.payment.CreatePaymentDto;
import petproject.dto.payment.PaymentDto;
import petproject.model.User;
import petproject.security.CustomUserDetailService;
import petproject.service.payment.PaymentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
@Tag(name = "Payment management",
        description = "Endpoints for payment management")
public class PaymentController {
    private final PaymentService paymentService;
    private final CustomUserDetailService customUserDetailService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Find payment by id",
            description = "Find payment by id")
    public PaymentDto getPaymentById(@PathVariable Long id) {
        return paymentService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_CUSTOMER')or hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new payment",
            description = "Endpoint for create a new payment")
    public PaymentDto createPayment(@RequestBody CreatePaymentDto createPaymentDto) {
        return paymentService.createPayment(createPaymentDto);
    }

    @PostMapping("/success")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')or hasRole('MANAGER')")
    @Operation(summary = "Updates payment status",
            description = "Updates payment status to 'PAID' "
                    + "and sends a notification to telegram chat")
    public PaymentDto successPayments(@RequestParam("session_id") String sessionId,
                                      Authentication authentication) {
        User user = customUserDetailService.getUserFromAuthentication(authentication);
        return paymentService.updatePaymentStatus(sessionId, user);
    }

    @PostMapping("/cancel")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @Operation(summary = "Cancel endpoint",
            description = "Endpoint for redirection in case of "
                    + "payment cancellation")
    public void cancelPayments(@RequestParam("session_id") String sessionId,
                               Authentication authentication) {
        User user = customUserDetailService.getUserFromAuthentication(authentication);
        paymentService.paymentCancel(sessionId, user);
    }
}
