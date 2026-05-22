package org.jeecg.modules.wms.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.system.query.QueryGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.system.query.QueryRuleEnum;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.common.enums.SerialNoPrefixEnum;
import org.jeecg.modules.common.service.ISerialNoService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.wms.vo.InventoryCheckVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.*;

import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.wms.entity.InventoryCheckDetail;
import org.jeecg.modules.wms.entity.InventoryCheck;
import org.jeecg.modules.wms.service.IInventoryCheckService;
import org.jeecg.modules.wms.service.IInventoryCheckDetailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;
import org.apache.shiro.authz.annotation.RequiresPermissions;
 /**
 * @Description: 盘库主表
 * @Author: jeecg-boot
 * @Date:   2026-05-19
 * @Version: V1.0
 */
@Tag(name="盘库主表")
@RestController
@RequestMapping("/wms/inventoryCheck")
@Slf4j
public class InventoryCheckController extends JeecgController<InventoryCheck, IInventoryCheckService> {

	@Autowired
	private IInventoryCheckService inventoryCheckService;

	@Autowired
	private IInventoryCheckDetailService inventoryCheckDetailService;

	@Autowired
	private ISerialNoService serialNoService;

	/*---------------------------------主表处理-begin-------------------------------------*/

	/**
	 * 分页列表查询
	 * @param inventoryCheck
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "盘库主表-分页列表查询")
	@Operation(summary="盘库主表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<InventoryCheck>> queryPageList(InventoryCheck inventoryCheck,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("checkScope", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("checkMethod", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("warehouseId", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("checkStatus", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("approveStatus", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<InventoryCheck> queryWrapper = QueryGenerator.initQueryWrapper(inventoryCheck, req.getParameterMap(),customeRuleMap);
		Page<InventoryCheck> page = new Page<InventoryCheck>(pageNo, pageSize);
		IPage<InventoryCheck> pageList = inventoryCheckService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
     *   添加
     * @param inventoryCheck
     * @return
     */
    @AutoLog(value = "盘库主表-添加")
    @Operation(summary="盘库主表-添加")
    @RequiresPermissions("wms:wms_inventory_check:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody InventoryCheck inventoryCheck) {

		inventoryCheck.setCheckNo(serialNoService.generateSerialNo(SerialNoPrefixEnum.INVENTORY_CHECK.getPrefix()));
		if(inventoryCheck.getCheckUserId() != null) {
			LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			if(user != null) {
				inventoryCheck.setCheckUserName(user.getRealname());
			}
		}
        inventoryCheckService.save(inventoryCheck);
        return Result.OK("添加成功！");
    }

