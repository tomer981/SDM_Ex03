package dto;


import java.util.Date;

public class FeedbackDTO {
    private String customerName;
    private Date date;
    private Integer rate;
    private String msg;


    public FeedbackDTO(String customerName, Date date, Integer rate, String msg) {
        this.customerName = customerName;
        this.date = date;
        this.rate = rate;
        this.msg = msg;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Date getDate() {
        return date;
    }

    public Integer getRate() {
        return rate;
    }

    public String getMsg() {
        return msg;
    }
}
