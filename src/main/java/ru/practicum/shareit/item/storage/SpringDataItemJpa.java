package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface SpringDataItemJpa extends JpaRepository<Item, Long> {
    List<Item> findByOwner_Id(Long ownerId);

    @Query("select i from Item i " +
            "where i.available = true and " +
            "(upper(i.name) like upper(concat('%', :text, '%')) " +
            " or upper(i.description) like upper(concat('%', :text, '%')))")
    List<Item> searchAvailableByText(@Param("text") String text);
}