    /**
     *  编辑
     * @param inventoryCheck
     * @return
     */
    @AutoLog(value = "盘库主表-编辑")
    @Operation(summary="盘库主表-编辑")
    @RequiresPermissions("wms:wms_inventory_check:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
    public Result<String> edit(@RequestBody InventoryCheck inventoryCheck) {
        inventoryCheckService.updateById(inventoryCheck);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     * @param id
     * @return
     */
    @AutoLog(value = "盘库主表-通过id删除")
    @Operation(summary="盘库主表-通过id删除")
    @RequiresPermissions("wms:wms_inventory_check:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name="id",required=true) String id) {
        inventoryCheckService.delMain(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @AutoLog(value = "盘库主表-批量删除")
    @Operation(summary="盘库主表-批量删除")
    @RequiresPermissions("wms:wms_inventory_check:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
        this.inventoryCheckService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

	 /**
	  * 盘点前预览库存
	  */
	 @AutoLog(value = "盘库主表-预览库存")
	 @Operation(summary = "盘库主表-预览库存")
	 @RequiresPermissions("wms:wms_inventory_check:preview")
	 @GetMapping(value = "/previewStock")
	 public Result<?> previewStock(@RequestParam("id") String checkId) {

		 // 根据盘点范围查询库存数据（不创建明细）
		 List<InventoryCheckDetail> previewList = inventoryCheckService.previewStock(checkId);
		 IPage<InventoryCheckDetail> page = new Page<>();
		 page.setRecords(previewList);
		 page.setTotal(previewList.size());
		 return Result.OK(page);
	 }

	 /**
	  * 开始盘点
	  */
	 @AutoLog(value = "盘库主表-开始盘点")
	 @Operation(summary = "盘库主表-开始盘点")
	 @RequiresPermissions("wms:wms_inventory_check:start")
	 @PostMapping(value = "/startCheck")
	 public Result<String> startCheck(@RequestParam(name="id",required=true) String id) {

		 inventoryCheckService.startCheck(id);
		 return Result.OK("开始盘点成功！");
	 }

//	 /**
//	  * 保存草稿
//	  */
//	 @AutoLog(value = "盘库主表-保存草稿")
//	 @Operation(summary = "盘库主表-保存草稿")
//	 @RequiresPermissions("wms:wms_inventory_check:saveDraft")
//	 @PostMapping(value = "/saveDraft")
//	 public Result<String> saveDraft( @RequestBody InventoryCheckVo inventoryCheckVo) {
//		 if (CollUtil.isEmpty(inventoryCheckVo.getInventoryCheckDetailList())) {
//			 return Result.error("明细数据不能为空");
//		 }
//
//
//		 inventoryCheckService.saveDraft(inventoryCheckVo.getId(), inventoryCheckVo.getInventoryCheckDetailList());
//		 return Result.OK("草稿保存成功！");
//	 }

	 /**
	  * 确认明细（单条）
	  */
	 @Operation(summary = "盘点明细-单条确认")
	 @PostMapping("/confirmDetail")
	 @Transactional(rollbackFor = Exception.class)
	 public Result<String> confirmDetail(@Valid @RequestBody InventoryCheckDetail detail) {
		 // 校验主表状态
		 InventoryCheck check = inventoryCheckService.getById(detail.getCheckId());
		 if (check == null || !"1".equals(check.getCheckStatus())) {
			 return Result.error("盘点单状态不正确");
		 }

		 // 校验必填
		 if (detail.getActualQty() == null || detail.getActualQty().compareTo(BigDecimal.ZERO) < 0) {
			 return Result.error( "实盘数量必填且不能为负数");
		 }

		 // 有差异必须填原因
		 if (detail.getDiffQty() != null && detail.getDiffQty().compareTo(BigDecimal.ZERO) != 0
				 && (detail.getDiffReason() == null || detail.getDiffReason().trim().isEmpty())) {
			 return Result.error( "有差异时必须填写差异原因");
		 }

		 // 更新明细
		 detail.setCheckStatus(detail.getCheckStatus());
		 detail.setCheckTime(new DateTime());

		 inventoryCheckDetailService.updateById(detail);

		 return Result.OK("确认成功");
	 }

	 /**
	  * 批量确认明细
	  */
	 @Operation(summary = "盘点明细-批量确认")
	 @PostMapping("/batchConfirm")
	 @Transactional(rollbackFor = Exception.class)
	 public Result<String> batchConfirm(@Valid @RequestBody InventoryCheckVo vo) {
		 if (vo.getId() == null) {
			 return Result.error("盘点单ID不能为空");
		 }

		 // 校验主表状态
		 InventoryCheck check = inventoryCheckService.getById(vo.getId());
		 if (check == null || !"1".equals(check.getCheckStatus())) {
			 return Result.error("盘点单状态不正确");
		 }

		 List<InventoryCheckDetail> details = vo.getInventoryCheckDetailList();
		 if (details == null || details.isEmpty()) {
			 return Result.error("确认明细不能为空");
		 }

		 // 逐条校验并更新
		 for (InventoryCheckDetail detail : details) {
			 if (detail.getId() == null) {
				 return Result.error("明细ID不能为空");
			 }
			 if (detail.getActualQty() == null || detail.getActualQty().compareTo(BigDecimal.ZERO) < 0) {
				 return Result.error("编码" + detail.getGoodsCode() + "实盘数量必填且不能为负数");
			 }
			 if (detail.getDiffQty() != null && detail.getDiffQty().compareTo(BigDecimal.ZERO) != 0
					 && (detail.getDiffReason() == null || detail.getDiffReason().trim().isEmpty())) {
				 return Result.error("编码" + detail.getGoodsCode() + "有差异时必须填写差异原因");
			 }

			 detail.setCheckTime(new Date());
			 inventoryCheckDetailService.updateById(detail);
		 }

		 return Result.OK("批量确认成功，共处理" + details.size() + "条");
	 }
	 /**
	  * 完成盘点
	  * 主表状态 1→2，校验全部明细已处理
	  */
	 @Operation(summary = "完成盘点")
	 @PostMapping("/finishCheck")
	 @Transactional(rollbackFor = Exception.class)
	 public Result<String> finishCheck(@RequestParam("id") String id) {
		 InventoryCheck check = inventoryCheckService.getById(id);
		 if (check == null) {
			 return Result.error("盘点单不存在");
		 }
		 if (!"1".equals(check.getCheckStatus())) {
			 return Result.error("盘点单状态不正确");
		 }

		 // 汇总明细数据
		 List<InventoryCheckDetail> details = inventoryCheckDetailService.lambdaQuery()
				 .eq(InventoryCheckDetail::getCheckId, id)
				 .list();

		 long pendingCount = 0;
		 long checkedCount = 0;
		 long diffCount = 0;
		 BigDecimal diffAmount = BigDecimal.ZERO;

		 for (InventoryCheckDetail d : details) {
			 String status = d.getCheckStatus();

			 // 未处理
			 if ("0".equals(status) || status == null) {
				 pendingCount++;
				 continue;
			 }

			 // 已处理（已盘或跳过）
			 checkedCount++;

			 // 差异统计（已盘且有差异）
			 if ("2".equals(status)
					 && d.getDiffQty() != null
					 && d.getDiffQty().compareTo(BigDecimal.ZERO) != 0) {
				 diffCount++;
			 }

			 // 差异金额累加（跳过的不算金额差异）
			 if ("2".equals(status) && d.getDiffAmount() != null) {
				 diffAmount = diffAmount.add(d.getDiffAmount().abs());
			 }
		 }

		 // 有未处理项，记录日志（前端已提示，这里允许完成）
		 if (pendingCount > 0) {
			 log.warn("盘点单{}完成时还有{}条未处理", id, pendingCount);
		 }

		 // 更新主表


		 check.setCheckedItems((int)checkedCount);
		 check.setDiffItems((int) diffCount);
		 check.setDiffAmount(diffAmount);

		 // 更新主表状态
		 check.setCheckStatus("2");
		 check.setCheckFinishedTime(new Date());
		 inventoryCheckService.updateById(check);

		 return Result.OK("盘点完成");
	 }
//	 /**
//	  * 提交盘点结果（完成盘点）
//	  */
//	 @AutoLog(value = "盘库主表-完成盘点")
//	 @Operation(summary = "盘库主表-完成盘点")
//	 @RequiresPermissions("wms:wms_inventory_check:submit")
//	 @PostMapping(value = "/submitCheck")
//	 public Result<String> submitCheck(@RequestBody InventoryCheckVo inventoryCheckVo) {
//		 if (CollUtil.isEmpty(inventoryCheckVo.getInventoryCheckDetailList())) {
//			 return Result.error("明细数据不能为空");
//		 }
//
//		 inventoryCheckService.submitCheck(inventoryCheckVo.getId(), inventoryCheckVo.getInventoryCheckDetailList());
//		 return Result.OK("盘点提交成功！");
//	 }

	 /**
	  * 审核盘点单
	  */
	 @AutoLog(value = "盘库主表-审核")
	 @Operation(summary = "盘库主表-审核")
	 @RequiresPermissions("wms:wms_inventory_check:approve")
	 @PostMapping(value = "/approveCheck")
	 public Result<String> approveCheck(@RequestBody InventoryCheck inventoryCheck) {

		 inventoryCheckService.approveCheck(inventoryCheck);
		 return Result.OK("审核完成！");
	 }


    /**
     * 导出
     * @return
     */
    @RequiresPermissions("wms:wms_inventory_check:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, InventoryCheck inventoryCheck) {
        return super.exportXls(request, inventoryCheck, InventoryCheck.class, "盘库主表");
    }

    /**
     * 导入
     * @return
     */
    @RequiresPermissions("wms:wms_inventory_check:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, InventoryCheck.class);
    }
	/*---------------------------------主表处理-end-------------------------------------*/
	

    /*--------------------------------子表处理-盘库明细表-begin----------------------------------------------*/
	/**
	 * 通过主表ID查询
	 * @return
	 */
	//@AutoLog(value = "盘库明细表-通过主表ID查询")
	@Operation(summary="盘库明细表-通过主表ID查询")
	@GetMapping(value = "/listInventoryCheckDetailByMainId")
    public Result<IPage<InventoryCheckDetail>> listInventoryCheckDetailByMainId(InventoryCheckDetail inventoryCheckDetail,
                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                    HttpServletRequest req) {
        QueryWrapper<InventoryCheckDetail> queryWrapper = QueryGenerator.initQueryWrapper(inventoryCheckDetail, req.getParameterMap());
        Page<InventoryCheckDetail> page = new Page<InventoryCheckDetail>(pageNo, pageSize);
        IPage<InventoryCheckDetail> pageList = inventoryCheckDetailService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

	/**
	 * 添加
	 * @param inventoryCheckDetail
	 * @return
	 */
	@AutoLog(value = "盘库明细表-添加")
	@Operation(summary="盘库明细表-添加")
	@PostMapping(value = "/addInventoryCheckDetail")
	public Result<String> addInventoryCheckDetail(@RequestBody InventoryCheckDetail inventoryCheckDetail) {
		inventoryCheckDetailService.save(inventoryCheckDetail);
		return Result.OK("添加成功！");
	}

    /**
	 * 编辑
	 * @param inventoryCheckDetail
	 * @return
	 */
	@AutoLog(value = "盘库明细表-编辑")
	@Operation(summary="盘库明细表-编辑")
	@RequestMapping(value = "/editInventoryCheckDetail", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> editInventoryCheckDetail(@RequestBody InventoryCheckDetail inventoryCheckDetail) {
		inventoryCheckDetailService.updateById(inventoryCheckDetail);
		return Result.OK("编辑成功!");
	}

	/**
	 * 通过id删除
	 * @param id
	 * @return
	 */
	@AutoLog(value = "盘库明细表-通过id删除")
	@Operation(summary="盘库明细表-通过id删除")
	@DeleteMapping(value = "/deleteInventoryCheckDetail")
	public Result<String> deleteInventoryCheckDetail(@RequestParam(name="id",required=true) String id) {
		inventoryCheckDetailService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "盘库明细表-批量删除")
	@Operation(summary="盘库明细表-批量删除")
	@DeleteMapping(value = "/deleteBatchInventoryCheckDetail")
	public Result<String> deleteBatchInventoryCheckDetail(@RequestParam(name="ids",required=true) String ids) {
	    this.inventoryCheckDetailService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

    /**
     * 导出
     * @return
     */
    @RequestMapping(value = "/exportInventoryCheckDetail")
    public ModelAndView exportInventoryCheckDetail(HttpServletRequest request, InventoryCheckDetail inventoryCheckDetail) {
		 // Step.1 组装查询条件
		 QueryWrapper<InventoryCheckDetail> queryWrapper = QueryGenerator.initQueryWrapper(inventoryCheckDetail, request.getParameterMap());
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		 // Step.2 获取导出数据
		 List<InventoryCheckDetail> pageList = inventoryCheckDetailService.list(queryWrapper);
		 List<InventoryCheckDetail> exportList = null;

		 // 过滤选中数据
		 String selections = request.getParameter("selections");
		 if (oConvertUtils.isNotEmpty(selections)) {
			 List<String> selectionList = Arrays.asList(selections.split(","));
			 exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
		 } else {
			 exportList = pageList;
		 }

		 // Step.3 AutoPoi 导出Excel
		 ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		 //此处设置的filename无效,前端会重更新设置一下
		 mv.addObject(NormalExcelConstants.FILE_NAME, "盘库明细表");
		 mv.addObject(NormalExcelConstants.CLASS, InventoryCheckDetail.class);
		 mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("盘库明细表报表", "导出人:" + sysUser.getRealname(), "盘库明细表"));
		 mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
		 return mv;
    }

    /**
     * 导入
     * @return
     */
    @RequestMapping(value = "/importInventoryCheckDetail/{mainId}")
    public Result<?> importInventoryCheckDetail(HttpServletRequest request, HttpServletResponse response, @PathVariable("mainId") String mainId) {
		 MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		 Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		 for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
       // 获取上传文件对象
			 MultipartFile file = entity.getValue();
			 ImportParams params = new ImportParams();
			 params.setTitleRows(2);
			 params.setHeadRows(1);
			 params.setNeedSave(true);
			 try {
				 List<InventoryCheckDetail> list = ExcelImportUtil.importExcel(file.getInputStream(), InventoryCheckDetail.class, params);
				 for (InventoryCheckDetail temp : list) {
                    temp.setCheckId(mainId);
				 }
				 long start = System.currentTimeMillis();
				 inventoryCheckDetailService.saveBatch(list);
				 log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
				 return Result.OK("文件导入成功！数据行数：" + list.size());
			 } catch (Exception e) {
				 log.error(e.getMessage(), e);
				 return Result.error("文件导入失败:" + e.getMessage());
			 } finally {
				 try {
					 file.getInputStream().close();
				 } catch (IOException e) {
					 e.printStackTrace();
				 }
			 }
		 }
		 return Result.error("文件导入失败！");
    }

    /*--------------------------------子表处理-盘库明细表-end----------------------------------------------*/




}
