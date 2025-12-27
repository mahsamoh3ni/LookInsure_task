package insurance.web.rest.errors;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class InsuranceException extends RuntimeException {

    private final InsuranceErrorType insuranceErrorType;
    private  String message;
    private final transient Map<String, Object> data;

    public InsuranceException(InsuranceErrorType insuranceErrorType, String message, Map<String, Object> data) {
        this.insuranceErrorType = insuranceErrorType;
        this.message = message;
        this.data = data;
    }

    public InsuranceException(InsuranceErrorType insuranceErrorType) {
        this(insuranceErrorType, insuranceErrorType.getMessageKey());
    }

    public InsuranceException(InsuranceErrorType insuranceErrorType, String message) {
        this(insuranceErrorType, message, null);
    }
}
