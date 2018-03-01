package com.syscxp.core.statemachine;

import com.syscxp.header.exception.CloudStateMachineException;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StateMachineImpl<T extends Enum<T>, K extends Enum<K>> implements StateMachine<T, K> {
    private Map<T, HashMap<K, T>> _chart = new HashMap<>();
    private List<StateMachineListener<T, K>> _listeners = new ArrayList<>();
    private List<StateMachineListener<T, K>> _listenersTmp = new ArrayList<>();
    private static final CLogger _logger = Utils.getLogger(StateMachineImpl.class);

    @Override
    public void addTranscation(T old, K evt, T next) {
        HashMap<K, T> entry = _chart.computeIfAbsent(old, k -> new HashMap<>(1));
        /*HashMap<K, T> entry = _chart.get(old);
        if (entry == null) {
           entry = new HashMap<>(1);
           _chart.put(old, entry);
        }*/
        entry.put(evt, next);
    }

    @Override
    public T getNextState(T old, K evt) {
        HashMap<K, T> entry = _chart.get(old);
        if (entry == null) {
            throw new CloudStateMachineException("Cannot find next state:" + "[old state: " + old + "," +
                    " state event: " + evt + "]");
        }
       
        T next = entry.get(evt);
        if (next == null) {
            throw new CloudStateMachineException("Cannot find next state:" + "[old state: " + old + "," +
                    " state event: " + evt + "]");
        }
        
        return next;
    }

    @Override
    public void addListener(StateMachineListener<T, K> l) {
        synchronized (_listeners) {
            _listeners.add(l);
        }
    }

    @Override
    public void removeListener(StateMachineListener<T, K> l) {
        synchronized (_listeners) {
            _listeners.remove(l);
        }
    }

    @Override
    public void fireBeforeListener(T old, K evt, T next, Object... args) {
        _listenersTmp.clear();
        synchronized (_listeners) {
            _listenersTmp.addAll(_listeners);
        }
        
        for (StateMachineListener<T, K> l : _listenersTmp) {
           try {
              l.before(old, evt, next, args); 
           } catch (Exception e) {
               _logger.warn("Unhandled exception while calling listener: " + l.getClass().getCanonicalName() +
                       ". before state changing. [current state:" + old + " event:" + evt + " next state: " + next + "]", e);
           }
        }
    }

    @Override
    public void fireAfterListener(T prev, K evt, T curr, Object... args) {
        _listenersTmp.clear();
        synchronized (_listeners) {
            _listenersTmp.addAll(_listeners);
        }
        
        for (StateMachineListener<T, K> l : _listenersTmp) {
           try {
              l.after(prev, evt, curr, args);
           } catch (Exception e) {
               _logger.warn("Unhandled exception while calling listener: " + l.getClass().getCanonicalName() +
                       " after state changing." + "[" + "previous state:" + prev + " event: " + evt +
                       " current state: " + curr + "]", e);
           }
        }
    }
}
