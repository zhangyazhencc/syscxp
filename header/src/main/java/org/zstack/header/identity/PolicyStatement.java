package org.zstack.header.identity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxhread on 17/8/3.
 */
public class PolicyStatement {

    private String uuid;
    private String name;
    private StatementEffect effect;
    private List<String> actions;

    public StatementEffect getEffect() {
        return effect;
    }

    public void setEffect(StatementEffect effect) {
        this.effect = effect;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public void addAction(String a) {
        if (actions == null) {
            actions = new ArrayList<String>();
        }
        actions.add(a);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
