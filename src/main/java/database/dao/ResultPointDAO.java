package main.java.database.dao;

import main.java.database.model.ResultPoint;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

public class ResultPointDAO {

    private EntityManager entityManager;

    public ResultPointDAO(){
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("jpa-persistence");
        entityManager = entityManagerFactory.createEntityManager();
    }

    public ResultPoint insertPoint(ResultPoint point) {
        entityManager.getTransaction().begin();
        entityManager.persist(point);
        entityManager.getTransaction().commit();
        return point;
    }

    public ResultPoint findResultPoint(String id) {
        return entityManager.find(ResultPoint.class, id);
    }

    public List<ResultPoint> findAllPoints() {
        TypedQuery<ResultPoint> query = entityManager.createQuery("Select e from ResultPoint e", ResultPoint.class);
        return query.getResultList();
    }

    public void removePoint(String id) {
        ResultPoint point = findResultPoint(id);
        if (point != null) {
            entityManager.getTransaction().begin();
            entityManager.remove(point);
            entityManager.getTransaction().commit();
        }
    }

    public void clearUserHistory(String userId){
        for(ResultPoint point : findAllPoints()){
            if(point.getUserId().equals(userId)){
                removePoint(point.getId());
            }
        }
    }
}
