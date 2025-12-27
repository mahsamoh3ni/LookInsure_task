package insurance.aop.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import insurance.config.ApplicationProperties;
import insurance.service.dto.BaseResponseDto;
import insurance.web.rest.consts.Header;
import insurance.web.rest.errors.InsuranceErrorType;
import insurance.web.rest.errors.InsuranceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.Locale;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ControllerExceptionHandler {
    private final ApplicationProperties applicationProperties;
    private final MessageSource messageSource;

    @ExceptionHandler(InsuranceException.class)
    @ResponseBody
    public ResponseEntity<BaseResponseDto<Void>> handleInsuranceException(HttpServletRequest req, InsuranceException e) {
        try {
            var locale = Locale.forLanguageTag(
                    !StringUtils.isEmpty(req.getHeader(Header.LOCALE)) ? req.getHeader(Header.LOCALE) : "En");

            var message = messageSource.getMessage(e.getInsuranceErrorType().getMessageKey(), null, locale);
            e.setMessage(message);

            return ResponseEntity.status(e.getInsuranceErrorType().getHttpStatus()).body(new BaseResponseDto<>(e, message));
        } finally {
            log.info("exception occurred: ", e);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<BaseResponseDto<Void>> handleMethodArgumentNotValidException(HttpServletRequest req, MethodArgumentNotValidException e) {
        log.error("validation exception occurred: ", e);
        var locale = req.getHeader(Header.LOCALE);
        return getBaseResponseDtoResponseEntity(locale, e.getBindingResult(), e.getMessage());
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResponseEntity<BaseResponseDto<Void>> handleBindException(HttpServletRequest req, BindException e) {
        log.error("validation exception occurred: ", e);
        var locale = req.getHeader(Header.LOCALE);
        return getBaseResponseDtoResponseEntity(locale, e.getBindingResult(), e.getMessage());
    }

    @ExceptionHandler(InvalidFormatException.class)
    @ResponseBody
    public ResponseEntity<BaseResponseDto<Void>> handleInvalidFormatException(HttpServletRequest req, InvalidFormatException e) {
        log.error("cannot parse json: ", e);
        var locale = req.getHeader(Header.LOCALE);
        return getBaseResponseDtoResponseEntity(locale, null, "{" + InsuranceErrorType.BAD_REQUEST.getMessageKey() + "}");
    }

    private ResponseEntity<BaseResponseDto<Void>> getBaseResponseDtoResponseEntity(String locale, BindingResult bindingResult, String message) {
        if (StringUtils.isEmpty(locale)) {
            locale = applicationProperties.getLocale();
        }

        var fieldError = bindingResult != null ? bindingResult.getFieldError() : null;
        var errorMsg = fieldError != null ? fieldError.getDefaultMessage() : message;

        if (errorMsg != null && errorMsg.startsWith("{")) {
            var newMsg = messageSource.getMessage(errorMsg.substring(1, errorMsg.length() - 1), null, Locale.forLanguageTag(locale));
            if (!StringUtils.isEmpty(newMsg)) {
                errorMsg = newMsg;
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponseDto<>(
                BaseResponseDto.ErrorData
                        .builder()
                        .errorCode(InsuranceErrorType.BAD_REQUEST.getCode())
                        .message(errorMsg)
                        .data(fieldError == null ? null : Collections.singletonMap("errorField", fieldError.getField()))
                        .build()
        ));
    }
}
