package models.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
@JsonIgnoreProperties(ignoreUnknown = true)
public enum Release {
    R1_3_0("1.3.0"), 
    R1_2_0("1.2.0"),
    R1_2_01("1.2.01");
    
    private String value;
    private Release(String str){
        value = str;
    }
    @JsonCreator
    public static Release fromString(String str){
            if(str.contains("1.3.0") || str.contains("130")){
                return R1_3_0;
            }else if(str.contains("1.2.0") || str.contains("120")){
                return R1_2_0;
            }else if(str.contains("1.2.01") || str.contains("1201") || str.contains("1.2.0.1")){
                return R1_2_01;
            }
            return null;
    }
    
    @JsonValue
    @Override
    public String toString() {
        return value;
    }
    
}
