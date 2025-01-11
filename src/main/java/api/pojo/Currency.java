package api.pojo;

import lombok.Getter;
import lombok.Setter;

public class Currency {
    @Getter
    @Setter
    private String code;
    @Getter
    @Setter
    private String symbol;
    @Getter
    @Setter
    private String rate;
    @Getter
    @Setter
    private String description;
    @Getter
    @Setter
    private double rate_float;
}
