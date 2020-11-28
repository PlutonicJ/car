package net.cyberspirit.car.ui.car;

import com.vaadin.flow.data.provider.Query;
import net.cyberspirit.car.entity.Car;
import net.cyberspirit.car.ui.car.overview.model.CarSearchOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Stateless
public class CarDataManager {

    private final Logger logger = LoggerFactory.getLogger(CarDataManager.class);

    @PersistenceContext
    private EntityManager entityManager;

    public List<Car> loadCars(Query<Car, CarSearchOptions> filterQuery) {
        CarSearchOptions searchOptions = filterQuery.getFilter().orElse(new CarSearchOptions());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Car> query = cb.createQuery(Car.class);
        Root<Car> root = query.from(Car.class);
        Predicate predicate = getPredicate(searchOptions, cb, root);
        if (predicate != null) {
            query.where(predicate);
        }
        return entityManager.createQuery(query)
                .setFirstResult(filterQuery.getOffset())
                .setMaxResults(filterQuery.getLimit())
                .getResultList();
    }

    private Predicate getPredicate(CarSearchOptions searchOptions, CriteriaBuilder cb, Root<Car> root) {
        Predicate predicate = null;
        String name = searchOptions.getName();
        if (name != null) {
            predicate = addAndPredicate(cb, predicate, cb.like(cb.upper(root.get("name")), "%" + name.toUpperCase() + "%"));
        }
        String brand = searchOptions.getBrand();
        if (brand != null) {
            predicate = addAndPredicate(cb, predicate, cb.like(cb.upper(root.get("brand")), "%" + brand.toUpperCase() + "%"));
        }
        return predicate;
    }

    private Predicate addAndPredicate(CriteriaBuilder cb, Predicate predicate, Predicate predicateToBeAdded) {
        if (predicate == null) {
            return predicateToBeAdded;
        }
        return cb.and(predicate, predicateToBeAdded);
    }

    public int numberOfCars(Query<Car, CarSearchOptions> filterQuery) {
        CarSearchOptions searchOptions = filterQuery.getFilter().orElse(new CarSearchOptions());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Car> root = query.from(Car.class);
        query.select(cb.count(root));
        Predicate predicate = getPredicate(searchOptions, cb, root);
        if (predicate != null) {
            query.where(predicate);
        }
        int count = entityManager.createQuery(query).getSingleResult().intValue();
        return count;
    }

    public Car load(long id) {
        return entityManager.find(Car.class, id);
    }

    public void save(Car entity) {
        if (entity.getId() == null) {
            logger.info("Persisting new entity!");
            entityManager.persist(entity);
        } else {
            logger.info("Merging existing entity with id " + entity.getId() + "!");
            entityManager.merge(entity);
        }
    }

    public void delete(Car entity) {
        Car entityFromDatabase = entityManager.find(Car.class, entity.getId());
        entityManager.remove(entityFromDatabase);
    }
}
