package com.jasavast.api.enumerasi;

public enum NasabahStatus {
    AKTIF,NONAKTIF;
    public String getStatus(NasabahStatus ns){
        return ns.name();
    }
}
