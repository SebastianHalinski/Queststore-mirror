package application;

import item.ItemModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class StudentStockModel {

    protected int studentId;
    protected Map<? extends ItemModel, ?> stock;

    public StudentStockModel(Integer studentId) {
        this.studentId = studentId;
        this.stock = new HashMap<>();

    }

    public Integer getStudentId() {
        return studentId;
    }
    public <K,V> K getItemByCosTam(V id) {
        for (Map.Entry<?,?> entry : stock.entrySet()) {
            if (Objects.equals(id, entry.getValue())) {
                @SuppressWarnings("unchecked")
                K art = (K) entry.getKey();
                return art;
            }
        }
        return null;
    }

    public <K extends ItemModel, V> K getItemNyName(String name) {
        for (Map.Entry<?,?> entry : stock.entrySet()) {
            @SuppressWarnings("unchecked")
            K item = (K) entry.getKey();
            String itemName = item.getName();
            if(itemName.equals(name)){
                return item;
            }
        }
        return null;
    }

    public <K,V> Map<K,V> getMapa() {
        @SuppressWarnings("unchecked")
        Map<K,V> map = (Map<K,V>) this.stock;
        return map;
    }

    public <T extends ItemModel, V> String toCustomString(){
        // setLevels();
        // Map<String,Integer> sorted = DataTool.sortDateMap(mapa); // sort map - result LinkedHashMap

        StringBuilder sb = new StringBuilder();
        sb.append("\n  Experience levels:\n\n\tlevel: required experience\n");
        int lineMultiplier = 45;
        String sign = "-";
        String line = DataTool.getMultipliedString(sign, lineMultiplier) + "\n";
        sb.append(line);
        for (Map.Entry<?,?> entry : stock.entrySet()) {
            @SuppressWarnings("unchecked")
            T key = (T) entry.getKey();
            @SuppressWarnings("unchecked")
            V value = (V) entry.getValue();
            sb.append(String.format("\t- %s: %s\n", key, value));
        }
        return sb.toString();
    }
//
//    public abstract Map getStock();
//
//    public abstract void setStock();     //use DAO

//    public abstract void addItem(ItemModel item);

}
