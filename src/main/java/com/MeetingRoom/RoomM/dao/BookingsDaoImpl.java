package com.MeetingRoom.RoomM.dao;

import com.MeetingRoom.RoomM.Enums.BookingStatus;
import com.MeetingRoom.RoomM.model.Bookings;
import com.MeetingRoom.RoomM.model.MeetingRooms;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
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
    public List<Bookings> findByStatusAndEndTimeBefore(BookingStatus status, LocalDateTime currentTime) {
        // Write the custom query using entityManager if needed
        String query = "SELECT b FROM Bookings b WHERE b.status = :status AND b.endTime < :currentTime";
        return entityManager.createQuery(query, Bookings.class)
                .setParameter("status", status)
                .setParameter("currentTime", currentTime)
                .getResultList();
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
    @Override
    public List<Bookings> findByRoomAndStatus(MeetingRooms room, BookingStatus status) {
        String jpql = "SELECT b FROM Bookings b WHERE b.room = :room AND b.status = :status";
        TypedQuery<Bookings> query = entityManager.createQuery(jpql, Bookings.class);
        query.setParameter("room", room);
        query.setParameter("status", status);
        return query.getResultList();
    }
    @Override
    public List<Bookings> findByStatusIn(List<BookingStatus> statuses) {
        String jpql = "SELECT b FROM Bookings b WHERE b.status IN :statuses";
        TypedQuery<Bookings> query = entityManager.createQuery(jpql, Bookings.class);
        query.setParameter("statuses", statuses);
        return query.getResultList();
    }

    // Retrieve booking history for a specific user
    @Override
    public List<Bookings> findByUserIdAndStatusIn(Long userId, List<BookingStatus> statuses) {
        String jpql = "SELECT b FROM Bookings b WHERE b.user.id = :userId AND b.status IN :statuses";
        TypedQuery<Bookings> query = entityManager.createQuery(jpql, Bookings.class);
        query.setParameter("userId", userId);
        query.setParameter("statuses", statuses);

        List<Bookings> results = query.getResultList();
        System.out.println("Fetched Bookings: " + results); // Debugging log
        return results;
    }


    // Retrieve booking history for a specific room
    @Override
    public List<Bookings> findByRoomIdAndStatusIn(Long roomId, List<BookingStatus> statuses) {
        String jpql = "SELECT b FROM Bookings b WHERE b.room.id = :roomId AND b.status IN :statuses";
        TypedQuery<Bookings> query = entityManager.createQuery(jpql, Bookings.class);
        query.setParameter("roomId", roomId);
        query.setParameter("statuses", statuses);
        return query.getResultList();
    }
    @Override
    public List<Bookings> findByRoomAndStatusAndTimeRange(MeetingRooms room, BookingStatus status, LocalDateTime startTime, LocalDateTime endTime) {
        String jpql = "SELECT b FROM Bookings b WHERE b.room = :room " +
                "AND b.status = :status " +
                "AND ((b.startTime < :endTime) AND (b.endTime > :startTime))";  // Check for overlapping time slots

        return entityManager.createQuery(jpql, Bookings.class)
                .setParameter("room", room)
                .setParameter("status", status)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime)
                .getResultList();
    }

    @Override
    public boolean isOverlapping(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {
        String query = "SELECT COUNT(b) FROM Bookings b WHERE b.room.id = :roomId " +
                "AND b.status = 'CONFIRMED' " +
                "AND ((b.startTime < :endTime AND b.endTime > :startTime))";

        Long count = entityManager.createQuery(query, Long.class)
                .setParameter("roomId", roomId)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime)
                .getSingleResult();

        return count > 0;
    }
    @Override
    public long countOverlappingBookingsExcludingCurrent(Long roomId, LocalDateTime startTime, LocalDateTime endTime, Long bookingId) {
        String query = "SELECT COUNT(b) FROM Bookings b WHERE b.room.id = :roomId AND " +
                "b.startTime <= :endTime AND b.endTime >= :startTime OR" +
                ":endTime > b.startTime AND :endTime < b.endTime OR"
                + ":startTime > b.startTime AND :endTime < b.endTime AND b.id <> :bookingId";
        return entityManager.createQuery(query, Long.class)
                .setParameter("roomId", roomId)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime)
                .setParameter("bookingId", bookingId)
                .getSingleResult();
    }

    @Override
    public long countUserOverlappingBookings(Long userId, LocalDateTime startTime, LocalDateTime endTime, Long bookingId) {
        String query = "SELECT COUNT(b) FROM Bookings b WHERE b.user.id = :userId AND " +
                "b.startTime <= :endTime AND b.endTime >= :startTime OR" +
                ":endTime > b.startTime AND :endTime < b.endTime OR"
                + ":startTime > b.startTime AND :endTime < b.endTime AND b.id <> :bookingId";
        return entityManager.createQuery(query, Long.class)
                .setParameter("userId", userId)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime)
                .setParameter("bookingId", bookingId)
                .getSingleResult();
    }
    }

