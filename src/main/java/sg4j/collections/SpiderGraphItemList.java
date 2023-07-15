package sg4j.collections;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import sg4j.core.SpiderGraphItem;
import sg4j.core.SpiderGraphValueName;
import sg4j.exceptions.IdenticalItemException;

public class SpiderGraphItemList implements Iterable<SpiderGraphItem> {

    private final List<SpiderGraphItem> items;

    private SpiderGraphValueNameList spiderGraphValueNameList;

    public void setSpiderGraphValueNameList(SpiderGraphValueNameList list) {
        this.spiderGraphValueNameList = list;
        this.notify(this.spiderGraphValueNameList);
    }

    public SpiderGraphItemList() {
        this.items = new ArrayList<>();
    }

    public synchronized void add(String itemName) {
        for (SpiderGraphItem item : this.items) {
            if (item.getName().equals(itemName))
                throw new IdenticalItemException();
        }

        SpiderGraphItem sgi = new SpiderGraphItem(itemName);

        if (spiderGraphValueNameList.size() > 0) {
            for (SpiderGraphValueName valueName : this.spiderGraphValueNameList.valueNames()) {
                sgi.getValues().put(valueName.getName(), valueName.getDefaultValue());
            }
        }
        this.items.add(sgi);
    }

    public synchronized void add(SpiderGraphItem item) {
        if (this.items.contains(item))
            throw new IdenticalItemException();
        else {
            if (spiderGraphValueNameList.size() > 0) {
                for (SpiderGraphValueName valueName : this.spiderGraphValueNameList.valueNames()) {
                    item.getValues().put(valueName.getName(), valueName.getDefaultValue());
                }
            }
            this.items.add(item);
        }
    }

    public synchronized boolean remove(SpiderGraphItem item) {
        return this.items.remove(item);
    }

    public synchronized boolean remove(String itemName) {
        return this.items.removeIf(item -> item.getName().equals(itemName));
    }

    synchronized void notify(SpiderGraphValueNameList valueList) {
        if (this.items.size() > 0) {
            List<SpiderGraphValueName> newValues = new ArrayList<>(valueList.valueNames());
            if (newValues.size() > this.items.get(0).getValues().size()) {
                List<SpiderGraphValueName> toRemoveHelperList = new ArrayList<SpiderGraphValueName>();
                for (String valueName : items.get(0).getValues().keySet()) {
                    toRemoveHelperList.add(new SpiderGraphValueName(valueName, null));
                }
                newValues.removeAll(toRemoveHelperList);
                for (SpiderGraphValueName valueName : newValues) {
                    for (SpiderGraphItem sgi : this.items) {
                        sgi.getValues().put(valueName.getName(), valueName.getDefaultValue());
                    }
                }
            } else if (newValues.size() < items.get(0).getValues().size()) {
                for (SpiderGraphItem sgi : this.items) {
                    sgi.getValues().keySet().removeIf(key -> !newValues.contains(new SpiderGraphValueName(key, null)));
                }
            }
        }
    }

    public synchronized SpiderGraphItem get(String name) {
        for (SpiderGraphItem sgi : this.items) {
            if (sgi.getName().equals(name))
                return sgi;
        }
        return null;
    }

    public synchronized SpiderGraphItem get(int index) {
        return this.items.get(index);
    }

    public synchronized double getBiggestValueAmongItems() {
        if (this.items.size() > 0) {
            if (this.items.get(0).getValues().size() == 0)
                return 0;
        } else
            return 0;

        double biggestVal = -Double.MAX_VALUE;

        for (SpiderGraphItem item : this.items) {
            Hashtable<String, Double> ht = item.getValues();
            for (String s : ht.keySet()) {
                if (biggestVal < ht.get(s)) {
                    biggestVal = ht.get(s);
                }
            }
        }
        return biggestVal;
    }

    public synchronized double getSmallestValueAmongItems() {
        if (this.items.size() > 0) {
            if (this.items.get(0).getValues().size() == 0)
                return 0;
        } else
            return 0;

        double smallestVal = Double.MAX_VALUE;

        for (SpiderGraphItem item : this.items) {
            Hashtable<String, Double> ht = item.getValues();
            for (String s : ht.keySet()) {
                if (smallestVal > ht.get(s)) {
                    smallestVal = ht.get(s);
                }
            }
        }
        return smallestVal;
    }

    public synchronized int size() {
        return items.size();
    }

    public synchronized double getValueRange() {
        double smallest = this.getSmallestValueAmongItems();
        double biggest = this.getBiggestValueAmongItems();
        if (biggest >= 0.0 && smallest >= 0.0)
            return biggest - smallest;
        else if (biggest >= 0.0 && smallest < 0.0) {
            return biggest + Math.abs(this.getSmallestValueAmongItems());
        } else {
            return Math.abs(this.getBiggestValueAmongItems()) - Math.abs(this.getSmallestValueAmongItems());
        }
    }

    @Override
    public String toString() {
        return "SpiderGraphItemList [items=" + this.items + "]";
    }

    @Override
    public Iterator<SpiderGraphItem> iterator() {
        return this.items.iterator();
    }

}
