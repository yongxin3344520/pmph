/*
 * Copyright 2017 BangChen Information Technology Ltd., Co.
 * Licensed under the Apache License 2.0.
 */
package com.bc.pmpheep.general.bean;

/**
 * 用于MongoDB文件存取服务的文件类型枚举
 *
 * @author L.X <gugia@qq.com>
 */
public enum FileType {

    GROUP_FILE("小组文件"),
    MATERIAL_NOTICE_ATTACHMENT("教材通知内容附件"),
    MATERIAL_NOTE_ATTACHMENT("教材备注附件"),
    SYLLABUS("教学大纲"),
    CMS_ATTACHMENT("CMS附件");

    private final String type;

    private FileType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
