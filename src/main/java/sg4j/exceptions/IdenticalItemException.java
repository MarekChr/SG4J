package sg4j.exceptions;

public class IdenticalItemException extends RuntimeException {

    public IdenticalItemException() {
        super("SpiderGraphValueList can not contain same values");
    }
}
