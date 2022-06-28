package com.jasavast.resource.vm;

import lombok.Data;
import org.json.JSONObject;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
public class ReqVM {
    @NotBlank
    private String method;
    @NotNull
    private Map data;

    public JSONObject toJson(){
        return new JSONObject().put("method",this.method)
                .put("data",this.data);
    }
}
