package org.openspaces.admin.application.hotredeploy;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * @author Anna_Babich
 */
public class MockedConsole extends InputStream {

    private List<String> list;
    private int readIndex = 0;
    private int listIndex = 0;

    public MockedConsole(List<String> list) {
        this.list = list;
    }

    @Override
    public int read() throws IOException {
        if (checkListIndex()) {
            String s = list.get(listIndex);
            if (checkStringIndex(s, readIndex)) {
                return s.charAt(readIndex++);
            } else {
                listIndex++;
                readIndex = 0;
                return read();
            }
        }
        return -1;
    }

    private boolean checkListIndex() {
        if (listIndex >= list.size()) {
            return false;
        }
        return true;
    }

    private boolean checkStringIndex(String s, int index) {
        if (index >= s.length()) {
            return false;
        }
        return true;
    }
}

