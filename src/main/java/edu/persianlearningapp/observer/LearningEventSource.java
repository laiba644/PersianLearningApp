package edu.persianlearningapp.observer;

import edu.persianlearningapp.domain.LearningEvent;

public class LearningEventSource extends Observable {
    private LearningEvent latestEvent;
    public LearningEvent getLatestEvent() { return latestEvent; }
    public void publish(LearningEvent event) {
        if (event == null) 
        	throw new IllegalArgumentException("event cannot be null");
        latestEvent = event;
        notifyObservers();
    }
    protected void dispatch(Observer observer) { 
    	observer.update(this); 
    	}
    
}
