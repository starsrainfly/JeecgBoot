package org.jeecg.modules.mes.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Schema(description = "物料需求与领料对比报表VO")
public class MaterialReqActualReportVo {

    @Excel(name = "生产订单号", width = 20)
    private String orderNo;

    @Excel(name = "产品编码", width = 15)
    private String productCode;

    @Excel(name = "产品名称", width = 20)
    private String productName;

    @Excel(name = "物料编码", width = 15)
    private String materialCode;

    @Excel(name = "物料名称", width = 20)
    private String materialName;

    @Excel(name = "规格型号", width = 15)
    private String materialSpec;

    @Excel(name = "物料类型", width = 12)
    private String materialTypeText;

    @Excel(name = "计划用量(kg)", width = 15, type = 10)
    private BigDecimal plannedQty;

    @Excel(name = "实际称重(kg)", width = 15, type = 10)
    private BigDecimal actualQty;

    @Excel(name = "差异量(kg)", width = 15, type = 10)
    private BigDecimal diffQty;

    @Excel(name = "差异率(%)", width = 12, type = 10)
    private BigDecimal diffRate;

    // 下钻用
    private String orderId;
    private String materialId;
}
