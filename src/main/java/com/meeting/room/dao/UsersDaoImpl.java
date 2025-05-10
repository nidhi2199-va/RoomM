package com.meeting.room.dao;

import com.meeting.room.model.Users;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class UsersDaoImpl implements UserDao {

    private final EntityManager entityManager;

    @Autowired
    public UsersDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Users save(Users user) {
        if (user.getId() == null) {
            entityManager.persist(user);
            return user;
        } else {
            return entityManager.merge(user);
        }
    }

    @Override
    public Optional<Users> findById(Long userId) {
        return Optional.ofNullable(entityManager.find(Users.class, userId));
    }

    @Override
    public List<Users> findAll() {
        return entityManager.createQuery("SELECT u FROM Users u", Users.class).getResultList();
    }

    @Override
    public void deleteById(Long userId) {
        Users user = entityManager.find(Users.class, userId);
        if (user != null) {
            entityManager.remove(user);
        }
    }

    @Override
    public Optional<Users> findByEmail(String email) {
        List<Users> users = entityManager.createQuery("SELECT u FROM Users u WHERE u.email = :email", Users.class)
                .setParameter("email", email)
                .getResultList();
        return users.stream().findFirst();
    }
}
