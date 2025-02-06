package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start <= :now AND b.end >= CURRENT_TIMESTAMP")
    List<Booking> findCurrentBookingsByBookerId(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.end < CURRENT_TIMESTAMP")
    List<Booking> findPastBookingsByBookerId(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start > CURRENT_TIMESTAMP")
    List<Booking> findFutureBookingsByBookerId(Long userId);

    List<Booking> findByBookerIdAndStatus(Long userId, Status status);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId ORDER BY b.start DESC")
    List<Booking> findByOwnerIdOrderByStartDesc(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP")
    List<Booking> findCurrentBookingsByOwnerId(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.end < CURRENT_TIMESTAMP")
    List<Booking> findPastBookingsByOwnerId(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.start > CURRENT_TIMESTAMP")
    List<Booking> findFutureBookingsByOwnerId(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.status = :status")
    List<Booking> findByOwnerIdAndStatus(Long userId, Status status);

    List<Booking> findByItemIdAndBookerId(Long itemId, Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.end < CURRENT_TIMESTAMP ORDER BY b.end DESC")
    List<Booking> findLastBookingsByItemId(Long itemId);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.start > CURRENT_TIMESTAMP ORDER BY b.start ASC")
    List<Booking> findNextBookingsByItemId(Long itemId);
}
