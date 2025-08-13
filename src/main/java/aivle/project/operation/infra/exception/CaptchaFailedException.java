package aivle.project.operation.infra.exception;

public class CaptchaFailedException extends RuntimeException {

    public CaptchaFailedException() {
        super("Captcha verification failed.");
    }

    public CaptchaFailedException(String message) {
        super(message);
    }

    public CaptchaFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CaptchaFailedException(Throwable cause) {
        super(cause);
    }
}
