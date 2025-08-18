package com.tencent.wxcloudrun.provider;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

/**
 * @author zhangyichuan
 */
@Data
@JacksonXmlRootElement(localName = "xml")
public class WxResponse {

    // 这些字段不需要在XML中输出，只用于内部处理
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer code;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String errorMsg;

    @JsonProperty("ToUserName")
    @JacksonXmlProperty(localName = "ToUserName")
    private String toUserName;

    @JsonProperty("FromUserName")
    @JacksonXmlProperty(localName = "FromUserName")
    private String fromUserName;

    @JsonProperty("CreateTime")
    @JacksonXmlProperty(localName = "CreateTime")
    private Integer createTime;

    @JsonProperty("MsgType")
    @JacksonXmlProperty(localName = "MsgType")
    private String msgType;

    @JsonProperty("Content")
    @JacksonXmlProperty(localName = "Content")
    private String content;

    public WxResponse() {
        this.code = 200;
        this.msgType = "text";
        this.content = "";
    }
}
