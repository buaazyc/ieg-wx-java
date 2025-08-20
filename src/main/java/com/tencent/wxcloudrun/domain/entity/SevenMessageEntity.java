package com.tencent.wxcloudrun.domain.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * @author zhangyichuan
 * @date 2025/5/22
 */
@Data
public class SevenMessageEntity implements Serializable {

  private String receiver;

  private String content;

  private boolean ok;

  private static final int PART_LENGTH = 2;

  /**
   * 构造函数
   *
   * @param input 微信输入的消息
   */
  public SevenMessageEntity(String input) {
    if (input == null || input.isEmpty()) {
      ok = false;
      return;
    }
    String[] parts = input.split("，", PART_LENGTH);
    if (parts.length != PART_LENGTH) {
      ok = false;
      return;
    }
    receiver = parts[0];
    content = parts[1];
    ok = true;
  }
}
