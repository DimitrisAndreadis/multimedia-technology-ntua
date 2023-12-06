package medialab.minesweeper;
public class InvalidDescriptionException extends Exception{
    public InvalidDescriptionException(String errorMessage, Throwable errorCause) {
        super(errorMessage, errorCause);
    }
}
