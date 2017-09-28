package ua.com.juja.microservices.teams.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor

public class ApiErrorMessage {
    /**
     * The status is duplicate http httpStatus internalErrorCode
     */
    private int httpStatus;
    /**
     * The code is internal error code for this exception
     */
    private String internalErrorCode;
    /**
     * The message for user
     */
    private String clientMessage;
    /**
     * The message  for developer
     */
    private String developerMessage;
    /**
     * The message  in exception
     */
    private String exceptionMessage;
    /**
     * List of detail error messages
     */
    private List<String> detailErrors;

    public static ApiErrorMessageBuilder builder(ApiErrorStatus apiStatus) {
        return new ApiErrorMessageBuilder(apiStatus);
    }

    ApiErrorMessage(String code, String clientMessage, String developerMessage) {
        this.httpStatus = 0;
        this.internalErrorCode = code;
        this.clientMessage = clientMessage;
        this.developerMessage = developerMessage;
        this.exceptionMessage = "";
        this.detailErrors = new ArrayList<>();
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public static class ApiErrorMessageBuilder {

        private ApiErrorMessage instance;

        private ApiErrorMessageBuilder(ApiErrorStatus apiStatus) {
            instance = new ApiErrorMessage(
                    apiStatus.internalCode(),
                    apiStatus.clientMessage(),
                    apiStatus.developerMessage()
            );
        }

        public ApiErrorMessageBuilder httpStatus(int status) {
            instance.httpStatus = status;
            return this;
        }

        public ApiErrorMessageBuilder exceptionMessage(String exceptionMessage) {
            instance.exceptionMessage = exceptionMessage;
            return this;
        }

        public ApiErrorMessageBuilder detailError(String detailError) {
            instance.detailErrors.add(detailError);
            return this;
        }

        public ApiErrorMessageBuilder detailErrors(List<String> detailErrors) {
            instance.detailErrors.addAll(detailErrors);
            return this;
        }

        public ApiErrorMessage build() {
            return instance;
        }
    }
}
