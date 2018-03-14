package com.syscxp.header.message;

public class APICreateMessage extends APIMessage {
    /**
     * @desc resource uuid which must be of version 4(random) with dash stripped. For example,
     * '5d94103e-1925-4d86-96c0-f05489c259ab' is stripped as '5d94103e19254d8696c0f05489c259ab'.
     * When the field is provided, it's used as uuid for resource to be created. An internal error
     * is raised if the uuid conflicted with any existing resource uuid
     */
    private String resourceUuid;

    public String getResourceUuid() {
        return resourceUuid;
    }

    public void setResourceUuid(String resourceUuid) {
        this.resourceUuid = resourceUuid;
    }
}
