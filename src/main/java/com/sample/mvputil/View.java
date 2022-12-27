package com.sample.mvputil;

public interface View<P extends  Presenter> {
    P getPresenter();
}
