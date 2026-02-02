package org.jeecg.modules.mdm.constant;

/**
 * 物料/产品类型常量（对应系统字典 item_type）
 *
 * 字典值：
 *   0 - 物料
 *   1 - 产品
 *   2 - 半产品
 */

public final class ItemTypeConstants {
    private ItemTypeConstants() {}//防止实例化
    //物料
    public static final String MATERIAL = "MATERIAL";
    //产品
    public static final String PRODUCT = "PRODUCT";
    //半产品
    public static final String SEMI_PRODUCT = "SEMI_PRODUCT";
}
