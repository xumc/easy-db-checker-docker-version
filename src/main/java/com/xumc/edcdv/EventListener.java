package com.xumc.edcdv;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mxu2 on 6/1/15.
 */
public class EventListener implements BinaryLogClient.EventListener {
    private List<Event> events;

    public EventListener() {
        events = new ArrayList<Event>();
    }

    public void onEvent(Event event) {
        try {
            events.add(event);
            EventType type = event.getHeader().getEventType();
            if (EventType.isDelete(type) || EventType.isUpdate(type) || EventType.isWrite(type)) {
                List<PackagedEvent> packagedEvents = new ArrayList<PackagedEvent>();
                PackagedEvent packagedEvent = new PackagedEvent(events);
                if (!packagedEvent.filterOutBy(EventFilter.getAllFilters())) {
                    packagedEvents.add(packagedEvent);
                }

                displayEvent(packagedEvents);
                events.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayEvent(List<PackagedEvent> packagedEvents) {
        for (PackagedEvent packagedEvent : packagedEvents) {
            try {
                StringBuilder sBuilder = new StringBuilder();
                sBuilder.append(PackagedEvent.showType(packagedEvent.getType())).append("  ")
                        .append(packagedEvent.getDatabase()).append(".")
                        .append(packagedEvent.getTableName());
                if (packagedEvent.getEntryName() != null) {
                    sBuilder.append("  Name => " + packagedEvent.getEntryName() + "  ");
                }
                if (packagedEvent.getEntryId() != null) {
                    sBuilder.append("  ID => " + packagedEvent.getEntryId());
                }
                sBuilder.append("\n");
                sBuilder.append("+-------------------------+-----------------------------+-----------------------------+\n");
                sBuilder.append("|       field        |          old value          |          new value          |\n");
                sBuilder.append("+-------------------------+-----------------------------+-----------------------------+\n");

                System.out.println(sBuilder.toString());
                for (List<PackagedEvent.Field> recordField : packagedEvent.getRecordFields()) {
                    for (PackagedEvent.Field f : recordField) {
                        StringBuilder sLine = new StringBuilder();
                        sLine.append("| ").append(Util.truncate(f.name, 24));
                        sLine.append("| ").append(Util.truncate(f.oldValue, 28));
                        sLine.append("| ").append(Util.truncate(f.newValue, 28)).append("|\n");
                        System.out.println(sLine.toString());
                    }
                    String splitLine = "+-------------------------+-----------------------------+-----------------------------+\n";
                    System.out.println(splitLine);
                }
                System.out.println();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
