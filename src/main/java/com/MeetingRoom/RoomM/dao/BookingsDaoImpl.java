package com.MeetingRoom.RoomM.dao;

import com.MeetingRoom.RoomM.Enums.BookingStatus;
import com.MeetingRoom.RoomM.model.Bookings;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class BookingsDaoImpl implements BookingsDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Bookings save(Bookings booking) {
        if (booking.getBookingId() == null) {
            entityManager.persist(booking);
            return booking;
        } else {
            return entityManager.merge(booking);
        }
    }


    @Override
    public Optional<Bookings> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Bookings.class, id));
    }

    @Override
    public void deleteById(Long id) {
        Bookings booking = entityManager.find(Bookings.class, id);
        if (booking != null) {
            entityManager.remove(booking);
        }
    }

    @Override
    public long countOverlappingBookings(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {
        String query = "SELECT COUNT(b) FROM Bookings b WHERE b.room.id = :roomId " +
                "AND b.status = 'CONFIRMED' " +  // Only confirmed bookings
                "AND ((b.startTime < :endTime AND b.endTime > :startTime))";

        return (Long) entityManager.createQuery(query)
                .setParameter("roomId", roomId)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime)
                .getSingleResult();
    }

    @Override
    public List<Bookings> findByUserId(Long userId) {
        return entityManager.createQuery("SELECT b FROM Bookings b WHERE b.user.id = :userId", Bookings.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public List<Bookings> findByMeetingRoomId(Long roomId) {
        return entityManager.createQuery("SELECT b FROM Bookings b WHERE b.room.id = :roomId", Bookings.class)
                .setParameter("roomId", roomId)
                .getResultList();
    }

    @Override
    public List<Bookings> findByStatus(BookingStatus status) {
        return entityManager.createQuery("SELECT b FROM Bookings b WHERE b.status = :status", Bookings.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public List<Bookings> findBookingsWithinTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return entityManager.createQuery("SELECT b FROM Bookings b WHERE b.startTime >= :startTime AND b.endTime <= :endTime", Bookings.class)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime)
                .getResultList();
    }
}
