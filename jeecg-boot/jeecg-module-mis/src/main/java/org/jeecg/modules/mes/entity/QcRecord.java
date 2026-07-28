package org.jeecg.modules.mes.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Description: 质检记录
 * @Author: jeecg-boot
 * @Date:   2026-07-24
 * @Version: V1.0
 */
@Schema(description="质检记录")
@Data
@TableName("mis_qc_record")
public class QcRecord implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;
	/**质检工单id*/
	@Excel(name = "质检工单id", width = 15)
    @Schema(description = "质检工单id")
    private String qcTaskId;
	/**质检工单号*/
	@Excel(name = "质检工单号", width = 15)
    @Schema(description = "质检工单号")
    private String qcTaskNo;
	/**来源工单id*/
	@Excel(name = "来源工单id", width = 15)
    @Schema(description = "来源工单id")
    private String sourceTaskId;
	/**来源工单号*/
	@Excel(name = "来源工单号", width = 15)
    @Schema(description = "来源工单号")
    private String sourceTaskNo;
	/**批次id*/
	@Excel(name = "批次id", width = 15)
    @Schema(description = "批次id")
    private String batchId;
	/**批次号*/
	@Excel(name = "批次号", width = 15)
    @Schema(description = "批次号")
    private String batchNo;
	/**生产订单号*/
	@Excel(name = "生产订单号", width = 15)
    @Schema(description = "生产订单号")
    private String orderNo;
	/**产品id*/
	@Excel(name = "产品id", width = 15)
    @Schema(description = "产品id")
    private String productId;
	/**产品编号*/
	@Excel(name = "产品编号", width = 15)
    @Schema(description = "产品编号")
    private String productCode;
	/**产品名称*/
	@Excel(name = "产品名称", width = 15)
    @Schema(description = "产品名称")
    private String productName;
	/**质检结果(pass合格/fail不合格/rework返工)*/
	@Excel(name = "质检结果(pass合格/fail不合格/rework返工)", width = 15, dicCode = "mes_qc_status")
    @Dict(dicCode = "mes_qc_status")
    @Schema(description = "质检结果(pass合格/fail不合格/rework返工)")
    private String qcResult;
	/**质检结论*/
	@Excel(name = "质检结论", width = 15)
    @Schema(description = "质检结论")
    private String qcConclusion;
	/**检验员id*/
	@Excel(name = "检验员id", width = 15, dictTable = "sys_user where del_flag='0' and status='1'", dicText = "realname", dicCode = "id")
    @Dict(dictTable = "sys_user where del_flag='0' and status='1'", dicText = "realname", dicCode = "id")
    @Schema(description = "检验员id")
    private String inspectorId;
	/**检验员*/
	@Excel(name = "检验员", width = 15)
    @Schema(description = "检验员")
    private String inspectorName;
	/**检验时间*/
	@Excel(name = "检验时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "检验时间")
    private Date inspectTime;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
	/**创建人*/
    @Schema(description = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private Date createTime;
	/**更新人*/
    @Schema(description = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @Schema(description = "所属部门")
    private String sysOrgCode;
}
