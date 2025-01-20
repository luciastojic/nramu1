package models;

public class Parts {
    private String image;
    private String name;
    private String model;
    private int quantity;

    public Parts() {
    }
    public Parts(String image, String name, String model, int quantity) {
        this.image = image;
        this.name = name;
        this.model = model;
        this.quantity = quantity;
    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Parts{" +
                "image='" + image + '\'' +
                ", name='" + name + '\'' +
                ", model='" + model + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
