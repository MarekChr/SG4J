package sg4j.exceptions;

public class IdenticalValueException extends RuntimeException {

    public IdenticalValueException() {
        super("SpiderGraphItemList can not contain same items");
    }
}
