package com.MeetingRoom.RoomM.dao;

import com.MeetingRoom.RoomM.model.MeetingRooms;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class MeetingRoomsDaoImpl implements MeetingRoomsDao {
    @PersistenceContext
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

    @Override
    public void deleteById(Long id) {
        MeetingRooms meetingRooms = entityManager.find(MeetingRooms.class, id);
        if (meetingRooms != null) {
            entityManager.remove(meetingRooms);
        }
    }
}
