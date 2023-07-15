package sg4j.core;

import java.util.Hashtable;

public class SpiderGraphItem {

    private String name;
    private Hashtable<String, Double> values;

    public SpiderGraphItem(String itemName) {
        this.name = itemName;
        this.values = new Hashtable<>();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Hashtable<String, Double> getValues() {
        return this.values;
    }

    public void setValueForValueName(String valueName, double value) {
        this.values.replace(valueName, value);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = prime * result + ((this.values == null) ? 0 : this.values.hashCode());
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
        SpiderGraphItem other = (SpiderGraphItem) obj;
        if (this.name == null) {
            if (other.name != null)
                return false;
        } else if (!this.name.equals(other.name))
            return false;

        return this.name.equals(other.name);
    }

    @Override
    public String toString() {
        return "SpiderGraphItem [name=" + this.name + ", values=" + this.values + "]";
    }

}
