package com.MeetingRoom.RoomM.dao;

import com.MeetingRoom.RoomM.model.MeetingRooms;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class MeetingRoomsDaoImpl implements MeetingRoomsDao {

    private final EntityManager entityManager;

    @Autowired
    public MeetingRoomsDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public MeetingRooms save(MeetingRooms meetingRooms) {
        if (meetingRooms.getId() == null) {
            entityManager.persist(meetingRooms);
            return meetingRooms;
        } else {
            return entityManager.merge(meetingRooms);
        }
    }

    @Override
    public Optional<MeetingRooms> findById(Long id) {
        return Optional.ofNullable(entityManager.find(MeetingRooms.class, id));
    }

    @Override
    public List<MeetingRooms> findAll() {
        return entityManager.createQuery("SELECT m FROM MeetingRooms m", MeetingRooms.class).getResultList();
    }

//    @Override
//    public void deleteById(Long id) {
//        MeetingRooms meetingRooms = entityManager.find(MeetingRooms.class, id);
//        if (meetingRooms != null) {
//            entityManager.remove(meetingRooms);
//        }
//    }
    @Override
    public MeetingRooms findByName(String name) {
        try {
            return entityManager.createQuery("SELECT r FROM MeetingRooms r WHERE r.name = :name", MeetingRooms.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // Return null if no room is found
        }
    }
    @Override
    public Optional<MeetingRooms> findAvailableRoom(LocalDateTime startTime, LocalDateTime endTime, int minCapacity) {
        String query = "SELECT r FROM MeetingRooms  r WHERE r.capacity >= :minCapacity AND " +
                "r.id NOT IN (SELECT b.room.id FROM Bookings b " +
                "WHERE ((b.startTime < :endTime AND b.endTime > :startTime) " +
                "AND b.status = 'CONFIRMED'))";

        List<MeetingRooms> availableRooms = entityManager.createQuery(query, MeetingRooms.class)
                .setParameter("minCapacity", minCapacity)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime)
                .setMaxResults(1) // Fetch only one available room
                .getResultList();

        return availableRooms.stream().findFirst();
    }

}
