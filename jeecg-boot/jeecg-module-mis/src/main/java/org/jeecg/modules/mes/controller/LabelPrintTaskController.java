package org.jeecg.modules.mes.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.commons.codec.binary.Base64;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.common.enums.SerialNoPrefixEnum;
import org.jeecg.modules.common.service.ISerialNoService;
import org.jeecg.modules.mes.entity.LabelPrintTask;
import org.jeecg.modules.mes.service.ILabelPrintTaskService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.service.ISysDepartService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: 标签打印
 * @Author: jeecg-boot
 * @Date:   2026-04-27
 * @Version: V1.0
 */
@Tag(name="标签打印")
@RestController
@RequestMapping("/mes/labelPrintTask")
@Slf4j
public class LabelPrintTaskController extends JeecgController<LabelPrintTask, ILabelPrintTaskService> {
	@Autowired
	private ILabelPrintTaskService labelPrintTaskService;
	@Autowired
	private ISerialNoService serialNoService;
	@Autowired
	private ISysDepartService sysDepartService;
	
	/**
	 * 分页列表查询
	 *
	 * @param labelPrintTask
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "标签打印-分页列表查询")
	@Operation(summary="标签打印-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<LabelPrintTask>> queryPageList(LabelPrintTask labelPrintTask,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("triggerType", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("companyId", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<LabelPrintTask> queryWrapper = QueryGenerator.initQueryWrapper(labelPrintTask, req.getParameterMap(),customeRuleMap);
		
		Page<LabelPrintTask> page = new Page<LabelPrintTask>(pageNo, pageSize);
		IPage<LabelPrintTask> pageList = labelPrintTaskService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param labelPrintTask
	 * @return
	 */
	@AutoLog(value = "标签打印-添加")
	@Operation(summary="标签打印-添加")
	@RequiresPermissions("mes:mis_label_print_task:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody LabelPrintTask labelPrintTask) {
		String taskNo = serialNoService.generateSerialNo(SerialNoPrefixEnum.LABEL_PRINT.getPrefix());
		labelPrintTask.setTaskNo(taskNo);
		if(labelPrintTask.getCompanyId() != null){
			SysDepart depart = sysDepartService.getDepartById(labelPrintTask.getCompanyId());
			if(depart != null){
				labelPrintTask.setCompanyName(depart.getDepartName());
			}
		}
		else{
			labelPrintTask.setCompanyName("");
		}

		// 生成二维码图片Base64（关键）
		if (StringUtils.isNotBlank(labelPrintTask.getQrContent())) {
			String qrImageBase64 = generateQrCodeImage(labelPrintTask.getQrContent(), 300, 300);
			labelPrintTask.setQrImage(qrImageBase64);
		}

		labelPrintTaskService.save(labelPrintTask);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param labelPrintTask
	 * @return
	 */
	@AutoLog(value = "标签打印-编辑")
	@Operation(summary="标签打印-编辑")
	@RequiresPermissions("mes:mis_label_print_task:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody LabelPrintTask labelPrintTask) {
		if(labelPrintTask.getCompanyId() != null){
			SysDepart depart = sysDepartService.getDepartById(labelPrintTask.getCompanyId());
			if(depart != null){
				labelPrintTask.setCompanyName(depart.getDepartName());
			}
		}
		else{
			labelPrintTask.setCompanyName("");
		}
		// 生成二维码图片Base64（关键）
		if (StringUtils.isNotBlank(labelPrintTask.getQrContent())) {
			String qrImageBase64 = generateQrCodeImage(labelPrintTask.getQrContent(), 300, 300);
			labelPrintTask.setQrImage(qrImageBase64);
		}
		labelPrintTaskService.updateById(labelPrintTask);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "标签打印-通过id删除")
	@Operation(summary="标签打印-通过id删除")
	@RequiresPermissions("mes:mis_label_print_task:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		labelPrintTaskService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "标签打印-批量删除")
	@Operation(summary="标签打印-批量删除")
	@RequiresPermissions("mes:mis_label_print_task:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.labelPrintTaskService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "标签打印-通过id查询")
	@Operation(summary="标签打印-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<LabelPrintTask> queryById(@RequestParam(name="id",required=true) String id) {
		LabelPrintTask labelPrintTask = labelPrintTaskService.getById(id);
		if(labelPrintTask==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(labelPrintTask);
	}

	/*
	* // 工序完工时，只存 qrContent，不生成图片
LabelPrintTask task = new LabelPrintTask();
task.setQrContent("{\"p\":\"101010001\",\"b\":\"MO2026031700004-01\"}");
// ... 其他字段 ...
labelPrintTaskService.save(task);*/

	 /**
	  * 生成标签预览图片
	  */
	 @Operation(summary="标签打印-生成预览图片")
	 @GetMapping(value = "/generateImage")
	 public Result<String> generateImage(@RequestParam(name="id",required=true) String id,
										 @RequestParam(name="dpi",required=false,defaultValue="300") Integer dpi) {
		 try {
			 String base64  = labelPrintTaskService.generateLabelImage(id, dpi);
			 log.info("controller接口调用：生成标签图片 Base64 长度: {}", base64.length());
			 log.info("Base64 前100字符: {}", base64.substring(0, Math.min(100, base64.length())));
			 return Result.OK(base64);
		 } catch (Exception e) {
			 log.error("生成标签图片失败", e);
			 return Result.error("生成图片失败: " + e.getMessage());
		 }
	 }

	 /**
	  * 执行打印
	  */
	 @AutoLog(value = "标签打印-执行打印")
	 @Operation(summary="标签打印-执行打印")
	 @PostMapping(value = "/print")
	 public Result<Map<String, Object>> printLabel(@RequestBody Map<String, Object> params) {
		 try {
			 String id = (String) params.get("id");
			 Integer copies = params.get("copies") != null ? Integer.parseInt(params.get("copies").toString()) : 1;

			 if (id == null) {
				 return Result.error("任务ID不能为空");
			 }

			 LabelPrintTask task = labelPrintTaskService.getById(id);
			 if (task == null) {
				 return Result.error("打印任务不存在");
			 }

			 // 累加已打印份数
			 Integer printedCopies = task.getCopies() != null ? task.getCopies() : 0;
			 task.setCopies(printedCopies + copies);
			 // 更新状态为已完成（实际项目中这里调用打印机SDK）
			 task.setStatus("COMPLETED");
			 task.setCopies(copies);
			 task.setPrintTime(new java.util.Date());
			 labelPrintTaskService.updateById(task);

			 Map<String, Object> result = new HashMap<>();
			 result.put("taskNo", task.getTaskNo());
			 result.put("status", "COMPLETED");
			 result.put("printTime", task.getPrintTime());
			 result.put("copies", task.getCopies());  // 返回累计份数
			 return Result.OK("打印成功", result);
		 } catch (Exception e) {
			 log.error("打印执行异常", e);
			 return Result.error("打印异常：" + e.getMessage());
		 }
	 }

	 /**
	  * 生成二维码图片并返回 Base64 字符串
	  * @param content 二维码内容
	  * @param width 宽度（像素）
	  * @param height 高度（像素）
	  * @return Base64 编码的图片字符串（带 data:image/png;base64, 前缀）
	  */
	 public static String generateQrCodeImage(String content, int width, int height) {
		 if (StringUtils.isBlank(content)) {
			 return null;
		 }

		 try {
			 // 1. 设置二维码参数
			 Map<EncodeHintType, Object> hints = new HashMap<>();
			 hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");  // 编码
			 hints.put(EncodeHintType.MARGIN, 1);               // 边距
			 hints.put(EncodeHintType.ERROR_CORRECTION, com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.M); // 纠错级别

			 // 2. 生成二维码矩阵
			 QRCodeWriter qrCodeWriter = new QRCodeWriter();
			 BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

			 // 3. 转为 BufferedImage
			 BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

			 // 4. 转为 Base64
			 ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			 ImageIO.write(image, "PNG", outputStream);
			 byte[] imageBytes = outputStream.toByteArray();

			 // 5. 返回 Base64 字符串（带前缀，前端可直接使用）
			 String base64 = Base64.encodeBase64String(imageBytes);
			 return "data:image/png;base64," + base64;

		 } catch (WriterException | IOException e) {
			 log.error("生成二维码失败: {}", e.getMessage());
			 return null;
		 }
	 }
    /**
    * 导出excel
    *
    * @param request
    * @param labelPrintTask
    */
    @RequiresPermissions("mes:mis_label_print_task:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, LabelPrintTask labelPrintTask) {
        return super.exportXls(request, labelPrintTask, LabelPrintTask.class, "mis_label_print_task");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("mes:mis_label_print_task:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, LabelPrintTask.class);
    }

}
