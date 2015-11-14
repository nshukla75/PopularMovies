package com.example.nitu.popularmovies.application;

import android.app.Application;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by nitus on 11/14/2015.
 */
public class PopMovieApp extends Application {
    public static final State STATE = State.getInstance();

    public static class State {
        private final AtomicBoolean isTwoPane = new AtomicBoolean();
        private final AtomicBoolean detailsPaneShown = new AtomicBoolean();

        public void setTwoPane(boolean b) {
            isTwoPane.set(b);
        }
        public boolean getTwoPane() {
            return isTwoPane.get();
        }

        public void setDetailsPaneShown(boolean detailsPaneShown) {
            this.detailsPaneShown.set(detailsPaneShown);
        }

        public boolean isDetailsPaneShown() {
            return detailsPaneShown.get();
        }

        private static class SingletonHolder {
            private static final State INSTANCE = new State();
        }

        public static State getInstance() {
            return SingletonHolder.INSTANCE;
        }

        private State() {
        }
    }
}
