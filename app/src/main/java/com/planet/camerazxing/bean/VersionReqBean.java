package com.planet.camerazxing.bean;

/**
 * Created by Administrator on 2018/2/8.
 */

public class VersionReqBean {

    private String ostype;
    private String packagename;
    private String version;

    public String getOsType() {
        return ostype;
    }

    public void setOsType(String ostype) {
        this.ostype = ostype;
    }

    public String getPackagename() {
        return packagename;
    }

    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
