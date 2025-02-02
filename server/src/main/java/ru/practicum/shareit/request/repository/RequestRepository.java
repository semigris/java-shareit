package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequestorIdOrderByCreatedDesc(Long requestorId);

    @Query("SELECT ir FROM Request ir WHERE ir.requestor.id != :userId ORDER BY ir.created DESC")
    List<Request> findAllByRequestorIdNot(Long userId, Pageable pageable);
}

