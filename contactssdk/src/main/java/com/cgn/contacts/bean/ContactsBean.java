package com.cgn.contacts.bean;

import java.util.ArrayList;
import java.util.List;

/**

 */
public class ContactsBean {
    private String name;
    private List<Bean> phoneList;
    private List<Bean> addressList;
    private List<Bean> emailList;
    private String company;
    private String position;
    private int flag;//等于1时表示添加或更新，等于2时表示删除

    public ContactsBean() {
        this.name = "";
        this.company = "";
        this.position = "";
        this.phoneList = new ArrayList<>();
        this.addressList = new ArrayList<>();
        this.emailList = new ArrayList<>();
        this.flag = -1;
    }

    public class Bean {
        private String label;
        private String data;

        public Bean() {
            this("", "");
        }

        public Bean(String alabel, String adata) {
            this.label = alabel;
            this.data = adata;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public List<Bean> getPhoneList() {
        return phoneList;
    }

    public void setPhoneList(List<Bean> phoneList) {
        this.phoneList = phoneList;
    }

    public List<Bean> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Bean> addressList) {
        this.addressList = addressList;
    }

    public List<Bean> getEmailList() {
        return emailList;
    }

    public void setEmailList(List<Bean> emailList) {
        this.emailList = emailList;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
