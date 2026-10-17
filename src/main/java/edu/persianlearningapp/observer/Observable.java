package edu.persianlearningapp.observer;

import java.util.ArrayList;
import java.util.List;

/** Pull-style Observer pattern base */
public abstract class Observable {
    private final List<Observer> observers = new ArrayList<Observer>();
    public void addObserver(Observer observer) {
        if (observer == null) 
        	throw new IllegalArgumentException("observer cannot be null");
        if (!observers.contains(observer)) 
        	observers.add(observer);
    }
    public void removeObserver(Observer observer) { observers.remove(observer); }
    protected void notifyObservers() {
        List<Observer> snapshot = new ArrayList<Observer>(observers);
        for (Observer observer : snapshot) dispatch(observer);
    }
    protected abstract void dispatch(Observer observer);
}
