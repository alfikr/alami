package com.jasavast.service;

import com.jasavast.core.util.DBUtils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApiAbstract {
    protected JSONObject req;
    protected JSONObject reqData;

    protected final DBUtils dbUtils;
    public void init(JSONObject req){
        this.req=req;
        this.reqData=req.has("data")?req.getJSONObject("data"):null;
    }

}