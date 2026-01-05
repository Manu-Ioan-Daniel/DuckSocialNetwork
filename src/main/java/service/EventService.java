package service;


import models.RaceEvent;
import repo.DbEventRepo;

import java.util.ArrayList;
import java.util.List;

public class EventService {
    private final UsersService usersService;
    private final DbEventRepo eventRepo;
    public EventService(UsersService usersService, DbEventRepo eventRepo) {
        this.usersService = usersService;
        this.eventRepo = eventRepo;
    }

    public List<RaceEvent> findAll() {
        List<RaceEvent> events = new ArrayList<>();
        eventRepo.findAll().forEach(events::add);
        return events;
    }
}
