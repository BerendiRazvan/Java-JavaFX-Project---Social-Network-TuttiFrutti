package com.example.map_proiect_extins.service;

import com.example.map_proiect_extins.domain.*;
import com.example.map_proiect_extins.repository.Repository;
import com.example.map_proiect_extins.utils.events.ChangeEventType;
import com.example.map_proiect_extins.utils.events.EventsStatusEvent;
import com.example.map_proiect_extins.utils.events.UserStatusEvent;
import com.example.map_proiect_extins.utils.observer.Observable;
import com.example.map_proiect_extins.utils.observer.Observer;
import com.example.map_proiect_extins.validators.ServiceException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EventService implements Observable<EventsStatusEvent> {
    private Repository<Long, User> userRepository;
    private Repository<Long, Event> eventDataBaseRepo;
    private Repository<Pair<Long>, EventParticipant> participantDataBaseRepo;

    public EventService(Repository<Long, User> userRepository, Repository<Long, Event> eventDataBaseRepo, Repository<Pair<Long>, EventParticipant> participantDataBaseRepo) {
        this.userRepository = userRepository;
        this.eventDataBaseRepo = eventDataBaseRepo;
        this.participantDataBaseRepo = participantDataBaseRepo;
    }

    private Long newId() {
        Boolean ok = false;
        Long id = 1L;
        while (eventDataBaseRepo.findOne(id) != null) {
            id++;
        }
        return id;
    }

    public void saveEvent(String name, String description, LocalDateTime startDate, LocalDateTime finishDate, User organiser) {
        try {
            Event e = new Event(name, description, startDate, finishDate, organiser);
            e.setId(newId());
            eventDataBaseRepo.save(e);

            notifyObservers(new EventsStatusEvent(ChangeEventType.ADD,e));

        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public void saveParticipant(Long userId,Long eventId) {
        try {
            EventParticipant e =  new EventParticipant(userId,eventId);
            participantDataBaseRepo.save(e);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public void deleteParticipant(Pair<Long> longPair) {
        try {
            participantDataBaseRepo.delete(longPair);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }


    public void updateParticipant(Long userId,Long eventId, Boolean notifications) {
        try {
            EventParticipant e =  new EventParticipant(userId,eventId);
            e.setNotifications(notifications);
            participantDataBaseRepo.update(e);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public List<EventDTO> findAllEventsDto(User loggedUser){
        List<EventDTO> eventList = new ArrayList<>();

        eventDataBaseRepo.findAll().forEach(e -> {
            Event event = e;
            Boolean status, notif;

            EventParticipant participant = participantDataBaseRepo.findOne(new Pair<>(loggedUser.getId(), event.getId()));
            if(participant == null){
                status = false;
                notif = false;
            }
            else {
                status = true;
                notif = participant.getNotifications();
            }

            if(e.getFinishDate().isAfter(LocalDateTime.now()))
                eventList.add(new EventDTO(event,status,notif));

        });

        return eventList
                .stream()
                .sorted((eveventDto1,eveventDto2) -> eveventDto1.getEvent().getStartDate().compareTo(eveventDto2.getEvent().getStartDate()))
                .collect(Collectors.toList());
    }


    public List<NotifDTO> notificateUser(User loggedUser) {
        //1 week
        //1 day
        //1 hour

        List<NotifDTO> eventNotif = new ArrayList<>();

        eventDataBaseRepo.findAll().forEach(e -> {
            Event event = e;
            Boolean status, notif;

            EventParticipant participant = participantDataBaseRepo.findOne(new Pair<>(loggedUser.getId(), event.getId()));
            if(participant != null){
                LocalDateTime toDateTime = e.getStartDate();
                LocalDateTime fromDateTime = LocalDateTime.now();

                LocalDateTime tempDateTime = LocalDateTime.from( fromDateTime );

                long days = tempDateTime.until( toDateTime, ChronoUnit.DAYS );

                long hours = tempDateTime.until( toDateTime, ChronoUnit.HOURS );

                if(participant.getNotifications()){
                    if(hours <= 1 && hours>=0)
                        eventNotif.add(new NotifDTO(e,"less then one hour."));
                    else {
                        if (days <= 1 && days>=0)
                            eventNotif.add(new NotifDTO(e, "less then one day."));
                        else {
                            if (days <= 7 && days>=0)
                                eventNotif.add(new NotifDTO(e, "less then one week."));
                        }
                    }
                }
            }

        });

        return eventNotif.stream()
                .sorted((eveventDto1,eveventDto2) -> eveventDto1.getEvent().getStartDate().compareTo(eveventDto2.getEvent().getStartDate()))
                .collect(Collectors.toList());
    }

    private List<Observer<EventsStatusEvent>> observers=new ArrayList<>();

    @Override
    public void addObserver(Observer<EventsStatusEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<EventsStatusEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(EventsStatusEvent t) {
        observers.stream().forEach(x->x.update(t));
    }
}
