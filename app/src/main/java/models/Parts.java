package models;

public class Parts {
    private String id;
    private String name;
    private String model;
    private int quantity;
    private String imageUrl;

    public Parts(){

    }
    // Konstruktor s id-om i ostalim atributima
    public Parts(String id, String name, String model, String quantity, String imageUrl) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.quantity = Integer.parseInt(quantity); // Pretvaranje stringa u int
        this.imageUrl = imageUrl;
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
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}