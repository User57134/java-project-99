package hexlet.code.mapper;

import hexlet.code.model.BaseEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.TargetType;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class ReferenceMapper {

    @PersistenceContext private EntityManager entityManager;

    /**
     * Метод берет на вход объект типа сущности и находит в базе по идентификатору в соответствующей
     * указанному типу таблице нужную запись, создает на ее основе сущность и возвращает.
     *
     * @param id - идентификатор записи
     * @param entityClass - объект типа сущности
     * @return объект сущности.
     * @param <T> - тип сущности
     */
    public <T extends BaseEntity> T toEntity(Long id, @TargetType Class<T> entityClass) {
        return (id != null) ? entityManager.find(entityClass, id) : null;
    }

    /**
     * Метод берет на вход объект типа сущности и находит в базе по слагу в соответствующей
     * указанному типу таблице нужную запись, создает на ее основе сущность и возвращает.
     *
     * @param slug - идентификатор записи
     * @param entityClass - объект типа сущности
     * @return объект сущности.
     * @param <T> - тип сущности
     */
    public <T extends BaseEntity> T toEntity(String slug, @TargetType Class<T> entityClass) {
        if (slug == null) {
            return null;
        }
        try {
            return entityManager
                    .createQuery(
                            "SELECT e FROM "
                                    + entityClass.getSimpleName()
                                    + " e WHERE e.slug = :slug",
                            entityClass)
                    .setParameter("slug", slug)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
