package com.ludicrus.ludicrus.storage.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by jpgarduno on 3/21/15.
 */
public class OrganizationLogo extends RealmObject{

    @PrimaryKey
    private int orgId;
    private String logo;

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
