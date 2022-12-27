package com.sample.mvputil;

import com.vaadin.flow.router.BeforeEnterEvent;

public interface Presenter<V extends View> {
    V getView();

    void setView(V view);

    void beforeEnter(BeforeEnterEvent event);

}
