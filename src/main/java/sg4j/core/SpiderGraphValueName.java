package sg4j.core;

import java.awt.Color;

public class SpiderGraphValueName {

    private String name;
    private double defaultValue;
    private Color color;

    public SpiderGraphValueName(String valueName, Color color) {
        this.name = valueName;
        this.defaultValue = 0.0;
        this.color = color;
    }

    public SpiderGraphValueName(String valueName, double defaultValue, Color color) {
        this.name = valueName;
        this.defaultValue = defaultValue;
        this.color = color;
    }

    public double getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(double defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "SpiderGraphValueName [name=" + this.name + ", defaultValue=" + this.defaultValue + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(this.defaultValue);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SpiderGraphValueName other = (SpiderGraphValueName) obj;

        if (this.name == null) {
            return other.name == null;
        } else {
            return this.name.equals(other.name);
        }
    }
}
