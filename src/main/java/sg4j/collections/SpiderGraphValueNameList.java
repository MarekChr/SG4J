package sg4j.collections;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sg4j.core.SpiderGraphValueName;
import sg4j.exceptions.IdenticalValueException;

public class SpiderGraphValueNameList implements Iterable<SpiderGraphValueName> {

    private SpiderGraphItemList observingItemList;
    private List<SpiderGraphValueName> valueNames;

    synchronized List<SpiderGraphValueName> valueNames() {
        return this.valueNames;
    }

    public SpiderGraphValueNameList(SpiderGraphItemList observer) {
        this.observingItemList = observer;
        this.valueNames = new ArrayList<SpiderGraphValueName>();
    }

    public synchronized void add(SpiderGraphValueName valueName) {
        if (this.valueNames.contains(valueName))
            throw new IdenticalValueException();
        else if (this.valueNames.add(valueName)) {
            this.notifyItemList();
        }
    }

    public synchronized void add(String valueName, Color color) {
        for (SpiderGraphValueName item : this.valueNames) {
            if (item.getName().equals(valueName))
                throw new IdenticalValueException();
        }
        if (this.valueNames.add(new SpiderGraphValueName(valueName, color))) {
            this.notifyItemList();
        }
    }

    public synchronized boolean remove(SpiderGraphValueName valueName) {
        boolean anyRemoved = this.valueNames.remove(valueName);
        if (anyRemoved) {
            this.notifyItemList();
        }
        return anyRemoved;
    }

    public synchronized boolean remove(String itemName) {
        boolean anyRemoved = this.valueNames.removeIf(valueName -> valueName.getName().equals(itemName));
        if (anyRemoved) {
            this.notifyItemList();
        }
        return anyRemoved;
    }

    public synchronized SpiderGraphValueName get(String name) {
        for (SpiderGraphValueName sgvn : this.valueNames) {
            if (sgvn.getName().equals(name))
                return sgvn;
        }
        return null;
    }

    public synchronized SpiderGraphValueName get(int index) {
        return this.valueNames.get(index);
    }

    synchronized void notifyItemList() {
        this.observingItemList.notify(this);
    }

    public int size() {
        return valueNames.size();
    }

    @Override
    public String toString() {
        return "SpiderGraphValueNameList [valueNames=" + this.valueNames + "]";
    }

    @Override
    public Iterator<SpiderGraphValueName> iterator() {
        return this.valueNames.iterator();
    }

}
