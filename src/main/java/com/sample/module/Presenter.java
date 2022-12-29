package com.sample.module;

import com.sample.entity.User;
import com.sample.mvputil.BasePresenter;
import com.sample.mvputil.BaseView;
import com.sample.wrapper.UserWrapper;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

@UIScope
@SpringComponent
public class Presenter extends BasePresenter<BaseView> {

    @Autowired
    private UserWrapper userWrapper;
    static Logger logger = LogManager.getLogger(Presenter.class);

    public void addUser(User user) {
        logger.debug("This is debug");
        logger.info("This is info");
        logger.trace("This is trace");
        userWrapper.addUser(user);
    }

    public void updateUser(User user) {
        userWrapper.updateUser(user);
    }

    public void deleteUser(User user) {
        userWrapper.deleteUser(user);
    }

    public User getUserById(Integer id) {
        return userWrapper.getUserById(id);
    }

    public Collection<User> getAllUser() {
        return userWrapper.getAllUserDataFromCache();
    }


}
