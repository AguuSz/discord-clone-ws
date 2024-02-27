package com.aguusz.discord.services;

import ar.edu.iua.iw3.backend.business.interfaces.IUserBusiness;
import ar.edu.iua.iw3.backend.exceptions.BadPasswordException;
import ar.edu.iua.iw3.backend.exceptions.BusinessException;
import ar.edu.iua.iw3.backend.exceptions.NotFoundException;
import ar.edu.iua.iw3.backend.model.User;
import ar.edu.iua.iw3.backend.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userDAO;

    public User getById(long id) throws NotFoundException, BusinessException {
        Optional<User> user;
        try {
            user = userDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (user.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra el usuari@ de ID: " + id).build();
        }
        return user.get();
    }

    @Override
    public User load(String usernameOrEmail) throws NotFoundException, BusinessException {
        Optional<User> ou;
        try {
            ou = userDAO.findOneByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (ou.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra el usuari@ email o nombre: " + usernameOrEmail).build();
        }
        return ou.get();
    }

    @Override
    public void changePassword(String usernameOrEmail, String oldPassword, String newPassword, PasswordEncoder pEncoder) throws BadPasswordException, NotFoundException, BusinessException {
        User user = load(usernameOrEmail);

        if (!pEncoder.matches(oldPassword, user.getPassword())) {
            throw BadPasswordException.builder().build();
        }
        user.setPassword(pEncoder.encode(newPassword));
        try {
            userDAO.save(user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public void disable(String usernameOrEmail) throws NotFoundException, BusinessException {
        setDisable(usernameOrEmail, false);
    }

    @Override
    public void enable(String usernameOrEmail) throws NotFoundException, BusinessException {
        setDisable(usernameOrEmail, true);
    }

    private void setDisable(String usernameOrEmail, boolean enable) throws NotFoundException, BusinessException {
        User user = load(usernameOrEmail);
        user.setEnabled(enable);
        try {
            userDAO.save(user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    public List<User> getAll() {
        return userDAO.findAll();
    }

    public List<?> getByRoleId(int id) {
        return userDAO.findAllByIdRole(id);
    }
}
