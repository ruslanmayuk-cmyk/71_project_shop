package app.exceptions;

public class CustomerNotFoundException extends Exception{
    public CustomerNotFoundException(int id) {
        super(String.format("Покупатель с идентификатором %d не найден", id));

    }
}
