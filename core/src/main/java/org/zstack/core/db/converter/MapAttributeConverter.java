package org.zstack.core.db.converter;

import org.zstack.utils.gson.JSONObjectUtil;

import javax.persistence.AttributeConverter;
import java.util.Map;

public class MapAttributeConverter implements AttributeConverter<Map, String> {
    @Override
    public String convertToDatabaseColumn(Map attribute) {
        return JSONObjectUtil.toJsonString(attribute);
    }

    @Override
    public Map convertToEntityAttribute(String dbData) {

        return JSONObjectUtil.toObject(dbData, Map.class);
    }
}
