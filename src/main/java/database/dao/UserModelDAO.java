package main.java.database.dao;

import main.java.database.model.UserModel;

import javax.persistence.*;
import java.util.List;

public class UserModelDAO {

    private EntityManager entityManager = Persistence.createEntityManagerFactory("jpa-persistence").createEntityManager();

    public UserModel insertUser(UserModel user) {
        entityManager.getTransaction().begin();
        entityManager.persist(user);
        entityManager.getTransaction().commit();
        return user;
    }

    public UserModel findUserByUserId(String userid) {
        return entityManager.find(UserModel.class, userid);
    }

    public UserModel findUserByUserName(String username) {
        List<UserModel> allUsers = findAllUsers();
        for (UserModel p: allUsers) {
            if(p.getUsername().equals(username)){
                return p;
            }
        }
        return null;
    }

    public List<UserModel> findAllUsers() {
        TypedQuery<UserModel> query = entityManager.createQuery("Select e from UserModel e", UserModel.class);
        return query.getResultList();
    }
}
