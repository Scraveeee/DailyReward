package de.scraveeee.dailyreward.database;

import java.util.LinkedList;
import java.util.List;

public class ReadyExecutor {

    private final List<Runnable> runnableList;
    private boolean ready;

    public ReadyExecutor() {
        this.runnableList = new LinkedList<>();
        this.ready = false;
    }

    public List<Runnable> getRunnableList() {
        return runnableList;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
        if (ready) {
            this.runnableList.forEach(Runnable::run);
        }
    }

    public boolean isReady() {
        return ready;
    }

    public void executeIfReady(Runnable runnable) {
        if (ready) {
            runnable.run();
            this.runnableList.forEach(Runnable::run);
            this.runnableList.clear();
            return;
        }
        this.runnableList.add(runnable);
    }
}
