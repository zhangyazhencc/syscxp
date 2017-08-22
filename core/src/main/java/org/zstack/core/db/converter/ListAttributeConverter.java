package org.zstack.core.db.converter;

import org.zstack.utils.gson.JSONObjectUtil;

import javax.persistence.AttributeConverter;
import java.util.List;

public class ListAttributeConverter implements AttributeConverter<List, String> {
    @Override
    public String convertToDatabaseColumn(List attribute) {
        return JSONObjectUtil.toJsonString(attribute);
    }

    @Override
    public List convertToEntityAttribute(String dbData) {

        return JSONObjectUtil.toObject(dbData, List.class);
    }
}
