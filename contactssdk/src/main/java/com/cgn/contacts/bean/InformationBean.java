package com.cgn.contacts.bean;

import java.util.List;

/**
 * Created by leij on 2020/3/21.
 */

public class InformationBean {

    private int version;
    private List<ContactsBean> contactsBeanList;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<ContactsBean> getContactsBeanList() {
        return contactsBeanList;
    }

    public void setContactsBeanList(List<ContactsBean> contactsBeanList) {
        this.contactsBeanList = contactsBeanList;
    }
}
