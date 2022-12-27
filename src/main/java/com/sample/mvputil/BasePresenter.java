package com.sample.mvputil;

import com.vaadin.flow.router.BeforeEnterEvent;

public abstract class BasePresenter<V extends View> implements Presenter<V> {

    V view;

    @Override
    public V getView() {
        return view;
    }

    @Override
    public void setView(V view) {
        this.view = view;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

    }
}
