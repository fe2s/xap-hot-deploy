package org.openspaces.admin.application.hotredeploy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openspaces.admin.application.hotredeploy.config.Config;
import org.openspaces.admin.application.hotredeploy.files.LocalFileManager;
import org.openspaces.admin.pu.ProcessingUnit;

import java.util.ArrayList;

/**
 * @author Anna_Babich
 */
public class RollbackTest {

    private ProcessingUnit space;
    private String puName = "space";
    private String gigaspacesLocation;

    @Before
    public void before() throws InterruptedException {

    }


    @Test
    public void test(){

    }

    @After
    public void after(){
        space.undeploy();
    }
}
