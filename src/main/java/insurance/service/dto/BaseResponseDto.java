package insurance.service.dto;

import insurance.web.rest.errors.InsuranceException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
public class BaseResponseDto<T> {

    private boolean successful;
    private T response;
    private ErrorData errorData;

    public static <S> BaseResponseDto<S> of(S response) {
        return new BaseResponseDto<>(response);
    }

    public static <S> BaseResponseDto<S> ok() {
        return new BaseResponseDto<>();
    }

    public static BaseResponseDto<?> error(ErrorData errorData) {
        return new BaseResponseDto<>(errorData);
    }

    public BaseResponseDto(T response) {
        this.successful = true;
        this.response = response;
    }

    public BaseResponseDto() {
        this.successful = true;
    }

    public BaseResponseDto(ErrorData errorData) {
        this.successful = false;
        this.errorData = errorData;
    }

    public BaseResponseDto(InsuranceException exception) {
        this.successful = false;
        this.errorData = ErrorData.builder()
                .errorCode(exception.getInsuranceErrorType().getCode())
                .message(exception.getMessage())
                .data(exception.getData())
                .build();
    }

    public BaseResponseDto(InsuranceException exception, String localizedErrorMessage) {
        this.successful = false;
        this.errorData = ErrorData.builder()
                .errorCode(exception.getInsuranceErrorType().getCode())
                .message(localizedErrorMessage)
                .data(exception.getData())
                .build();
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ErrorData {
        private int errorCode;
        private String message;
        private Map<String, Object> data;
    }

}
