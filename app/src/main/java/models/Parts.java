package models;

public class Parts {
    private String id;
    private String name;
    private String model;
    private int quantity;

    public Parts(){

    }
    // Konstruktor s id-om i ostalim atributima
    public Parts(String id, String name, String model, String quantity) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.quantity = Integer.parseInt(quantity); // Pretvaranje stringa u int
    }

    // Getteri
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getModel() {
        return model;
    }

    public int getQuantity() {
        return quantity;
    }
}