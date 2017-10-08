package com.syscxp.header;

import com.syscxp.header.message.Message;

import java.util.List;

public interface Service extends Component {
    void handleMessage(Message msg);

    String getId();

    int getSyncLevel();

    List<String> getAliasIds();
}
