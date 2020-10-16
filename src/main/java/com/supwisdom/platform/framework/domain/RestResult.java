package com.supwisdom.platform.framework.domain;


import java.util.HashMap;
import java.util.Map;

@Deprecated
public class RestResult extends BaseResult {

    private static final long serialVersionUID = -8006907665513682712L;
    private String message;
    private Object data;

    public RestResult() {
        super(Status.OK, null);
    }

    public RestResult(Status status, Object response) {
        super(status, response);

        if (Status.OK.equals(status)) {
            Map<String, Object> data = new HashMap<>();
            data.put("ok", response);
            this.data = data;
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void addData(OperatorType ot, Object data) {
        this.message = ot.getType() + "成功";
        this.data = data;
    }

    public static enum OperatorType {

        QUERY(0x01, "查询"), OPERATE(0x10, "操作"), CREATE(0x11, "创建"), UPDATE(0x12, "更新"), DELETE(0x13, "删除");

        private int code;
        private String type;

        private OperatorType(int code, String type) {
            this.type = type;
        }

        public int getCode() {
            return this.code;
        }

        public String getType() {
            return this.type;
        }
    }

}
