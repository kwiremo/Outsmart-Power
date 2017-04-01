package com.outsmart.outsmartpower.network.records;

/**
 * Created by Rene Moise on 3/4/2017.
 */

/**
 * This class extends record. It is a credential record and contain ssid and password field.
 */
public class CredentialBaseRecord extends BaseRecord {

    private String ssid;
    private String password;

    public CredentialBaseRecord(String ssid, String password) {
        this.ssid = ssid;
        this.password = password;
    }

    @Override
    public String toJSONString() {
        try{
            data.put("type","CRED");
            data.put("s",ssid);
            data.put("p",password);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return data.toString();
    }
}
