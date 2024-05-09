package ru.practicum.utils;

import ru.practicum.model.Task;

import java.util.ArrayList;
import java.util.List;

public class HistoryMapper {
    public static String historyToString(List<Task> history) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            builder.append(history.get(i).getId());
            if (i < history.size() - 1) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> historyIds = new ArrayList<>();
        String[] idTasks = value.split(", ");
        for (String id : idTasks) {
            historyIds.add(Integer.parseInt(id));
        }
        return historyIds;
    }
}
