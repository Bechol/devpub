package ru.bechol.devpub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bechol.devpub.models.Tag;

import java.util.List;

/**
 * Класс TagRepository.
 * Реализация слоя доступа к данным для Tag.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see Tag
 * @see ru.bechol.devpub.service.TagService
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * Метод findByQuery.
     * Возврат тегов, имя которых начинается со строки, указанной в запросе.
     *
     * @param query - строка запроса.
     * @return - коллекция тегов.
     */
    @Query("from Tag as tag where tag.name like concat(:query, '%')")
    List<Tag> findByQuery(@Param("query") String query);

    /**
     * Метод findByNameIn.
     * Поиск по всем именам тегов в коллекции.
     *
     * @param names - имена искомых тегов.
     * @return - коллекция найденных тегов.
     */
    List<Tag> findByNameIn(List<String> names);

}
