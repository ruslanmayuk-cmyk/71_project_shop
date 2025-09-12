package app.exceptions;

public class ProductNotFoundException extends Exception{
    public ProductNotFoundException(int id) {

        super(String.format("Продукт с идентификатором %d не найден", id));
    }

    public ProductNotFoundException(String title) {
        super(String.format("Продукт с именем %s не найден", title));
    }
}
