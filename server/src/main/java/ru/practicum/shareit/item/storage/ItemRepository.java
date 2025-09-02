package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i " +
            "WHERE i.available = true AND " +
            "(LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))")
    List<Item> searchAvailableItems(@Param("text") String text);

    List<Item> findByOwnerId(Long ownerId);

    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.comments c LEFT JOIN FETCH c.author WHERE i.id = :id")
    Optional<Item> findByIdWithCommentsAndAuthors(Long id);

    @Query("SELECT i FROM Item i WHERE i.requestId = :requestId")
    List<Item> findItemsByRequestId(Long requestId);
}
