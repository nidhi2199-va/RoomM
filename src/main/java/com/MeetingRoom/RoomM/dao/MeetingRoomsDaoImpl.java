package com.MeetingRoom.RoomM.dao;

import com.MeetingRoom.RoomM.model.MeetingRooms;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
    public MeetingRooms save(MeetingRooms room) {
        if (room.getId() == null) {
            entityManager.persist(room);
            return room;
        } else {
            return entityManager.merge(room);
        }
    }

    @Override
    public Optional<MeetingRooms> findById(Long roomId) {
        return Optional.ofNullable(entityManager.find(MeetingRooms.class, roomId));
    }

    @Override
    public List<MeetingRooms> findAll() {
        return entityManager.createQuery("SELECT r FROM MeetingRooms r", MeetingRooms.class).getResultList();
    }

    @Override
    public void deleteById(Long roomId) {
        MeetingRooms room = entityManager.find(MeetingRooms.class, roomId);
        if (room != null) {
            entityManager.remove(room);
        }
    }

    @Override
    public Optional<MeetingRooms> findByName(String roomName) {
        List<MeetingRooms> rooms = entityManager.createQuery("SELECT r FROM MeetingRooms r WHERE r.roomName = :roomName", MeetingRoom.class)
                .setParameter("roomName", roomName)
                .getResultList();
        return rooms.stream().findFirst();
    }
}
