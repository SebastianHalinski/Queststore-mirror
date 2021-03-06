package model;

import dao.PassiveModelDAOImpl;
import enums.Table;
import managers.SQLProcessManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class StudentsQuestsDAO extends PassiveModelDAOImpl<StudentsQuests> {

    StudentsQuestsDAO(Connection connection) {
        super(connection);
    }

    public Map<Quest,LocalDate> load(int ownerId) {
        Quest quest;
        LocalDate date;
        int QUEST_ID_INDEX = 0;
        int DATE_INDEX = 1;
        Map<Quest,LocalDate> questsStock = new HashMap<>();

        List<String[]> dataCollection = getQuestStockData(ownerId);
        if (dataCollection.size() > 0) {
            for (String[] data : dataCollection) {
                int questId = Integer.parseInt(data[QUEST_ID_INDEX]);
                quest = ModelDaoFactory.getByType(QuestDAO.class).getModelById(questId);
                date = LocalDate.parse(data[DATE_INDEX]);
                questsStock.put(quest, date);
            }
        }
        return questsStock;
    }

    public boolean saveModel(StudentsQuests studentsQuests) {

        Map<Quest,LocalDate> questsStock  = studentsQuests.getStock();

        if(questsStock.size() > 0) {
            int ownerId = studentsQuests.getOwnerId();

            try {
                clearQuests(ownerId);
                String query = String.format("INSERT INTO %s VALUES(null, ?, ?, ?)", getDefaultTable());
                PreparedStatement preparedStatement = getConnection().prepareStatement(query);
                Set<Quest> quests = questsStock.keySet();
                LocalDate[] dates = questsStock.values().toArray(new LocalDate[0]);
                String date;
                int questId;
                int index = 0;
                for(Quest quest : quests) {
                    questId = quest.getId();
                    LocalDate localDate = dates[index];
                    date = localDate.toString();
                    preparedStatement.setInt(1, ownerId);
                    preparedStatement.setInt(2, questId);
                    preparedStatement.setString(3, date);
                    preparedStatement.addBatch();
                    index++;
                }
                SQLProcessManager.executeBatch(preparedStatement, getConnection());
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    protected void setDefaultTable(){
        setDefaultTable(Table.STUDENTS_QUESTS);
    }

    private List<String[]> getQuestStockData(int ownerId) {
        String query = String.format("SELECT quests_id, date FROM %s WHERE owner_id=?",
                getDefaultTable());
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(query);
            preparedStatement.setInt(1, ownerId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return SQLProcessManager.getObjectsDataCollection(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private void clearQuests(int ownerId) throws SQLException {
        String clearQuery = String.format("DELETE FROM %s WHERE owner_id=?", getDefaultTable());
        PreparedStatement preparedStatement = getConnection().prepareStatement(clearQuery);
        preparedStatement.setInt(1, ownerId);
        SQLProcessManager.executeUpdate(preparedStatement);
    }
}
