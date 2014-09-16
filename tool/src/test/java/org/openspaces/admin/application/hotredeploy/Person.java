package org.openspaces.admin.application.hotredeploy;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

/**
 * @author Anna_Babich
 */
@SpaceClass
public class Person {

    private String id;

    public Person() {
    }

    public Person(String id) {
        this.id = id;
    }
    @SpaceId
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
