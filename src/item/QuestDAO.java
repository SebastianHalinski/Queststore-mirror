package item;

import java.util.List;
import java.util.ArrayList;

import application.DbManagerDAO;
import application.FactoryDAO;
import application.Table;

public class QuestDAO extends FactoryDAO {

    private final static int ID_INDEX = 0;
    private final static int NAME_INDEX = 1;
    private final static int TYPE_INDEX = 2;
    private final static int DESCRIPTION_INDEX = 3;
    private final static int REWARD_INDEX = 4;

    public QuestDAO(){
        this.DEFAULT_TABLE = Table.QUESTS.getName();
    }

    public List<QuestModel> getManyObjects(String query) {
        dao = new DbManagerDAO();
        List<String[]> dataCollection = dao.getData(query);
        return getManyObjects(dataCollection);
    }

    public List<QuestModel> getManyObjects(List<String[]> dataCollection) {
        ArrayList<QuestModel> quests = new ArrayList<>();
        for (String [] record : dataCollection) {
            QuestModel quest = getOneObject(record);
            quests.add(quest);
        }
        return quests;
    }

    public QuestModel getOneObject(String query) {
        dao = new DbManagerDAO();
        String[] record = dao.getData(query).get(0);
        return getOneObject(record);
    }

    public QuestModel getOneObject(String[] record) {
        int id = Integer.parseInt(record[ID_INDEX]);
        char itemType = record[TYPE_INDEX].charAt(0);
        String itemName = record[NAME_INDEX];
        String itemDescription = record[DESCRIPTION_INDEX];
        int reward = Integer.parseInt(record[REWARD_INDEX]);
        return new QuestModel(id, itemType, itemName, itemDescription, reward);
    }

    public <T> void saveObject(T t){
        QuestModel quest = (QuestModel) t;
        String itemId = String.valueOf(quest.getId());
        String itemType = String.valueOf(quest.getType());
        String itemName = quest.getName();
        String itemDescription = quest.getDescription();
        String reward = String.valueOf(quest.getReward());

        String query;
        if (itemId.equals("-1")) {
            query = String.format(
                    "INSERT INTO %s " +
                            "VALUES(null, '%s', '%s', '%s', %s);",
                    DEFAULT_TABLE, itemName, itemType, itemDescription, reward);
        } else {
            query = String.format("UPDATE %s SET name='%s' , type='%s', description='%s', reward=%s " +
                    "WHERE id=%s;", DEFAULT_TABLE, itemName, itemType, itemDescription, reward, itemId);
        }
        DbManagerDAO dao = new DbManagerDAO();
        dao.inputData(query);
    }
}
